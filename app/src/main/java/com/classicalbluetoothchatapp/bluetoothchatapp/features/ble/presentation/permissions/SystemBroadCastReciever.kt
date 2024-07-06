package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation.permissions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
fun SystemBroadCastReceiver(
    systemAction: String,
    onSystemEvent: (intent: Intent?) -> Unit
){

    val context = LocalContext.current
    val currentSystemEvent by rememberUpdatedState(newValue = onSystemEvent)

    DisposableEffect(context, systemAction) {

        val intentFilter = IntentFilter(systemAction)
        val broadCastReceiver = object: BroadcastReceiver(){
            override fun onReceive(p0: Context?, intent: Intent?) {
                currentSystemEvent(intent)
            }
        }
        context.registerReceiver(broadCastReceiver, intentFilter)

        onDispose {
            context.unregisterReceiver(broadCastReceiver)
        }
    }

}