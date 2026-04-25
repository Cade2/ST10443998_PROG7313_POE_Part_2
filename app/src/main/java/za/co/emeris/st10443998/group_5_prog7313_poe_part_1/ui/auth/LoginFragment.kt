package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.MainActivity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = StashRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, AuthViewModel.Factory(repository))[AuthViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                val username = binding.etUsername.text.toString().trim()
                val password = binding.etPassword.text.toString()
                Log.d("StashAuth", "LoginFragment: login button clicked for username=$username")

                lifecycleScope.launch(Dispatchers.Main) {
                    val user = viewModel.loginUser(username, password)
                    if (user != null) {
                        Log.d("StashAuth", "LoginFragment: saving session for userId=${user.id}")
                        requireContext()
                            .getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putInt("userId", user.id)
                            .putString("username", user.username)
                            .apply()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    } else {
                        Log.d("StashAuth", "LoginFragment: login failed, showing error")
                        Snackbar.make(binding.root, "Invalid username or password", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.tvRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val username = binding.etUsername.text?.toString()?.trim() ?: ""
        val password = binding.etPassword.text?.toString() ?: ""

        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            binding.tilUsername.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
