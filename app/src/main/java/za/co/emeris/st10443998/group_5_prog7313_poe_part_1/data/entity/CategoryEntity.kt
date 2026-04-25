package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a spending category owned by a user.
 * [colorHex] stores the category colour as a hex string (e.g. "#2E7D32").
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,
    val userId: Int
)
