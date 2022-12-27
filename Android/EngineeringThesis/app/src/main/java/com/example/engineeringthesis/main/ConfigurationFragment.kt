package com.example.engineeringthesis.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.engineeringthesis.R
import com.example.engineeringthesis.others.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.engineeringthesis.others.Constants.KEY_NAME
import com.example.engineeringthesis.others.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setup.*
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ConfigurationFragment : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        val sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        val prevStarted: Boolean =
            sharedPreferences.getBoolean(getString(R.string.is_accessed), false)
        if (!prevStarted) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(getString(R.string.is_accessed), java.lang.Boolean.TRUE)
            editor.apply()
            setContentView(R.layout.fragment_setup)

            val saveButton = findViewById<Button>(R.id.buttonSaveConfiguration)
            saveButton.setOnClickListener {
                val ifPersonalDataCorrect = writePersonalDataToSharedPreferences()
                if (ifPersonalDataCorrect) moveToMenu()
            }
        } else moveToMainActivity()
    }

    private fun writePersonalDataToSharedPreferences(): Boolean {
        val name = userName.text.toString().capitalize(Locale.ROOT)
        val weight = userWeight.text.toString()
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
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        val toolbarText = "Świetnie, zaczynamy $name!"
        Toast.makeText(this, toolbarText, Toast.LENGTH_SHORT).show()
        return true
    }

    private fun moveToMenu() {
        val intent = Intent(this, MainMenu::class.java)
        startActivity(intent)
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}