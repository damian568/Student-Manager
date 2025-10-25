package com.example.studentmanager.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.studentmanager.data.Student
import com.example.studentmanager.databinding.DialogEditStudentBinding
import com.example.studentmanager.viewModel.StudentViewModel

class EditStudentDialog(private val student: Student) : DialogFragment() {

    private var _binding: DialogEditStudentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditStudentBinding.inflate(LayoutInflater.from(context))
        binding.apply {
            editStudentName.setText(student.name)
            editStudentEmail.setText(student.email)
            editStudentCourse.setText(student.course)
            editStudentGrade.setText(student.grade)
            editStudentSpeciality.setText(student.speciality)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Edit Student")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ -> saveChanges() }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun saveChanges() {
        val updated = student.copy(
            name = binding.editStudentName.text.toString().trim(),
            email = binding.editStudentEmail.text.toString().trim(),
            course = binding.editStudentCourse.text.toString().trim(),
            grade = binding.editStudentGrade.text.toString().trim(),
            speciality = binding.editStudentSpeciality.text.toString().trim()
        )
        viewModel.updateStudent(updated)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}