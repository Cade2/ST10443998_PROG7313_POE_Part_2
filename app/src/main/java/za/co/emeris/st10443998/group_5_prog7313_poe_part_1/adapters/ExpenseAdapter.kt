package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ItemExpenseBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Expense

class ExpenseAdapter(
    private val items: List<Expense>
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvCategory.text = item.category.uppercase()
            tvDescription.text = item.description
            tvAmount.text = "R%.2f".format(item.amount)
            tvDate.text = item.date
            viewCategoryBorder.backgroundTintList =
                android.content.res.ColorStateList.valueOf(item.categoryColor)
            ivPhotoIndicator.visibility = if (item.hasPhoto) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount(): Int = items.size
}
