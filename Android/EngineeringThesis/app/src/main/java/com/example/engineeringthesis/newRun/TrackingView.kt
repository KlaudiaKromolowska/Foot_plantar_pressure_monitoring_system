package com.example.engineeringthesis.newRun

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.engineeringthesis.R
import com.example.engineeringthesis.chart.BluetoothController
import com.example.engineeringthesis.database.Run
import com.example.engineeringthesis.main.MainMenu
import com.example.engineeringthesis.oldRuns.RunsViewModel
import com.example.engineeringthesis.others.Constants
import com.example.engineeringthesis.others.Constants.EVENT_PAUSE_USING_SERVICE
import com.example.engineeringthesis.others.Constants.EVENT_START_OR_RESUME_USING_SERVICE
import com.example.engineeringthesis.others.Constants.EVENT_STOP_USING_SERVICE
import com.example.engineeringthesis.others.Constants.MAP_ZOOM
import com.example.engineeringthesis.others.Constants.POLYLINE_COLOR
import com.example.engineeringthesis.others.Constants.POLYLINE_WIDTH
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToLong

@AndroidEntryPoint
class TrackingView : Fragment(R.layout.fragment_tracking) {
    private val viewModelModel: RunsViewModel by viewModels()
    private var menu: Menu? = null
    private var map: GoogleMap? = null

    private var isTracking = false
    private var allPathPoints = mutableListOf<Polyline>()
    private var nowTimeInMillis = 0L
    private var tableAvgRight = mutableListOf<Float>()
    private var tableAvgLeft = mutableListOf<Float>()


    @set:Inject
    var weight = 70f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)


        nowTimeInMillis = 0L


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        TrackingService.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                loadingPanel.visibility = VISIBLE
            } else
                loadingPanel.visibility = GONE
        })
        googleMapView.onCreate(savedInstanceState)
        buttonToggleRun.setOnClickListener {
            toggleRun()
        }

        buttonFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }
        googleMapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
        buttonCancel.setOnClickListener {
            showCancelTrackingDialog()
        }
        buttonCancel.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.icon_delete,
            0,
            0,
            0
        )
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && nowTimeInMillis > 0L) {
            buttonToggleRun.text = getString(R.string.start)
            buttonFinishRun.visibility = VISIBLE
            buttonCancel.visibility = VISIBLE

        } else if (isTracking) {
            buttonToggleRun.text = getString(R.string.stop)
            menu?.getItem(0)?.isVisible = true
            buttonFinishRun.visibility = GONE
            buttonCancel.visibility = GONE
        }

    }

    private fun subscribeToObservers() {

        TrackingService.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                loadingPanel.visibility = VISIBLE
            } else
                loadingPanel.visibility = GONE
        })
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })

        TrackingService.allPathPoints.observe(viewLifecycleOwner, {
            allPathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, {
            nowTimeInMillis = it
            val formattedTime = Tracking.getFormattedStoppedWatchTime(nowTimeInMillis, true)
            viewTimer.text = formattedTime
        })

        BluetoothController.controllerDataRight.observe(viewLifecycleOwner, {
            var valueTable = (it.average() * 10).toInt() / 10.toFloat() - 38
            if (valueTable < 0.0) valueTable = 0.0F
            if (isTracking) tableAvgRight.add(valueTable)
        })
        BluetoothController.controllerDataLeft.observe(viewLifecycleOwner, {
            var valueTable = (it.average() * 10).toInt() / 10.toFloat() - 38
            if (valueTable < 0.0) valueTable = 0.0F
            if (isTracking) tableAvgLeft.add(valueTable)
        })
        BluetoothController.errors.observe(viewLifecycleOwner, ::receiveError)
    }

    private fun receiveError(isError: Boolean) {
        val dev = BluetoothController.bAdapter.bondedDevices
        if (!dev.any { it.address == Constants.myDeviceLeft.toString() } || !dev.any { it.address == Constants.myDeviceRight.toString() }) {
            showDialog()
        }
        if (isError && isTracking) {
            showDialog()
        }

    }

    private fun showDialog() {
        val message = SpannableString("Spróbuj połączyć ponownie")
        message.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            message.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val dialogTracking = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.error_bt_header))
            .setMessage(getString(R.string.error_bt_dialog))
            .setPositiveButton(getString(R.string.save_your_run_dialog)) { _, _ ->
                endRunAndSaveToDb()
            }
            .setNegativeButton(getString(R.string.cancel_run_icon)) { _, _ ->
                showCancelTrackingDialog()
            }
            .setNeutralButton(message) { _, _ ->
                BluetoothController.turnOff()
                Thread.sleep(500)
                BluetoothController.turnOn()
            }
            .create()

        val dialogNoTracking =
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(getString(R.string.error_bt_header))
                .setMessage(getString(R.string.error_bt_dialog))
                .setNeutralButton(message) { _, _ ->
                    BluetoothController.turnOff()
                    Thread.sleep(500)
                    BluetoothController.turnOn()
                }
                .create()
        if (isTracking) {
            dialogTracking.show()
        } else {
            dialogNoTracking.show()
        }

    }

    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(EVENT_PAUSE_USING_SERVICE)
        } else {
            loadingPanel.visibility = VISIBLE
            sendCommandToService(EVENT_START_OR_RESUME_USING_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbr_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (nowTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.cancel_run_header))
            .setMessage(getString(R.string.cancel_run_dialog))
            .setIcon(R.drawable.icon_delete)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                stopRun()
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }


    private fun stopRun() {
        sendCommandToService(EVENT_STOP_USING_SERVICE)
        Thread.sleep(300)
        val intent = Intent(context, MainMenu::class.java)
        startActivity(intent)

    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in allPathPoints) {
            for (position in polyline) {
                bounds.include(position)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                googleMapView.width,
                googleMapView.height,
                (googleMapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in allPathPoints) {
                distanceInMeters += Tracking.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                ((distanceInMeters / 1000f) / (nowTimeInMillis / 1000f / 60 / 60) * 10).roundToLong() / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val avgRight = (((tableAvgRight.average() * 10).toInt()) / 10).toFloat()
            var maxRight = tableAvgRight.maxOrNull()
            if (maxRight == null) maxRight = 0.0F else maxRight.toFloat()
            val avgLeft = (((tableAvgLeft.average() * 10).toInt()) / 10).toFloat()
            var maxLeft = tableAvgLeft.maxOrNull()
            if (maxLeft == null) maxLeft = 0.0F else maxLeft.toFloat()
            val run = Run(
                bmp,
                dateTimestamp,
                nowTimeInMillis,
                avgSpeed,
                distanceInMeters,
                caloriesBurned,
                avgLeft,
                maxLeft,
                avgRight,
                maxRight
            )

            viewModelModel.insertRun(run)
            Toast.makeText(context, getString(R.string.run_is_saved), Toast.LENGTH_SHORT).show()
            stopRun()
        }
    }

    private fun moveCameraToUser() {
        if (allPathPoints.isNotEmpty() && allPathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    allPathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines() {
        for (polyline in allPathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (allPathPoints.isNotEmpty() && allPathPoints.last().size > 1) {
            val preLastLatLng = allPathPoints.last()[allPathPoints.last().size - 2]
            val lastLatLng = allPathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    override fun onResume() {
        super.onResume()
        googleMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        googleMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        googleMapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        googleMapView.onPause()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        googleMapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        googleMapView.onSaveInstanceState(outState)
    }

    private fun requestPermissions() {
        if (Tracking.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.user_permission_location_text),
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.user_permission_location_text),
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }
}