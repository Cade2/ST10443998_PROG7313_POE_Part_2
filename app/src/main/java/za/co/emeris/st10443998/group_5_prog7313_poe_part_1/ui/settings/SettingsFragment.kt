package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.AuthActivity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
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
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
