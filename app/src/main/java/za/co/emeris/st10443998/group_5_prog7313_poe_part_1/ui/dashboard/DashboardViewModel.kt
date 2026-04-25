package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.dashboard

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.CategoryProgress
import java.util.Calendar

/**
 * ViewModel for [DashboardFragment].
 *
 * Combines three Room LiveData sources — categories, budget goals, and expenses — into a
 * single [CategoryProgress] list via [MediatorLiveData]. The result is recomputed whenever
 * any of the three underlying tables change.
 */
class DashboardViewModel(private val repository: StashRepository) : ViewModel() {

    private var cachedUserId = -1
    private var progressLiveData: LiveData<List<CategoryProgress>>? = null

    /**
     * Returns a [LiveData] stream of [CategoryProgress] for each category belonging to [userId].
     *
     * - spent = sum of this month's expenses in that category
     * - budget = the category's maxGoal for the current month (0 if not set)
     *
     * The LiveData updates automatically whenever categories, goals, or expenses change.
     */
    fun getCategoryProgress(userId: Int): LiveData<List<CategoryProgress>> {
        if (progressLiveData != null && cachedUserId == userId) return progressLiveData!!
        cachedUserId = userId

        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        val monthPrefix = "%04d-%02d".format(year, month)

        val categoriesLd = repository.getAllCategoriesForUser(userId)
        val goalsLd = repository.getGoalsForMonth(userId, month, year)
        val expensesLd = repository.getAllExpensesForUser(userId)

        val result = MediatorLiveData<List<CategoryProgress>>()

        fun recompute() {
            val categories = categoriesLd.value ?: return
            val goals = goalsLd.value ?: emptyList()
            val expenses = expensesLd.value ?: emptyList()

            val monthExpenses = expenses.filter { it.date.startsWith(monthPrefix) }
            val goalsMap = goals.associateBy { it.categoryId }
            val spentMap = monthExpenses.groupBy { it.categoryId }
                .mapValues { (_, exps) -> exps.sumOf { it.amount } }

            val list = categories.map { cat ->
                val color = runCatching { Color.parseColor(cat.colorHex) }.getOrElse { Color.GRAY }
                CategoryProgress(
                    name = cat.name,
                    color = color,
                    spent = spentMap[cat.id] ?: 0.0,
                    budget = goalsMap[cat.id]?.maxGoal ?: 0.0
                )
            }
            Log.d(TAG, "recompute: ${list.size} categories, month=$monthPrefix")
            result.value = list
        }

        result.addSource(categoriesLd) { recompute() }
        result.addSource(goalsLd) { recompute() }
        result.addSource(expensesLd) { recompute() }

        progressLiveData = result
        return result
    }

    /**
     * Returns a time-appropriate greeting string based on the current hour.
     *
     * - before 12:00 → "Good morning"
     * - 12:00–16:59 → "Good afternoon"
     * - 17:00+       → "Good evening"
     */
    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    companion object {
        private const val TAG = "Dashboard"
    }

    /** @see ViewModelProvider.Factory */
    class Factory(private val repository: StashRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DashboardViewModel(repository) as T
    }
}
