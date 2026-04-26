package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository

// ST10318188- Ronald Fell
/**
 * ViewModel for [AddExpenseFragment].
 *
 * Holds transient UI state ([selectedPhotoPath], [selectedCategoryId]) so it
 * survives configuration changes, and delegates all data operations to [repository].
 */
class AddExpenseViewModel(private val repository: StashRepository) : ViewModel() {

    /** Absolute file path or content URI string of the attached photo, or null if none. */
    var selectedPhotoPath: String? = null

    /** The Room primary-key ID of the category chosen in the dropdown, or null if not yet chosen. */
    var selectedCategoryId: Int? = null

    /**
     * Returns a [LiveData] stream of all categories belonging to [userId].
     * The fragment observes this to populate the category dropdown in real time.
     */
    fun getCategoriesForUser(userId: Int): LiveData<List<CategoryEntity>> =
        repository.getAllCategoriesForUser(userId)

    /**
     * Inserts [expense] into the database and returns the generated row ID.
     * Must be called from a coroutine — delegates directly to the repository suspend function.
     */
    suspend fun saveExpense(expense: ExpenseEntity): Long =
        repository.insertExpense(expense)

    /**
     * Creates an [AddExpenseViewModel] with the given [repository].
     * Pass this to [ViewModelProvider] instead of the default factory.
     */
    class Factory(private val repository: StashRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AddExpenseViewModel(repository) as T
    }
}
