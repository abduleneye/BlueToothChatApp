package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.BluetoothDevice
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.BluetoothDeviceDomain
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")


class AndroidBluetoothController(
    private val context: Context
): BluetoothController {

    private val bluetoothManager by lazy{
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy{
        bluetoothManager?.adapter
    }

    //test: listOf(BluetoothDeviceDomain(name = "test", address = "test"))
    private  val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    private  val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()


    private val foundDeviceReceiver = FoundDeviceReceiver{device ->

        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices){
                devices
            }else{
                devices + newDevice
            }

        }


    }
    init {
        updatePairedDevices()
    }
    override fun startDiscovery() {
        Log.d("Chk", "Start")

        Toast.makeText(context, "Started discovery ent", Toast.LENGTH_LONG).show()

//        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
//            Toast.makeText(context, "Started discovery ret", Toast.LENGTH_LONG).show()
//            return
//        }
        Toast.makeText(context, "Started discovery", Toast.LENGTH_LONG).show()

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
        Log.d("Chk", "Start")

    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    //get list of paired devices
    private fun updatePairedDevices(){

//        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
//            Log.d("Chk", "Failed Pair")
//            return
//        }

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    //utility function for permission check
    private fun hasPermission(permission: String): Boolean{
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}