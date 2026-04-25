package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryGoal
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryGoalAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentBudgetGoalsBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Category

class BudgetGoalsFragment : Fragment() {

    private var _binding: FragmentBudgetGoalsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryGoalList()

        binding.btnManageCategories.setOnClickListener {
            findNavController().navigate(R.id.action_budget_to_categories)
        }

        binding.btnSaveGoals.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCategoryGoalList() {
        val goals = listOf(
            CategoryGoal(Category(1, "Groceries", 0, Color.parseColor("#2E7D32")), "500", "900"),
            CategoryGoal(Category(2, "Transport", 0, Color.parseColor("#1565C0")), "200", "600"),
            CategoryGoal(Category(3, "Entertainment", 0, Color.parseColor("#6A1B9A")), "100", "300"),
            CategoryGoal(Category(4, "Utilities", 0, Color.parseColor("#E65100")), "400", "800"),
            CategoryGoal(Category(5, "Dining", 0, Color.parseColor("#C62828")), "100", "400")
        )

        binding.rvCategoryGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CategoryGoalAdapter(goals)
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
