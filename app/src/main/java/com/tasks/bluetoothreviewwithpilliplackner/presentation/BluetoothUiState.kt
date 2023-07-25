package com.tasks.bluetoothreviewwithpilliplackner.presentation

import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage :String? = null
)
