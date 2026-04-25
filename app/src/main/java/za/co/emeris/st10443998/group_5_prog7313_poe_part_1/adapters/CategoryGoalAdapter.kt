package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ItemCategoryGoalBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Category

data class CategoryGoal(val category: Category, var minGoal: String = "", var maxGoal: String = "")

class CategoryGoalAdapter(
    private val items: MutableList<CategoryGoal> = mutableListOf()
) : RecyclerView.Adapter<CategoryGoalAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var minWatcher: TextWatcher? = null
        var maxWatcher: TextWatcher? = null
    }

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

            // Remove old watchers before resetting text to avoid double-trigger on rebind
            holder.minWatcher?.let { etMin.removeTextChangedListener(it) }
            holder.maxWatcher?.let { etMax.removeTextChangedListener(it) }

            etMin.setText(item.minGoal)
            etMax.setText(item.maxGoal)

            // Attach fresh watchers that write back into the data object in place
            holder.minWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) { item.minGoal = s?.toString() ?: "" }
            }.also { etMin.addTextChangedListener(it) }

            holder.maxWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) { item.maxGoal = s?.toString() ?: "" }
            }.also { etMax.addTextChangedListener(it) }
        }
    }

    override fun getItemCount(): Int = items.size

    /** Replaces the entire list and refreshes the adapter. */
    fun updateGoals(newItems: List<CategoryGoal>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    /** Returns the current list including any in-progress user edits. */
    fun getGoals(): List<CategoryGoal> = items.toList()
}
