package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
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

    /** @see ViewModelProvider.Factory */
    class Factory(private val repository: StashRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ExpensesViewModel(repository) as T
    }
}
