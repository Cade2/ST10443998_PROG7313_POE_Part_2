package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.UserEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import java.security.MessageDigest

/** Outcome of a register or login operation. */
sealed class AuthResult {
    /** The operation completed without error. */
    object Success : AuthResult()
    /** The operation failed with a human-readable [message]. */
    data class Error(val message: String) : AuthResult()
}

/**
 * ViewModel for authentication screens.
 *
 * Handles input validation, SHA-256 password hashing, and delegation to
 * [StashRepository] for all database operations. Never touches the UI directly.
 *
 * Obtain via [Factory] so the repository is injected at construction time.
 */
class AuthViewModel(private val repository: StashRepository) : ViewModel() {

    /**
     * Returns the SHA-256 hex digest of [password].
     * Passwords are never stored in plain text.
     */
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Validates inputs, checks for duplicate usernames, hashes the password,
     * and inserts a new [UserEntity].
     *
     * @return [AuthResult.Success] on insertion, or [AuthResult.Error] with a
     *         reason if validation fails or the username is already taken.
     */
    suspend fun registerUser(username: String, email: String, password: String): AuthResult {
        Log.d(TAG, "registerUser: validating inputs for username=$username")

        if (username.isBlank()) {
            Log.d(TAG, "registerUser: rejected — blank username")
            return AuthResult.Error("Username cannot be empty")
        }
        if (!email.contains("@")) {
            Log.d(TAG, "registerUser: rejected — invalid email")
            return AuthResult.Error("Enter a valid email address")
        }
        if (password.length < 6) {
            Log.d(TAG, "registerUser: rejected — password too short")
            return AuthResult.Error("Password must be at least 6 characters")
        }

        val existing = repository.getUserByUsername(username)
        if (existing != null) {
            Log.d(TAG, "registerUser: rejected — username already taken: $username")
            return AuthResult.Error("Username already taken")
        }

        val user = UserEntity(
            username = username,
            email = email,
            passwordHash = hashPassword(password)
        )
        repository.insertUser(user)
        Log.d(TAG, "registerUser: success for username=$username")
        return AuthResult.Success
    }

    /**
     * Hashes [password] and looks up the matching [UserEntity] in the database.
     *
     * @return The [UserEntity] on success, or null if credentials are invalid.
     */
    suspend fun loginUser(username: String, password: String): UserEntity? {
        Log.d(TAG, "loginUser: attempting login for username=$username")
        val user = repository.getUserByCredentials(username, hashPassword(password))
        if (user != null) {
            Log.d(TAG, "loginUser: success, userId=${user.id}")
        } else {
            Log.d(TAG, "loginUser: failed — no match for username=$username")
        }
        return user
    }

    companion object {
        private const val TAG = "StashAuth"
    }

    /**
     * Creates an [AuthViewModel] with the given [repository].
     * Pass this to [androidx.lifecycle.ViewModelProvider] instead of using the
     * default factory so the repository dependency is satisfied.
     */
    class Factory(private val repository: StashRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AuthViewModel(repository) as T
    }
}
