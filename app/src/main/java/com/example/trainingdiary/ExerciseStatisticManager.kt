package com.example.trainingdiary

import com.example.trainingdiary.models.ExerciseStat
import android.content.Context
import android.util.Log
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

class ExerciseStatisticManager {
    @OptIn(ExperimentalSerializationApi::class)
    companion object {

        fun saveExerciseStat(context: Context, stat: ExerciseStat, approach: Int) {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val cbor = Cbor { encodeDefaults = true }
            val bytes = cbor.encodeToByteArray(stat)
            editor.putString(stat.exerciseId.toString() + "_$approach", bytes.toBase64())
            editor.apply()
        }

        private fun ByteArray.toBase64(): String = android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT)

        fun getExerciseStat(context: Context, exerciseId: Int, approach: Int): ExerciseStat? {
            Log.d("WEIDGHT", "getExerciseStat approach: $approach")
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val base64String = sharedPreferences.getString(exerciseId.toString() + "_$approach", null) ?: return null
            val bytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
            val cbor = Cbor { encodeDefaults = true }
            val decodeFromByteArray = cbor.decodeFromByteArray<ExerciseStat?>(bytes)
            return decodeFromByteArray
        }

        fun updateExerciseStat(context: Context, exerciseId: Int, approachNumber:Int, weight: Float) {
            var stat = getExerciseStat(context, exerciseId, approachNumber)
            if (stat == null) {
                stat = ExerciseStat(exerciseId)
            }
            stat.addWeight(approachNumber, weight)
            saveExerciseStat(context, stat, approachNumber)
        }

        fun getTopWeights(stat: ExerciseStat): List<Map.Entry<Float, Int>> {
            return stat.weightCountMap.entries
                .sortedByDescending { it.value }
                .toList()
        }
    }
}