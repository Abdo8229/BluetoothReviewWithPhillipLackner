package com.tasks.bluetoothreviewwithpilliplackner.domain.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val errors : SharedFlow<String>
    val scannedDevises: StateFlow<List<BluetoothDevice>>
    val pairedDevises: StateFlow<List<BluetoothDevice>>
    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult>
    fun closeConnection()
    fun startDiscovery()
    fun stopDiscovery()
    fun release()

}