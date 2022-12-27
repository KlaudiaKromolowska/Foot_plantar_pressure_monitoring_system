package com.example.engineeringthesis.chart

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.engineeringthesis.others.Constants
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object BluetoothController {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    val bAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val bluetoothDataRight = MutableLiveData<ByteArray>()
    val controllerDataRight: LiveData<ByteArray> = bluetoothDataRight
    private var jobRight: Job? = null
    private var bluetoothSocketRight: BluetoothSocket? = null

    private val bluetoothDataLeft = MutableLiveData<ByteArray>()
    val controllerDataLeft: LiveData<ByteArray> = bluetoothDataLeft
    private var jobLeft: Job? = null
    private var bluetoothSocketLeft: BluetoothSocket? = null

    private val isError = MutableLiveData<Boolean>()
    val errors: LiveData<Boolean> = isError

    fun turnOn() {
        receiveDataRight()
        receiveDataLeft()
    }

    fun turnOff() {
        jobRight?.cancel()
        bluetoothSocketRight?.close()
        jobLeft?.cancel()
        bluetoothSocketLeft?.close()
    }

    private fun receiveDataRight() {
        jobRight = coroutineScope.launch(Dispatchers.Main) {
            try {
                connectRight(Constants.myDeviceRight.toString())
            } catch (e: CancellationException) {
                Timber.i("CANCELLED")
            } finally {
                bluetoothSocketRight?.close()
                bluetoothSocketRight = null
            }
        }
    }

    private suspend fun connectRight(device: String) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            lateinit var mmOutStream: OutputStream
            try {
                val bluetoothDevice = bluetoothAdapter.getRemoteDevice(device)
                bluetoothSocketRight = bluetoothDevice.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                )
                bluetoothSocketRight?.connect()
                val bluetoothSocketRight = bluetoothSocketRight ?: return
                val mmInStream: InputStream = bluetoothSocketRight.inputStream
                mmOutStream = bluetoothSocketRight.outputStream
                mmOutStream.write(1)
                while (bluetoothSocketRight.isConnected) {
                    val outputStream = ByteArrayOutputStream()
                    while (true) {
                        val mmBuffer = ByteArray(32)
                        val count = mmInStream.read(mmBuffer)
                        if (count <= 0) {
                            outputStream.flush()
                            outputStream.close()
                            break
                        }
                        outputStream.write(mmBuffer, 0, count)
                        if (outputStream.size() == 18 && outputStream.toByteArray()[17] == 10.toByte()) {
                            bluetoothDataRight.postValue(outputStream.toByteArray())
                            outputStream.flush()
                            outputStream.close()
                            break
                        } else if (outputStream.size() > 18) {
                            outputStream.flush()
                            outputStream.close()
                            break
                        }
                        delay(50)
                    }
                }
            } catch (e: Exception) {
                isError.postValue(true)
                mmOutStream = bluetoothSocketRight!!.outputStream
                mmOutStream.write(2)
                jobRight?.cancel()
            }
        }
    }


    private fun receiveDataLeft() {
        jobLeft = coroutineScope.launch(Dispatchers.IO) {
            try {
                connectLeft(Constants.myDeviceLeft.toString())
            } catch (e: CancellationException) {
                Timber.i("CANCELLED")
            } finally {
                bluetoothSocketLeft?.close()
                bluetoothSocketLeft = null
            }
        }
    }

    private suspend fun connectLeft(device: String) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            lateinit var mmOutStream: OutputStream
            try {
                val bluetoothDevice = bluetoothAdapter.getRemoteDevice(device)
                bluetoothSocketLeft = bluetoothDevice.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                )
                bluetoothSocketLeft?.connect() //TO DO :ADD SEND '1' and '0'
                val bluetoothSocketLeft = bluetoothSocketLeft ?: return
                val mmInStream: InputStream = bluetoothSocketLeft.inputStream
                mmOutStream = bluetoothSocketLeft.outputStream
                mmOutStream.write(1)
                while (bluetoothSocketLeft.isConnected) {
                    val outputStream = ByteArrayOutputStream()
                    while (true) {
                        val mmBuffer = ByteArray(32)
                        val count = mmInStream.read(mmBuffer)
                        if (count <= 0) {
                            outputStream.flush()
                            outputStream.close()
                            break
                        }
                        outputStream.write(mmBuffer, 0, count)
                        if (outputStream.size() == 18 && outputStream.toByteArray()[17] == 10.toByte()) {
                            bluetoothDataLeft.postValue(outputStream.toByteArray())
                            outputStream.flush()
                            outputStream.close()
                            break
                        } else if (outputStream.size() > 18) {
                            outputStream.flush()
                            outputStream.close()
                            break
                        }
                        delay(50)
                    }
                }
            } catch (e: Exception) {
                isError.postValue(true)
                mmOutStream = bluetoothSocketLeft!!.outputStream
                mmOutStream.write(2)
                jobLeft?.cancel()
            }
        }
    }

    fun clearErrors() {
        isError.postValue(false)
    }
}