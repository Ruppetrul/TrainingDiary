package com.example.trainingdiary.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Exercise (
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "title") val firstName: String,
)