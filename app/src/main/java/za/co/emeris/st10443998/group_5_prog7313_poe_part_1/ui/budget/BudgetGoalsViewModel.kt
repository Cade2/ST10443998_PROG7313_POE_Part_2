package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.BudgetGoalEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import java.util.Calendar

/**
 * ViewModel for [BudgetGoalsFragment].
 * Exposes combined categories + goals data and upsert for the current month.
 */
class BudgetGoalsViewModel(private val repository: StashRepository) : ViewModel() {

    /**
     * Returns the current calendar month (1-based) and year as a [Pair].
     */
    fun getCurrentMonthAndYear(): Pair<Int, Int> {
        val cal = Calendar.getInstance()
        return (cal.get(Calendar.MONTH) + 1) to cal.get(Calendar.YEAR)
    }

    /**
     * Returns a [LiveData] stream of budget goals for [userId] in the current month/year.
     */
    fun getGoalsForCurrentMonth(userId: Int): LiveData<List<BudgetGoalEntity>> {
        val (month, year) = getCurrentMonthAndYear()
        return repository.getGoalsForMonth(userId, month, year)
    }

    /**
     * Returns a [LiveData] that emits a [Pair] of (categories, goals) whenever either changes.
     * Goals default to an empty list if not yet loaded so categories drive the initial emission.
     */
    fun getCategoriesWithGoals(userId: Int): LiveData<Pair<List<CategoryEntity>, List<BudgetGoalEntity>>> {
        val result = MediatorLiveData<Pair<List<CategoryEntity>, List<BudgetGoalEntity>>>()
        val (month, year) = getCurrentMonthAndYear()
        val categoriesLd = repository.getAllCategoriesForUser(userId)
        val goalsLd = repository.getGoalsForMonth(userId, month, year)

        fun combine() {
            val cats = categoriesLd.value ?: return
            val goals = goalsLd.value ?: emptyList()
            result.value = cats to goals
        }

        result.addSource(categoriesLd) { combine() }
        result.addSource(goalsLd) { combine() }
        return result
    }

    /**
     * Upserts [goal] for the matching (userId, categoryId, month, year) combination.
     */
    suspend fun saveGoal(goal: BudgetGoalEntity) =
        repository.upsertGoal(goal)

    /** @see ViewModelProvider.Factory */
    class Factory(private val repository: StashRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            BudgetGoalsViewModel(repository) as T
    }
}
