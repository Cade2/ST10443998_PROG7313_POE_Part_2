package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.budget

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryGoal
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryGoalAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.BudgetGoalEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentBudgetGoalsBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Category

class BudgetGoalsFragment : Fragment() {

    private var _binding: FragmentBudgetGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BudgetGoalsViewModel
    private lateinit var goalAdapter: CategoryGoalAdapter

    private var userId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = requireContext()
            .getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
            .getInt("userId", -1)

        val repository = StashRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, BudgetGoalsViewModel.Factory(repository))[BudgetGoalsViewModel::class.java]

        goalAdapter = CategoryGoalAdapter()
        binding.rvCategoryGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalAdapter
            isNestedScrollingEnabled = false
        }

        viewModel.getCategoriesWithGoals(userId).observe(viewLifecycleOwner) { (categories, goals) ->
            val goalsMap = goals.associateBy { it.categoryId }
            val items = categories.map { entity ->
                val color = runCatching { Color.parseColor(entity.colorHex) }.getOrElse { Color.GRAY }
                val existing = goalsMap[entity.id]
                CategoryGoal(
                    category = Category(id = entity.id, name = entity.name, colorResId = 0, color = color),
                    minGoal = existing?.minGoal?.let { "%.0f".format(it) } ?: "",
                    maxGoal = existing?.maxGoal?.let { "%.0f".format(it) } ?: ""
                )
            }
            goalAdapter.updateGoals(items)
        }

        binding.btnManageCategories.setOnClickListener {
            findNavController().navigate(R.id.action_budget_to_categories)
        }

        binding.btnSaveGoals.setOnClickListener { saveGoals() }
    }

    private fun saveGoals() {
        val goals = goalAdapter.getGoals()
        val (month, year) = viewModel.getCurrentMonthAndYear()

        for (goal in goals) {
            val minStr = goal.minGoal.trim()
            val maxStr = goal.maxGoal.trim()

            if (minStr.isEmpty() && maxStr.isEmpty()) continue

            val min = minStr.toDoubleOrNull()
            val max = maxStr.toDoubleOrNull()

            if (min == null && minStr.isNotEmpty()) {
                Snackbar.make(binding.root, "Invalid minimum for ${goal.category.name}", Snackbar.LENGTH_LONG).show()
                return
            }
            if (max == null && maxStr.isNotEmpty()) {
                Snackbar.make(binding.root, "Invalid maximum for ${goal.category.name}", Snackbar.LENGTH_LONG).show()
                return
            }
            if (min != null && max != null && min > max) {
                Snackbar.make(binding.root, "Min must be ≤ max for ${goal.category.name}", Snackbar.LENGTH_LONG).show()
                return
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            goals.forEach { goal ->
                val minStr = goal.minGoal.trim()
                val maxStr = goal.maxGoal.trim()
                if (minStr.isEmpty() && maxStr.isEmpty()) return@forEach

                viewModel.saveGoal(
                    BudgetGoalEntity(
                        userId = userId,
                        categoryId = goal.category.id,
                        minGoal = minStr.toDoubleOrNull() ?: 0.0,
                        maxGoal = maxStr.toDoubleOrNull() ?: 0.0,
                        month = month,
                        year = year
                    )
                )
            }
            Snackbar.make(binding.root, getString(R.string.toast_saved), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
