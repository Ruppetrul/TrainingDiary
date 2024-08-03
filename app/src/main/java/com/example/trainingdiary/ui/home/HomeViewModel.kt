package com.example.trainingdiary.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.ExerciseHistoryRepository
import com.example.trainingdiary.models.ExerciseHistoryWithExercise

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseHistoryRepository: ExerciseHistoryRepository

    init {

        val database = AppDatabase.getDatabase(application)
        val exerciseHistoryDao = database.exerciseDao()

        exerciseHistoryRepository = ExerciseHistoryRepository(exerciseHistoryDao)
    }

    fun getByTimestamp(timestamp: Int): LiveData<List<ExerciseHistoryWithExercise>> {
        return exerciseHistoryRepository.getHistoryForPosition(timestamp)
    }
}