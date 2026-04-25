package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.expenses

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.ExpenseAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentExpensesBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Expense

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupExpenseList()

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_expenses_to_addExpense)
        }
    }

    private fun setupExpenseList() {
        val expenses = listOf(
            Expense(1, "Groceries", Color.parseColor("#2E7D32"),
                "Weekly grocery run", 245.50, "12 Mar 2026", true),
            Expense(2, "Transport", Color.parseColor("#1565C0"),
                "Monthly bus pass", 320.00, "11 Mar 2026", false),
            Expense(3, "Entertainment", Color.parseColor("#6A1B9A"),
                "Netflix subscription", 159.00, "10 Mar 2026", false),
            Expense(4, "Utilities", Color.parseColor("#E65100"),
                "Electricity bill", 456.00, "08 Mar 2026", false),
            Expense(5, "Dining", Color.parseColor("#C62828"),
                "Lunch with colleagues", 185.00, "07 Mar 2026", true)
        )

        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ExpenseAdapter(expenses)
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
