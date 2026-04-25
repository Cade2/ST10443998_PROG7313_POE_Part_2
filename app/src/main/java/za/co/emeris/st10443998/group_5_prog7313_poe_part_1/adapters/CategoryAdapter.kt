package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ItemCategoryBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Category

class CategoryAdapter(
    private val items: List<Category>,
    private val onEditClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvCategoryName.text = item.name
            viewCategoryColour.backgroundTintList =
                android.content.res.ColorStateList.valueOf(item.color)
            btnEdit.setOnClickListener { onEditClick(item) }
        }
    }

    override fun getItemCount(): Int = items.size
}
