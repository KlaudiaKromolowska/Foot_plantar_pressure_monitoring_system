package com.example.engineeringthesis.others

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.graphics.Color

object Constants {
    const val RUNNING_MAIN_DATABASE_NAME = "running_db"

    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val NOTIFICATION_ID_CHANNEL = "tracking_channel"
    const val NOTIFICATION_NAME_CHANNEL = "Tracking"
    const val NOTIFICATION_ID = 1

    const val NAME_SHARED_PREFERENCES = "sharedPref"
    var KEY_NAME = "KEY_NAME"
    var KEY_WEIGHT = "KEY_WEIGHT"
    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val THE_FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_WIDTH = 10f
    const val POLYLINE_COLOR = Color.RED
    const val MAP_ZOOM = 15f

    const val EVENT_START_OR_RESUME_USING_SERVICE = "ACTION_START_ON_RESUME_SERVICE"
    const val EVENT_PAUSE_USING_SERVICE = "EVENT_PAUSE_USING_SERVICE"
    const val EVENT_STOP_USING_SERVICE = "EVENT_STOP_USING_SERVICE"
    const val EVENT_SHOW_TRACKING_FRAGMENT = "EVENT_SHOW_TRACKING_FRAGMENT"

    const val TIMER_UPDATE_INTERVAL = 50L
    const val originX = 0f
    const val originY = 0f
    const val cellSide = 40f
    const val numberOfRow = 21
    const val numberOfCol = 9
    lateinit var colors: Array<String>
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var myDeviceRight: BluetoothDevice = bluetoothAdapter.getRemoteDevice("20:18:07:13:54:9C")
    var myDeviceLeft: BluetoothDevice = bluetoothAdapter.getRemoteDevice("00:20:12:08:B8:E6")
}