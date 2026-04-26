package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.AuthActivity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        val username = prefs.getString("username", "") ?: ""

        binding.tvUsername.text = username.ifEmpty { "User" }
        binding.tvAvatarInitials.text = username.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

        if (username.isNotEmpty()) {
            val repository = StashRepository.getInstance(requireContext())
            lifecycleScope.launch(Dispatchers.Main) {
                val user = repository.getUserByUsername(username)
                binding.tvEmail.text = user?.email ?: ""
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.itemChangePassword.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, _ ->
            Toast.makeText(requireContext(), getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show()
        }

        binding.switchBiometric.setOnCheckedChangeListener { _, _ ->
            Toast.makeText(requireContext(), getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, _ ->
            // No-op for now
        }

        binding.btnLogout.setOnClickListener {
            Log.d("StashAuth", "SettingsFragment: logging out, clearing session")
            requireContext()
                .getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
            Log.d("StashAuth", "SettingsFragment: prefs cleared, navigating to AuthActivity")
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
