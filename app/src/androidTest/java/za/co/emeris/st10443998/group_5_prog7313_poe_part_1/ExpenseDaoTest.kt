package za.co.emeris.st10443998.group_5_prog7313_poe_part_1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.AppDatabase
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.UserEntity
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented tests for [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.ExpenseDao].
 * Uses an in-memory Room database so each test run starts with a clean state.
 *
 * A [UserEntity] and [CategoryEntity] are inserted in [setup] because [ExpenseEntity]
 * declares foreign key constraints that cascade from both tables.
 */
@RunWith(AndroidJUnit4::class)
class ExpenseDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private var userId = 0
    private var categoryId = 0

    @Before
    fun setup() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        userId = db.userDao().insertUser(
            UserEntity(username = "testuser", email = "test@test.com", passwordHash = "abc")
        ).toInt()

        categoryId = db.categoryDao().insertCategory(
            CategoryEntity(name = "Groceries", colorHex = "#4CAF50", userId = userId)
        ).toInt()
    }

    @After
    fun teardown() {
        db.close()
    }

    /**
     * Verifies that a single inserted expense is present in the list returned by
     * [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.ExpenseDao.getAllExpensesForUser].
     */
    @Test
    fun testInsertExpense() = runBlocking {
        db.expenseDao().insertExpense(
            ExpenseEntity(
                amount = 100.0, date = "2026-04-10",
                startTime = "08:00", endTime = "08:30",
                description = "Test expense",
                categoryId = categoryId, userId = userId
            )
        )

        val expenses = db.expenseDao().getAllExpensesForUser(userId).getOrAwaitValue()
        assertFalse(expenses.isEmpty())
    }

    /**
     * Verifies that [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.ExpenseDao.getExpensesByDateRange]
     * returns only expenses whose date falls within the specified range.
     * Inserts one expense in April, one in mid-April, and one in May; expects only 2 in the April query.
     */
    @Test
    fun testDateRangeFilter() = runBlocking {
        val base = ExpenseEntity(
            amount = 50.0, startTime = "09:00", endTime = "09:30",
            description = "Expense", categoryId = categoryId, userId = userId,
            date = ""
        )
        db.expenseDao().insertExpense(base.copy(date = "2026-04-01"))
        db.expenseDao().insertExpense(base.copy(date = "2026-04-15"))
        db.expenseDao().insertExpense(base.copy(date = "2026-05-01"))

        val april = db.expenseDao()
            .getExpensesByDateRange(userId, "2026-04-01", "2026-04-30")
            .getOrAwaitValue()

        assertEquals(2, april.size)
    }

    /**
     * Verifies that [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.ExpenseDao.getTotalByCategoryInRange]
     * correctly sums the amounts of all matching expenses within the date range.
     */
    @Test
    fun testTotalByCategory() = runBlocking {
        db.expenseDao().insertExpense(
            ExpenseEntity(
                amount = 50.0, date = "2026-04-05",
                startTime = "10:00", endTime = "10:15",
                description = "First", categoryId = categoryId, userId = userId
            )
        )
        db.expenseDao().insertExpense(
            ExpenseEntity(
                amount = 75.0, date = "2026-04-10",
                startTime = "11:00", endTime = "11:30",
                description = "Second", categoryId = categoryId, userId = userId
            )
        )

        val total = db.expenseDao().getTotalByCategoryInRange(
            userId, categoryId, "2026-04-01", "2026-04-30"
        )
        assertEquals(125.0, total!!, 0.001)
    }
}

/**
 * Synchronously retrieves the current value of a [LiveData] by attaching a one-shot observer.
 * Times out after 2 seconds if no value is emitted.
 */
private fun <T> LiveData<T>.getOrAwaitValue(): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            removeObserver(this)
        }
    }
    observeForever(observer)
    check(latch.await(2, TimeUnit.SECONDS)) { "LiveData value was never emitted." }
    @Suppress("UNCHECKED_CAST")
    return data as T
}
