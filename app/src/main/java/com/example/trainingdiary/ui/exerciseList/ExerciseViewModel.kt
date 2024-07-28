package com.example.trainingdiary.ui.exerciseList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.models.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    suspend fun getExercises(): List<Exercise> {
        return withContext(Dispatchers.IO) {
            exerciseDao.getAllRecords()
        }
    }
}