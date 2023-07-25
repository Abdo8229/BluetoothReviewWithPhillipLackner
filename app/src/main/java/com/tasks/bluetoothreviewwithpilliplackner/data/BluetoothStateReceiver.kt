package com.tasks.bluetoothreviewwithpilliplackner.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BluetoothStateReceiver(
    private val onStateChanged: (isConnected: Boolean, BluetoothDevice) -> Unit
) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val device =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            } else {
                intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }

        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                onStateChanged(true, device ?: return)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                onStateChanged(false, device ?: return)
            }

        }
//        device?.let {
//            if (!it.name.isNullOrEmpty()) {
//                onStateChanged(false, it)
//
//            }
//                    Log.d("TAG", "onReceive: ${it?.name ?: "No Name"} ${it?.address}")

    }
}
