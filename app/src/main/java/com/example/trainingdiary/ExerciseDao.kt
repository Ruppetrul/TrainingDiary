package com.example.trainingdiary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.trainingdiary.models.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    fun getAll(): List<Exercise>

    @Query("SELECT COUNT(*) FROM exercise")
    fun getCount(): Int

    @Insert
    suspend fun insertAll(entities: List<Exercise>)

    @Query("SELECT * FROM exercise")
    fun getAllRecords(): List<Exercise>
}