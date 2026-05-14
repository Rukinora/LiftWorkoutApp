package com.example.lift_workoutapp.data

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object WorkoutSession {

    var workoutStarted = mutableStateOf(false)

    var seconds = mutableIntStateOf(0)

    val exercises = mutableStateListOf<ExerciseEntry>()
}