package com.example.appblesafhere.presentation.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.appblesafhere.presentation.viewmodel.BleScanViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

val BUTTON_SERVICE_UUID :UUID = UUID.fromString("a72bbab0-fa0f-4af4-b29e-283349cff831")
val BUTTON_CHARACTERISTIC_UUID :UUID = UUID.fromString("efe21743-1642-4ccf-a686-8f9275717c7f")
val BUTTON_DESCRIPTOR_UUID :UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context:Context,
    private val bluetoothDevice: BluetoothDevice,
    private val BleScanViewModel: BleScanViewModel
){
    val isConnected = MutableStateFlow(false)
    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())

    private val callback = object:BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            val connected = newState == BluetoothGatt.STATE_CONNECTED
            if(connected){
                services.value = gatt.services
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

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.v("bluetooth", String(characteristic.value))
            BleScanViewModel.updateBLEData(String(characteristic.value))
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.v("bluetooth", "Descriptor ${descriptor.uuid} of characteristic ${descriptor.characteristic.uuid}: write success")
            }
            else {
                Log.v("bluetooth", "Descriptor ${descriptor.uuid} of characteristic ${descriptor.characteristic.uuid}: write fail (status=$status)")
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

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun startReceivingButtonUpdates() {
        val service = gatt?.getService(BUTTON_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(BUTTON_CHARACTERISTIC_UUID)
        if (characteristic != null) {
            gatt?.setCharacteristicNotification(characteristic, true)

            val CLIENT_CONFIG_DESCRIPTOR = BUTTON_DESCRIPTOR_UUID
            val desc = characteristic.getDescriptor(CLIENT_CONFIG_DESCRIPTOR)
            desc?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            gatt?.writeDescriptor(desc)
        }
    }

}