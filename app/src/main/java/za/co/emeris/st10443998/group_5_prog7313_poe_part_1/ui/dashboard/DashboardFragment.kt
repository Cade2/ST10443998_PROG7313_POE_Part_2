package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryProgressAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentDashboardBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.CategoryProgress

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryList()

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addExpense)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_settings)
        }
    }

    private fun setupCategoryList() {
        val categories = listOf(
            CategoryProgress("Groceries", Color.parseColor("#2E7D32"), 450.0, 800.0),
            CategoryProgress("Transport", Color.parseColor("#1565C0"), 320.0, 600.0),
            CategoryProgress("Entertainment", Color.parseColor("#6A1B9A"), 180.0, 300.0),
            CategoryProgress("Utilities", Color.parseColor("#E65100"), 750.0, 750.0)
        )

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CategoryProgressAdapter(categories)
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
