package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.graph

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentSpendingGraphBinding

class SpendingGraphFragment : Fragment() {

    private var _binding: FragmentSpendingGraphBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GraphViewModel
    private var userId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpendingGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = requireContext()
            .getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
            .getInt("userId", -1)

        val repository = StashRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, GraphViewModel.Factory(repository))[GraphViewModel::class.java]

        binding.barChart.setNoDataText("No expenses recorded this month.")
        binding.barChart.setNoDataTextColor(Color.parseColor("#1B2A4A"))

        val (start, end) = viewModel.getCurrentMonthRange()
        viewModel.getMonthlyExpenses(userId, start, end).observe(viewLifecycleOwner) { expenses ->
            if (expenses.isNullOrEmpty()) {
                binding.barChart.clear()
                binding.barChart.invalidate()
                return@observe
            }

            val dailyTotals = expenses
                .groupBy { it.date }
                .mapValues { (_, exps) -> exps.sumOf { it.amount } }
                .toSortedMap()

            val sortedDates = dailyTotals.keys.toList()
            val entries = sortedDates.mapIndexed { index, date ->
                BarEntry(index.toFloat(), dailyTotals[date]!!.toFloat())
            }

            val labels = sortedDates.map { date ->
                date.substringAfterLast("-").trimStart('0').ifEmpty { "0" }
            }

            val dataSet = BarDataSet(entries, "Daily Spending").apply {
                color = Color.parseColor("#C9982A")
                valueTextColor = Color.parseColor("#1B2A4A")
                valueTextSize = 10f
            }

            val barData = BarData(dataSet).apply { barWidth = 0.7f }

            binding.barChart.apply {
                data = barData
                description.isEnabled = false
                legend.isEnabled = false
                setFitBars(true)

                axisLeft.apply {
                    removeAllLimitLines()
                    axisMinimum = 0f
                    textColor = Color.parseColor("#1B2A4A")
                }
                axisRight.isEnabled = false

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    setDrawGridLines(false)
                    granularity = 1f
                    textColor = Color.parseColor("#1B2A4A")
                }

                invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
