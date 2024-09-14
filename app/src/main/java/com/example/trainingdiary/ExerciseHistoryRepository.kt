package com.example.trainingdiary

import androidx.lifecycle.LiveData
import com.example.trainingdiary.models.ExerciseHistoryWithExercise
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class ExerciseHistoryRepository(private val exerciseHistoryDao: ExerciseDao) {

    fun getHistoryForPosition(position: Int): LiveData<List<ExerciseHistoryWithExercise>> {
        val (startOfDay, endOfDay) = getStartAndEndOfDay(LocalDate.ofEpochDay(position.toLong()))
        return exerciseHistoryDao.getExerciseHistoryByDate(startOfDay, endOfDay)
    }

    private fun getStartAndEndOfDay(date: LocalDate): Pair<Date, Date> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay()

        val zonedStartOfDay = startOfDay.atZone(ZoneId.systemDefault()).toInstant()
        val zonedEndOfDay = endOfDay.atZone(ZoneId.systemDefault()).toInstant()

        return Pair(Date.from(zonedStartOfDay), Date.from(zonedEndOfDay))
    }

    fun getHistoryDates(): List<Long> {
        return exerciseHistoryDao.getExerciseHistoryDates()
    }
}