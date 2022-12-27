package com.example.engineeringthesis.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeed(): LiveData<Float>

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT * FROM running_table ORDER BY avgLeft DESC")
    fun getAllRunsSortedByAvgLeft(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY maxLeft DESC")
    fun getAllRunsSortedByMaxLeft(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY avgRight DESC")
    fun getAllRunsSortedByAvgRight(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY maxRight DESC")
    fun getAllRunsSortedByMaxRight(): LiveData<List<Run>>

    @Query("SELECT AVG(avgLeft) FROM running_table")
    fun getTotalAvgLeft(): LiveData<Float>

    @Query("SELECT MAX(maxLeft) FROM running_table")
    fun getTotalMaxLeft(): LiveData<Float>

    @Query("SELECT AVG(avgRight) FROM running_table")
    fun getTotalAvgRight(): LiveData<Float>

    @Query("SELECT MAX(maxRight) FROM running_table")
    fun getTotalMaxRight(): LiveData<Float>
}