package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.BadgeAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentAchievementsBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Badge

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBadgeGrid()
    }

    private fun setupBadgeGrid() {
        val badges = listOf(
            // Earned badges
            Badge(1, getString(R.string.badge_first_save), getString(R.string.badge_first_save_desc),
                R.drawable.ic_badge_star, isEarned = true),
            Badge(2, getString(R.string.badge_week_streak), getString(R.string.badge_week_streak_desc),
                R.drawable.ic_flame, isEarned = true),
            Badge(3, getString(R.string.badge_budget_keeper), getString(R.string.badge_budget_keeper_desc),
                R.drawable.ic_badge_trophy, isEarned = true),
            // Locked badges
            Badge(4, getString(R.string.badge_category_master), getString(R.string.badge_category_master_desc),
                R.drawable.ic_badge_star, isEarned = false),
            Badge(5, getString(R.string.badge_saver_100), getString(R.string.badge_saver_100_desc),
                R.drawable.ic_badge_trophy, isEarned = false),
            Badge(6, getString(R.string.badge_month_streak), getString(R.string.badge_month_streak_desc),
                R.drawable.ic_flame, isEarned = false)
        )

        binding.rvBadges.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = BadgeAdapter(badges)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
