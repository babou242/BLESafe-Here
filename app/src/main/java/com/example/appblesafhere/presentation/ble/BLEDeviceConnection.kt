package com.example.appblesafhere.presentation.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

val BUTTON_CHARACTERISTIC_UUID :UUID = UUID.fromString("efe21743-1642-4ccf-a686-8f9275717c7f")

class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context:Context,
    private val bluetoothDevice: BluetoothDevice
){
    val isConnected = MutableStateFlow(false)
    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())

    private val callback = object:BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            val connected = newState == BluetoothGatt.STATE_CONNECTED
            if(connected){
                services.value = gatt.services
                print("youyouyoy")
            }
            isConnected.value = connected
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            services.value = gatt.services
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (characteristic.uuid == BUTTON_CHARACTERISTIC_UUID) {
                Log.v("bluetooth", String(characteristic.value))
            }
        }
    }
    private var gatt:BluetoothGatt? = null

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connect(){
        gatt = bluetoothDevice.connectGatt(context,false,callback)
    }
    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }
    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverServices(){
        gatt?.discoverServices()
    }

//    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
//    fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID){
//        val service = gatt?.getService(serviceUUID)
//        val characteristic = service?.getCharacteristic(characteristicUUID)
//
//        if (characteristic != null) {
//            val success = gatt?.readCharacteristic(characteristic)
//            Log.v("bluetooth", "Read status: ${characteristic.value}")
//        }
//    }
@RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID): String? {
    val service = gatt?.getService(serviceUUID)
    val characteristic = service?.getCharacteristic(characteristicUUID)

    if (characteristic != null) {
        val success = gatt?.readCharacteristic(characteristic)
        if (success == true) {
            return characteristic.value?.let { String(it) } ?: "No value"
        }
    }
    return null
}

}