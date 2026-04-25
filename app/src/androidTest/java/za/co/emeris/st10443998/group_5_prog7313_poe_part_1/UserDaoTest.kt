package za.co.emeris.st10443998.group_5_prog7313_poe_part_1

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.AppDatabase
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.UserEntity

/**
 * Instrumented tests for [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.UserDao].
 * Uses an in-memory Room database so each test run starts with a clean state.
 */
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() {
        db.close()
    }

    /**
     * Verifies that a newly inserted user can be retrieved by username
     * and that the returned entity's username matches the inserted value.
     */
    @Test
    fun testInsertUser() = runBlocking {
        val user = UserEntity(username = "testuser", email = "test@test.com", passwordHash = "abc123")
        db.userDao().insertUser(user)

        val retrieved = db.userDao().getUserByUsername("testuser")
        assertNotNull(retrieved)
        assertEquals("testuser", retrieved!!.username)
    }

    /**
     * Verifies that [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.UserDao.getUserByCredentials]
     * returns a non-null result when both username and passwordHash are correct.
     */
    @Test
    fun testGetByCredentials_validPassword() = runBlocking {
        val user = UserEntity(username = "testuser", email = "test@test.com", passwordHash = "hashed123")
        db.userDao().insertUser(user)

        val result = db.userDao().getUserByCredentials("testuser", "hashed123")
        assertNotNull(result)
    }

    /**
     * Verifies that [za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.dao.UserDao.getUserByCredentials]
     * returns null when the password hash does not match the stored hash.
     */
    @Test
    fun testGetByCredentials_wrongPassword() = runBlocking {
        val user = UserEntity(username = "testuser", email = "test@test.com", passwordHash = "hashed123")
        db.userDao().insertUser(user)

        val result = db.userDao().getUserByCredentials("testuser", "wronghash")
        assertNull(result)
    }
}
