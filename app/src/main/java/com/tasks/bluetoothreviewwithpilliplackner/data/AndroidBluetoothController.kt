package com.tasks.bluetoothreviewwithpilliplackner.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothController
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothDeviceDomain
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothMessage
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.ConnectionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
) : BluetoothController {


    private val bluetoothManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(BluetoothManager::class.java)
        } else {
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        }
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    //Scanned or paired devices states
    private val _scannedDevises = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevises: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevises.asStateFlow()
    private val _pairedDevises = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevises: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevises.asStateFlow()

    //    server & client socket states
    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    //    data transfer service and depend on Socket
    private var dataTransferService: BluetoothDataTransferService? = null

    //    bluetooth state receiver
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    //    errors shared flow for state receiver
    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Can't connect to non-paired device.")
            }
        }
    }

    init {
        updatePairedDevices()
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
//        val  a = device.address ?: "Address"
//        val  n = device.name ?: "name"
//        Log.d("TAG", "Androidcontroller :${(a + n) ?: "Null"}  ")
        _scannedDevises.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN))
            return
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow<ConnectionResult> {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "chat_service",
                UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null
                }
                emit(ConnectionResult.ConnectionEstablished)
                currentClientSocket?.let { socket ->
                    currentServerSocket?.close()
                    val service = BluetoothDataTransferService(socket)
                    dataTransferService = service

                    emitAll(
                        service
                            .listentForIncomingMessage()
                            .map {
                                ConnectionResult.TransferSucceeded(it)
                            }
                    )
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothDevice): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }
            val bluetoothDevice = bluetoothAdapter
                ?.getRemoteDevice(device.address)
            currentClientSocket = bluetoothDevice
                ?.createInsecureRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            stopDiscovery()
//            if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == false) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    _errors.emit("Can't connect to non-paired device")
//                }
//              return@flow
//            }

            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)
                    BluetoothDataTransferService(socket).also {
                        dataTransferService = it
                        emitAll(
                            it.listentForIncomingMessage()
                                .map {
                                    ConnectionResult
                                        .TransferSucceeded(it)
                                }
                        )
                    }
                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }

            }

        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)

    }

    override suspend fun trySendMessage(message: String): BluetoothMessage? {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT) || dataTransferService == null) {
            return null
        }
        val buletoothMessage = BluetoothMessage(
            message = message,
            senderName = bluetoothAdapter?.name ?: "unknown name",
            isFromLocalUser = true
        )
        dataTransferService?.sendMessage(buletoothMessage.toByteArray())
        return buletoothMessage

    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        closeConnection()
    }

    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT))
            return

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevises.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            context.checkPermission(permission, 1, 2) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        const val SERVICE_UUID = "9715fa99-0d49-457e-9e80-79590144e6f0"
    }
}