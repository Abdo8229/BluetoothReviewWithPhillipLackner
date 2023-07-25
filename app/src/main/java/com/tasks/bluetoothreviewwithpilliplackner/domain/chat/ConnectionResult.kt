package com.tasks.bluetoothreviewwithpilliplackner.domain.chat

sealed interface ConnectionResult{
    object ConnectionEstablished: ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage):ConnectionResult
    data class Error(val massage : String): ConnectionResult
}