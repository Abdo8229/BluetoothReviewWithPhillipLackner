package com.tasks.bluetoothreviewwithpilliplackner.data

import android.annotation.SuppressLint
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun android.bluetooth.BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {

        return BluetoothDeviceDomain(
            name = name?:"",
            address = address?:""
        )
}