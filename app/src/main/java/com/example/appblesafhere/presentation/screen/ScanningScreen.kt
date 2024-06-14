package com.example.appblesafhere.presentation.screen

import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appblesafhere.presentation.Screen
import com.example.appblesafhere.presentation.ble.PERMISSION_BLUETOOTH_CONNECT
import com.example.appblesafhere.presentation.view.DeviceItem

@RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
@Composable
fun ScanningScreen(
    modifier : Modifier,
    isScanning: Boolean,
    foundDevices: List<BluetoothDevice>,
    startScanning: () -> Unit,
    stopScanning: () -> Unit,
    selectDevice: (BluetoothDevice) -> Unit,
    activeDevice: BluetoothDevice?,
    navController: NavController
){
    if (activeDevice == null) {
        Column (
            modifier = modifier.fillMaxSize(),
        ){
            if (isScanning) {
                Text("Scanning...")

                Button(onClick = stopScanning) {
                    Text("Stop Scanning")
                }
            }
            else {
                Button(onClick = startScanning) {
                    Text("Start Scanning")
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(foundDevices) { device ->
                    DeviceItem(
                        deviceName = device.name,
                        selectDevice = { selectDevice(device) }
                    )
                }
            }
        }
    }
    else{
        navController.navigate(Screen.DeviceScreen.route)
    }
}