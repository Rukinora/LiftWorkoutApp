package com.example.lift_workoutapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

import com.example.lift_workoutapp.data.AppSettings
import com.example.lift_workoutapp.database.WorkoutDatabase

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val dao = remember {
        WorkoutDatabase.getDatabase(context).workoutDao()
    }

    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Weight Type:")

        Spacer(modifier = Modifier.height(12.dp))

        Row {

            FilterChip(
                selected = AppSettings.weightUnit == "lbs",
                onClick = {
                    AppSettings.weightUnit = "lbs"
                },
                label = {
                    Text("lbs")
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            FilterChip(
                selected = AppSettings.weightUnit == "kgs",
                onClick = {
                    AppSettings.weightUnit = "kgs"
                },
                label = {
                    Text("kgs")
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                showDeleteDialog = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Delete Workout History")
        }

        if (showDeleteDialog) {

            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },

                title = {
                    Text("Delete Workout History")
                },

                text = {
                    Text("Are you sure? This cannot be undone.")
                },

                confirmButton = {

                    Button(
                        onClick = {

                            scope.launch {
                                dao.deleteAllWorkouts()
                            }

                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Yes")
                    }
                },

                dismissButton = {

                    Button(
                        onClick = {
                            showDeleteDialog = false
                        }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}