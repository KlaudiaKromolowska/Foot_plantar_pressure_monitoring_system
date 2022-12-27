package com.example.engineeringthesis.main

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.engineeringthesis.R
import com.example.engineeringthesis.others.Constants
import com.example.engineeringthesis.others.Constants.KEY_NAME
import com.example.engineeringthesis.others.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.settings.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsView : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true
    private lateinit var switchGreen: Button
    private lateinit var switchRed: Button
    private lateinit var switchPink: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        setContentView(R.layout.settings)

        val name = sharedPreferences.getString(KEY_NAME, "")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 70f)
        userWeight2.setText(weight.toString())
        userName2.setText(name)

        val saveButton = findViewById<Button>(R.id.buttonSaveChanges)
        saveButton.setOnClickListener {
            val ifPersonalDataCorrect = writePersonalDataToSharedPreferences()
            if (ifPersonalDataCorrect) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.datas_changed_correctly),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.fill_fields_correctly),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        //COLORS
        switchGreen = findViewById(R.id.buttonGreen)
        switchPink = findViewById(R.id.buttonPink)
        switchRed = findViewById(R.id.buttonRed)
        switchGreen.setOnClickListener {
            Constants.colors = arrayOf(
                "#E0F2F1",
                "#B2DFDB",
                "#80CBC4",
                "#4DB6AC",
                "#26A69A",
                "#009688",
                "#00897B",
                "#00796B",
                "#00695C",
                "#004D40"
            )
        }

        switchPink.setOnClickListener {
            Constants.colors = arrayOf(
                "#FCE4EC",
                "#F8BBD0",
                "#F48FB1",
                "#F06292",
                "#EC407A",
                "#E91E63",
                "#D81B60",
                "#C2185B",
                "#AD1457",
                "#880E4F"
            )
        }

        switchRed.setOnClickListener {
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
        }
    }

    private fun writePersonalDataToSharedPreferences(): Boolean {
        val name = userName2.text.toString().capitalize(Locale.ROOT)
        val weight = userWeight2.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return false
        }
        if (name.length < 3) {
            Toast.makeText(this, getString(R.string.name_too_short), Toast.LENGTH_SHORT).show()
            return false
        }
        if (weight.toFloat() < 20.0) {
            Toast.makeText(this, getString(R.string.weight_too_low), Toast.LENGTH_SHORT).show()
            return false
        }
        if (weight.toFloat() > 150.0) {
            Toast.makeText(this, getString(R.string.weight_too_high), Toast.LENGTH_SHORT).show()
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true
    }
}