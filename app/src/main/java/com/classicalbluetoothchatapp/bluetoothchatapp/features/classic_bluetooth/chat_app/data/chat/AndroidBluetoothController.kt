package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothDevice
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.BluetoothDeviceDomain
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothController
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.chat.ConnectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")


class AndroidBluetoothController(
    private val context: Context
): BluetoothController {

    private val bluetoothManager by lazy{
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy{
        bluetoothManager?.adapter
    }

    //test: listOf(BluetoothDeviceDomain(name = "test", address = "test"))
    private  val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    private  val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()

    private val _isConnected = MutableStateFlow<Boolean>(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()


    private val foundDeviceReceiver = FoundDeviceReceiver{device ->

        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices){
                devices
            }else{
                devices + newDevice
            }

        }


    }

    private val bluetoothStateReceiver = BluetoothStateReceiver{ isConnected, blueToothDevice ->

    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null


    init {
        updatePairedDevices()
    }
    override fun startDiscovery() {
        Log.d("Chk", "Start")

        Toast.makeText(context, "Started discovery ent", Toast.LENGTH_LONG).show()

//        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
//            Toast.makeText(context, "Started discovery ret", Toast.LENGTH_LONG).show()
//            return
//        }
        Toast.makeText(context, "Started discovery", Toast.LENGTH_LONG).show()

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
        Log.d("Chk", "Start")

    }

    override fun startBlueToothServer(): Flow<ConnectionResult> {
        return flow{
            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
                throw SecurityException("BLUETOOTH_CONNECT permission")

            }
          currentServerSocket =   bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "Chat_service",
                UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true

            while (shouldLoop){
               currentClientSocket =  try {
                    currentServerSocket?.accept()
                } catch (e: IOException){
                    shouldLoop = false
                    null
                }
                emit(ConnectionResult.connectionEstablished)

                currentServerSocket?.let {
                    currentServerSocket?.close()
                }
            }

        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)


    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {

        return flow{
            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
                throw SecurityException("No BLUETOOTH_CONNECT permision")
            }

            currentClientSocket = bluetoothAdapter
                ?.getRemoteDevice(device.address)
                ?.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))
            stopDiscovery()

            currentClientSocket?.let { socket ->
                try{
                    socket.connect()
                    emit(ConnectionResult.connectionEstablished)


                }catch (e: IOException){
                    socket.close()
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)

    }

    override fun closeConnection() {
        currentServerSocket?.close()
        currentClientSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        closeConnection()
    }

    //get list of paired devices
    private fun updatePairedDevices(){

//        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
//            Log.d("Chk", "Failed Pair")
//            return
//        }

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    //utility function for permission check
    private fun hasPermission(permission: String): Boolean{
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        const val SERVICE_UUID = "27b7d1da-08c7-4505-a6d1-2459987e5e2d"
    }
}