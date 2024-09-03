package com.example.trainingdiary.ui.bodyPartList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.models.BodyPart
import com.example.trainingdiary.models.ExerciseWithBodyParts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BodyPartViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    suspend fun getBodyTypes(): List<BodyPart> {
        return withContext(Dispatchers.IO) {
            exerciseDao.getAllBodyTypes()
        }
    }

    suspend fun getExercisesByName(name: String): List<ExerciseWithBodyParts> {
        val searchQuery = "%$name%"

        return withContext(Dispatchers.IO) {
            exerciseDao.getExercisesByName(searchQuery)
        }
    }
}