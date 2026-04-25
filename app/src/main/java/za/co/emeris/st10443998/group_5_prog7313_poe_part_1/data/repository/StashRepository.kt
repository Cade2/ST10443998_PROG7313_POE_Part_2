package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.AppDatabase
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.BudgetGoalEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.UserEntity

/**
 * Single source of truth for all data operations in the Stash app.
 *
 * Wraps [AppDatabase] DAOs and exposes suspend functions (for one-shot writes/reads)
 * and [LiveData] streams (for reactive UI observation) to ViewModels.
 * All database I/O is delegated to Room's internal executor — never call these
 * functions on the main thread.
 *
 * Obtain the singleton via [StashRepository.getInstance].
 */
class StashRepository private constructor(private val database: AppDatabase) {

    // ── User ──────────────────────────────────────────────────────────────────

    /**
     * Inserts [user] into the database and returns the generated row ID.
     * Throws [android.database.sqlite.SQLiteConstraintException] if the username already exists.
     */
    suspend fun insertUser(user: UserEntity): Long {
        Log.d(TAG, "insertUser: username=${user.username}")
        return database.userDao().insertUser(user)
    }

    /**
     * Returns the [UserEntity] matching [username], or null if no account exists with that name.
     */
    suspend fun getUserByUsername(username: String): UserEntity? {
        Log.d(TAG, "getUserByUsername: username=$username")
        return database.userDao().getUserByUsername(username)
    }

    /**
     * Returns the [UserEntity] matching both [username] and [passwordHash],
     * or null if the credentials are invalid.
     */
    suspend fun getUserByCredentials(username: String, passwordHash: String): UserEntity? {
        Log.d(TAG, "getUserByCredentials: username=$username")
        return database.userDao().getUserByCredentials(username, passwordHash)
    }

    // ── Category ──────────────────────────────────────────────────────────────

    /**
     * Inserts [category] and returns the generated row ID.
     */
    suspend fun insertCategory(category: CategoryEntity): Long {
        Log.d(TAG, "insertCategory: name=${category.name}, userId=${category.userId}")
        return database.categoryDao().insertCategory(category)
    }

    /**
     * Updates an existing [category] (matched by its primary key).
     */
    suspend fun updateCategory(category: CategoryEntity) {
        Log.d(TAG, "updateCategory: id=${category.id}, name=${category.name}")
        database.categoryDao().updateCategory(category)
    }

    /**
     * Deletes [category]. Cascades to all expenses that reference this category.
     */
    suspend fun deleteCategory(category: CategoryEntity) {
        Log.d(TAG, "deleteCategory: id=${category.id}, name=${category.name}")
        database.categoryDao().deleteCategory(category)
    }

    /**
     * Returns a [LiveData] stream of all categories belonging to [userId], ordered alphabetically.
     * The UI layer can observe this to react to add/edit/delete changes automatically.
     */
    fun getAllCategoriesForUser(userId: Int): LiveData<List<CategoryEntity>> {
        Log.d(TAG, "getAllCategoriesForUser: userId=$userId")
        return database.categoryDao().getAllCategoriesForUser(userId)
    }

    /**
     * Returns a one-shot snapshot of all categories for [userId].
     * Prefer this over [getAllCategoriesForUser] when ongoing observation is not needed
     * (e.g. populating a category dropdown before showing a form).
     */
    suspend fun getCategoriesOnce(userId: Int): List<CategoryEntity> {
        Log.d(TAG, "getCategoriesOnce: userId=$userId")
        return database.categoryDao().getCategoriesOnce(userId)
    }

    // ── Expense ───────────────────────────────────────────────────────────────

    /**
     * Inserts [expense] and returns the generated row ID.
     */
    suspend fun insertExpense(expense: ExpenseEntity): Long {
        Log.d(TAG, "insertExpense: amount=${expense.amount}, date=${expense.date}, categoryId=${expense.categoryId}")
        return database.expenseDao().insertExpense(expense)
    }

