package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a monthly budget goal for one category.
 *
 * The composite unique index on (userId, categoryId, month, year) ensures that
 * each user can have at most one min/max goal per category per calendar month,
 * enabling safe upsert semantics in [BudgetGoalDao].
 */
@Entity(
    tableName = "budget_goals",
    indices = [Index(value = ["userId", "categoryId", "month", "year"], unique = true)]
)
data class BudgetGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val minGoal: Double,
    val maxGoal: Double,
    val month: Int,
    val year: Int
)
