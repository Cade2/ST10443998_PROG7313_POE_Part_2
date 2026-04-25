package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository

/**
 * ViewModel for [CategoriesFragment].
 * Exposes CRUD operations for user-owned spending categories.
 */
class CategoryViewModel(private val repository: StashRepository) : ViewModel() {

    /**
     * Returns a [LiveData] stream of all categories belonging to [userId], ordered by name.
     */
    fun getCategories(userId: Int): LiveData<List<CategoryEntity>> =
        repository.getAllCategoriesForUser(userId)

    /**
     * Inserts a new category with [name] and [colorHex] for [userId].
     */
    suspend fun addCategory(name: String, colorHex: String, userId: Int) {
        repository.insertCategory(CategoryEntity(name = name, colorHex = colorHex, userId = userId))
    }

    /**
     * Updates [category] in the database (matched by its primary key).
     */
    suspend fun updateCategory(category: CategoryEntity) =
        repository.updateCategory(category)

    /**
     * Deletes [category] and cascades to all associated expenses.
     */
    suspend fun deleteCategory(category: CategoryEntity) =
        repository.deleteCategory(category)

    /** @see ViewModelProvider.Factory */
    class Factory(private val repository: StashRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CategoryViewModel(repository) as T
    }
}
