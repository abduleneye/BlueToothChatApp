package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.chat

data class BlueToothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)

