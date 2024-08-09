package com.example.trainingdiary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.trainingdiary.models.Approach
import com.example.trainingdiary.models.BodyPart
import com.example.trainingdiary.models.BodyPartExerciseRelation
import com.example.trainingdiary.models.Exercise
import com.example.trainingdiary.models.ExerciseHistory
import java.util.Date

@Database(entities = [
    Exercise::class,
    ExerciseHistory::class,
    Approach::class,
    BodyPart::class,
    BodyPartExerciseRelation::class,
], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "GENERAL_DB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}