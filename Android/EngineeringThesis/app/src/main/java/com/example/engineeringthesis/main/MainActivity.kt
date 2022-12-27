package com.example.engineeringthesis.main


import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
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
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(fadein, fadeout)
        setContentView(R.layout.activity_main)
        val menuAnimation = AnimationUtils.loadAnimation(this, menuanimation)
        val textMenuAnimation = AnimationUtils.loadAnimation(this, textmenuanimation)


        BluetoothController.turnOff()
        BluetoothController.clearErrors()
        // CREATE ANIMATION ON START PAGE
        val backgroundImageView = findViewById<ImageView>(R.id.backgroundImageView)
        val textOnStart = findViewById<LinearLayout>(R.id.textOnStart)
        val textHome = findViewById<LinearLayout>(R.id.text_home)
        val menus = findViewById<LinearLayout>(R.id.menus)


        backgroundImageView.animate().translationY(-1900f).setDuration(2000).startDelay = 2200
        textOnStart.animate().translationY(140f).alpha(0f).setDuration(2000).startDelay = 1500
        menus.startAnimation(menuAnimation)
        textHome.startAnimation(textMenuAnimation)

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
        val buttonInsoles = findViewById<Button>(R.id.check_your_device)
        buttonInsoles.setOnClickListener {
            val intent = Intent(this, InsoleChartsManager::class.java)
            startActivity(intent)
        }

        val buttonSettings = findViewById<Button>(R.id.settings)
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsView::class.java)
            startActivity(intent)
        }

        val oldRunsButton = findViewById<Button>(R.id.oldRunsButton)
        oldRunsButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(fadein_longer, fadeout_longer, fadein, fadeout)
                .replace(R.id.wholeLayout, RunsView()).addToBackStack("TAG").commit()
        }

        val buttonStatistic = findViewById<Button>(R.id.statisticButton)
        buttonStatistic.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(fadein, fadeout, fadein, fadeout)
                .replace(R.id.wholeLayout, StatisticFragment()).addToBackStack("TAG").commit()
        }

        val buttonTracking = findViewById<Button>(R.id.trackingButton)
        buttonTracking.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(slide_right, slide_left, slide_right, slide_left)
                .replace(R.id.wholeLayout, TrackingView()).addToBackStack("TAG").commit()
        }
    }
}
