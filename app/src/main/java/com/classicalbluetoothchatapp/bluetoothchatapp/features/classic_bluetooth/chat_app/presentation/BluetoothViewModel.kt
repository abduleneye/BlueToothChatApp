package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothController
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothDeviceDomain
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
//Function: interacts with the BluetoothController
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
): ViewModel() {
    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ){scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messages = if(state.isConnected) state.messages else emptyList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update {
                it.copy(
                    isConnected = isConnected
                )
            }

        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _state.update {
                it.copy(
                    errorMessage = error
                )
            }
        }.launchIn(viewModelScope)

    }

    fun connectToDevice(device: BluetoothDeviceDomain){
        _state.update {
            it.copy(
                isConnecting = true
            )
        }
        deviceConnectionJob = bluetoothController.connectToDevice(device)
            .listen()
    }



    fun disconnectFromDevice(){
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false
            )
        }
    }

    fun waitForIncomingConnections(){
        _state.update {
            it.copy(isConnecting = true)
        }
        deviceConnectionJob = bluetoothController
            .startBlueToothServer()
            .listen()

    }

    fun sendMessage(message: String){
        viewModelScope.launch {
            val blueToothMessage = bluetoothController.trySendingMesage(message = message)
            if (blueToothMessage != null){
                _state.update {
                    it.copy(
                        messages = it.messages + blueToothMessage
                    )
                }
            }
        }
    }


    fun startScan(){
        bluetoothController.startDiscovery()
        Log.d("Chk", "Start VM")

    }

//    fun pairDevice(device: BluetoothDeviceDomain){
//        bluetoothController.pairDevice(device)
//    }

    fun stopScan(){
        Log.d("Chk", "Stop VM")
        bluetoothController.stopDiscovery()
    }

    //Launch or connect to server, we want to have the same connection logic int he flow
    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when(result){

               is ConnectionResult.connectionEstablished -> {

                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }

                }

                is ConnectionResult.TransferSucceeded -> {
                    _state.update {
                        it.copy(
                            messages = it.messages + result.message
                        )
                    }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message
                        )
                    }
                }
            }

        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                    )
                }
            }
            .launchIn(viewModelScope)

    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}