package com.tasks.bluetoothreviewwithpilliplackner.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.tasks.bluetoothreviewwithpilliplackner.retrofitinlinetest.responce.PostsResponceItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //    private val bluetoothManager by lazy {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            applicationContext.getSystemService(BluetoothManager::class.java)
//        } else {
//            applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        }
//    }
//    private val bluetoothAdapter by lazy {
//        bluetoothManager?.adapter
//    }
//    private val isBluetoothEnable: Boolean
//        get() = bluetoothAdapter?.isEnabled == true
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val enableBluetoothLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { /* Not needed */ }
//        val permissionLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { perms ->
//            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
//            } else true
//            if (canEnableBluetooth && !isBluetoothEnable) {
//                enableBluetoothLauncher.launch(
//                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                )
//            }
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) permissionLauncher.launch(
//            arrayOf(
//                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT
//            )
//        )
//        setContent {
//            BluetoothReviewWithPillipLacknerTheme {
//                val viewModel: BluetoothViewModel by viewModels()
//                val state by viewModel.state.collectAsState()
//                LaunchedEffect(key1 = state.errorMessage) {
//                    state.errorMessage?.let { message ->
//                        Toast.makeText(
//                            applicationContext, message, Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//                LaunchedEffect(key1 = state.isConnected) {
//                    if (state.isConnected) {
//                        Toast.makeText(
//                            applicationContext, "You are connected!", Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//                Surface(
//                    color = MaterialTheme.colors.background
//                ) {
//                    when {
//                        state.isConnecting -> {
//                            Column(
//                                modifier = Modifier.fillMaxSize(),
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center
//                            ) {
//                                CircularProgressIndicator()
//                                Text(text = "Connecting...")
//                            }
//                        }
//
//                        state.isConnected -> {
//                            ChatScreen(
//                                state = state,
//                                onDisconnect = viewModel::disConnectFromDevice,
//                                onSendMessage = viewModel::sendMessage
//                            )
//                        }
//                        else -> {
//                            DeviceScreen(
//                                state = state,
//                                onStartScan = viewModel::startScan,
//                                onStopScan = viewModel::stopDiscovery,
//                                onDeviceClick = viewModel::connectToDevice,
//                                onStartServer = viewModel::waitForIncomingConnection
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val arr = intArrayOf(4, 3, 6, 1, 7, 2, 1)
//        println(bubbleSort(arr))
//        println("Hello world")

    }

    fun bubbleSort(arr: IntArray): IntArray {
        var swap = true
        while (swap) {
            swap = false
            for (i in 0 until arr.size - 1) {
                if (arr[i] > arr[i + 1]) {
                    val temp = arr[i]
                    arr[i] = arr[i + 1]
                    arr[i + 1] = temp
                }
            }
        }
        return arr
    }

}
