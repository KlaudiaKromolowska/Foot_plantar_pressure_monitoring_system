package com.example.engineeringthesis.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run(
        var mapImage: Bitmap? = null,
        var timestamp: Long = 0L,
        var timeInMillis: Long = 0L,
        var avgSpeedInKMH: Float = 0f,
        var distanceInMeters: Int = 0,
        var caloriesBurned: Int = 0,
        var avgLeft: Float = 0f,
        var maxLeft: Float = 0f,
        var avgRight: Float = 0f,
        var maxRight: Float = 0f,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
