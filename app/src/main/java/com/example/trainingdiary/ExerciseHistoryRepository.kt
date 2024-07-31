package com.example.trainingdiary

import androidx.lifecycle.LiveData
import com.example.trainingdiary.models.ExerciseHistoryWithExercise

class ExerciseHistoryRepository(private val exerciseHistoryDao: ExerciseDao) {

    fun getTodayHistory(): LiveData<List<ExerciseHistoryWithExercise>> {
        return exerciseHistoryDao.getAllHistoryWithExercises()
    }
}