package com.example.trainingdiary.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Exercise (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
)

@Entity
data class ExerciseWithBodyParts(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,

    @Relation(
        entity = BodyPart::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BodyPartExerciseRelation::class,
            parentColumn = "exerciseId",
            entityColumn = "bodyPartId"
        )
    )
    val bodyParts: List<BodyPart>
)