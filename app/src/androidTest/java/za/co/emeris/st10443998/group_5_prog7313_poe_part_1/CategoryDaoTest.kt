package za.co.emeris.st10443998.group_5_prog7313_poe_part_1

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.AppDatabase
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity

/**
 * Instrumented tests for [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.CategoryDao].
 * Uses an in-memory Room database so each test run starts with a clean state.
 */
@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() {
        db.close()
    }

    /**
     * Verifies that inserting two categories for the same user results in both
     * being returned by [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.CategoryDao.getCategoriesOnce].
     */
    @Test
    fun testInsertAndGetAll() = runBlocking {
        db.categoryDao().insertCategory(CategoryEntity(name = "Groceries", colorHex = "#4CAF50", userId = 1))
        db.categoryDao().insertCategory(CategoryEntity(name = "Transport", colorHex = "#2196F3", userId = 1))

        val categories = db.categoryDao().getCategoriesOnce(1)
        assertEquals(2, categories.size)
    }

    /**
     * Verifies that deleting a category removes it from the database and that
     * [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.CategoryDao.getCategoriesOnce]
     * returns an empty list when no categories remain.
     */
    @Test
    fun testDeleteCategory() = runBlocking {
        val id = db.categoryDao().insertCategory(
            CategoryEntity(name = "Groceries", colorHex = "#4CAF50", userId = 1)
        )
        val inserted = db.categoryDao().getCategoriesOnce(1).first()
        db.categoryDao().deleteCategory(inserted)

        val remaining = db.categoryDao().getCategoriesOnce(1)
        assertTrue(remaining.isEmpty())
    }
}
