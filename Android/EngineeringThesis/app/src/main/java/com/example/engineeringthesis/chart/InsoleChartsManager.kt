package com.example.engineeringthesis.chart

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.engineeringthesis.R
import com.example.engineeringthesis.main.MainMenu
import com.example.engineeringthesis.others.Constants


class InsoleChartsManager : AppCompatActivity() {
    private lateinit var rightFootImage: RightFootView
    private lateinit var leftFootImage: LeftFootView
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        setContentView(R.layout.checkyourinsoles)
        button = findViewById(R.id.button_start_insoles)
        rightFootImage = findViewById(R.id.right_view)
        leftFootImage = findViewById(R.id.left_view)

        BluetoothController.errors.observe(this, ::receiveError)
        button.setOnClickListener {
            if (button.text == "START") {
                button.text = getString(R.string.stop)
                leftFootImage.visibility = INVISIBLE
                rightFootImage.visibility = INVISIBLE
                BluetoothController.turnOn()
            } else if (button.text == "STOP") {
                button.text = getString(R.string.start)
                BluetoothController.turnOff()
            }

            BluetoothController.controllerDataRight.observe(this, ::fillsRightFootView)
            BluetoothController.controllerDataLeft.observe(this, ::fillsLeftFootView)


        }
    }

    private fun receiveError(isError: Boolean) {
        val dev = BluetoothController.bAdapter.bondedDevices
        if (!dev.any { it.address == Constants.myDeviceLeft.toString() } || !dev.any { it.address == Constants.myDeviceRight.toString() }) {
            showDialog()
        }
        if (isError && button.text == "STOP") {
            BluetoothController.clearErrors()
            showDialog()
            BluetoothController.turnOff()
        }

    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val message = SpannableString(getString(R.string.understand))
        message.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            message.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setTitle(R.string.error_bt_header)
        builder.setMessage(R.string.error_bt_dialog)
        builder.setPositiveButton(message) { _, _ ->
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }
        builder.show()

    }

    private fun fillsRightFootView(bytes: ByteArray?) {
        rightFootImage.visibility = VISIBLE
        rightFootImage.upDateControllerData(bytes?.take(16)?.map {
            it.toInt() - 38
        }?.toIntArray() ?: IntArray(16))
    }

    private fun fillsLeftFootView(bytes: ByteArray?) {
        leftFootImage.visibility = VISIBLE
        leftFootImage.upDateControllerData(bytes?.take(16)?.map {
            it.toInt() - 38
        }?.toIntArray() ?: IntArray(16))
    }

}

