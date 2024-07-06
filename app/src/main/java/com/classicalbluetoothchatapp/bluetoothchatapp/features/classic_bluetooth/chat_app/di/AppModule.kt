package com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.data.chat.AndroidBluetoothController
import com.classicalbluetoothchatapp.bluetoothchatapp.features.classic_bluetooth.chat_app.domain.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesBluetoothController(@ApplicationContext context: Context): BluetoothController{
        return AndroidBluetoothController(context = context)
    }

    @Provides
    @Singleton
    fun provideBlueToothAdapter(@ApplicationContext context: Context): BluetoothAdapter{
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }
}