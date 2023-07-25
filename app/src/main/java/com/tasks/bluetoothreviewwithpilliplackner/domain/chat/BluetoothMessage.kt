package com.tasks.bluetoothreviewwithpilliplackner.domain.chat

data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)