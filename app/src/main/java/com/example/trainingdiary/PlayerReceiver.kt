package com.example.trainingdiary

import NotificationHelper
import NotificationHelper.EXTRA_APPROACH
import NotificationHelper.EXTRA_APPROACH_ID
import NotificationHelper.EXTRA_EXERCISE
import NotificationHelper.EXTRA_REPEAT
import NotificationHelper.EXTRA_WEIGHT
import NotificationHelper.createNotification
import NotificationHelper.createPlayer
import NotificationHelper.hideNotification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.trainingdiary.models.Approach
import com.example.trainingdiary.ui.home.HistoryHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayerReceiver : BroadcastReceiver() {
    private val TAG = "PlayerReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val action = intent?.action
            val extras = intent?.extras

            if (extras != null) {
                for (key in extras.keySet()) {
                    val value = extras[key]
                    Log.d("PlayerReceiver", "Key: $key, Value: $value")
                }
            } else {
                Log.d("PlayerReceiver", "No extras found in the intent")
            }

            var weight = intent?.getFloatExtra(EXTRA_WEIGHT, 0f) ?: 0f
            var repeat = intent?.getIntExtra(EXTRA_REPEAT, 0) ?: 0
            var exercise = intent?.getStringExtra(EXTRA_EXERCISE) ?: ""
            var approach = intent?.getIntExtra(EXTRA_APPROACH, 0) ?: 0
            var approachId = intent?.getIntExtra(EXTRA_APPROACH_ID, 1) ?: 0

            Log.d(TAG, "onReceive: $weight")
            Log.d(TAG, "onReceive: $repeat")
            Log.d(TAG, "onReceive: $exercise")
            Log.d(TAG, "onReceive: $approach")
            Log.d(TAG, "onReceive: $approachId")
            when (action) {
                NotificationHelper.ACTION_INCREMENT_WEIGHT -> {
                    createNotification(context, exercise, approach, ++weight, repeat, approachId)
                }
                NotificationHelper.ACTION_DECREMENT_WEIGHT -> {
                    createNotification(context, exercise, approach, --weight, repeat, approachId)
                }
                NotificationHelper.ACTION_INCREMENT_REPEAT -> {
                    createNotification(context, exercise, approach, weight, ++repeat, approachId)
                }
                NotificationHelper.ACTION_DECREMENT_REPEAT -> {
                    createNotification(context, exercise, approach, weight, --repeat, approachId)
                }
                NotificationHelper.ACTION_SAVE -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = AppDatabase.getDatabase(context)
                        val exerciseHistoryDao = database.exerciseDao()
                        val exerciseHistoryRepository =
                            ExerciseHistoryRepository(exerciseHistoryDao)

                        val approach2 = exerciseHistoryRepository.selectApproach(approachId)

                        approach2.weight = weight
                        approach2.repeatCount = repeat
                        approach2.confirmed = true

                        exerciseHistoryRepository.updateApproach(approach2)
                    }.apply {
                        createPlayer(context, true)
                    }
                }
            }
        }
    }
}