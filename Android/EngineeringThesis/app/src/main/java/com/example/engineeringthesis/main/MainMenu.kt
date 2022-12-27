package com.example.engineeringthesis.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.engineeringthesis.R
import com.example.engineeringthesis.R.anim.*
import com.example.engineeringthesis.chart.BluetoothController
import com.example.engineeringthesis.chart.InsoleChartsManager
import com.example.engineeringthesis.newRun.TrackingView
import com.example.engineeringthesis.oldRuns.RunsView
import com.example.engineeringthesis.others.Constants
import com.example.engineeringthesis.statisticRuns.StatisticFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(fadein, fadeout)
        setContentView(R.layout.main_menu)
        BluetoothController.turnOff()
        BluetoothController.clearErrors()


        Constants.colors = arrayOf(
            "#ffe9d4",
            "#ffbe7e",
            "#ffbf2c",
            "#ff8d1a",
            "#ff6800",
            "#e83700",
            "#c60a00",
            "#930000",
            "#670000",
            "#3d0000"
        )

        val buttonInsoles = findViewById<Button>(R.id.check_your_device_menu)
        buttonInsoles.setOnClickListener {
            val intent = Intent(this, InsoleChartsManager::class.java)
            startActivity(intent)
        }

        val buttonSettings = findViewById<Button>(R.id.settings_menu)
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsView::class.java)
            startActivity(intent)
        }
        val oldRunsButton = findViewById<Button>(R.id.oldRunsButton_menu)
        oldRunsButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(fadein_longer, fadeout_longer, fadein, fadeout)
                .replace(R.id.wholeLayout, RunsView()).addToBackStack("TAG").commit()
        }
        val buttonStatistic = findViewById<Button>(R.id.statisticButton_menu)
        buttonStatistic.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(fadein, fadeout, fadein, fadeout)
                .replace(R.id.wholeLayout, StatisticFragment()).addToBackStack("TAG").commit()
        }
        val buttonTracking = findViewById<Button>(R.id.trackingButton_menu)
        buttonTracking.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(slide_right, slide_left, slide_right, slide_left)
                .replace(R.id.wholeLayout, TrackingView()).addToBackStack("TAG").commit()

        }
    }
}
