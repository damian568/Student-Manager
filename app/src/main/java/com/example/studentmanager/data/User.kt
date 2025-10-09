package com.example.studentmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.studentmanager.enums.Converters
import com.example.studentmanager.enums.Gender

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey val uid: String,
    var username: String,
    var email: String,
    var password: String,
    var gender: Gender
)
