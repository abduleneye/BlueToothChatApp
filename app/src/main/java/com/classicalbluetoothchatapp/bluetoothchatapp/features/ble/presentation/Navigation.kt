package com.classicalbluetoothchatapp.bluetoothchatapp.features.ble.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(
    onBlueToothStateChanged: ()->Unit
){
    
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreenRoutes.StartScreen.routes) {
        composable(route = ScreenRoutes.StartScreen.routes){

            StartScreen(
                navController = navController
            )

        }

        composable(route = ScreenRoutes.TemperatureHumidityScreen.routes){
            
            TemperatureHumidityScreen(
                navController = navController,
                onBlueToothStateChanged = onBlueToothStateChanged
            )

        }
        
    }

}