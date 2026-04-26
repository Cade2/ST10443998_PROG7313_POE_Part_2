package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.graph

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentSpendingGraphBinding
import java.util.Calendar

class SpendingGraphFragment : Fragment() {

    private var _binding: FragmentSpendingGraphBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GraphViewModel
    private var userId = -1

    private var startDate = ""
    private var endDate = ""

    private var currentLiveData: LiveData<List<ExpenseEntity>>? = null
    private var currentObserver: Observer<List<ExpenseEntity>>? = null

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

        binding.barChart.setNoDataText("No expenses recorded for this period.")
        binding.barChart.setNoDataTextColor(Color.parseColor("#1B2A4A"))

        // Default to current month on first load and populate the date fields.
        val (defaultStart, defaultEnd) = viewModel.getCurrentMonthRange()
        startDate = defaultStart
        endDate = defaultEnd
        binding.etStartDate.setText(startDate)
        binding.etEndDate.setText(endDate)
        observeExpenses(startDate, endDate)

        binding.etStartDate.setOnClickListener {
            pickDate { date -> startDate = date; binding.etStartDate.setText(date); applyFilter() }
        }
        binding.tilStartDate.setEndIconOnClickListener {
            pickDate { date -> startDate = date; binding.etStartDate.setText(date); applyFilter() }
        }

        binding.etEndDate.setOnClickListener {
            pickDate { date -> endDate = date; binding.etEndDate.setText(date); applyFilter() }
        }
        binding.tilEndDate.setEndIconOnClickListener {
            pickDate { date -> endDate = date; binding.etEndDate.setText(date); applyFilter() }
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
        // If only start is set, default end to the last day of the current month.
        val effectiveEnd = if (endDate.isNotEmpty()) endDate else viewModel.getCurrentMonthRange().second
        observeExpenses(startDate, effectiveEnd)
    }

    private fun observeExpenses(start: String, end: String) {
        currentObserver?.let { currentLiveData?.removeObserver(it) }
        val observer = Observer<List<ExpenseEntity>> { expenses -> renderChart(expenses) }
        currentObserver = observer
        val liveData = viewModel.getMonthlyExpenses(userId, start, end)
        currentLiveData = liveData
        liveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderChart(expenses: List<ExpenseEntity>?) {
        if (expenses.isNullOrEmpty()) {
            binding.barChart.clear()
            binding.barChart.invalidate()
            return
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
