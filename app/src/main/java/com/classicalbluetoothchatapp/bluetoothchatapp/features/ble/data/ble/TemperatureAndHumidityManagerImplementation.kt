
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data.ConnectionState
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data.TemperatureAndHumidityManager
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data.TemperatureHumidityResult
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.util.Resource
import com.example.bletutorial.data.ble.isIndicatable
import com.example.bletutorial.data.ble.isNotifiable
import com.example.bletutorial.data.ble.printGattTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@SuppressLint("MissingPermission")
class TemperatureAndHumidityManagerImplementation @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
): TemperatureAndHumidityManager {

    private val DEVICE_NAME = "Jingy_Sensor_HumTemp"
    private val TEMPT_HUMIDITY_SERVICE_UUID = "0000xx20-0000-1000-0000-00885f9b34fb"
    private val TEMPT_HUMIDITY_CHARACTERISTICS_UUID = "0000xx20-0000-1000-0000-00885f9b34fb"


    val CCCD_DISCRIPTOR_UUID = "0000290-0000-1000"

    override val data: MutableSharedFlow<Resource<TemperatureHumidityResult>> = MutableSharedFlow()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var gatt: BluetoothGatt? = null

    private var isScanning = false

    private var coroutineScope = CoroutineScope(Dispatchers.Default)

    private val scanCallBack = object: ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            //Scan for multiple names
            //Or Scan for address
            if (result?.device?.name == DEVICE_NAME){
                coroutineScope.launch {
                    data.emit(
                        Resource.Loading(message = "Connecting to device...")
                    )
                }

                if(isScanning){

                        result.device?.connectGatt(context,  false, gattCallBack)
                    isScanning = false
                    bleScanner.stopScan(this)
                }

            }
        }
    }


    private var currentConnectionAttempt = 1
    private var MAXIMUM_CONNECTION_ATTEMPT = 5
    private val gattCallBack = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (status == BluetoothGatt.GATT_SUCCESS){
                if (newState == BluetoothProfile.STATE_CONNECTED){
                    coroutineScope.launch {
                        data.emit(Resource.Loading(message = "Discovering Services"))
                    }
                    gatt?.discoverServices()
                    this@TemperatureAndHumidityManagerImplementation.gatt = gatt
                }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                    coroutineScope.launch {
                        data.emit(Resource.Success(data = TemperatureHumidityResult(
                            temperature = 0f,
                            humidity = 0f,
                            connectionState = ConnectionState.Disconnected)))
                    }
                    gatt?.close()


                }}else{
                    gatt?.close()
                currentConnectionAttempt += 1
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Attempting to connect${currentConnectionAttempt}/${MAXIMUM_CONNECTION_ATTEMPT}"))
                }

            }
            if (currentConnectionAttempt <= MAXIMUM_CONNECTION_ATTEMPT){
                startReceiving()
            }else{
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not connect to ble device"))
                }
            }

        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            with(gatt){
                this?.printGattTable()
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Adjusting MTU Space..."))
                }
                gatt?.requestMtu(517)

            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            val characteristics = findCharacteristics(
serviceUUID = TEMPT_HUMIDITY_SERVICE_UUID,
                characteristicsUUID = TEMPT_HUMIDITY_CHARACTERISTICS_UUID
            )

            if (characteristics == null){
                coroutineScope.launch {
                    data.emit(
                        Resource.Error(
                            errorMessage = "Could not find temp and humidity publisher"
                        )
                    )

                }
                return
            }
            enableNotification(characteristics)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic){
                when(this?.uuid){
                    UUID.fromString(TEMPT_HUMIDITY_CHARACTERISTICS_UUID) -> {
                        //Check manual for specific data format
                        //XX XX XX XX XX XX
                        //23 45 32 00 12 34
                        val multiplicator  = if(value.first().toInt() > 0) -1 else 1
                        val temperature = value[1].toInt() + value[2].toInt() / 10f
                        val humidity = value[4].toInt() + value[5].toInt() / 10f
                        val tempHumidityResult = TemperatureHumidityResult(
                            temperature = multiplicator + temperature,
                            humidity = humidity,
                            connectionState = ConnectionState.Connected
                        )
                        coroutineScope.launch {
                            data.emit(
                                Resource.Success(
                                    data = tempHumidityResult
                                )
                            )
                        }

                    }else -> Unit

                }
            }
        }

        //We can read or write to ble device intuition for write
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            characteristic?.value
        }
    }

    private fun example(){
        val xtics = gatt?.getService(UUID.fromString("XXXXX"))?.getCharacteristic(UUID.fromString("XXXX"))
        gatt?.readCharacteristic(xtics)
    }

    private fun enableNotification(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DISCRIPTOR_UUID)
        val payLoad = when{
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, true) == false){
                Log.d("BLE bluetooth manager", "set characteristics notification failed")
                return
            }

            writeDescription(cccdDescriptor, payLoad)
        }

    }

    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray){
        gatt?.let {gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)

        } ?: error("Not connected to a BLE Device")

    }

    private fun findCharacteristics(serviceUUID: String, characteristicsUUID: String):BluetoothGattCharacteristic?{
        return gatt?.services?.find { service ->
            service.uuid.toString() == serviceUUID
        }?.characteristics?.find { characteristics ->
            characteristics.uuid.toString() == characteristicsUUID
        }


    }


    override fun startReceiving() {
        coroutineScope.launch {
            data.emit(Resource.Loading(message = "ScanningBle Devices..."))

        }

        isScanning = true
        bleScanner.startScan(null, scanSettings, scanCallBack)

    }



    override fun reconnect() {
        gatt?.connect()

    }

    override fun disconnect() {
        gatt?.disconnect()
    }



    override fun closeConnection() {

        bleScanner.stopScan(scanCallBack)
        val characteristics = findCharacteristics(TEMPT_HUMIDITY_SERVICE_UUID, TEMPT_HUMIDITY_CHARACTERISTICS_UUID)
            if(characteristics != null){
                disconnectCharacteristics(characteristics)
            }
        gatt?.close()

    }

    private fun disconnectCharacteristics(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DISCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let {cccdDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, false) == false){
                Log.d("TemperatureHumidityManager", "set characteristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }




}