package com.example.trainingdiary

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.trainingdiary.models.Approach
import com.example.trainingdiary.models.BodyPart
import com.example.trainingdiary.models.BodyPartExerciseRelation
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

    @Query("SELECT COUNT(*) FROM bodypart")
    fun getBodyPartCount(): Int

    @Insert
    suspend fun insertExercise(entity: Exercise): Long

    @Insert
    suspend fun insertAll(entities: List<Exercise>)

    @Insert
    suspend fun insertBodyPartsAll(entities: List<BodyPart>)

    @Query("SELECT exercise.* FROM bodypartexerciserelation LEFT JOIN exercise ON bodypartexerciserelation.exerciseId = exercise.id WHERE bodyPartId = :bodyPart")
    fun getExercisesByBodyPart(bodyPart : Int): List<Exercise>

    @Query("SELECT * FROM bodypart")
    fun getAllBodyTypes(): List<BodyPart>

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

    @Insert
    suspend fun insertPartExerciseRelation(relation: BodyPartExerciseRelation): Long
}