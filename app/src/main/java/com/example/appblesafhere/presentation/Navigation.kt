package com.example.appblesafhere.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appblesafhere.presentation.screen.DeviceScreen
import com.example.appblesafhere.presentation.screen.PermissionScreen
import com.example.appblesafhere.presentation.screen.ScanningScreen
import com.example.appblesafhere.presentation.view.TopBar
import com.example.appblesafhere.presentation.viewmodel.BleScanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun Navigation() {

    val viewModel: BleScanViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    LaunchedEffect(uiState.bleData) {
        uiState.bleData?.let { bleData ->
            when (bleData) {
                "Single Press" -> navController.navigate(Screen.SinglePressScreen.route)
                "Long Press" -> navController.navigate(Screen.LongPressScreen.route)
                "Double Press" -> navController.navigate(Screen.DoublePressScreen.route)
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.PermissionScreen.route) {
        composable(route = Screen.PermissionScreen.route) {
            Scaffold(
                topBar = { TopBar(title = "Permission") },
                content = { paddingValues ->
                    PermissionScreen(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            )
        }
        composable(route = Screen.ScanningScreen.route) {

            Scaffold(
                topBar = { TopBar(title = "Device") },
                content = { paddingValues ->
                    ScanningScreen(
                        modifier = Modifier.padding(paddingValues),
                        isScanning = uiState.isScanning,
                        foundDevices = uiState.foundDevices,
                        startScanning = viewModel::startScanning,
                        stopScanning = viewModel::stopScanning,
                        selectDevice = { device ->
                            viewModel.stopScanning()
                            viewModel.setActiveDevice(device)
                        },
                        activeDevice= uiState.activeDevice,
                        navController= navController)
                }
            )
        }
        composable(route = Screen.DeviceScreen.route) {
            Scaffold(
                topBar = { TopBar(title = "Device") },
                content = { paddingValues ->
                    DeviceScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        unselectDevice = {
                            viewModel.disconnectActiveDevice()
                            viewModel.setActiveDevice(null)
                        },
                        isDeviceConnected = uiState.isDeviceConnected,
                        discoveredCharacteristics = uiState.discoveredCharacteristics,
                        connect = viewModel::connectActiveDevice,
                        discoverServices = viewModel::discoverActiveDeviceServices,
                        readButtonState = viewModel::readCharacteristic,
                        navController = navController,
                        activeDevice = uiState.activeDevice,
                        enableNotification = viewModel::startReceivingButtonUpdates
                    )
                }
            )
        }
        composable(route = Screen.SinglePressScreen.route) {
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
                ){
                Text(text = "Single Press",
                    fontSize = 24.sp)
            }

        }
        composable(route = Screen.LongPressScreen.route) {
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(text = "Long Press",
                    fontSize = 24.sp)
            }
        }
        composable(route = Screen.DoublePressScreen.route) {
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Double Press",
                    fontSize = 24.sp
                    )
            }
        }

    }
}

sealed class Screen(val route: String){
    object PermissionScreen: Screen("permission_screen")
    object ScanningScreen: Screen("scan_screen")
    object DeviceScreen: Screen("device_screen")
    object SinglePressScreen: Screen("single_press_screen")
    object LongPressScreen: Screen("long_press_screen")
    object DoublePressScreen: Screen("double_press_screen")

}