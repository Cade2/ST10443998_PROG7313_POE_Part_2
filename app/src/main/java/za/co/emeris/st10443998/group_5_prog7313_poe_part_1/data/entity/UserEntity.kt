package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing an app user.
 * The [username] column has a unique index to prevent duplicate accounts.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String
)
