package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.expenses

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.CategoryProgress
import java.util.Calendar

/**
 * ViewModel for [ExpensesFragment].
 * Exposes date-ranged expense queries and category lookup for UI mapping.
 */
class ExpensesViewModel(private val repository: StashRepository) : ViewModel() {

    /**
     * Returns a [LiveData] stream of all expenses for [userId], newest first.
     */
    fun getAllExpenses(userId: Int): LiveData<List<ExpenseEntity>> =
        repository.getAllExpensesForUser(userId)

    /**
     * Returns a [LiveData] stream of expenses for [userId] in [[start], [end]] (yyyy-MM-dd).
     */
    fun getExpensesByDateRange(userId: Int, start: String, end: String): LiveData<List<ExpenseEntity>> =
        repository.getExpensesByDateRange(userId, start, end)

    /**
     * Returns a [LiveData] stream of all categories for [userId].
     * Used by the fragment to build a categoryId → entity map for display mapping.
     */
    fun getCategoriesForUser(userId: Int): LiveData<List<CategoryEntity>> =
        repository.getAllCategoriesForUser(userId)

    /**
     * Returns the first and last day of the current calendar month as a "yyyy-MM-dd" pair.
     */
    fun getCurrentMonthRange(): Pair<String, String> {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-01".format(year, month) to "%04d-%02d-%02d".format(year, month, lastDay)
    }

    /**
     * Returns a [LiveData] stream of [CategoryProgress] items (budget = 0) representing the
     * total amount spent per category within [[start], [end]] (yyyy-MM-dd).
     *
     * Only categories that have at least one expense in the period are included.
     * Results are sorted by total spend descending so the highest-spending category appears first.
     * The [CategoryProgress.budget] is always 0 — callers should hide any progress bar UI.
     */
    fun getCategoryTotalsForPeriod(userId: Int, start: String, end: String): LiveData<List<CategoryProgress>> {
        val result = MediatorLiveData<List<CategoryProgress>>()
        val expensesLd = repository.getExpensesByDateRange(userId, start, end)
        val categoriesLd = repository.getAllCategoriesForUser(userId)

        fun recompute() {
            val expenses = expensesLd.value ?: return
            val categories = categoriesLd.value ?: return
            val catMap = categories.associateBy { it.id }
            val list = expenses
                .groupBy { it.categoryId }
                .mapValues { (_, exps) -> exps.sumOf { it.amount } }
                .mapNotNull { (catId, total) ->
                    val cat = catMap[catId] ?: return@mapNotNull null
                    CategoryProgress(
                        name = cat.name,
                        color = runCatching { Color.parseColor(cat.colorHex) }.getOrElse { Color.GRAY },
                        spent = total,
                        budget = 0.0
                    )
                }
                .sortedByDescending { it.spent }
            result.value = list
        }

        result.addSource(expensesLd) { recompute() }
        result.addSource(categoriesLd) { recompute() }
        return result
    }

    /** @see ViewModelProvider.Factory */
    class Factory(private val repository: StashRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ExpensesViewModel(repository) as T
    }
}
