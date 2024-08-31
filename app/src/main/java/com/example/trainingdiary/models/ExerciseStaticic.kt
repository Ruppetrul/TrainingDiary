package com.example.trainingdiary.models

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseStat(
    val exerciseId: Int, //TODO нужна ли?
    val weightCountMap: MutableMap<Float, Int> = mutableMapOf()
) {
    fun addWeight(approach: Int, weight: Float) {
        weightCountMap[weight] = weightCountMap.getOrDefault(weight, 0) + 1
    }
}
