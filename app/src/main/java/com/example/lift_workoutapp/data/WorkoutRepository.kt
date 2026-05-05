package com.example.lift_workoutapp.data


object WorkoutRepository {
    val finishedWorkouts = mutableListOf<FinishedWorkout>()
}

data class FinishedWorkout(
    val dateMillis: Long = System.currentTimeMillis(),
    val exercises: List<ExerciseEntry>
)