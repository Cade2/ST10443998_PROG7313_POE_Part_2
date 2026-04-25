package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ItemBadgeBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Badge

class BadgeAdapter(
    private val items: List<Badge>
) : RecyclerView.Adapter<BadgeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemBadgeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBadgeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvBadgeName.text = item.name
            tvBadgeDescription.text = item.description
            ivBadgeIcon.setImageResource(item.iconResId)

            if (item.isEarned) {
                ivBadgeIcon.clearColorFilter()
                tvBadgeName.setTextColor(
                    ContextCompat.getColor(root.context, R.color.colorOnSurface)
                )
                root.alpha = 1.0f
            } else {
                ivBadgeIcon.setColorFilter(
                    ContextCompat.getColor(root.context, R.color.colorBadgeLocked)
                )
                tvBadgeName.setTextColor(
                    ContextCompat.getColor(root.context, R.color.colorBadgeLocked)
                )
                root.alpha = 0.6f
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
