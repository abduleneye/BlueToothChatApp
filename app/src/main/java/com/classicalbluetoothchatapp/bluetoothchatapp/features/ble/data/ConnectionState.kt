package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data

interface ConnectionState {
    object Connected: ConnectionState
    object Disconnected: ConnectionState
    object Uninitialized: ConnectionState
    object CurrentlyInitializing: ConnectionState
}