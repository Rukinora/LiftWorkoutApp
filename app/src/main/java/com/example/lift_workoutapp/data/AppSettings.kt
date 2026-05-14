package com.example.lift_workoutapp.data


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppSettings {
    var weightUnit by mutableStateOf("lbs")
}