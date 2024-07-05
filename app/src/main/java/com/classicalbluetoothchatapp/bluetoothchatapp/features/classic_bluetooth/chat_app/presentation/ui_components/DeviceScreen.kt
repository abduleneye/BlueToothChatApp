package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.presentation.ui_components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothDevice
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothDeviceDomain
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.presentation.BluetoothUiState

@Composable
fun DeviceScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceClicked: (BluetoothDevice) -> Unit,
    onStartServer: ()-> Unit
){

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
    ){

        BluetoothDeviceList(
            pairedDevices = state.pairedDevices,
            scannedDevices = state.scannedDevices,
            onClick =
              onDeviceClicked
            ,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f))

        Row(
            modifier =  Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ){

            Button(onClick = onStartScan
              //  Log.d("Chk", "Start UI")
                //Toast.makeText(context, "Start button", Toast.LENGTH_LONG).show()

            ) {
                Text(text = "Start Scan")

            }
            Button(onClick =
               // Toast.makeText(context, "Stop button", Toast.LENGTH_LONG).show()
                onStopScan
            ) {
                Text(text = "Stop Scan")

            }
            Button(onClick =
               // Toast.makeText(context, "Server button clicked", Toast.LENGTH_LONG).show()
                onStartServer
            ) {
                Text(text = "Start Sever")

            }

        }
    }

}

@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice)-> Unit,
    modifier: Modifier = Modifier

){
    LazyColumn(
        modifier = modifier
    ) {

        item {
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        items(pairedDevices){device ->
            Text(
                text = device.name?: "(No name)",

                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
            Text(
                text = device.address?: "(No addr)",

                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )

        }

        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        items(scannedDevices){device ->
            Text(
                text = device.name?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
            Text(
                text = device.address?: "(No addr)",

                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )

        }

    }
}
