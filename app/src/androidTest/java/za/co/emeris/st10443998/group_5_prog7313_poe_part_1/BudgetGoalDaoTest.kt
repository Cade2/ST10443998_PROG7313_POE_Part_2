package za.co.emeris.st10443998.group_5_prog7313_poe_part_1

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.AppDatabase
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.BudgetGoalEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.UserEntity

/**
 * Instrumented tests for [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.BudgetGoalDao].
 * Uses an in-memory Room database so each test run starts with a clean state.
 *
 * A [UserEntity] and [CategoryEntity] are inserted in [setup] because [BudgetGoalEntity]
 * shares userId and categoryId values that must correspond to real rows when foreign key
 * checks are active.
 */
@RunWith(AndroidJUnit4::class)
class BudgetGoalDaoTest {

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
     * Verifies that upserting a [BudgetGoalEntity] persists the goal to the database and that
     * [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.BudgetGoalDao.getGoalForCategory]
     * returns an entity with the correct [BudgetGoalEntity.minGoal] and [BudgetGoalEntity.maxGoal] values.
     */
    @Test
    fun testUpsertAndRetrieve() = runBlocking {
        val goal = BudgetGoalEntity(
            userId = userId,
            categoryId = categoryId,
            minGoal = 100.0,
            maxGoal = 500.0,
            month = 4,
            year = 2026
        )
        db.budgetGoalDao().upsertGoal(goal)

        val retrieved = db.budgetGoalDao().getGoalForCategory(userId, categoryId, month = 4, year = 2026)
        assertNotNull(retrieved)
        assertEquals(100.0, retrieved!!.minGoal, 0.001)
        assertEquals(500.0, retrieved.maxGoal, 0.001)
    }
}
