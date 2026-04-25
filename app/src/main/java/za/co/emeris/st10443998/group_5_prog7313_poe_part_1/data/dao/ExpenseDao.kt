package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity

/**
 * DAO for [ExpenseEntity] — insert, update, delete, and date-range queries.
 *
 * All date parameters must be in "YYYY-MM-DD" format so SQLite string comparison
 * produces correct chronological ordering.
 */
@Dao
interface ExpenseDao {

    /**
     * Inserts [expense] and returns the generated row ID.
     */
    @Insert
    suspend fun insertExpense(expense: ExpenseEntity): Long

    /**
     * Updates an existing [expense] matched by its primary key.
     */
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    /**
     * Deletes [expense].
     */
    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    /**
     * Returns a [LiveData] stream of expenses for [userId] whose [date][ExpenseEntity.date]
     * falls within [[startDate], [endDate]], ordered newest first.
     */
    @Query(
        "SELECT * FROM expenses " +
        "WHERE userId = :userId AND date BETWEEN :startDate AND :endDate " +
        "ORDER BY date DESC"
    )
    fun getExpensesByDateRange(
        userId: Int,
        startDate: String,
        endDate: String
    ): LiveData<List<ExpenseEntity>>

    /**
     * Returns the total amount spent by [userId] in [categoryId] within [[startDate], [endDate]],
     * or null if no matching expenses exist.
     */
    @Query(
        "SELECT SUM(amount) FROM expenses " +
        "WHERE userId = :userId AND categoryId = :categoryId " +
        "AND date BETWEEN :startDate AND :endDate"
    )
    suspend fun getTotalByCategoryInRange(
        userId: Int,
        categoryId: Int,
        startDate: String,
        endDate: String
    ): Double?

    /**
     * Returns a [LiveData] stream of all expenses for [userId], ordered newest first.
     */
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpensesForUser(userId: Int): LiveData<List<ExpenseEntity>>
}
