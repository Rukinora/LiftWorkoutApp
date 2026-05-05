package com.example.lift_workoutapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts ORDER BY dateMillis ASC")
    fun getAllWorkoutsFlow(): Flow<List<WorkoutEntity>>
}