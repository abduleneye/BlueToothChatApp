package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain

import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.BluetoothDevice
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun release()


}