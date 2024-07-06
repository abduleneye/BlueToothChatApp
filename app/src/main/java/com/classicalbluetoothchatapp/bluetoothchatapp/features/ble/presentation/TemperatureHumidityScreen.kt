package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation

import android.bluetooth.BluetoothAdapter
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation.permissions.SystemBroadCastReceiver

@Composable
fun TemperatureHumidityScreen(
    navController: NavController,
    onBlueToothStateChanged: ()->Unit
){
SystemBroadCastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { BlueToothState ->
    val action = BlueToothState?.action?: return@SystemBroadCastReceiver
    if(action == BluetoothAdapter.ACTION_STATE_CHANGED){
        onBlueToothStateChanged()
    }
    
}
}
