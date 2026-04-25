package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.UserEntity

/**
 * DAO for [UserEntity] — registration and credential lookup.
 */
@Dao
interface UserDao {

    /**
     * Inserts [user] and returns the generated row ID.
     * Throws [android.database.sqlite.SQLiteConstraintException] if the username already exists.
     */
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    /**
     * Returns the user with the given [username], or null if not found.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    /**
     * Returns the user matching both [username] and [passwordHash], or null if credentials are invalid.
     */
    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :passwordHash LIMIT 1")
    suspend fun getUserByCredentials(username: String, passwordHash: String): UserEntity?
}
