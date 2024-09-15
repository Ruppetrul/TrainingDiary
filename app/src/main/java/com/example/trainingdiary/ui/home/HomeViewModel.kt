package com.example.trainingdiary.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.ExerciseHistoryRepository
import com.example.trainingdiary.models.ExerciseHistoryWithExercise
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.time.LocalDate
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseHistoryRepository: ExerciseHistoryRepository

    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int> get() = _position

    lateinit var adapter : MyFragmentStateAdapter

    init {
        val database = AppDatabase.getDatabase(application)
        val exerciseHistoryDao = database.exerciseDao()

        exerciseHistoryRepository = ExerciseHistoryRepository(exerciseHistoryDao)
    }

    fun getByPosition(timestamp: Int): LiveData<List<ExerciseHistoryWithExercise>> {
        return exerciseHistoryRepository.getHistoryForPosition(timestamp)
    }

    fun setPosition(date: CalendarDay) {
        //Because the months are counting down from 0.
        _position.value = adapter.getPositionForDate(LocalDate.of(date.year, date.month + 1, date.day))
    }

    fun getDayOfEpoch() : Long {
        val epochStart = Calendar.getInstance().apply {
            set(1970, Calendar.JANUARY, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val currentTimeMillis = System.currentTimeMillis()

        val differenceMillis = currentTimeMillis - epochStart

        return differenceMillis / (1000 * 60 * 60 * 24)
    }
}