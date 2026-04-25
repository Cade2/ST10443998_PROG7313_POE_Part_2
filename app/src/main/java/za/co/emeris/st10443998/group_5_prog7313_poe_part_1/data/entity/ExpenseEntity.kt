package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a single expense entry.
 *
 * Foreign keys cascade deletes so that removing a category or user automatically
 * removes all associated expenses.
 *
 * [date] must be formatted as "YYYY-MM-DD" so that SQLite string ordering produces
 * correct chronological results in range queries.
 * [startTime] and [endTime] record the time window of the expense (e.g. "08:00", "09:30").
 * [photoPath] is the absolute path to an attached receipt image, or null if none.
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index("userId")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val categoryId: Int,
    val userId: Int,
    val photoPath: String? = null
)
