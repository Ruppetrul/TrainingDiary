package com.example.trainingdiary.ui.home.CalendarSheet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.ExerciseHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarSheetViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseHistoryRepository: ExerciseHistoryRepository

    private val _trainingDates = MutableLiveData<List<Long>>()
    val trainingDates: LiveData<List<Long>> get() = _trainingDates

    init {

        val database = AppDatabase.getDatabase(application)
        val exerciseHistoryDao = database.exerciseDao()

        exerciseHistoryRepository = ExerciseHistoryRepository(exerciseHistoryDao)
    }

    fun getTrainingDates() {
        viewModelScope.launch {
            val dates = withContext(Dispatchers.IO) {
                exerciseHistoryRepository.getHistoryDates()
            }
            _trainingDates.value = dates
        }
    }
}