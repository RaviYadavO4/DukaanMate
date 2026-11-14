package com.portfolio.dukaanmate.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.portfolio.dukaanmate.data.local.entity.InvoiceEntity
import com.portfolio.dukaanmate.domain.model.CartItem
import com.portfolio.dukaanmate.ui.components.BarcodeScannerView
import com.portfolio.dukaanmate.ui.components.CartItemRow
import com.portfolio.dukaanmate.ui.localization.LocalStrings
import com.portfolio.dukaanmate.ui.viewmodel.DukaanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    viewModel: DukaanViewModel,
    onBillGenerated: (InvoiceEntity, List<CartItem>) -> Unit
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val cartItems = viewModel.cartItems
    val allProducts by viewModel.allProducts.collectAsState()

    var showScanner by remember { mutableStateOf(true) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showProductPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(strings.posBillingTitle, fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = { showScanner = !showScanner }) {
                        Icon(
                            if (showScanner) Icons.Default.VideocamOff else Icons.Default.QrCodeScanner,
                            contentDescription = "Toggle Camera"
                        )
                    }
                    IconButton(onClick = { showProductPicker = true }) {
                        Icon(Icons.Default.PlaylistAdd, contentDescription = "Add Manually")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(strings.totalAmount, style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "₹${String.format("%.2f", viewModel.getCartTotal())}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = { showCheckoutDialog = true },
                            modifier = Modifier.height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.generateBill, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (showScanner) {
                BarcodeScannerView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),
                    onBarcodeScanned = { code ->
                        viewModel.scanBarcodeForBilling(code) { missingCode ->
                            Toast.makeText(
                                context,
                                "${strings.productNotFoundToast} $missingCode",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${strings.cartItems} (${cartItems.sumOf { it.quantity }})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { showProductPicker = true }) {
                    Text(strings.pickFromList, fontWeight = FontWeight.Bold)
                }
            }

            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(strings.emptyCartGuide, color = MaterialTheme.colorScheme.secondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrease = { viewModel.updateCartQuantity(item.product.id, 1) },
                            onDecrease = { viewModel.updateCartQuantity(item.product.id, -1) }
                        )
                    }
                }
            }
        }

        if (showCheckoutDialog) {
            var customerName by remember { mutableStateOf("") }
            var customerPhone by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showCheckoutDialog = false },
                shape = RoundedCornerShape(24.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text(strings.customerDetails, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text(strings.customerName) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text(strings.whatsappNumber) },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            showCheckoutDialog = false
                            viewModel.checkoutBill(customerName, customerPhone) { invoice, items ->
                                onBillGenerated(invoice, items)
                            }
                        }
                    ) {
                        Text(strings.generatePdfBill, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCheckoutDialog = false }) { Text(strings.cancel) }
                }
            )
        }

        if (showProductPicker) {
            AlertDialog(
                onDismissRequest = { showProductPicker = false },
                shape = RoundedCornerShape(24.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text(strings.selectProductTitle, fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(allProducts) { product ->
                            TextButton(
                                onClick = {
                                    viewModel.addToCart(product)
                                    showProductPicker = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(product.name)
                                    Text("₹${product.sellPrice} (${strings.inStock}: ${product.stockQty})")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showProductPicker = false }) { Text(strings.close) }
                }
            )
        }
    }
}