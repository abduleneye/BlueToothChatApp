package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data.ConnectionState
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data.TemperatureAndHumidityManager
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemptHumidityViewModel @Inject constructor(
    private  val temperatureAndHumidityManager: TemperatureAndHumidityManager
): ViewModel(){
    var initalizingMessage by mutableStateOf<String?>(null)
        private set

    var errorMesssage by mutableStateOf<String?>(null)
        private set

    var temperature by mutableStateOf(0f)
        private set

    var humidity by mutableStateOf(0f)
        private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)
        private set

    private fun subscribeToChanges(){
        viewModelScope.launch {
            temperatureAndHumidityManager.data.collect{result ->
                when(result){
                    is Resource.Success -> {

                        connectionState = result.data.connectionState
                        temperature = result.data.temperature
                        humidity = result.data.humidity

                    }

                    is Resource.Loading -> {
                        initalizingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing

                    }

                    is Resource.Error -> {
                        errorMesssage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized

                    }
                }
            }
        }
    }
    fun disconnect(){
        temperatureAndHumidityManager.disconnect()
    }
    fun reconnect(){
        temperatureAndHumidityManager.reconnect()
    }

    fun initializeConnection(){
        errorMesssage = null
        subscribeToChanges()
        temperatureAndHumidityManager.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        temperatureAndHumidityManager.closeConnection()
    }
}