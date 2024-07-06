package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation

sealed class ScreenRoutes(val routes: String) {
    object StartScreen:ScreenRoutes("start_screen")
    object TemperatureHumidityScreen:ScreenRoutes("tempt_humid_screen")
}