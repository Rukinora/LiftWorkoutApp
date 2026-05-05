package com.example.lift_workoutapp.database




import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dateMillis: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val setNumber: Int,
    val reps: Int,
    val weight: Double
)