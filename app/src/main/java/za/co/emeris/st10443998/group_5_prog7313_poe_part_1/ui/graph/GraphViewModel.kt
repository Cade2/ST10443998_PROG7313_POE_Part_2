package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.graph

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import java.util.Calendar

/**
 * ViewModel for [SpendingGraphFragment].
 * Provides date-ranged expense data for bar chart rendering.
 */
class GraphViewModel(private val repository: StashRepository) : ViewModel() {

    /**
     * Returns a [LiveData] stream of expenses for [userId] within [[start], [end]] (yyyy-MM-dd).
     */
    fun getMonthlyExpenses(userId: Int, start: String, end: String): LiveData<List<ExpenseEntity>> =
        repository.getExpensesByDateRange(userId, start, end)

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
            GraphViewModel(repository) as T
    }
}
