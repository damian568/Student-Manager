package com.example.studentmanager.fragments.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.studentmanager.R
import com.example.studentmanager.databinding.FragmentRegistrationScreenBinding
import com.example.studentmanager.enums.Gender
import androidx.fragment.app.viewModels
import com.example.studentmanager.MainActivity
import com.example.studentmanager.user.UserDatabase
import com.example.studentmanager.user.UserRepository
import com.example.studentmanager.viewModel.UserViewModel
import com.example.studentmanager.viewModel.UserViewModelFactory

class RegistrationScreen : Fragment() {
    private var _binding: FragmentRegistrationScreenBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels {
        val userDao = UserDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(userDao)
        UserViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add text watchers for validation
        setupTextWatchers()

        observeViewModel()

        buttonClickListeners()
    }

    override fun onPause() {
        super.onPause()
        // Show them again when leaving register screen
        (activity as? MainActivity)?.apply {
            binding.toolbar.visibility = View.VISIBLE
            binding.bottomNavigation.visibility = View.VISIBLE
            bottomNavigation()
        }
    }

    // Button click listeners
    private fun buttonClickListeners() {
        binding.regButton.setOnClickListener { handleRegistration() }
        binding.loginTxt.setOnClickListener { goToLoginScreen() }
    }

    // ---------------------- Observers ----------------------
    private fun observeViewModel() {
        userViewModel.registrationSuccess.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                showToast("Registration successful!")
                goToStudentScreen()
            }
        }

        userViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { showToast(it) }
        }
    }

    // ---------------------- Text Watchers ----------------------
    private fun setupTextWatchers() {
        addTextWatcher(
            binding.username,
            binding.regUsername
        ) { checkIsEmpty(binding.username.text.toString()) }
        addTextWatcher(
            binding.email,
            binding.regEmail
        ) { checkIsValidEmail(binding.email.text.toString()) }
        addTextWatcher(
            binding.password,
            binding.regPassword
        ) { checkIsEmpty(binding.password.text.toString()) }
        addTextWatcher(
            binding.confirmPassword,
            binding.regConfirmPassword
        ) { checkConfirmPassword() }
    }

    private fun addTextWatcher(
        editText: EditText,
        layout: com.google.android.material.textfield.TextInputLayout,
        errorCheck: () -> String?
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                layout.error = errorCheck()
                binding.regButton.isEnabled = true
            }
        })
    }

    // ---------------------- Validation ----------------------
    private fun checkIsEmpty(text: String) =
        if (TextUtils.isEmpty(text)) getString(R.string.return_text) else null

    private fun checkIsValidEmail(email: String): String? =
        if (!Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) getString(R.string.return_email_text) else null

    private fun checkConfirmPassword(): String? {
        val password = binding.password.text.toString()
        val confirm = binding.confirmPassword.text.toString()
        return when {
            TextUtils.isEmpty(confirm) -> getString(R.string.return_text)
            confirm != password -> getString(R.string.return_con_pass_text)
            else -> null
        }
    }

    private fun getSelectedGender(): Gender = when (binding.radioGroupGender.checkedRadioButtonId) {
        R.id.btnRadioMan -> Gender.Male
        R.id.btnRadioWoman -> Gender.Female
        R.id.btnRadioOther -> Gender.Other
        else -> Gender.Other
    }

    private fun handleRegistration() {
        val username = binding.username.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val gender = getSelectedGender()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields")
            return
        }

        userViewModel.registerUser(username, email, password, gender)
    }

    // ---------------------- Navigation & Toast ----------------------
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun goToLoginScreen() {
        val action = RegistrationScreenDirections.actionRegistrationScreenToLoginScreen()
        findNavController().navigate(action)
    }

    private fun goToStudentScreen() {
        val currentDest = findNavController().currentDestination?.id
        if (currentDest != R.id.studentsScreen) {
            findNavController().navigate(R.id.studentsScreen)
        }
    }

    override fun onResume() {
        super.onResume()
        // Hide toolbar and bottom nav when on register screen
        (activity as? MainActivity)?.apply {
            binding.toolbar.visibility = View.GONE
            binding.bottomNavigation.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}