package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryProgressAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private lateinit var categoryProgressAdapter: CategoryProgressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        val username = prefs.getString("username", "there") ?: "there"

        val repository = StashRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, DashboardViewModel.Factory(repository))[DashboardViewModel::class.java]

        binding.tvGreeting.text = "${viewModel.getGreeting()}, $username"

        categoryProgressAdapter = CategoryProgressAdapter(emptyList())
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryProgressAdapter
            isNestedScrollingEnabled = false
        }

        viewModel.getCategoryProgress(userId).observe(viewLifecycleOwner) { progressList ->
            categoryProgressAdapter = CategoryProgressAdapter(progressList)
            binding.rvCategories.adapter = categoryProgressAdapter

            val totalSpent = progressList.sumOf { it.spent }
            val totalBudget = progressList.sumOf { it.budget }

            binding.tvOverallAmount.text = "R%.0f / R%.0f".format(totalSpent, totalBudget)

            val progress = if (totalBudget > 0) ((totalSpent / totalBudget) * 100).toInt() else 0
            binding.pbOverall.progress = progress.coerceIn(0, 100)

            when {
                totalBudget <= 0 -> {
                    binding.tvMotivationalTitle.text = "Set your budget goals!"
                    binding.tvMotivationalSubtitle.text = "Head to Budget Goals to get started."
                }
                totalSpent <= totalBudget * 0.75 -> {
                    binding.tvMotivationalTitle.text = "You're on track this month!"
                    binding.tvMotivationalSubtitle.text = "Keep up the great saving habits."
                }
                totalSpent <= totalBudget -> {
                    binding.tvMotivationalTitle.text = "Watch your spending!"
                    binding.tvMotivationalSubtitle.text = "You're getting close to your budget limit."
                }
                else -> {
                    binding.tvMotivationalTitle.text = "Budget exceeded!"
                    binding.tvMotivationalSubtitle.text = "You've gone over budget. Time to cut back."
                }
            }
        }

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addExpense)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_settings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
