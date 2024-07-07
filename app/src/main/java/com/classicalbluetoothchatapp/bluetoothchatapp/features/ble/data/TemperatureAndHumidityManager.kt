package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data

import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface TemperatureAndHumidityManager {
    val data: MutableSharedFlow<Resource<TemperatureHumidityResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

}