package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.presentation

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.BluetoothDevice
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun startScan(){
        bluetoothController.startDiscovery()
    }

    fun stopScan(){
         bluetoothController.stopDiscovery()
    }
}