package com.example.trainingdiary.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(tableName = "exercise_history",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        parentColumns = ["id"],
        childColumns = ["exerciseId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ExerciseHistory (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: Int,
    val datetime: Date
)

data class ExerciseHistoryWithExercise(
    @Embedded val exerciseHistory: ExerciseHistory,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: Exercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseHistoryId"
    )
    val approaches: List<Approach>,
    @Relation(
        entity = BodyPart::class,
        parentColumn = "exerciseId",
        entityColumn = "id",
        associateBy = Junction(
            value = BodyPartExerciseRelation::class,
            parentColumn = "exerciseId",
            entityColumn = "bodyPartId"
        )
    )
    val bodyParts: List<BodyPart>
)