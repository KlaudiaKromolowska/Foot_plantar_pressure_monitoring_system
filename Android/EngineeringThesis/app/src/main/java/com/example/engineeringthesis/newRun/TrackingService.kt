package com.example.engineeringthesis.newRun

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.engineeringthesis.R
import com.example.engineeringthesis.chart.BluetoothController
import com.example.engineeringthesis.others.Constants.EVENT_PAUSE_USING_SERVICE
import com.example.engineeringthesis.others.Constants.EVENT_START_OR_RESUME_USING_SERVICE
import com.example.engineeringthesis.others.Constants.EVENT_STOP_USING_SERVICE
import com.example.engineeringthesis.others.Constants.LOCATION_UPDATE_INTERVAL
import com.example.engineeringthesis.others.Constants.NOTIFICATION_ID
import com.example.engineeringthesis.others.Constants.NOTIFICATION_ID_CHANNEL
import com.example.engineeringthesis.others.Constants.NOTIFICATION_NAME_CHANNEL
import com.example.engineeringthesis.others.Constants.THE_FASTEST_LOCATION_INTERVAL
import com.example.engineeringthesis.others.Constants.TIMER_UPDATE_INTERVAL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private var isFirstRun = true
    private var serviceKilled = false
    private val timeRunInSeconds = MutableLiveData<Long>()


    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var curNotificationBuilder: NotificationCompat.Builder
    private var isTimberEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    companion object {
        val isLoading = MutableLiveData<Boolean>()
        val timeRunInMillis = MutableLiveData<Long>()
        val allPathPoints = MutableLiveData<Polylines>()
        val isTracking = MutableLiveData<Boolean>()

    }

    private fun postInitialValues() {
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
        isTracking.postValue(false)
        allPathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    private fun killService() {
        isFirstRun = true
        serviceKilled = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                EVENT_START_OR_RESUME_USING_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        startTimer()
                    }
                }
                EVENT_PAUSE_USING_SERVICE -> {
                    pauseService()
                }
                EVENT_STOP_USING_SERVICE -> {
                    killService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        startTimer()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        timeRunInSeconds.observe(this, {
            if (!serviceKilled) {
                val notification = curNotificationBuilder
                    .setContentText(Tracking.getFormattedStoppedWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    private fun startTimer() {
        BluetoothController.turnOn()
        isLoading.postValue(false)
        addEmptyPolyline()
        isTracking.postValue(true)
        isTimberEnabled = true
        var isBluetoothOn = true
        CoroutineScope(Dispatchers.Main).launch {
            timeStarted = System.currentTimeMillis()
            while (isTracking.value!!) {
                if (!isBluetoothOn) {
                    BluetoothController.turnOn()
                    isBluetoothOn = true
                }
                lapTime = System.currentTimeMillis() - timeStarted

                timeRunInMillis.postValue(timeRun + lapTime)

                if (lastSecondTimestamp + 1000L <= timeRunInMillis.value!!) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            BluetoothController.turnOff()
            isBluetoothOn = false
            timeRun += lapTime
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("new location: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimberEnabled = false
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText =
            if (isTracking) getString(R.string.stop_notification) else getString(R.string.resume_notification)
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = EVENT_PAUSE_USING_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = EVENT_START_OR_RESUME_USING_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if (!serviceKilled) {
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.icon_pause, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
    }

    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (Tracking.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = THE_FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            allPathPoints.value?.apply {
                last().add(pos)
                allPathPoints.postValue(this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_ID_CHANNEL,
            NOTIFICATION_NAME_CHANNEL,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun addEmptyPolyline() = allPathPoints.value?.apply {
        add(mutableListOf())
        allPathPoints.postValue(this)
    } ?: allPathPoints.postValue(mutableListOf(mutableListOf()))
}