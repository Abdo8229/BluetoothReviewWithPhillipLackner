package com.tasks.bluetoothreviewwithpilliplackner.domain.chat

sealed interface ConnectionResult{
    object ConnectionEstablished: ConnectionResult
    data class Error(val massage : String): ConnectionResult
}