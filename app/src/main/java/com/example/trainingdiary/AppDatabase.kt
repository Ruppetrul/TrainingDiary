package com.example.trainingdiary

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trainingdiary.models.Exercise

@Database(entities = [Exercise::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
}