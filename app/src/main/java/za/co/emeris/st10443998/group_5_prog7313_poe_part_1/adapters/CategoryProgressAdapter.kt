package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ItemCategoryProgressBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.CategoryProgress

class CategoryProgressAdapter(
    private val items: List<CategoryProgress>
) : RecyclerView.Adapter<CategoryProgressAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryProgressBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvCategoryName.text = item.name
            viewCategoryDot.backgroundTintList =
                android.content.res.ColorStateList.valueOf(item.color)
            pbCategory.progress = item.progressPercent
            pbCategory.progressTintList =
                android.content.res.ColorStateList.valueOf(item.color)
            tvAmount.text = item.displayText
        }
    }

    override fun getItemCount(): Int = items.size
}
