package com.example.engineeringthesis.statisticRuns

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.engineeringthesis.R
import com.example.engineeringthesis.newRun.Tracking
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistic.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistic) {
    private val viewModel: StatisticViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart(avgSpeedBarChart)
        setupBarChart(RightFootBar)
        setupBarChart(LeftFootBar)
    }

    private fun setupBarChart(chart: BarChart) {
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            setDrawGridLines(false)
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            textSize = 16F
            typeface = context?.let { ResourcesCompat.getFont(it, R.font.alegreya_sans_sc_medium) }
        }
        chart.description.isEnabled = false
        chart.axisLeft.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            textSize = 12F
            setDrawGridLines(false)
            typeface = context?.let { ResourcesCompat.getFont(it, R.font.alegreya_sans_sc_medium) }
        }
        chart.axisRight.apply {
            typeface = context?.let {
                ResourcesCompat.getFont(it, R.font.alegreya_sans_sc_medium)
            }
        }
        chart.legend.apply {
            typeface = context?.let { ResourcesCompat.getFont(it, R.font.alegreya_sans_sc_medium) }
        }
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, {
            it?.let {
                val totalTimeRun = Tracking.getFormattedStoppedWatchTime(it)
                viewTotalTime.text = totalTimeRun
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, {
            it?.let {
                val avgSpeed = (it * 20f).roundToInt() / 20f
                val avgSpeedString = "$avgSpeed km/h"
                viewAverageSpeed.text = avgSpeedString
            }
        })


        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, {
            it?.let {
                val totalCalories = "$it kcal"
                viewTotalCalories.text = totalCalories
            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, {
            it?.let {
                val distance = it / 1000f
                val totalDistance = (distance * 10f).roundToLong() / 10f
                val totalDistanceString = "$totalDistance km"
                viewTotalDistance.text = totalDistanceString
            }
        })

        viewModel.runsSortedDate.observe(viewLifecycleOwner, { it ->
            it?.let { it ->
                val allAvgSpeed = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
                val barDataSet = BarDataSet(allAvgSpeed, getString(R.string.average_speed)).apply {
                    valueTextColor = Color.BLACK
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                    valueTypeface =
                        context?.let { ResourcesCompat.getFont(it, R.font.alegreya_sans_sc_medium) }
                }
                avgSpeedBarChart.data = BarData(barDataSet)
                avgSpeedBarChart.invalidate()

                val allAvgLeft = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgLeft) }
                val barDataSetLeft =
                    BarDataSet(allAvgLeft, getString(R.string.average_pressure)).apply {
                        valueTextColor = Color.BLACK
                        color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                        valueTypeface = context?.let {
                            ResourcesCompat.getFont(
                                it,
                                R.font.alegreya_sans_sc_medium
                            )
                        }
                    }
                LeftFootBar.data = BarData(barDataSetLeft)
                LeftFootBar.invalidate()

                val allAvgRight = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgRight) }
                val barDataSetRight =
                    BarDataSet(allAvgRight, getString(R.string.average_pressure)).apply {
                        valueTextColor = Color.BLACK
                        color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                        valueTypeface = context?.let {
                            ResourcesCompat.getFont(
                                it,
                                R.font.alegreya_sans_sc_medium
                            )
                        }
                    }
                RightFootBar.data = BarData(barDataSetRight)
                RightFootBar.invalidate()
            }
        })

        viewModel.totalAvgLeft.observe(viewLifecycleOwner, {
            it?.let {
                val avgLeft = (it * 20f).roundToInt() / 20f
                val avgLeftString = "$avgLeft %"
                viewLeftFootAvg.text = avgLeftString
            }
        })

        viewModel.totalMaxLeft.observe(viewLifecycleOwner, {
            it?.let {
                val maxLeft = (it * 20f).roundToInt() / 20f
                val maxLeftString = "$maxLeft %"
                viewLeftFootMax.text = maxLeftString
            }
        })

        viewModel.totalAvgRight.observe(viewLifecycleOwner, {
            it?.let {
                val avgRight = (it * 20f).roundToInt() / 20f
                val avgRightString = "$avgRight %"
                viewRightFootAvg.text = avgRightString
            }
        })

        viewModel.totalMaxRight.observe(viewLifecycleOwner, {
            it?.let {
                val maxRight = (it * 20f).roundToInt() / 20f
                val maxRightString = "$maxRight %"
                viewRightFootMax.text = maxRightString
            }
        })
    }
}