package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.BudgetGoalEntity

/**
 * DAO for [BudgetGoalEntity] — upsert and month-scoped queries.
 *
 * The unique index on (userId, categoryId, month, year) in [BudgetGoalEntity] ensures that
 * [upsertGoal] correctly replaces an existing goal rather than inserting a duplicate.
 */
@Dao
interface BudgetGoalDao {

    /**
     * Inserts [goal] if no matching row exists for the (userId, categoryId, month, year)
     * combination, or replaces the existing row if one does.
     */
    @Upsert
    suspend fun upsertGoal(goal: BudgetGoalEntity)

    /**
     * Returns a [LiveData] stream of all budget goals for [userId] in the given [month]/[year].
     * Observers are notified automatically when goals change.
     */
    @Query(
        "SELECT * FROM budget_goals " +
        "WHERE userId = :userId AND month = :month AND year = :year"
    )
    fun getGoalsForMonth(userId: Int, month: Int, year: Int): LiveData<List<BudgetGoalEntity>>

    /**
     * Returns the budget goal for [userId] + [categoryId] in [month]/[year], or null if not set.
     */
    @Query(
        "SELECT * FROM budget_goals " +
        "WHERE userId = :userId AND categoryId = :categoryId " +
        "AND month = :month AND year = :year LIMIT 1"
    )
    suspend fun getGoalForCategory(
        userId: Int,
        categoryId: Int,
        month: Int,
        year: Int
    ): BudgetGoalEntity?
}
