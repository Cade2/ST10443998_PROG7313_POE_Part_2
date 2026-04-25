package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentAddExpenseBinding

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryDropdown()
        setupFrequencyDropdown()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.switchRecurring.setOnCheckedChangeListener { _, isChecked ->
            binding.tilFrequency.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.btnAttachPhoto.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveExpense.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(requireContext(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupCategoryDropdown() {
        val categories = listOf("Groceries", "Transport", "Entertainment", "Utilities", "Dining")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.acvCategory.setAdapter(adapter)
    }

    private fun setupFrequencyDropdown() {
        val frequencies = listOf("Daily", "Weekly", "Monthly")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, frequencies)
        binding.acvFrequency.setAdapter(adapter)
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val amount = binding.etAmount.text?.toString()?.trim() ?: ""
        val date = binding.etDate.text?.toString()?.trim() ?: ""
        val category = binding.acvCategory.text?.toString()?.trim() ?: ""

        binding.tilAmount.error = if (amount.isEmpty()) {
            isValid = false; getString(R.string.error_required_field)
        } else null

        binding.tilDate.error = if (date.isEmpty()) {
            isValid = false; getString(R.string.error_required_field)
        } else null

        binding.tilCategory.error = if (category.isEmpty()) {
            isValid = false; getString(R.string.error_required_field)
        } else null

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
