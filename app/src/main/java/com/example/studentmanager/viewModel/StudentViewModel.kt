package com.example.studentmanager.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentmanager.data.Student
import com.example.studentmanager.student.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {

    val allStudents: LiveData<List<Student>> = repository.allStudents.asLiveData()

    fun addStudent(student: Student) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertStudent(student)
    }

    fun updateStudent(student: Student) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateStudent(student)
    }

    fun deleteStudent(student: Student) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteStudent(student)
    }
}