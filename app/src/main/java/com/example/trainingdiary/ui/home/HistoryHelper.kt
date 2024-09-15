package com.example.trainingdiary.ui.home

import android.content.Context
import android.util.Log
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.ExerciseHistoryRepository
import com.example.trainingdiary.models.Approach
import com.example.trainingdiary.models.ExerciseHistoryWithExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class HistoryHelper(context: Context) {

    val exerciseHistoryRepository: ExerciseHistoryRepository

    init {
        val database = AppDatabase.getDatabase(context)
        val exerciseHistoryDao = database.exerciseDao()

        exerciseHistoryRepository = ExerciseHistoryRepository(exerciseHistoryDao)
    }

    data class ApproachResult(
        val firstNotConfirmedApproach: Approach?,
        val approachExerciseTitle: String?,
        val approachIndex: Int?,
        val hasExercisesWithoutApproaches: Boolean
    )

    suspend fun getFirstNotConfirmedApproach(): ApproachResult {
        val dayOfEpoch = getDayOfEpoch()
        Log.d("TAG", "dayOfEpoch: $dayOfEpoch")

        return withContext(Dispatchers.IO) {
            val history = getByPosition(dayOfEpoch.toInt())

            var firstNotConfirmedApproach: Approach? = null
            var approachExerciseTitle: String? = null
            var approachIndex: Int? = null
            var hasExercisesWithoutApproaches = false

            Log.d("TAG", "getFirstNotConfirmedApproach: $history")
            outer@ for ((itemIndex, item) in history.withIndex()) {
                val approaches = item.approaches

                if (approaches.isEmpty()) {
                    hasExercisesWithoutApproaches = true
                }

                if (approaches.isNotEmpty()) {
                    for ((approachIndexInList, approach) in approaches.withIndex()) {
                        if (!approach.confirmed) {
                            firstNotConfirmedApproach = approach
                            approachExerciseTitle = item.exercise?.title
                            approachIndex = approachIndexInList + 1
                            break@outer
                        }
                        Log.d("TAG", "getNextNotConfirmedApproach: $approach")
                    }
                }
            }
            ApproachResult(firstNotConfirmedApproach, approachExerciseTitle, approachIndex, hasExercisesWithoutApproaches)
        }
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

    fun getByPosition(timestamp: Int): List<ExerciseHistoryWithExercise> {
        return exerciseHistoryRepository.getHistoryForPosition2(timestamp)
    }
}