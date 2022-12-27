package com.example.engineeringthesis.oldRuns

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.engineeringthesis.R
import com.example.engineeringthesis.database.Run
import com.example.engineeringthesis.newRun.Tracking
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*


class RunsAdapter : RecyclerView.Adapter<RunsAdapter.RunViewHolder>() {
    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)
    fun getRunEntry(pos: Int): Run = differ.currentList[pos]
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_run,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(run.mapImage).into(itemRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            viewDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.avgSpeedInKMH} km/h"
            viewAvgSpeed.text = avgSpeed

            val distanceInKm = "${run.distanceInMeters / 1000f} km"
            viewDistance.text = distanceInKm

            viewTime.text = Tracking.getFormattedStoppedWatchTime(run.timeInMillis)

            val caloriesBurned = "${run.caloriesBurned} kcal"
            viewCalories.text = caloriesBurned

            val avgLeft = "${run.avgLeft} %"
            viewLeftAvg.text = avgLeft

            val maxLeft = "${run.maxLeft} %"
            viewLeftMax.text = maxLeft

            val avgRight = "${run.avgRight} %"
            viewRightAvg.text = avgRight

            val maxRight = "${run.maxRight} %"
            viewRightMax.text = maxRight
        }
    }
}