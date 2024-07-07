package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.di

import TemperatureAndHumidityManagerImplementation
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.data.TemperatureAndHumidityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BleAppModule {

    @Provides
    @Singleton
    fun provideBlueToothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    @Provides
    @Singleton
    fun provideTemperatureHumidityManager(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter

    ): TemperatureAndHumidityManager{
        return TemperatureAndHumidityManagerImplementation(
            bluetoothAdapter = bluetoothAdapter,
            context = context
        )

    }

}