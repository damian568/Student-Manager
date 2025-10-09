package com.example.studentmanager.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanager.user.UserRepository
import com.example.studentmanager.data.User
import com.example.studentmanager.enums.Gender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    //private val _registrationSuccess = MutableLiveData<Boolean>()
    //val registrationSuccess: LiveData<Boolean> get() = _registrationSuccess

    private val _registrationSuccess = MutableLiveData<User?>()
    val registrationSuccess: LiveData<User?> get() = _registrationSuccess

    // LiveData to track the currently logged-in user
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    // LiveData for login success/error messages
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Flag to distinguish login vs registration navigation
    private var isLoginFlow = false

    // Set the current logged-in user
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun getIsLoginFlow() = isLoginFlow
    fun clearLoginFlow() { isLoginFlow = false }

    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            withContext(Dispatchers.Main) {
                callback(user)
            }
        }
    }

    fun registerUser(username: String, email: String, password: String, gender: Gender) {
        viewModelScope.launch {
            val user = User(uid = UUID.randomUUID().toString(), username = username, email = email, password = password, gender = gender)
            val success = repository.registerUser(user)
            if (success) {
                //_currentUser.value = user
                _registrationSuccess.value = user
            } else {
                _errorMessage.value = "Email already registered"
            }
        }
    }

    // Check email + password
    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByEmail(email)
            withContext(Dispatchers.Main) {
                when {
                    user == null -> _errorMessage.value = "User not found"
                    user.password != password -> _errorMessage.value = "Incorrect password"
                    else -> {
                        _currentUser.value = user
                        _loginSuccess.value = true
                    }
                }
            }
        }
    }

    // Optional: clear error after showing
    fun clearError() {
        _errorMessage.value = null
    }

//    // Fetch profile
//    fun fetchUserProfile(uid: String) {
//        firestore.collection("users").document(uid).get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    _currentUser.value = document.toObject(User::class.java)
//                } else {
//                    _errorMessage.value = "User data not found"
//                }
//            }
//            .addOnFailureListener { e ->
//                _errorMessage.value = "Error fetching user: ${e.message}"
//            }
//    }
//
//    // Forgot Password
//    fun sendPasswordResetEmail(email: String, callback: (Boolean, String?) -> Unit) {
//        auth.sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) callback(true, null)
//                else callback(false, task.exception?.message)
//            }
//    }
}