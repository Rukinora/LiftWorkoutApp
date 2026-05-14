package com.example.lift_workoutapp.screens
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import com.example.lift_workoutapp.data.AppSettings
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
import com.example.lift_workoutapp.data.WorkoutSession



@Composable
fun WorkoutScreen(modifier: Modifier = Modifier) {



    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember {
        WorkoutDatabase.getDatabase(context).workoutDao()
    }

    var isPaused by remember { mutableStateOf(false) }

    var showExercisePicker by remember { mutableStateOf(false) }
    var workoutStarted by WorkoutSession.workoutStarted

    var seconds by WorkoutSession.seconds

    val exercises = WorkoutSession.exercises


    LaunchedEffect(workoutStarted, isPaused) {
        while (workoutStarted && !isPaused) {
            delay(1000)
            seconds++
        }
    }
    //    ,"", "","", ""
    val exerciseMap = mapOf(
        "My List" to listOf("Bench Press", "Squat", "Deadlift"),
        //Missing Forearm execises
        "Arms" to listOf("Bicep Curl", "Hammer Curl", "Tricep Pushdown", "Triceps Dip",
            "Barbell Curl", "Alternating Dumbbell Curl","Rope Cable Curl", "EZ Barbell Curl",
            "EZ Barbell Preacher Curl", "Hammer Curl","Incline Dumbbell Curl",
            "Dumbbell Concentration Curl","Single-Arm Low Pulley Cable Curl",
            "Straight Bar Low Pulley Cable Curl","Standing High Pulley Cable Curl",
            "Seated Barbell Wrist Curl","Seated Barbell Wrist Extension", "Reverse Barbell Curl",
            "Dumbbell Shoulder Press", "Dumbbell Lateral Raise", "Dumbbell Front Raise",
            "High Cable Rear Delt Fly", "Smith Machine Shoulder Press", "Barbell Upright Row",
            "Bent-Over Lateral Raise", "Cable One-Arm Lateral Raise", "Dumbbell Push Press",
            "Barbell Push Press", "Single-Arm Cable Front Raise", "Barbell Front Raise",
            "Seated Barbell Shoulder Press", "Seated Behind the Neck Barbell Shoulder Press",
            "Standing Barbell Shoulder Press", "Standing Behind the Neck Barbell Shoulder Press",
            "Alternate Dumbbell Front Raise Neutral Grip",
            "One-Arm Low-Pulley Front Raise Neutral Grip", "Two-Handed Dumbbell Front Raise",
            "Lying Triceps Extension", "Triceps Pressdown", "Cable Rope Pushdown",
            "Dumbbell Overhead Triceps Extension", "Close Grip Bench Press", "Kickback",
            "Reverse Grip Cable Triceps Extension with Barbell",
            "Single-Arm Cable Triceps Extension",
            "Single-Arm Cable Triceps Extension with Supinated Grip",
            "Lying Dumbbell Triceps Extension", "Seated Barbell French Press",
            "Bench Dips", "Parallel Dip Bar", "Dumbbell Shoulder Press", "Dumbbell Lateral Raise",
            "Dumbbell Front Raise", "High Cable Rear Delt Fly", "Smith Machine Shoulder Press",
            "Barbell Upright Row", "Bent-Over Lateral Raise", "Cable One-Arm Lateral Raise",
            "Dumbbell Push Press", "Barbell Push Press", "Single-Arm Cable Front Raise",
            "Barbell Front Raise", "Seated Barbell Shoulder Press",
            "Seated Behind the Neck Barbell Shoulder Press", "Standing Barbell Shoulder Press",
            "Standing Behind the Neck Barbell Shoulder Press",
            "Alternate Dumbbell Front Raise Neutral Grip",
            "One-Arm Low-Pulley Front Raise Neutral Grip", "Two-Handed Dumbbell Front Raise"),
        "Legs" to listOf("Squat", "Leg Press", "Calf Raise","Leg Extension", "Lunge",
            "Lying Leg Curl", "Hack Squat" ,"Seated Leg Curl", "Single Leg Extension","Front Squat",
            "Dumbbell Stiff-Leg Deadlift" ,"Barbell Stiff-Leg Deadlift", "Dumbbell Goblet Squat",
            "Knee Tuck Jumps", "Burpees" ,"Bodyweight Squat", "Medicine Ball Squat",
            "Barbell Bulgarian Split Squat", "Bodyweight Bulgarian Split Squat" ,
            "Mini-Band Air Squat", "Jump Squat","Wall Sit", "Medicine Ball Deadlift" ,
            "Single Leg Bodyweight Deadlift", "Kettlebell Sumo Deadlift","Bodyweight Glute Bridge",
            "Single Leg Glute Bridge" ,"Banded Glute Bridge", "Smith Machine Hip Thrust",
            "Barbell Hip Thrust", "Band Seated Hip Abduction" ,"Seated Hip Abduction Machine",
            "Standing Cable Abduction","Side Lying Leg Raise", "Glute Ham Raise" ,
            "Dumbbell Step Up", "Lateral Mini-Band Walk","Standing Knee Raise", "Kettlebell Swings",
            "Standing Cable Kickback", "Side Lying Hip Raise","quat Sit to Reach"),
        "Back" to listOf("Lat Pulldown", "Barbell Row", "Deadlift",
            "Dumbbell Bent-Over Row (Single Arm)", "Wide-Grip Pulldown","Seated Cable Row",
            "Close-Grip Pulldown" ,"Barbell Row", "Behind-Neck Pulldown","Reverse-Grip Pulldown",
            "Rope Pulldown" ,"T-Bar Rows", "Barbell Bent Over Rows Supinated Grip","Pull Up",
            "Behind the Neck Pull Up" ,"Pull Up with a Supinated Grip", "Straight Arm Lat Pulldown",
            "Dumbbell Bent Over Rows", "Dumbbell Pullover" ,"Barbell Pullover", "Barbell Deadlift",
            "Barbell Sumo Deadlift", "Trap Bar Deadlift" ,"Dumbbell Deadlift", "Barbell Shrug",
            "Dumbbell Shrugs"),
        "Chest" to listOf("Bench Press", "Incline Press", "Chest Fly","Barbell Bench Press",
            "Incline Dumbbell Bench Press","Pec Deck", "Cable Crossover",
            "Incline Barbell Bench Press", "Dumbbell Bench Press","Dumbbell Fly",
            "Incline Dumbbell Fly","Chest Press Machine", "Barbell Declined Bench Press",
            "Dumbbell Declined Bench Press", "Push Ups"),
        "Core" to listOf("Plank", "Crunches", "Leg Raises","Crunch", "Oblique Crunch",
            "Crunch Machine", "Rope Ab Pulldown" ,"Plank", "Hanging Leg Raise",
            "Bent Knee Reverse Crunch", "Long Arm Crunch" ,"Plank Get Ups")
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

                            WorkoutSession.workoutStarted.value = false
                            WorkoutSession.seconds.intValue = 0
                            WorkoutSession.exercises.clear()

                            showExercisePicker = false
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
                var searchText by remember { mutableStateOf("") }

                val filteredExercises = exerciseMap[selectedTab]
                    ?.filter { it.contains(searchText, ignoreCase = true) }
                    ?: emptyList()

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = tabs.indexOf(selectedTab),
                        containerColor = Color(0xFF121212),
                        contentColor = Color(0xFFFFC94D),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(
                                    tabPositions[tabs.indexOf(selectedTab)]
                                ),
                                color = Color(0xFFFFC94D)
                            )
                        }
                    ) {
                        tabs.forEach { tab ->
                            Tab(
                                selected = selectedTab == tab,
                                onClick = {
                                    selectedTab = tab
                                    searchText = ""
                                },
                                text = {
                                    Text(
                                        tab,
                                        color = if (selectedTab == tab)
                                            Color(0xFFFFC94D)
                                        else
                                            Color.Gray
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search exercises...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFFFC94D),
                            focusedContainerColor = Color(0xFF1E1E1E),
                            unfocusedContainerColor = Color(0xFF1E1E1E),
                            focusedIndicatorColor = Color(0xFFFFC94D),
                            unfocusedIndicatorColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredExercises) { exerciseName ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1F1F1F)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = exerciseName,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 12.dp),
                                        softWrap = true,
                                        maxLines = 2,
                                        color = Color.White
                                    )

                                    Button(
                                        onClick = {
                                            exercises.add(ExerciseEntry(exerciseName))
                                            showExercisePicker = false
                                        },
                                        modifier = Modifier.width(80.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFF6AB00),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text("Add")
                                    }
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

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = AppSettings.weightUnit,
            color = Color.White,
            modifier = Modifier.width(32.dp)
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