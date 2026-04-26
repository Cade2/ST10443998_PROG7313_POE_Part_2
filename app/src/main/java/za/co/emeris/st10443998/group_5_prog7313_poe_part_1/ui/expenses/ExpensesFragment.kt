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
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryProgressAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.ExpenseAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentExpensesBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.CategoryProgress
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Expense
import java.util.Calendar

// ST10318188 - Ronald Fell - Add Expense Feature with Photo Attachment

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpensesViewModel
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var totalsAdapter: CategoryProgressAdapter

    private var categoryMap: Map<Int, CategoryEntity> = emptyMap()
    private var currentEntities: List<ExpenseEntity> = emptyList()

    // Tracked LiveData + observer pairs so old observers are removed before re-subscribing.
    private var currentExpenseLiveData: LiveData<List<ExpenseEntity>>? = null
    private var currentExpenseObserver: Observer<List<ExpenseEntity>>? = null

    private var currentTotalsLiveData: LiveData<List<CategoryProgress>>? = null
    private var currentTotalsObserver: Observer<List<CategoryProgress>>? = null

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

        expenseAdapter = ExpenseAdapter(emptyList()) { expense -> showExpenseDetail(expense) }
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
            isNestedScrollingEnabled = false
        }

        totalsAdapter = CategoryProgressAdapter(emptyList())
        binding.rvCategoryTotals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = totalsAdapter
            isNestedScrollingEnabled = false
        }

        // Category map is shared by both the expense list and the totals computation.
        viewModel.getCategoriesForUser(userId).observe(viewLifecycleOwner) { cats ->
            categoryMap = cats.associateBy { it.id }
            if (currentEntities.isNotEmpty()) rebuildExpenseDisplay()
        }

        // Start with all expenses and all-time totals.
        observeAllExpenses()

        binding.etStartDate.setOnClickListener { pickDate { date -> binding.etStartDate.setText(date); applyFilter() } }
        binding.tilStartDate.setEndIconOnClickListener { pickDate { date -> binding.etStartDate.setText(date); applyFilter() } }

        binding.etEndDate.setOnClickListener { pickDate { date -> binding.etEndDate.setText(date); applyFilter() } }
        binding.tilEndDate.setEndIconOnClickListener { pickDate { date -> binding.etEndDate.setText(date); applyFilter() } }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_expenses_to_settings)
        }

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

    // "All time" uses a span wide enough to capture every possible date stored as yyyy-MM-dd.
    private fun observeAllExpenses() {
        swapExpenseObserver(viewModel.getAllExpenses(userId))
        swapTotalsObserver(viewModel.getCategoryTotalsForPeriod(userId, "0001-01-01", "9999-12-31"))
    }

    private fun observeFiltered(start: String, end: String) {
        swapExpenseObserver(viewModel.getExpensesByDateRange(userId, start, end))
        swapTotalsObserver(viewModel.getCategoryTotalsForPeriod(userId, start, end))
    }

    private fun swapExpenseObserver(newLiveData: LiveData<List<ExpenseEntity>>) {
        currentExpenseObserver?.let { currentExpenseLiveData?.removeObserver(it) }
        val observer = Observer<List<ExpenseEntity>> { entities ->
            currentEntities = entities ?: emptyList()
            if (categoryMap.isNotEmpty()) rebuildExpenseDisplay()
        }
        currentExpenseObserver = observer
        currentExpenseLiveData = newLiveData
        newLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun swapTotalsObserver(newLiveData: LiveData<List<CategoryProgress>>) {
        currentTotalsObserver?.let { currentTotalsLiveData?.removeObserver(it) }
        val observer = Observer<List<CategoryProgress>> { totals ->
            totalsAdapter.updateItems(totals ?: emptyList())
        }
        currentTotalsObserver = observer
        currentTotalsLiveData = newLiveData
        newLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun rebuildExpenseDisplay() {
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
        expenseAdapter.updateExpenses(expenses)
    }

    private fun showExpenseDetail(expense: Expense) {
        val entity = currentEntities.find { it.id == expense.id } ?: return
        val photoPath = entity.photoPath

        val bitmap = if (photoPath != null) {
            runCatching {
                if (photoPath.startsWith("content://")) {
                    val uri = android.net.Uri.parse(photoPath)
                    requireContext().contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                } else {
                    BitmapFactory.decodeFile(photoPath)
                }
            }.getOrNull()
        } else null

        val ctx = requireContext()
        val padding = (24 * ctx.resources.displayMetrics.density).toInt()

        val layout = android.widget.LinearLayout(ctx).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
        }

        val tvCategory = android.widget.TextView(ctx).apply {
            text = expense.category
            textSize = 14f
            setTextColor(android.graphics.Color.parseColor("#616161"))
        }
        val tvAmount = android.widget.TextView(ctx).apply {
            text = java.lang.String.format(java.util.Locale.US, "R%,.2f", expense.amount)
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(android.graphics.Color.parseColor("#1B2A4A"))
            setPadding(0, (4 * ctx.resources.displayMetrics.density).toInt(), 0, 0)
        }
        val tvDate = android.widget.TextView(ctx).apply {
            text = expense.date
            textSize = 14f
            setTextColor(android.graphics.Color.parseColor("#616161"))
            setPadding(0, (4 * ctx.resources.displayMetrics.density).toInt(), 0, 0)
        }
        val ivPhoto = ImageView(ctx).apply {
            visibility = View.GONE
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(0, (12 * ctx.resources.displayMetrics.density).toInt(), 0, 0)
        }

        layout.addView(tvCategory)
        layout.addView(tvAmount)
        layout.addView(tvDate)
        layout.addView(ivPhoto)

        if (bitmap != null) {
            ivPhoto.setImageBitmap(bitmap)
            ivPhoto.visibility = View.VISIBLE
        }

        AlertDialog.Builder(ctx)
            .setTitle(expense.description)
            .setView(layout)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
