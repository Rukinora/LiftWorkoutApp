package com.example.lift_workoutapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lift_workoutapp.database.WorkoutDatabase
import com.example.lift_workoutapp.database.WorkoutEntity
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import java.util.Calendar
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlin.math.ceil

@Composable
fun StatsScreen(modifier: Modifier = Modifier) {
    //Set Value to True = real app data
    //Value = False, uses fake data
    var useRealData by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dao = remember {
        WorkoutDatabase.getDatabase(context).workoutDao()
    }

    val allWorkouts by dao.getAllWorkoutsFlow()
        .collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf("All Time") }
    var selectedTab by remember { mutableStateOf("Muscle Group") }
    var selectedMuscle by remember { mutableStateOf("Arms") }

    val filteredWorkouts = filterWorkouts(allWorkouts, selectedFilter)

    val muscleGroups = listOf("Arms", "Legs", "Back", "Chest", "Core")

    val radarStats = muscleGroups.associateWith { group ->
        filteredWorkouts
            .filter { it.muscleGroup == group }
            .sumOf { it.reps * it.weight }
            .toInt()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Stats",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            listOf("Week", "Month", "Year", "All Time").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RadarStatsChart(
            values = radarStats,
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = if (selectedTab == "Muscle Group") 0 else 1) {
            Tab(
                selected = selectedTab == "Muscle Group",
                onClick = { selectedTab = "Muscle Group" },
                text = { Text("Muscle Group") }
            )

            Tab(
                selected = selectedTab == "Exercise Group",
                onClick = { selectedTab = "Exercise Group" },
                text = { Text("Exercise Group") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == "Muscle Group") {
            Row {
                muscleGroups.forEach { group ->
                    FilterChip(
                        selected = selectedMuscle == group,
                        onClick = { selectedMuscle = group },
                        label = { Text(group) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$selectedMuscle Strength Progress",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            MuscleVolumeLineChart(
                workouts = if (useRealData) {
                    filteredWorkouts.filter { it.muscleGroup == selectedMuscle }
                } else {
                    addTestWorkoutData(
                        filteredWorkouts.filter { it.muscleGroup == selectedMuscle }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )
        } else {
            Text("Exercise Group stats coming next")
        }
    }
}

@Composable
fun RadarStatsChart(
    values: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val labels = values.keys.toList()
    val maxValue = (values.values.maxOrNull() ?: 1).coerceAtLeast(1)

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 3.2f
        val labelRadius = radius + 55f
        val angleStep = 2 * PI / labels.size

        // grid rings
        val levels = 7
        for (level in 1..levels) {
            val levelRadius = radius * level / levels.toFloat()

            val ringPoints = labels.mapIndexed { index, _ ->
                val angle = angleStep * index - PI / 2
                Offset(
                    center.x + cos(angle).toFloat() * levelRadius,
                    center.y + sin(angle).toFloat() * levelRadius
                )
            }

            val path = Path().apply {
                moveTo(ringPoints[0].x, ringPoints[0].y)
                ringPoints.drop(1).forEach { lineTo(it.x, it.y) }
                close()
            }

            drawPath(
                path = path,
                color = Color.Gray.copy(alpha = 0.25f),
                style = Stroke(width = 2f)
            )
        }

        val outerPoints = labels.mapIndexed { index, _ ->
            val angle = angleStep * index - PI / 2
            Offset(
                center.x + cos(angle).toFloat() * radius,
                center.y + sin(angle).toFloat() * radius
            )
        }

        outerPoints.forEach {
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = center,
                end = it,
                strokeWidth = 2f
            )
        }

        val statPoints = labels.mapIndexed { index, label ->
            val value = values[label] ?: 0
            val percent = value.toFloat() / maxValue.toFloat()
            val angle = angleStep * index - PI / 2

            Offset(
                center.x + cos(angle).toFloat() * radius * percent,
                center.y + sin(angle).toFloat() * radius * percent
            )
        }

        val statPath = Path().apply {
            moveTo(statPoints[0].x, statPoints[0].y)
            statPoints.drop(1).forEach { lineTo(it.x, it.y) }
            close()
        }

        drawPath(
            path = statPath,
            color = Color(0xFFFFC94D).copy(alpha = 0.45f)
        )

        drawPath(
            path = statPath,
            color = Color(0xFFFFC94D),
            style = Stroke(width = 5f)
        )

        labels.forEachIndexed { index, label ->
            val angle = angleStep * index - PI / 2
            val labelX = center.x + cos(angle).toFloat() * labelRadius
            val labelY = center.y + sin(angle).toFloat() * labelRadius

            drawContext.canvas.nativeCanvas.drawText(
                label,
                labelX,
                labelY,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 34f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }
    }
}

@Composable
fun MuscleVolumeLineChart(
    workouts: List<WorkoutEntity>,
    modifier: Modifier = Modifier
) {
    val sessionVolumes = workouts
        .groupBy { it.dateMillis }
        .mapValues { entry ->
            entry.value.sumOf { it.reps * it.weight }
        }
        .toList()
        .sortedBy { it.first }

    Canvas(modifier = modifier) {

        if (sessionVolumes.isEmpty()) return@Canvas

        val paddingLeft = 70f
        val paddingBottom = 50f
        val paddingTop = 30f

        val graphHeight = size.height - paddingTop - paddingBottom
        val graphWidth = size.width - paddingLeft - 20f

        val maxVolume = sessionVolumes.maxOf { it.second }
        val chartMax = (ceil(maxVolume / 1000.0) * 1000).coerceAtLeast(1000.0)

        // 🔥 Horizontal grid lines
        val lines = 5
        for (i in 0..lines) {
            val y = paddingTop + graphHeight * i / lines
            val value = chartMax - (chartMax * i / lines)

            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(paddingLeft, y),
                end = Offset(size.width, y),
                strokeWidth = 2f
            )

            drawContext.canvas.nativeCanvas.drawText(
                "${(value / 1000).toInt()}k",
                paddingLeft - 10f,
                y + 10f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        val points = sessionVolumes.mapIndexed { index, item ->
            val x = paddingLeft + index * (graphWidth / (sessionVolumes.size - 1).coerceAtLeast(1))

            val percent = (item.second / chartMax).toFloat()
            val y = paddingTop + graphHeight - (percent * graphHeight)

            Offset(x, y)
        }

        // 🔥 Smooth curve
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points[0].x, points[0].y)

                for (i in 0 until points.lastIndex) {
                    val current = points[i]
                    val next = points[i + 1]

                    val midX = (current.x + next.x) / 2f

                    cubicTo(
                        midX,
                        current.y,
                        midX,
                        next.y,
                        next.x,
                        next.y
                    )
                }
            }
        }

        drawPath(
            path = path,
            color = Color(0xFFFFC94D),
            style = Stroke(width = 5f)
        )

        points.forEach {
            drawCircle(
                color = Color(0xFFFFC94D),
                radius = 8f,
                center = it
            )
        }
    }
}


fun filterWorkouts(
    workouts: List<WorkoutEntity>,
    filter: String
): List<WorkoutEntity> {
    val startTime = when (filter) {
        "Week" -> getStartOfWeek()
        "Month" -> getStartOfMonth()
        "Year" -> getStartOfYear()
        else -> 0L
    }

    return workouts.filter { it.dateMillis >= startTime }
}

fun getStartOfWeek(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun getStartOfMonth(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun getStartOfYear(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_YEAR, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun addTestWorkoutData(realData: List<WorkoutEntity>): List<WorkoutEntity> {
    val calendar = Calendar.getInstance()
    val fakeData = mutableListOf<WorkoutEntity>()

    val testVolumes = listOf(
        Triple(-180, "Arms", 1200.0),
        Triple(-120, "Arms", 1800.0),
        Triple(-60, "Arms", 2300.0),
        Triple(-14, "Arms", 2000.0),
        Triple(-7, "Arms", 2600.0),
        Triple(0, "Arms", 3000.0),

        Triple(-180, "Chest", 2000.0),
        Triple(-90, "Chest", 2800.0),
        Triple(-30, "Chest", 3500.0),
        Triple(0, "Chest", 4200.0),

        Triple(-180, "Legs", 3000.0),
        Triple(-90, "Legs", 4200.0),
        Triple(-30, "Legs", 3900.0),
        Triple(0, "Legs", 4800.0)
    )

    testVolumes.forEachIndexed { index, item ->
        val daysAgo = item.first
        val group = item.second
        val volume = item.third

        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.DAY_OF_YEAR, daysAgo)

        fakeData.add(
            WorkoutEntity(
                id = -index,
                dateMillis = calendar.timeInMillis,
                exerciseName = "Test $group",
                muscleGroup = group,
                setNumber = 1,
                reps = 10,
                weight = volume / 10.0
            )
        )
    }

    return realData + fakeData
}