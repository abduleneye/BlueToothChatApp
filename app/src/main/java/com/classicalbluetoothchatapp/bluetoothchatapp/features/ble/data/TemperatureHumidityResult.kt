package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data

data class TemperatureHumidityResult(
    var temperature: Float,
    var humidity: Float,
    var connectionState: ConnectionState
)
