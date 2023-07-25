package com.tasks.bluetoothreviewwithpilliplackner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothController
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothDeviceDomain
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(private val bluetoothController: BluetoothController) :
    ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevises,
        bluetoothController.pairedDevises,
        _state
    ) { scannedDecides, pairedDevices, state ->
        state.copy(scannedDevices = scannedDecides, pairedDevices = pairedDevices)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)
    private var deviceConnectionJob: Job? = null
    // init block
//   update connected, connecting, and errorMessage States
    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update {
                it.copy(isConnected = isConnected)
            }
        }.launchIn(viewModelScope)
        bluetoothController.errors.onEach { errors ->
            _state.update {
                it.copy(errorMessage = errors)
            }
        }.launchIn(viewModelScope)
    }
    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController.connectToDevice(device).listen()
    }

    fun disConnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {
            it.copy(
                isConnected = false,
                isConnecting = false
            )
        }
    }

    fun waitForIncomingConnection() {
        _state.update {
            it.copy(isConnecting = true)
        }
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }
    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopDiscovery() {
        bluetoothController.stopDiscovery()
    }

    fun release() {
        bluetoothController.release()
    }

    //helper connection fun
    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.massage
                        )
                    }
                }

            }
        }.catch { throwable ->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    isConnected = false,
                    isConnecting = false
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}