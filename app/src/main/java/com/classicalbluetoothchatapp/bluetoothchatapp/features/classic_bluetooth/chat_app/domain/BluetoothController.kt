package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain

import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.BluetoothDeviceDomain
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.chat.ConnectionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()

    fun stopDiscovery()

    fun release()

                               //Reactive data structure
    fun startBlueToothServer(): Flow<ConnectionResult>

    fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult>

    fun closeConnection()


}