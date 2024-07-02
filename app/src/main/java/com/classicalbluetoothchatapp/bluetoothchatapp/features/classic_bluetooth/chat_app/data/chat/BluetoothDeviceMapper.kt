package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain{
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}