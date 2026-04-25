package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.auth

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
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = StashRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, AuthViewModel.Factory(repository))[AuthViewModel::class.java]

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val username = binding.etUsername.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString()
                Log.d("StashAuth", "RegisterFragment: register button clicked for username=$username")

                lifecycleScope.launch(Dispatchers.Main) {
                    when (val result = viewModel.registerUser(username, email, password)) {
                        is AuthResult.Success -> {
                            Log.d("StashAuth", "RegisterFragment: registration success, navigating to login")
                            Snackbar.make(
                                binding.root,
                                "Account created! Please log in.",
                                Snackbar.LENGTH_LONG
                            ).show()
                            findNavController().popBackStack()
                        }
                        is AuthResult.Error -> {
                            Log.d("StashAuth", "RegisterFragment: registration error — ${result.message}")
                            Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        binding.tvLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val username = binding.etUsername.text?.toString()?.trim() ?: ""
        val email = binding.etEmail.text?.toString()?.trim() ?: ""
        val password = binding.etPassword.text?.toString() ?: ""
        val confirmPassword = binding.etConfirmPassword.text?.toString() ?: ""

        binding.tilUsername.error = if (username.isEmpty()) {
            isValid = false; getString(R.string.error_required_field)
        } else null

        binding.tilEmail.error = if (email.isEmpty()) {
            isValid = false; getString(R.string.error_required_field)
        } else null

        binding.tilPassword.error = if (password.isEmpty()) {
            isValid = false; getString(R.string.error_required_field)
        } else null

        binding.tilConfirmPassword.error = if (confirmPassword.isEmpty()) {
            isValid = false; getString(R.string.error_required_field)
        } else if (password != confirmPassword) {
            isValid = false; "Passwords do not match"
        } else null

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
