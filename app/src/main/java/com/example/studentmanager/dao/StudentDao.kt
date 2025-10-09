package com.example.studentmanager.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studentmanager.data.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert
    suspend fun insertStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("SELECT * FROM students WHERE course = :course LIMIT 1")
    suspend fun sortStudentByCourse(course: String): Student?

    @Query("SELECT * FROM students WHERE grade = :grade LIMIT 1")
    suspend fun sortStudentByGrade(grade: String): Student?

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<Student>>
}