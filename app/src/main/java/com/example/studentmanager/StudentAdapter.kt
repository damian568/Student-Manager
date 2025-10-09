package com.example.studentmanager

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.studentmanager.data.Student
import com.example.studentmanager.databinding.StudentItemBinding
import com.example.studentmanager.viewModel.StudentViewModel

class StudentAdapter(
    private val context: Context,
    private val viewModel: StudentViewModel
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private val students = mutableListOf<Student>()

    inner class StudentViewHolder(val binding: StudentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = StudentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun getItemCount(): Int = students.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        with(holder.binding) {
            studentName.text = student.name
            studentEmail.text = student.email
            studentCourses.text = student.course
            studentGrade.text = student.grade
            studentSpeciality.text = student.speciality

            editIcon.setOnClickListener { showEditDialog(student) }
            deleteIcon.setOnClickListener { viewModel.deleteStudent(student) }
        }
    }

    fun setStudents(list: List<Student>) {
        students.clear()
        students.addAll(list)
        notifyDataSetChanged()
    }

    private fun showEditDialog(student: Student?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_student, null)
        val nameField = dialogView.findViewById<EditText>(R.id.editStudentName)
        val emailField = dialogView.findViewById<EditText>(R.id.editStudentEmail)
        val courseField = dialogView.findViewById<EditText>(R.id.editStudentCourse)
        val gradeField = dialogView.findViewById<EditText>(R.id.editStudentGrade)
        val specialityField = dialogView.findViewById<EditText>(R.id.editStudentSpeciality)

        student?.let {
            nameField.setText(it.name)
            emailField.setText(it.email)
            courseField.setText(it.course)
            gradeField.setText(it.grade)
            specialityField.setText(it.speciality)
        }

        AlertDialog.Builder(context)
            .setTitle(if (student == null) "Add Student" else "Edit Student")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val newStudent = Student(
                    id = student?.id ?: System.currentTimeMillis().toInt(),
                    name = nameField.text.toString().trim(),
                    email = emailField.text.toString().trim(),
                    course = courseField.text.toString().trim(),
                    grade = gradeField.text.toString().trim(),
                    speciality = specialityField.text.toString().trim()
                )
                if (student == null) viewModel.addStudent(newStudent)
                else viewModel.updateStudent(newStudent)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}