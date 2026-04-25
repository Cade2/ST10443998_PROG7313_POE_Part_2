package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity

/**
 * DAO for [CategoryEntity] — full CRUD plus reactive and snapshot queries.
 */
@Dao
interface CategoryDao {

    /**
     * Inserts [category] and returns the generated row ID.
     */
    @Insert
    suspend fun insertCategory(category: CategoryEntity): Long

    /**
     * Updates an existing [category] matched by its primary key.
     */
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * Deletes [category]. Cascades to all expenses that reference this category.
     */
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    /**
     * Returns a [LiveData] stream of all categories belonging to [userId], ordered alphabetically.
     * Observers are notified automatically whenever the table changes.
     */
    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    fun getAllCategoriesForUser(userId: Int): LiveData<List<CategoryEntity>>

    /**
     * Returns a one-shot snapshot of all categories for [userId].
     * Use this when you need the list without ongoing observation (e.g. populating a spinner).
     */
    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    suspend fun getCategoriesOnce(userId: Int): List<CategoryEntity>
}
