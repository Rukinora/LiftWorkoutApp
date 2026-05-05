package com.example.lift_workoutapp.screens
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.lift_workoutapp.data.ExerciseEntry
import com.example.lift_workoutapp.data.SetRow
import com.example.lift_workoutapp.data.WorkoutRepository
import com.example.lift_workoutapp.data.FinishedWorkout
import androidx.compose.ui.platform.LocalContext
import com.example.lift_workoutapp.database.WorkoutDatabase
import com.example.lift_workoutapp.database.WorkoutEntity
import kotlinx.coroutines.launch




@Composable
fun WorkoutScreen(modifier: Modifier = Modifier) {



    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember {
        WorkoutDatabase.getDatabase(context).workoutDao()
    }
    var workoutStarted by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    var showExercisePicker by remember { mutableStateOf(false) }

    val exercises = remember { mutableStateListOf<ExerciseEntry>() }

    LaunchedEffect(workoutStarted, isPaused) {
        while (workoutStarted && !isPaused) {
            delay(1000)
            seconds++
        }
    }

    val exerciseMap = mapOf(
        "My List" to listOf("Bench Press", "Squat", "Deadlift"),
        "Arms" to listOf("Bicep Curl", "Hammer Curl", "Tricep Pushdown"),
        "Legs" to listOf("Squat", "Leg Press", "Calf Raise"),
        "Back" to listOf("Lat Pulldown", "Barbell Row", "Deadlift"),
        "Chest" to listOf("Bench Press", "Incline Press", "Chest Fly"),
        "Core" to listOf("Plank", "Crunches", "Leg Raises")
    )

    var selectedTab by remember { mutableStateOf("My List") }
    val tabs = exerciseMap.keys.toList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!workoutStarted) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        workoutStarted = true
                        isPaused = false
                        seconds = 0
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF6AB00), // dark orange
                        contentColor = Color.Black
                    )

                ) {
                    Text("Start Workout")
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = formatTime(seconds),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = { isPaused = !isPaused }
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause"
                        )
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            val now = System.currentTimeMillis()

                            exercises.forEach { exercise ->
                                exercise.rows.forEachIndexed { index, row ->

                                    val reps = row.reps.toIntOrNull()
                                    val weight = row.weight.toDoubleOrNull()

                                    if (reps != null && weight != null) {
                                        dao.insertWorkout(
                                            WorkoutEntity(
                                                dateMillis = now,
                                                exerciseName = exercise.name,
                                                muscleGroup = exercise.name.getMuscleGroup(),
                                                setNumber = index + 1,
                                                reps = reps,
                                                weight = weight
                                            )
                                        )
                                    }
                                }
                            }

                            workoutStarted = false
                            showExercisePicker = false
                            seconds = 0
                            exercises.clear()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC94D),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Finish")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (showExercisePicker) {
                ScrollableTabRow(
                    selectedTabIndex = tabs.indexOf(selectedTab),
                    containerColor = Color(0xFF121212), // dark background
                    contentColor = Color(0xFFFFFFFF),    // selected tab color
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(
                                tabPositions[tabs.indexOf(selectedTab)]
                            ),
                            color = Color(0xFFFFC94D) // 👈 THIS REMOVES PINK
                        )
                    }
                )


                {
                    tabs.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = { Text(tab) },

                        )

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(exerciseMap[selectedTab] ?: emptyList()) { exerciseName ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1f1f1f) // dark gray
                            )

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(exerciseName)

                                Button(
                                    onClick = {
                                        exercises.add(ExerciseEntry(exerciseName))
                                        showExercisePicker = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF6AB00), // dark orange
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(exercises) { exercise ->
                        ExerciseCard(exercise)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showExercisePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3A86FF) // blue
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Exercise"
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Add Exercise")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E) // dark
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text("Set", modifier = Modifier.weight(1f))
                Text("Reps", modifier = Modifier.weight(1f))
                Text("Weight", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(48.dp))
            }

            exercise.rows.forEachIndexed { index, row ->
                SetInputRow(
                    setNumber = index + 1,
                    row = row,
                    onDelete = {
                        if (exercise.rows.size > 1) {
                            exercise.rows.removeAt(index)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = {
                    exercise.rows.add(SetRow())
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFFC94D) // orange
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Set"
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text("Add Set")
            }
        }
    }
}

@Composable
fun SetInputRow(
    setNumber: Int,
    row: SetRow,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = setNumber.toString(),
            modifier = Modifier.weight(0.6f)
        )

        Spacer(modifier = Modifier.width(6.dp))

        TextField(
            value = row.reps,
            onValueChange = { row.reps = it },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFFFC94D),

                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,

                focusedIndicatorColor = Color(0xFFFFC94D),
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.width(6.dp))

        TextField(
            value = row.weight,
            onValueChange = { row.weight = it },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFFFFC94D),
                unfocusedIndicatorColor = Color.Gray
            )
        )

        IconButton(
            onClick = {
                if (confirmDelete) {
                    onDelete()
                } else {
                    confirmDelete = true
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Set",
                tint = if (confirmDelete) Color.Red else Color.Gray
            )
        }
    }
}

fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

fun String.getMuscleGroup(): String {
    return when (this) {
        "Bicep Curl", "Hammer Curl", "Tricep Pushdown" -> "Arms"
        "Squat", "Leg Press", "Calf Raise" -> "Legs"
        "Lat Pulldown", "Barbell Row", "Deadlift" -> "Back"
        "Bench Press", "Incline Press", "Chest Fly" -> "Chest"
        "Plank", "Crunches", "Leg Raises" -> "Core"
        else -> "Other"
    }
}