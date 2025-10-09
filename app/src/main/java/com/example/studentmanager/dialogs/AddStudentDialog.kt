package com.example.studentmanager.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.studentmanager.data.Student
import com.example.studentmanager.databinding.DialogAddStudentBinding
import com.example.studentmanager.viewModel.StudentViewModel

class AddStudentDialog : DialogFragment() {

    private var _binding: DialogAddStudentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddStudentBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setTitle("Add New Student")
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ -> addStudent() }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun addStudent() {
        val name = binding.addStudentName.text.toString().trim()
        val email = binding.addStudentEmail.text.toString().trim()
        val course = binding.addStudentCourse.text.toString().trim()
        val grade = binding.addStudentGrade.text.toString().trim()
        val speciality = binding.addStudentSpeciality.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) return

        val student = Student(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            name = name,
            email = email,
            course = course,
            grade = grade,
            speciality = speciality
        )

        viewModel.addStudent(student)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}