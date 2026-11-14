package com.portfolio.dukaanmate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.portfolio.dukaanmate.ui.localization.LocalStrings

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Double, Int, Int) -> Unit
) {
    val strings = LocalStrings.current
    var barcode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var sellPrice by remember { mutableStateOf("") }
    var buyPrice by remember { mutableStateOf("") }
    var stockQty by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("5") }
    var showScanner by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(strings.addProductTitle, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (showScanner) {
                    BarcodeScannerView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        onBarcodeScanned = { scanned ->
                            barcode = scanned
                            showScanner = false
                        }
                    )
                }

                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text(strings.barcodeNumber) },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { showScanner = !showScanner }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan")
                        }
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.productName) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sellPrice,
                        onValueChange = { sellPrice = it },
                        label = { Text(strings.sellPrice) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = buyPrice,
                        onValueChange = { buyPrice = it },
                        label = { Text(strings.buyPrice) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockQty,
                        onValueChange = { stockQty = it },
                        label = { Text(strings.totalStock) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = minStock,
                        onValueChange = { minStock = it },
                        label = { Text(strings.alertLimit) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    val sell = sellPrice.toDoubleOrNull() ?: 0.0
                    val buy = buyPrice.toDoubleOrNull() ?: 0.0
                    val qty = stockQty.toIntOrNull() ?: 0
                    val min = minStock.toIntOrNull() ?: 5
                    if (barcode.isNotBlank() && name.isNotBlank() && sell > 0) {
                        onSave(barcode, name, sell, buy, qty, min)
                    }
                }
            ) {
                Text(strings.save, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
