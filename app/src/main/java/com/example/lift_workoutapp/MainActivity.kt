package com.example.lift_workoutapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.lift_workoutapp.screens.*
import com.example.lift_workoutapp.ui.theme.LiftworkoutAppTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LiftworkoutAppTheme {
                WorkoutApp()
            }
        }
    }
}

@Composable
fun WorkoutApp() {
    var selectedScreen by remember { mutableStateOf("Home") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedScreen == "Home",
                    onClick = { selectedScreen = "Home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Lists",
                    onClick = { selectedScreen = "Lists" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Lists") },
                    label = { Text("Lists") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Workout",
                    onClick = { selectedScreen = "Workout" },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Workout") },
                    label = { Text("Workout") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Activity",
                    onClick = { selectedScreen = "Activity" },
                    icon = { Icon(Icons.Default.DirectionsRun, contentDescription = "Activity") },
                    label = { Text("Activity") }
                )

                NavigationBarItem(
                    selected = selectedScreen == "Stats",
                    onClick = { selectedScreen = "Stats" },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
                    label = { Text("Stats") }
                )
            }
        }
    ) { paddingValues ->

        when (selectedScreen) {
            "Home" -> HomeScreen(Modifier.padding(paddingValues))
            "Lists" -> ListsScreen(Modifier.padding(paddingValues))
            "Workout" -> WorkoutScreen(Modifier.padding(paddingValues))
            "Activity" -> ActivityScreen(Modifier.padding(paddingValues))
            "Stats" -> StatsScreen(Modifier.padding(paddingValues))
        }
    }
}