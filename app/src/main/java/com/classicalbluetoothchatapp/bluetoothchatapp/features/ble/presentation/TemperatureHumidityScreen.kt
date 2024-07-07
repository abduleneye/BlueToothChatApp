package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation

import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data.ConnectionState
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation.permissions.PermissionUtils
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation.permissions.SystemBroadCastReceiver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TemperatureHumidityScreen(
    navController: NavController,
    onBlueToothStateChanged: ()->Unit,
    viewModel: TemptHumidityViewModel = hiltViewModel()
){
SystemBroadCastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { BlueToothState ->
    val action = BlueToothState?.action?: return@SystemBroadCastReceiver
    if(action == BluetoothAdapter.ACTION_STATE_CHANGED){
        onBlueToothStateChanged()
    }
    
}

    val permissionsState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{ _, event ->
                if(event == Lifecycle.Event.ON_START){
                    if(!permissionsState.allPermissionsGranted){
                        permissionsState.launchMultiplePermissionRequest()
                        if(permissionsState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected){
                            viewModel.reconnect()
                        }
                    }
                    if (event == Lifecycle.Event.ON_STOP){
                        if(bleConnectionState == ConnectionState.Connected){
                            viewModel.disconnect()
                        }
                    }
                    onDispose{

                    }

                }


            }
            lifecycleOwner.lifecycle.addObserver(observer = observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer = observer)
            }
        }
     )

    LaunchedEffect(key1 = permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted){
            if (bleConnectionState == ConnectionState.Uninitialized){
                viewModel.initializeConnection()
            }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Column(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .aspectRatio(1f)
                .border(
                    BorderStroke(
                        width = 5.dp,
                        color = Color.Blue
                    ),
                    RoundedCornerShape(10.dp),

                    ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            if (bleConnectionState == ConnectionState.CurrentlyInitializing){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    //CircularProgressIndicator()
                    if(viewModel.initalizingMessage != null){
                        Text(
                            text = viewModel.initalizingMessage!!

                        )
                    }

                }
            }else if(
                !permissionsState.allPermissionsGranted
            ){
                Text(
                    text = "Go to app settings and allow the missing permissions",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }else if(viewModel.errorMesssage != null){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = viewModel.errorMesssage!!
                    )
                    Button(onClick = {
                        if (permissionsState.allPermissionsGranted){
                            viewModel.initializeConnection()
                        }
                    }) {
                        
                        Text(text = "Try Again.")
                        
                    }

                }

            }else if(
                bleConnectionState == ConnectionState.Connected
            ){

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Text(
                        text = "humidity; ${viewModel.humidity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "humidity; ${viewModel.temperature}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                }

            }


        }

    }

}
