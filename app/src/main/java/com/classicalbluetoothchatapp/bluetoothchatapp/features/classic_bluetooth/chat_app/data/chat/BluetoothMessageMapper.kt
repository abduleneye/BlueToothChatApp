package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.chat

import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.chat.BlueToothMessage

fun String.toBlueToothMessage(isFromLocalUser: Boolean): BlueToothMessage{
    val name = substringBeforeLast("#")
    val message = substringAfter("#")
    return BlueToothMessage(
        message = message,
        senderName = name,
        isFromLocalUser = isFromLocalUser
    )
}
fun BlueToothMessage.toByteArray(): ByteArray{
    return "$senderName#$message".encodeToByteArray()
}