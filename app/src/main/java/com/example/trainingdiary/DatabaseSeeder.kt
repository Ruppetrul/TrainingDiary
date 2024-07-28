package com.example.trainingdiary

import com.example.trainingdiary.models.Exercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseSeeder(private val database: AppDatabase) {
    fun seed() {
        CoroutineScope(Dispatchers.IO).launch {
            if (database.exerciseDao().getCount() == 0) {
                val initialData = listOf(
                    Exercise(title = "Бег"),
                    Exercise(title = "Подтягивания"),
                    Exercise(title = "Анжумани")
                )
                database.exerciseDao().insertAll(initialData)
            }
        }
    }
}