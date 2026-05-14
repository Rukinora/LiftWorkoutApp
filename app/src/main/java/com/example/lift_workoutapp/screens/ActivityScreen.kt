package com.example.lift_workoutapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

import com.example.lift_workoutapp.database.WorkoutDatabase
import com.example.lift_workoutapp.database.WorkoutEntity

@Composable
fun ActivityScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val dao = remember {
        WorkoutDatabase.getDatabase(context).workoutDao()
    }

    var workouts by remember {
        mutableStateOf<List<WorkoutEntity>>(emptyList())
    }

    LaunchedEffect(Unit) {
        dao.getAllWorkouts().collectLatest {
            workouts = it
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            Text(
                text = "Activity",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            WorkoutHeatmap(workouts)
        }

        item {
            WorkoutStreakCard(workouts)
        }

        item {
            WeeklySummaryCard(workouts)
        }

        item {
            RecoveryCard(workouts)
        }

        item {
            Text(
                text = "Workout History",
                style = MaterialTheme.typography.titleLarge
            )
        }

        val grouped = workouts.groupBy {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                .format(Date(it.dateMillis))
        }

        items(grouped.entries.toList()) { entry ->

            val date = entry.key
            val sessions = entry.value

            val totalVolume = sessions.sumOf {
                it.reps * it.weight
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = date,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${sessions.size} sets logged",
                        color = Color.LightGray
                    )

                    Text(
                        text = "${totalVolume.toInt()} ${sessions.firstOrNull()?.let { "lbs" } ?: ""} volume",
                        color = Color(0xFFFFC94D)
                    )
                }
            }
        }
    }
}


@Composable
fun WorkoutHeatmap(workouts: List<WorkoutEntity>) {

    val grouped = workouts.groupBy {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date(it.dateMillis))
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Workout Heatmap",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            repeat(5) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(7) { day ->

                        val intensity = (0..4).random()

                        val color = when (intensity) {
                            0 -> Color.DarkGray
                            1 -> Color(0xFF6B4E00)
                            2 -> Color(0xFFAA7700)
                            3 -> Color(0xFFFFA500)
                            else -> Color(0xFFFFC94D)
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color,
                                    RoundedCornerShape(6.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun WorkoutStreakCard(workouts: List<WorkoutEntity>) {

    val streak = calculateWorkoutStreak(workouts)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Workout Streak",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$streak day streak 🔥",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC94D)
            )
        }
    }
}

@Composable
fun WeeklySummaryCard(workouts: List<WorkoutEntity>) {

    val weeklyVolume = workouts.sumOf {
        it.reps * it.weight
    }

    val muscleCounts = workouts.groupingBy {
        it.muscleGroup
    }.eachCount()

    val favoriteMuscle = muscleCounts.maxByOrNull {
        it.value
    }?.key ?: "None"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Weekly Summary",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Total Sets: ${workouts.size}")
            Text("Total Volume: ${weeklyVolume.toInt()}")
            Text("Favorite Muscle: $favoriteMuscle")
        }
    }
}

@Composable
fun RecoveryCard(workouts: List<WorkoutEntity>) {

    val latestMuscles = workouts
        .groupBy { it.muscleGroup }
        .mapValues { entry ->
            entry.value.maxOfOrNull {
                it.dateMillis
            } ?: 0L
        }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Recovery Status",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            latestMuscles.forEach { muscle ->

                val daysAgo = ((System.currentTimeMillis() - muscle.value)
                        / (1000 * 60 * 60 * 24)).toInt()

                val recoveryText = when {
                    daysAgo >= 4 -> "Recovered"
                    daysAgo >= 2 -> "Recovering"
                    else -> "Fatigued"
                }

                val recoveryColor = when {
                    daysAgo >= 4 -> Color.Green
                    daysAgo >= 2 -> Color.Yellow
                    else -> Color.Red
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(muscle.key)

                    Text(
                        text = recoveryText,
                        color = recoveryColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


fun calculateWorkoutStreak(workouts: List<WorkoutEntity>): Int {

    if (workouts.isEmpty()) return 0

    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val uniqueDays = workouts
        .map {
            formatter.format(Date(it.dateMillis))
        }
        .distinct()
        .sortedDescending()

    var streak = 0

    val calendar = Calendar.getInstance()

    uniqueDays.forEachIndexed { index, day ->

        val expected = formatter.format(calendar.time)

        if (day == expected) {
            streak++
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
    }

    return streak
}