package com.example.studentmanager.dialogs

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.studentmanager.data.User
import com.example.studentmanager.databinding.DialogEditUserBinding
import com.example.studentmanager.enums.Gender
import com.example.studentmanager.viewModel.UserViewModel
import kotlin.getValue

class EditUserDialog(private val user: User): DialogFragment() {

    private var _binding: DialogEditUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupButtons()
    }

    private fun setupUI() {
        // Pre-fill user data
        binding.editUserName.setText(user.username)
        binding.editUserEmail.setText(user.email)

        // Gender spinner setup
        val genders = listOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, genders)
        binding.genderUserSpinner.adapter = adapter

        val genderIndex = when (user.gender) {
            Gender.Male -> 0
            Gender.Female -> 1
            else -> 2
        }
        binding.genderUserSpinner.setSelection(genderIndex)
    }

    private fun setupButtons() {
        binding.saveEditButton.setOnClickListener { saveChanges() }
        binding.cancelEditButton.setOnClickListener { dismiss() }
    }

    private fun saveChanges() {
        val newUsername = binding.editUserName.text.toString().trim()
        val newEmail = binding.editUserEmail.text.toString().trim()
        val newGender = when (binding.genderUserSpinner.selectedItemPosition) {
            0 -> Gender.Male
            1 -> Gender.Female
            else -> Gender.Other
        }

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updated = user.copy(
            username = newUsername,
            email = newEmail,
            gender = newGender
        )
        viewModel.updateUser(updated)
        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}