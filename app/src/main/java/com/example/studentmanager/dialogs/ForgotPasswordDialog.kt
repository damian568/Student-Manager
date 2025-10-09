package com.example.studentmanager.dialogs

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.studentmanager.databinding.DialogForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordDialog : DialogFragment() {
    private var _binding: DialogForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogForgotPasswordBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
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

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Password reset email sent!",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}