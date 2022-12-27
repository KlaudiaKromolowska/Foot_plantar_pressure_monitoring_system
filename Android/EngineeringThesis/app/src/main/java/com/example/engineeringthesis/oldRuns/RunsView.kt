package com.example.engineeringthesis.oldRuns

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.engineeringthesis.R
import com.example.engineeringthesis.database.Run
import com.example.engineeringthesis.newRun.Tracking
import com.example.engineeringthesis.others.Constants.REQUEST_CODE_LOCATION_PERMISSION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class RunsView : Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {
    private val viewModelModel: RunsViewModel by viewModels()

    private lateinit var runsAdapter: RunsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        when (viewModelModel.sortType) {
            SortDataType.DATE -> selectorFilter.setSelection(0)
            SortDataType.RUNNING_TIME -> selectorFilter.setSelection(1)
            SortDataType.DISTANCE -> selectorFilter.setSelection(2)
            SortDataType.AVG_SPEED -> selectorFilter.setSelection(3)
            SortDataType.CALORIES_BURNED -> selectorFilter.setSelection(4)
            SortDataType.AVG_LEFT -> selectorFilter.setSelection(5)
            SortDataType.MAX_LEFT -> selectorFilter.setSelection(6)
            SortDataType.AVG_RIGHT -> selectorFilter.setSelection(7)
            SortDataType.MAX_RIGHT -> selectorFilter.setSelection(8)
        }

        selectorFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> viewModelModel.sortRuns(SortDataType.DATE)
                    1 -> viewModelModel.sortRuns(SortDataType.RUNNING_TIME)
                    2 -> viewModelModel.sortRuns(SortDataType.DISTANCE)
                    3 -> viewModelModel.sortRuns(SortDataType.AVG_SPEED)
                    4 -> viewModelModel.sortRuns(SortDataType.CALORIES_BURNED)
                    5 -> viewModelModel.sortRuns(SortDataType.AVG_LEFT)
                    6 -> viewModelModel.sortRuns(SortDataType.MAX_LEFT)
                    7 -> viewModelModel.sortRuns(SortDataType.AVG_RIGHT)
                    8 -> viewModelModel.sortRuns(SortDataType.MAX_RIGHT)
                }
            }
        }

        viewModelModel.runs.observe(viewLifecycleOwner, {
            runsAdapter.submitList(it)
        })

    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else requestPermissions()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun setupRecyclerView() = recyclerViewRuns.apply {
        runsAdapter = RunsAdapter()
        adapter = runsAdapter
        layoutManager = LinearLayoutManager(requireContext())
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    Timber.i("OnMove do nothing")
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val runEntry: Run = runsAdapter.getRunEntry(position)

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModelModel.deleteRun(runEntry)
                        Timber.i("Run deleted successful!")
                    }
                }
            })

        helper.attachToRecyclerView(recyclerViewRuns)
    }

    private fun requestPermissions() {
        if (Tracking.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.user_permission_location_text),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.user_permission_location_text),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    }
}

