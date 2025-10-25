package com.example.studentmanager.dialogs

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.studentmanager.databinding.DialogForgotPasswordBinding
import com.example.studentmanager.user.UserDatabase
import com.example.studentmanager.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordDialog : DialogFragment() {
    private var _binding: DialogForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClickListeners()
    }

    private fun buttonClickListeners() {
        binding.btnSendReset.setOnClickListener { sendPasswordResetEmail() }
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun sendPasswordResetEmail() {
        val email = binding.sendEmail.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            binding.sendEmail.error = "Please enter your email"
            return
        }

        lifecycleScope.launch {
            val dao = UserDatabase.getDatabase(requireContext()).userDao()
            val repo = UserRepository(dao)
            val user = withContext(Dispatchers.IO) { repo.getUserByEmail(email) }
            withContext(Dispatchers.Main) {
                if (user != null) {
                    Toast.makeText(
                        requireContext(),
                        "Your password is: ${user.password}",
                        Toast.LENGTH_LONG
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}