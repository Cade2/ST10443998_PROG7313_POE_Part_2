package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.BudgetGoalDao
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.CategoryDao
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.ExpenseDao
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.UserDao
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.BudgetGoalEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.ExpenseEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.UserEntity

/**
 * Single Room database for the Stash app.
 *
 * Access via [AppDatabase.getInstance] — never construct directly.
 * Bump [version] and provide a [androidx.room.migration.Migration] whenever the schema changes.
 */
@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ExpenseEntity::class,
        BudgetGoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** Provides access to user account operations. */
    abstract fun userDao(): UserDao

    /** Provides access to spending category operations. */
    abstract fun categoryDao(): CategoryDao

    /** Provides access to expense entry operations. */
    abstract fun expenseDao(): ExpenseDao

    /** Provides access to monthly budget goal operations. */
    abstract fun budgetGoalDao(): BudgetGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton [AppDatabase] instance, creating it if needed.
         * Uses double-checked locking to remain safe under concurrent access.
         *
         * @param context Any [Context] — the application context is extracted internally.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stash_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
