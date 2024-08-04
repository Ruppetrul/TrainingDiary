package com.example.trainingdiary

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.trainingdiary.models.Approach
import com.example.trainingdiary.models.Exercise
import com.example.trainingdiary.models.ExerciseHistory
import com.example.trainingdiary.models.ExerciseHistoryWithExercise
import java.util.Date

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

    @Transaction
    @Query("SELECT * FROM exercise_history")
    fun getAllHistoryWithExercises(): LiveData<List<ExerciseHistoryWithExercise>>

    @Insert
    fun insertHistory(exerciseHistory: ExerciseHistory)

    @Query("DELETE FROM exercise_history WHERE id = :id")
    fun deleteHistoryById(id: Int)

    @Query("SELECT * FROM exercise_history WHERE datetime >= :startOfDay AND datetime < :endOfDay")
    fun getExerciseHistoryByDate(startOfDay: Date, endOfDay: Date): LiveData<List<ExerciseHistoryWithExercise>>

    @Insert
    fun insertApproach(approach: Approach)

    @Update
    fun updateApproach(approach: Approach)

    @Query("DELETE FROM Approach WHERE id = :id")
    fun deleteApproachById(id: Int)
}