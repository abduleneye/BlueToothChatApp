package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.presentation

import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothDevice
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.chat.BlueToothMessage

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BlueToothMessage> = emptyList()
)
