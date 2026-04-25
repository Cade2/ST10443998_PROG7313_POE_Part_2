package za.co.emeris.st10443998.group_5_prog7313_poe_part_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("stash_prefs", MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        Log.d("StashAuth", "AuthActivity: checking session, userId=$userId")

        if (userId > 0) {
            Log.d("StashAuth", "AuthActivity: active session found, navigating to MainActivity")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        Log.d("StashAuth", "AuthActivity: no session, showing auth flow")
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
