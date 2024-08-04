package com.example.trainingdiary.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Approach (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "exerciseHistoryId") val exerciseHistoryId: Int,
    @ColumnInfo(name = "repeatCount") val repeatCount: Int,
    @ColumnInfo(name = "weight") val weight: Float,
)