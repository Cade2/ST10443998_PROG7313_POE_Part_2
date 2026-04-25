package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.expenses

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentAddExpenseBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddExpenseViewModel
    private var categoryList: List<CategoryEntity> = emptyList()
    private var userId: Int = -1

    // Kept as a field so the camera result callback can read it after the activity returns
    private var cameraImageFile: File? = null

    // ── Activity result launchers (must be registered before onCreateView) ────

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val path = cameraImageFile?.absolutePath
                Log.d(TAG, "Camera capture success, path=$path")
                viewModel.selectedPhotoPath = path
                showPhotoThumbnail()
            } else {
                Log.d(TAG, "Camera capture cancelled or failed")
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    Log.d(TAG, "Gallery pick success, uri=$uri")
                    viewModel.selectedPhotoPath = uri.toString()
                    showPhotoThumbnail()
                }
            } else {
                Log.d(TAG, "Gallery pick cancelled")
            }
        }

    // ── Fragment lifecycle ────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = requireContext()
            .getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
            .getInt("userId", -1)
        Log.d(TAG, "onViewCreated: userId=$userId")

        val repository = StashRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(
            this, AddExpenseViewModel.Factory(repository)
        )[AddExpenseViewModel::class.java]

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.switchRecurring.setOnCheckedChangeListener { _, isChecked ->
            binding.tilFrequency.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        setupCategoryDropdown()
        setupFrequencyDropdown()
        setupDatePicker()
        setupTimePickers()
        setupPhotoAttachment()
        setupSaveButton()

        // Restore thumbnail if the ViewModel survived a config change
        if (viewModel.selectedPhotoPath != null) {
            Log.d(TAG, "Restoring photo thumbnail after config change")
            showPhotoThumbnail()
        }
    }

    // ── Category dropdown ─────────────────────────────────────────────────────

    private fun setupCategoryDropdown() {
        viewModel.getCategoriesForUser(userId).observe(viewLifecycleOwner) { categories ->
            categoryList = categories
            Log.d(TAG, "Category list updated: ${categories.size} items")
            val names = categories.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                names
            )
            binding.acvCategory.setAdapter(adapter)
        }

        binding.acvCategory.setOnItemClickListener { _, _, position, _ ->
            if (position < categoryList.size) {
                viewModel.selectedCategoryId = categoryList[position].id
                binding.tilCategory.error = null
                Log.d(TAG, "Category selected: ${categoryList[position].name}, id=${categoryList[position].id}")
            }
        }
    }

    private fun setupFrequencyDropdown() {
        val frequencies = listOf("Daily", "Weekly", "Monthly")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            frequencies
        )
        binding.acvFrequency.setAdapter(adapter)
    }

    // ── Date picker ───────────────────────────────────────────────────────────

    private fun setupDatePicker() {
        val openPicker = View.OnClickListener { showDatePicker() }
        binding.etDate.setOnClickListener(openPicker)
        binding.tilDate.setEndIconOnClickListener(openPicker)
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val formatted = "%04d-%02d-%02d".format(year, month + 1, day)
                binding.etDate.setText(formatted)
                binding.tilDate.error = null
                Log.d(TAG, "Date selected: $formatted")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // ── Time pickers ──────────────────────────────────────────────────────────

    private fun setupTimePickers() {
        binding.etStartTime.setOnClickListener {
            showTimePicker { time ->
                binding.etStartTime.setText(time)
                binding.tilStartTime.error = null
                Log.d(TAG, "Start time selected: $time")
            }
        }
        binding.etEndTime.setOnClickListener {
            showTimePicker { time ->
                binding.etEndTime.setText(time)
                binding.tilEndTime.error = null
                Log.d(TAG, "End time selected: $time")
            }
        }
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute -> onTimeSelected("%02d:%02d".format(hour, minute)) },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    // ── Photo attachment ──────────────────────────────────────────────────────

    private fun setupPhotoAttachment() {
        binding.btnAttachPhoto.setOnClickListener {
            Log.d(TAG, "Photo attach button clicked")
            AlertDialog.Builder(requireContext())
                .setTitle("Attach Photo")
                .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                    when (which) {
                        0 -> launchCamera()
                        1 -> launchGallery()
                    }
                }
                .show()
        }
    }

    private fun launchCamera() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "photo_$timestamp.jpg"
            )
            cameraImageFile = file
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            Log.d(TAG, "Launching camera, output=${file.absolutePath}")
            takePictureLauncher.launch(uri)
        } catch (e: Exception) {
            Log.d(TAG, "Camera launch failed: ${e.message}")
            Snackbar.make(binding.root, "Could not launch camera", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun launchGallery() {
        Log.d(TAG, "Launching gallery picker")
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        pickImageLauncher.launch(intent)
    }

    private fun showPhotoThumbnail() {
        val photoPath = viewModel.selectedPhotoPath ?: return
        val uri = if (photoPath.startsWith("content://")) {
            Uri.parse(photoPath)
        } else {
            Uri.fromFile(File(photoPath))
        }
        binding.ivPhotoThumbnail.visibility = View.VISIBLE
        binding.ivPhotoThumbnail.setImageURI(uri)
        Log.d(TAG, "Thumbnail displayed, path=$photoPath")
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    private fun setupSaveButton() {
        binding.btnSaveExpense.setOnClickListener {
            Log.d(TAG, "Save button clicked")
            if (!validateInputs()) return@setOnClickListener

            val expense = ExpenseEntity(
                amount = binding.etAmount.text.toString().trim().toDouble(),
                date = binding.etDate.text.toString().trim(),
                startTime = binding.etStartTime.text.toString().trim(),
                endTime = binding.etEndTime.text.toString().trim(),
                description = binding.etDescription.text.toString().trim(),
                categoryId = viewModel.selectedCategoryId!!,
                userId = userId,
                photoPath = viewModel.selectedPhotoPath
            )
            Log.d(TAG, "Saving expense: amount=${expense.amount}, date=${expense.date}, categoryId=${expense.categoryId}")

            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val id = viewModel.saveExpense(expense)
                    Log.d(TAG, "Expense saved, id=$id")
                    Snackbar.make(binding.root, "Expense saved", Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    Log.d(TAG, "Expense save failed: ${e.message}")
                    Snackbar.make(binding.root, "Failed to save expense", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private fun validateInputs(): Boolean {
        var isValid = true

        val amountStr = binding.etAmount.text?.toString()?.trim() ?: ""
        val amount = amountStr.toDoubleOrNull()
        binding.tilAmount.error = when {
            amountStr.isEmpty() -> { isValid = false; getString(R.string.error_required_field) }
            amount == null || amount <= 0 -> { isValid = false; "Amount must be greater than 0" }
            else -> null
        }

        val date = binding.etDate.text?.toString()?.trim() ?: ""
        binding.tilDate.error =
            if (date.isEmpty()) { isValid = false; getString(R.string.error_required_field) } else null

        val startTime = binding.etStartTime.text?.toString()?.trim() ?: ""
        binding.tilStartTime.error =
            if (startTime.isEmpty()) { isValid = false; getString(R.string.error_required_field) } else null

        val endTime = binding.etEndTime.text?.toString()?.trim() ?: ""
        if (startTime.isNotEmpty() && endTime.isNotEmpty() && !isEndTimeAfterStartTime(startTime, endTime)) {
            binding.tilEndTime.error = "End time must be after start time"
            isValid = false
        } else {
            binding.tilEndTime.error = null
        }

        val description = binding.etDescription.text?.toString()?.trim() ?: ""
        binding.tilDescription.error =
            if (description.isEmpty()) { isValid = false; getString(R.string.error_required_field) } else null

        binding.tilCategory.error =
            if (viewModel.selectedCategoryId == null) { isValid = false; getString(R.string.error_required_field) } else null

        Log.d(TAG, "validateInputs: isValid=$isValid")
        return isValid
    }

    private fun isEndTimeAfterStartTime(startTime: String, endTime: String): Boolean {
        return try {
            val (sh, sm) = startTime.split(":").map { it.toInt() }
            val (eh, em) = endTime.split(":").map { it.toInt() }
            (eh * 60 + em) > (sh * 60 + sm)
        } catch (e: Exception) {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "AddExpense"
    }
}
