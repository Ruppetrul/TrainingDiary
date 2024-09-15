package com.example.trainingdiary

import NotificationHelper
import NotificationHelper.EXTRA_APPROACH
import NotificationHelper.EXTRA_EXERCISE
import NotificationHelper.EXTRA_REPEAT
import NotificationHelper.EXTRA_WEIGHT
import NotificationHelper.createNotification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

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

            Log.d(TAG, "onReceive: $weight")
            Log.d(TAG, "onReceive: $repeat")
            Log.d(TAG, "onReceive: $exercise")
            Log.d(TAG, "onReceive: $approach")
            when (action) {
                NotificationHelper.ACTION_INCREMENT_WEIGHT -> {
                    createNotification(context, exercise, approach, ++weight, repeat)
                }
                NotificationHelper.ACTION_DECREMENT_WEIGHT -> {
                    createNotification(context, exercise, approach, --weight, repeat)
                }
                NotificationHelper.ACTION_INCREMENT_REPEAT -> {
                    createNotification(context, exercise, approach, weight, ++repeat)
                }
                NotificationHelper.ACTION_DECREMENT_REPEAT -> {
                    createNotification(context, exercise, approach, weight, --repeat)
                }
                NotificationHelper.ACTION_SAVE -> {
                    //TODO save
                    //TODO next exercise
                }
            }
        }
    }
}