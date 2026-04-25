package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model

data class CategoryProgress(
    val name: String,
    val color: Int,
    val spent: Double,
    val budget: Double
) {
    val progressPercent: Int get() = if (budget > 0) ((spent / budget) * 100).toInt().coerceIn(0, 100) else 0
    val displayText: String get() = "R%.0f / R%.0f".format(spent, budget)
}
