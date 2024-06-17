package com.example.appblesafhere.presentation.screen

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appblesafhere.presentation.Screen
import com.example.appblesafhere.presentation.viewmodel.BleDeviceViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun DeviceScreen(
    modifier : Modifier = Modifier,
    unselectDevice: () -> Unit,
    isDeviceConnected: Boolean,
    discoveredCharacteristics: Map<String, List<String>>,
    connect: () -> Unit,
    discoverServices: () -> Unit,
    readButtonState :(serviceUuid: UUID, characteristicUuid: UUID) -> String?,
    navController: NavController,
    enableNotification :() -> Unit,
    activeDevice: BluetoothDevice?
) {
    val viewModel :BleDeviceViewModel= viewModel()
    val buttonState by viewModel.buttonState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    if (activeDevice != null) {
        Column(
            modifier = modifier.scrollable(rememberScrollState(), Orientation.Vertical)
        ) {
            Button(onClick = connect) {
                Text("1. Connect")
            }
            Text("Device connected: $isDeviceConnected")
            Button(onClick = discoverServices, enabled = isDeviceConnected) {
                Text("2. Discover Services")
            }
            LazyColumn {
                items(discoveredCharacteristics.keys.sorted()) { serviceUuid ->
                    if (serviceUuid == "a72bbab0-fa0f-4af4-b29e-283349cff831") {
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            discoveredCharacteristics[serviceUuid]?.forEach {characteristicUuid ->
                                if (characteristicUuid =="efe21743-1642-4ccf-a686-8f9275717c7f") {
                                    Row{
                                        Button(onClick = {
                                            coroutineScope.launch {
                                                viewModel.readButtonState(UUID.fromString(serviceUuid), UUID.fromString(characteristicUuid), readButtonState)
                                            }
                                        }) {

                                            Text(text = "Know ButtonState")
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Button(onClick = enableNotification) {
                                            Icon(imageVector = Icons.Default.Send, contentDescription = null)
                                        }
                                    }
                                    val key = "$serviceUuid-$characteristicUuid"
                                    val state = buttonState[key] ?: "Unknown"
                                    Text(text = "ButtonState: $state")
                                }
                            }

                        }
                    }
                }
            }

            OutlinedButton(modifier = Modifier.padding(top = 40.dp),  onClick = unselectDevice) {
                Text("Disconnect")
            }
        }
    }
    else{
        navController.navigate(Screen.ScanningScreen.route)
    }
}