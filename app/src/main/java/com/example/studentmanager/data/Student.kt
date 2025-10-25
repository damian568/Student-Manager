package com.example.studentmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: Int,
    var name: String,
    var email: String,
    var course: String,
    var grade: String,
    var speciality: String
)
