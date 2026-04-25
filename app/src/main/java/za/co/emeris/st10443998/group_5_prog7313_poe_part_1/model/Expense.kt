package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model

data class Expense(
    val id: Int,
    val category: String,
    val categoryColor: Int,
    val description: String,
    val amount: Double,
    val date: String,
    val hasPhoto: Boolean = false
)
