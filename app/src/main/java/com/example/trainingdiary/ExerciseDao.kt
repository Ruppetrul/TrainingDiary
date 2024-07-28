package com.example.trainingdiary

import androidx.room.Dao
import androidx.room.Query
import com.example.trainingdiary.models.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    fun getAll(): List<Exercise>
}