package com.example.studentmanager.enums

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromGender(gender: Gender): String = gender.name

    @TypeConverter
    fun toGender(value: String): Gender = Gender.valueOf(value)
}