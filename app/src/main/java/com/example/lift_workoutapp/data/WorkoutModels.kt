package com.example.lift_workoutapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.compose.runtime.mutableStateListOf

data class ExerciseEntry(
    val name: String,
    val rows: MutableList<SetRow> = mutableStateListOf(
        SetRow(),
        SetRow(),
        SetRow()
    )
)

class SetRow(
    reps: String = "",
    weight: String = ""
) {
    var reps by mutableStateOf(reps)
    var weight by mutableStateOf(weight)
}