    /**
     * Updates an existing [expense] matched by its primary key.
     */
    suspend fun updateExpense(expense: ExpenseEntity) {
        Log.d(TAG, "updateExpense: id=${expense.id}, amount=${expense.amount}")
        database.expenseDao().updateExpense(expense)
    }

    /**
     * Deletes [expense].
     */
    suspend fun deleteExpense(expense: ExpenseEntity) {
        Log.d(TAG, "deleteExpense: id=${expense.id}")
        database.expenseDao().deleteExpense(expense)
    }

    /**
     * Returns a [LiveData] stream of expenses for [userId] whose date falls within
     * [[startDate], [endDate]], ordered newest first.
     * Dates must be formatted as "YYYY-MM-DD".
     */
    fun getExpensesByDateRange(
        userId: Int,
        startDate: String,
        endDate: String
    ): LiveData<List<ExpenseEntity>> {
        Log.d(TAG, "getExpensesByDateRange: userId=$userId, $startDate → $endDate")
        return database.expenseDao().getExpensesByDateRange(userId, startDate, endDate)
    }

    /**
     * Returns the total amount spent by [userId] in [categoryId] within [[startDate], [endDate]],
     * or null if no expenses match. Dates must be formatted as "YYYY-MM-DD".
     */
    suspend fun getTotalByCategoryInRange(
        userId: Int,
        categoryId: Int,
        startDate: String,
        endDate: String
    ): Double? {
        Log.d(TAG, "getTotalByCategoryInRange: userId=$userId, categoryId=$categoryId, $startDate → $endDate")
        return database.expenseDao().getTotalByCategoryInRange(userId, categoryId, startDate, endDate)
    }

    /**
     * Returns a [LiveData] stream of all expenses for [userId], ordered newest first.
     */
    fun getAllExpensesForUser(userId: Int): LiveData<List<ExpenseEntity>> {
        Log.d(TAG, "getAllExpensesForUser: userId=$userId")
        return database.expenseDao().getAllExpensesForUser(userId)
    }

    // ── Budget Goal ───────────────────────────────────────────────────────────

    /**
     * Inserts or updates the budget goal for the (userId, categoryId, month, year) combination.
     * If a goal already exists for that combination it is replaced in full.
     */
    suspend fun upsertGoal(goal: BudgetGoalEntity) {
        Log.d(TAG, "upsertGoal: userId=${goal.userId}, categoryId=${goal.categoryId}, ${goal.month}/${goal.year}, min=${goal.minGoal}, max=${goal.maxGoal}")
        database.budgetGoalDao().upsertGoal(goal)
    }

    /**
     * Returns a [LiveData] stream of all budget goals for [userId] in the given [month]/[year].
     */
    fun getGoalsForMonth(userId: Int, month: Int, year: Int): LiveData<List<BudgetGoalEntity>> {
        Log.d(TAG, "getGoalsForMonth: userId=$userId, $month/$year")
        return database.budgetGoalDao().getGoalsForMonth(userId, month, year)
    }

    /**
     * Returns the budget goal for [userId] + [categoryId] in [month]/[year],
     * or null if no goal has been set for that combination.
     */
    suspend fun getGoalForCategory(
        userId: Int,
        categoryId: Int,
        month: Int,
        year: Int
    ): BudgetGoalEntity? {
        Log.d(TAG, "getGoalForCategory: userId=$userId, categoryId=$categoryId, $month/$year")
        return database.budgetGoalDao().getGoalForCategory(userId, categoryId, month, year)
    }

    // ── Singleton ─────────────────────────────────────────────────────────────

    companion object {
        private const val TAG = "StashRepository"

        @Volatile
        private var INSTANCE: StashRepository? = null

        /**
         * Returns the singleton [StashRepository] instance, creating it with [AppDatabase] if needed.
         * Uses double-checked locking to remain safe under concurrent access.
         *
         * @param context Any [Context] — the application context is extracted internally.
         */
        fun getInstance(context: Context): StashRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StashRepository(
                    AppDatabase.getInstance(context)
                ).also { INSTANCE = it }
            }
        }
    }
}
