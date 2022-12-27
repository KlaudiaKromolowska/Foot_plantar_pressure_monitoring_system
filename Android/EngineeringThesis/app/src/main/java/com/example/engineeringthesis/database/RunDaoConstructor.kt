package com.example.engineeringthesis.database

import javax.inject.Inject

class RunDaoConstructor @Inject constructor(private val runDao: RunDAO) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run)
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)
    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()
    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()
    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()
    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()
    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
    fun getAllRunsSortedByAvgLeft() = runDao.getAllRunsSortedByAvgLeft()
    fun getAllRunsSortedByMaxLeft() = runDao.getAllRunsSortedByMaxLeft()
    fun getAllRunsSortedByAvgRight() = runDao.getAllRunsSortedByAvgRight()
    fun getAllRunsSortedByMaxRight() = runDao.getAllRunsSortedByMaxRight()
    fun getTotalAvgLeft() = runDao.getTotalAvgLeft()
    fun getTotalMaxLeft() = runDao.getTotalMaxLeft()
    fun getTotalAvgRight() = runDao.getTotalAvgRight()
    fun getTotalMaxRight() = runDao.getTotalMaxRight()
}