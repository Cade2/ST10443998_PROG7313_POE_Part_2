package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.expenses

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.ExpenseAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentExpensesBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Expense
import java.io.File
import java.util.Calendar

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpensesViewModel
    private lateinit var adapter: ExpenseAdapter

    private var categoryMap: Map<Int, CategoryEntity> = emptyMap()
    private var currentEntities: List<ExpenseEntity> = emptyList()

    private var currentLiveData: LiveData<List<ExpenseEntity>>? = null
    private var currentObserver: Observer<List<ExpenseEntity>>? = null

    private var userId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = requireContext()
            .getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
            .getInt("userId", -1)

        val repository = StashRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ExpensesViewModel.Factory(repository))[ExpensesViewModel::class.java]

        adapter = ExpenseAdapter(emptyList()) { expense ->
            showExpenseDetail(expense)
        }
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ExpensesFragment.adapter
            isNestedScrollingEnabled = false
        }

        viewModel.getCategoriesForUser(userId).observe(viewLifecycleOwner) { cats ->
            categoryMap = cats.associateBy { it.id }
            rebuildDisplay()
        }

        observeAllExpenses()

        binding.etStartDate.setOnClickListener { pickDate { date -> binding.etStartDate.setText(date); applyFilter() } }
        binding.tilStartDate.setEndIconOnClickListener { pickDate { date -> binding.etStartDate.setText(date); applyFilter() } }

        binding.etEndDate.setOnClickListener { pickDate { date -> binding.etEndDate.setText(date); applyFilter() } }
        binding.tilEndDate.setEndIconOnClickListener { pickDate { date -> binding.etEndDate.setText(date); applyFilter() } }

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_expenses_to_addExpense)
        }
    }

    private fun pickDate(onPicked: (String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, y, m, d -> onPicked("%04d-%02d-%02d".format(y, m + 1, d)) },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun applyFilter() {
        val start = binding.etStartDate.text?.toString()?.trim() ?: ""
        val end = binding.etEndDate.text?.toString()?.trim() ?: ""
        if (start.isNotEmpty() && end.isNotEmpty()) {
            observeFiltered(start, end)
        } else {
            observeAllExpenses()
        }
    }

    private fun observeAllExpenses() {
        swapObserver(viewModel.getAllExpenses(userId))
    }

    private fun observeFiltered(start: String, end: String) {
        swapObserver(viewModel.getExpensesByDateRange(userId, start, end))
    }

    private fun swapObserver(newLiveData: LiveData<List<ExpenseEntity>>) {
        currentObserver?.let { currentLiveData?.removeObserver(it) }
        val observer = Observer<List<ExpenseEntity>> { entities ->
            currentEntities = entities ?: emptyList()
            rebuildDisplay()
        }
        currentObserver = observer
        currentLiveData = newLiveData
        newLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun rebuildDisplay() {
        val expenses = currentEntities.map { entity ->
            val cat = categoryMap[entity.categoryId]
            Expense(
                id = entity.id,
                category = cat?.name ?: "Unknown",
                categoryColor = runCatching {
                    android.graphics.Color.parseColor(cat?.colorHex ?: "#607D8B")
                }.getOrElse { android.graphics.Color.GRAY },
                description = entity.description,
                amount = entity.amount,
                date = entity.date,
                hasPhoto = entity.photoPath != null
            )
        }
        adapter.updateExpenses(expenses)
    }

    private fun showExpenseDetail(expense: Expense) {
        val entity = currentEntities.find { it.id == expense.id } ?: return
        val photoPath = entity.photoPath

        if (photoPath != null && File(photoPath).exists()) {
            val imageView = ImageView(requireContext()).apply {
                val bmp = BitmapFactory.decodeFile(photoPath)
                setImageBitmap(bmp)
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            AlertDialog.Builder(requireContext())
                .setTitle(expense.description)
                .setView(imageView)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle(expense.description)
                .setMessage(
                    "${expense.category}\n" +
                    "R%.2f".format(expense.amount) + "\n" +
                    expense.date
                )
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
