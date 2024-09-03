package com.example.trainingdiary.ui.exerciseList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.models.BodyPart
import com.example.trainingdiary.models.ExerciseWithBodyParts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    suspend fun getExercisesByBodyPart(bodyPart: Int): List<ExerciseWithBodyParts> {
        return withContext(Dispatchers.IO) {
            exerciseDao.getExercisesByBodyPart(bodyPart)
        }
    }

    suspend fun getBodyTypes(): List<BodyPart> {
        return withContext(Dispatchers.IO) {
            exerciseDao.getAllBodyTypes()
        }
    }
}