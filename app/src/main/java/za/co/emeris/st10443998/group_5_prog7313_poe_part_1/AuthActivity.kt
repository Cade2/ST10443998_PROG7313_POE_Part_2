package za.co.emeris.st10443998.group_5_prog7313_poe_part_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
