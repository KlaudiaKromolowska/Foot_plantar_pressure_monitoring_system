package com.example.engineeringthesis.oldRuns

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engineeringthesis.database.Run
import com.example.engineeringthesis.database.RunDaoConstructor
import kotlinx.coroutines.launch

class RunsViewModel @ViewModelInject constructor(
    private val runDaoConstructor: RunDaoConstructor,
) : ViewModel() {

    private val runsSortedByCaloriesBurned = runDaoConstructor.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMills = runDaoConstructor.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = runDaoConstructor.getAllRunsSortedByAvgSpeed()
    private val runsSortedByDate = runDaoConstructor.getAllRunsSortedByDate()
    private val runsSortedByDistance = runDaoConstructor.getAllRunsSortedByDistance()
    private val runsSortedByAvgLeft = runDaoConstructor.getAllRunsSortedByAvgLeft()
    private val runsSortedByMaxLeft = runDaoConstructor.getAllRunsSortedByMaxLeft()
    private val runsSortedByAvgRight = runDaoConstructor.getAllRunsSortedByAvgRight()
    private val runsSortedByMaxRight = runDaoConstructor.getAllRunsSortedByMaxRight()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortDataType.DATE

    init {
        runs.addSource(runsSortedByDate) { result ->
            if (sortType == SortDataType.DATE) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByCaloriesBurned) { result ->
            if (sortType == SortDataType.CALORIES_BURNED) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByTimeInMills) { result ->
            if (sortType == SortDataType.RUNNING_TIME) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByAvgSpeed) { result ->
            if (sortType == SortDataType.AVG_SPEED) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByDistance) { result ->
            if (sortType == SortDataType.DISTANCE) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByAvgLeft) { result ->
            if (sortType == SortDataType.AVG_LEFT) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByMaxLeft) { result ->
            if (sortType == SortDataType.MAX_LEFT) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByAvgRight) { result ->
            if (sortType == SortDataType.AVG_RIGHT) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByMaxRight) { result ->
            if (sortType == SortDataType.MAX_RIGHT) {
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortDataType) = when (sortType) {
        SortDataType.DATE -> runsSortedByDate.value?.let { runs.value = it }
        SortDataType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
        SortDataType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let { runs.value = it }
        SortDataType.RUNNING_TIME -> runsSortedByTimeInMills.value?.let { runs.value = it }
        SortDataType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { runs.value = it }
        SortDataType.AVG_LEFT -> runsSortedByAvgLeft.value?.let { runs.value = it }
        SortDataType.MAX_LEFT -> runsSortedByMaxLeft.value?.let { runs.value = it }
        SortDataType.AVG_RIGHT -> runsSortedByAvgRight.value?.let { runs.value = it }
        SortDataType.MAX_RIGHT -> runsSortedByMaxRight.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        runDaoConstructor.insertRun(run)
    }

    fun deleteRun(run: Run) = viewModelScope.launch {
        runDaoConstructor.deleteRun(run)
    }


}