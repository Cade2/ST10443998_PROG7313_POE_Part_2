package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ItemCategoryGoalBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Category

data class CategoryGoal(val category: Category, var minGoal: String = "", var maxGoal: String = "")

class CategoryGoalAdapter(
    private val items: List<CategoryGoal>
) : RecyclerView.Adapter<CategoryGoalAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryGoalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryGoalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvCategoryName.text = item.category.name
            viewCategoryDot.backgroundTintList =
                android.content.res.ColorStateList.valueOf(item.category.color)
            etMin.setText(item.minGoal)
            etMax.setText(item.maxGoal)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getGoals(): List<CategoryGoal> = items
}
