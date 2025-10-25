package com.example.studentmanager.user

import com.example.studentmanager.dao.UserDao
import com.example.studentmanager.data.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun registerUser(user: User): Boolean {
        val existingUser = userDao.getUserByEmail(user.email)
        return if (existingUser == null) {
            userDao.insertUser(user)
            true
        } else {
            false // email already exists
        }
    }

    fun getUserByUid(uid: String): Flow<User?> =
        userDao.getUserByUid(uid)

    suspend fun updateUser(user: User) = userDao.updateUser(user)
}