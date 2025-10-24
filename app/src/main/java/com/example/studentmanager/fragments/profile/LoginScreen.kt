package com.example.studentmanager.fragments.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.studentmanager.MainActivity
import com.example.studentmanager.R
import com.example.studentmanager.user.UserDatabase
import com.example.studentmanager.user.UserRepository
import com.example.studentmanager.databinding.FragmentLoginScreenBinding
import com.example.studentmanager.dialogs.ForgotPasswordDialog
import com.example.studentmanager.viewModel.UserViewModel
import com.example.studentmanager.viewModel.UserViewModelFactory

class LoginScreen : Fragment() {

    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels {
        val userDao = UserDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(userDao)
        UserViewModelFactory(repository)
    }
    private val PREFS_NAME = "user_prefs"
    private val KEY_EMAIL = "email"
    private val KEY_PASSWORD = "password"
    private val KEY_REMEMBER = "remember_me"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSavedCredentials()
        buttonClickListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Hide toolbar and bottom nav when on login screen
        (activity as? MainActivity)?.apply {
            binding.toolbar.visibility = View.GONE
            binding.bottomNavigation.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        // Show them again when leaving login screen
        (activity as? MainActivity)?.apply {
            binding.toolbar.visibility = View.VISIBLE
            binding.bottomNavigation.visibility = View.VISIBLE
            bottomNavigation()
        }
    }

    //Button click listeners
    private fun buttonClickListeners() {
        binding.logButton.setOnClickListener {
            handleLogin()
        }

        binding.registerTxt.setOnClickListener {
            userViewModel.clearError()
            goToRegistrationScreen()
        }

        binding.forgotPass.setOnClickListener {
            ForgotPasswordDialog().show(parentFragmentManager, "ForgotPasswordDialog")
        }

        // 🔹 If checkbox unchecked → instantly clear saved login data
        binding.rememberMe.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) clearSavedCredentials()
        }

    }

    private fun loadSavedCredentials() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val remember = prefs.getBoolean(KEY_REMEMBER, false)
        val email = prefs.getString(KEY_EMAIL, "")
        val password = prefs.getString(KEY_PASSWORD, "")

        if (remember && !email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            binding.email.setText(email)
            binding.password.setText(password)
            binding.rememberMe.isChecked = true

            // 🔹 Auto-login immediately
            userViewModel.loginUser(email, password)
        } else {
            binding.email.text.clear()
            binding.password.text.clear()
            binding.rememberMe.isChecked = false
        }
    }

    private fun saveCredentialsIfNeeded(email: String, password: String) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        if (binding.rememberMe.isChecked) {
            editor.putString(KEY_EMAIL, email)
            editor.putString(KEY_PASSWORD, password)
            editor.putBoolean(KEY_REMEMBER, true)
        } else {
            editor.clear()
        }
        editor.apply()
    }

    private fun clearSavedCredentials() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        binding.email.text.clear()
        binding.password.text.clear()
        showToast("Credentials cleared")
    }

    // ---------------------- Observers ----------------------
    private fun observeViewModel() {
        userViewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                showToast("Login successful!")
                goToStudentScreen()
            }
        }

        userViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { showToast(it) }
        }
    }

    // ---------------------- Login ----------------------
    private fun handleLogin() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty()) {
            showToast("Please enter email")
            return
        } else if (password.isEmpty()) {
            showToast("Please enter password")
            return
        }

        userViewModel.getUserByEmail(email) { user ->
            if (user == null) {
                showToast("User not found")
            } else if (user.password != password) {  // assuming User entity has `password` field
                showToast("Incorrect password")
            } else {
                saveCredentialsIfNeeded(email, password)
                showToast("Login successful!")
                userViewModel.setCurrentUser(user) // optional: track logged-in user in ViewModel
                goToStudentScreen()
            }
        }

        userViewModel.loginUser(email, password)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun goToStudentScreen() {
        val currentDest = findNavController().currentDestination?.id
        if (currentDest != R.id.studentsScreen) {
            findNavController().navigate(R.id.studentsScreen)
        }
    }

    private fun goToRegistrationScreen() {
        val action = LoginScreenDirections.actionLoginScreenToRegistrationScreen()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}