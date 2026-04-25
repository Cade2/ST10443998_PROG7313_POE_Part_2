package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.graph

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentSpendingGraphBinding

class SpendingGraphFragment : Fragment() {

    private var _binding: FragmentSpendingGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpendingGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBarChart()
    }

    private fun setupBarChart() {
        val entries = listOf(
            BarEntry(0f, 320f),
            BarEntry(1f, 450f),
            BarEntry(2f, 280f),
            BarEntry(3, 520f),
            BarEntry(4f, 390f),
            BarEntry(5f, 410f),
            BarEntry(6f, 245f)
        )

        val dataSet = BarDataSet(entries, "Daily Spending").apply {
            colors = listOf(
                Color.parseColor("#2E7D32"),
                Color.parseColor("#1565C0"),
                Color.parseColor("#6A1B9A"),
                Color.parseColor("#E65100"),
                Color.parseColor("#C62828"),
                Color.parseColor("#2E7D32"),
                Color.parseColor("#1565C0")
            )
            valueTextColor = Color.parseColor("#1B2A4A")
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.7f
        }

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)

            // Dashed limit lines
            val maxGoalLine = LimitLine(500f, "Max Goal").apply {
                lineColor = Color.RED
                lineWidth = 1.5f
                enableDashedLine(10f, 5f, 0f)
                textColor = Color.RED
                textSize = 10f
            }
            val minGoalLine = LimitLine(200f, "Min Goal").apply {
                lineColor = Color.BLUE
                lineWidth = 1.5f
                enableDashedLine(10f, 5f, 0f)
                textColor = Color.BLUE
                textSize = 10f
            }

            axisLeft.apply {
                addLimitLine(maxGoalLine)
                addLimitLine(minGoalLine)
                axisMinimum = 0f
            }
            axisRight.isEnabled = false
            xAxis.apply {
                setDrawGridLines(false)
                setDrawLabels(true)
            }

            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
