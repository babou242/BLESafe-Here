package com.example.appblesafhere.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class BleDeviceViewModel (private val application: Application):AndroidViewModel(application){
    private val _buttonState = MutableStateFlow<Map<String,String?>>(emptyMap())
    val buttonState : StateFlow<Map<String, String?>> = _buttonState

    fun updateButtonState(serviceUuid: UUID, characteristicUuid:UUID, state:String?){
        val key ="$serviceUuid-$characteristicUuid"
        _buttonState.value = _buttonState.value.toMutableMap().apply {
            this[key] = state
        }
    }
    fun readButtonState(serviceUuid: UUID, characteristicUuid: UUID, readButtonState: (serviceUuid: UUID, characteristicUuid: UUID) -> String?) {
        viewModelScope.launch {
            val state = readButtonState(serviceUuid, characteristicUuid)
            updateButtonState(serviceUuid, characteristicUuid, state)
        }
    }

}