package com.example.studentmanager.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studentmanager.data.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserByUid(uid: String): Flow<User?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Update
    suspend fun updateUser(user: User)
}
