package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation.Navigation
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.presentation.ui_components.ChatScreen
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.presentation.ui_components.DeviceScreen
import com.classicalbluetoothchatapp.bluetoothchatapp.ui.theme.BlueToothChatAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var bluetoothAdapter: BluetoothAdapter

//    private val bluetoothManager by lazy{
//        applicationContext.getSystemService(BluetoothManager::class.java)
//    }
//
//    private val bluetoothAdapter by lazy{
//        bluetoothManager?.adapter
//    }
//
//    private val isBluetoothEnabled: Boolean
//        get() = bluetoothAdapter?.isEnabled == true
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        val enableBluetoothLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ){ /** Not Needed **/ }
//        val permissionLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ){ perms ->
//
//            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
//                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
//           }else {
//                true
//            }
//
//            if(canEnableBluetooth && !isBluetoothEnabled){
//                enableBluetoothLauncher.launch(
//                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                )
//            }
//
//            }
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
//            permissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.BLUETOOTH_CONNECT,
//                    Manifest.permission.BLUETOOTH_SCAN
//                )
//            )
//        }else{
//            permissionLauncher.launch(
//                arrayOf(
//
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.BLUETOOTH_ADMIN,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                    Manifest.permission.BLUETOOTH_PRIVILEGED,
//                    Manifest.permission.BLUETOOTH,
//
//
//
//
//                    )
//            )
//        }







            setContent {
            BlueToothChatAppTheme {

                Navigation(
                    onBlueToothStateChanged = {
                        showBlueToothDialog()
                    }
                )

//                val viewModel = hiltViewModel<BluetoothViewModel>()
//                val state by viewModel.state.collectAsState()
//
//                LaunchedEffect(key1 = state.errorMessage) {
//                    state.errorMessage?.let { message ->
//                        Toast.makeText(
//                            applicationContext,
//                            message,
//                            Toast.LENGTH_LONG
//                        ).show()
//
//                    }
//
//                }
//
//                LaunchedEffect(key1 = state.isConnected) {
//                    if(state.isConnected){
//                        Toast.makeText(
//                            applicationContext,
//                            "You are connected!",
//                            Toast.LENGTH_LONG
//                        ).show()
//
//                    }
//
//                }
//
//                Surface(
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    when{
//                        state.isConnecting ->{
//                            Box(
//                                modifier =
//                                Modifier
//                                    .fillMaxSize()
//                            ) {
//
//                                Column(
//                                    modifier = Modifier
//                                        .fillMaxSize(),
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//
//                                   // CircularProgressIndicator()
//                                    Text(text = "Connecting...")
//
//                                }
//
//                            }
//                        }
//
//                        state.isConnected -> {
//                            ChatScreen(
//                                state = state,
//                                onDisconnect = viewModel::disconnectFromDevice,
//                                onSendMessage = viewModel::sendMessage
//                            )
//                        }
//
//                        else -> {
//                        DeviceScreen(
//                            onStartScan = viewModel::startScan,
//                            onStopScan = viewModel::stopScan,
//                            state = state,
//                            onDeviceClicked = viewModel::connectToDevice,
//                            onStartServer = viewModel::waitForIncomingConnections,
//
//                        )
//                        }
//                    }
//
//
//                }



            }
        }


    }

    override fun onStart() {
        super.onStart()
        showBlueToothDialog()
    }

    private var isBlueToothDialogAlreadyShown = false

    private fun showBlueToothDialog(){

        if (!bluetoothAdapter.isEnabled){
            if(!isBlueToothDialogAlreadyShown){
                val enableBlueToothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBlueToothIntentForResult.launch(enableBlueToothIntent)
                isBlueToothDialogAlreadyShown = true
            }

        }

    }

    private val startBlueToothIntentForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->

        isBlueToothDialogAlreadyShown = false


        if (result.resultCode != Activity.RESULT_OK){
            showBlueToothDialog()
        }


    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BlueToothChatAppTheme {
        Greeting("Android")
    }
}