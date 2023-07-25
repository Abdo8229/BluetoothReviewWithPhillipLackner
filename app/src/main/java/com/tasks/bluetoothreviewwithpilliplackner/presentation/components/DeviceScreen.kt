package com.tasks.bluetoothreviewwithpilliplackner.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothDevice

@Composable
fun DeviceScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onStartServer: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BluetoothDevicesList(
            pairedDevices = state.pairedDevices,
            scannedDevices = state.scannedDevices,
            onClick =  onDeviceClick ,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = onStartScan) { Text(text = "Start Scan") }
            Button(onClick = onStopScan) { Text(text = "Stop Scan") }
            Button(onClick = onStartServer) { Text(text = "Start Server") }

        }
    }
}

@Composable
fun BluetoothDevicesList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) { device ->
            Text(
                text = device.name ?: "No name",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)


            )

        }
        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices) { device ->
            Text(
                text = device.name ?: "No name",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )

        }
    }

}

//
//@Composable
//@Preview(
//    showBackground = true,
//    showSystemUi = true
//)
//fun DeviceScreenPreview() {
//    DeviceScreen(state = BluetoothUiState(
//        listOf(BluetoothDevice("Samsung S23 Ultra", "166:ASA:AFSF:ASF")),
//        listOf(BluetoothDevice("Readmi9", "123:acc:65a:cc")),
//        false,
//        true,
//        null
//    ),
//        onStartScan = {},
//        onStopScan = {},
//        onDeviceClick = {},
//        onStartServer = {}
//
//    )
//}
