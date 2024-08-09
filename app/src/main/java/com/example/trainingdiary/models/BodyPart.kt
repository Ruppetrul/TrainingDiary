package com.example.trainingdiary.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BodyPart (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
)

@Entity
data class BodyPartExerciseRelation (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "bodyPartId") val bodyPartId: Int,
    @ColumnInfo(name = "exerciseId") val exerciseId: Int,
)