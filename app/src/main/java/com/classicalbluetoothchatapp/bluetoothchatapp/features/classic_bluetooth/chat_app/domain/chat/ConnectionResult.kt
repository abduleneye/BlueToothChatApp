package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.chat

sealed interface ConnectionResult {

    object connectionEstablished: ConnectionResult

    data class TransferSucceeded(val message: BlueToothMessage): ConnectionResult

    data class Error(val message: String): ConnectionResult
}