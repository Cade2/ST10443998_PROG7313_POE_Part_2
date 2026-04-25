package za.co.emeris.st10443998.group_5_prog7313_poe_part_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // Hide bottom nav for non-main screens (add expense, categories, settings)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addExpenseFragment,
                R.id.categoriesFragment,
                R.id.settingsFragment -> {
                    binding.bottomNavigation.visibility = android.view.View.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = android.view.View.VISIBLE
                }
            }
        }
    }
}
