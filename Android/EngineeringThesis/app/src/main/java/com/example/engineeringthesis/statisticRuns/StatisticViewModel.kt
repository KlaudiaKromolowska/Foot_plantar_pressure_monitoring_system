package com.example.engineeringthesis.statisticRuns

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.engineeringthesis.database.RunDaoConstructor

class StatisticViewModel @ViewModelInject constructor(
    runDaoConstructor: RunDaoConstructor,
) : ViewModel() {

    val totalTimeRun = runDaoConstructor.getTotalTimeInMillis()
    val totalCaloriesBurned = runDaoConstructor.getTotalCaloriesBurned()
    val totalAvgSpeed = runDaoConstructor.getTotalAvgSpeed()
    val runsSortedDate = runDaoConstructor.getAllRunsSortedByDate()
    val totalDistance = runDaoConstructor.getTotalDistance()
    val totalAvgLeft = runDaoConstructor.getTotalAvgLeft()
    val totalMaxLeft = runDaoConstructor.getTotalMaxLeft()
    val totalAvgRight = runDaoConstructor.getTotalAvgRight()
    val totalMaxRight = runDaoConstructor.getTotalMaxRight()

}