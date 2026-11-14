package com.portfolio.dukaanmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.portfolio.dukaanmate.ui.components.LanguageTogglePill
import com.portfolio.dukaanmate.ui.components.StatMiniCard
import com.portfolio.dukaanmate.ui.components.ThemeToggleButton
import com.portfolio.dukaanmate.ui.localization.LocalStrings
import com.portfolio.dukaanmate.ui.viewmodel.DukaanViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DukaanViewModel,
    onNavigateToBilling: () -> Unit,
    onNavigateToInventory: () -> Unit
) {
    val todaySales by viewModel.todaySales.collectAsState()
    val lowStockList by viewModel.lowStockProducts.collectAsState()
    val invoices by viewModel.allInvoices.collectAsState()
    val products by viewModel.allProducts.collectAsState()

    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val currentLanguage by viewModel.language.collectAsState()
    val strings = LocalStrings.current

    val todayDate = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(Date())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("DukaanMate", fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = todayDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    ThemeToggleButton(
                        isDarkTheme = isDarkTheme,
                        onToggle = { viewModel.toggleTheme() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageTogglePill(
                        currentLanguage = currentLanguage,
                        onToggle = { viewModel.toggleLanguage() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Premium Gradient Hero Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF006C4C),
                                    Color(0xFF004D40)
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.todaySales,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "LIVE POS",
                                    color = Color(0xFF89F8C7),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "₹${String.format("%.2f", todaySales ?: 0.0)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onNavigateToBilling,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF006C4C)
                            )
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.scanAndBill, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            // 2. Two Mini Stat Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMiniCard(
                        modifier = Modifier.weight(1f),
                        title = strings.totalProducts,
                        value = "${products.size}",
                        icon = Icons.Default.Inventory2,
                        tint = Color(0xFF006C4C),
                        onClick = onNavigateToInventory
                    )
                    StatMiniCard(
                        modifier = Modifier.weight(1f),
                        title = strings.totalBills,
                        value = "${invoices.size}",
                        icon = Icons.Default.ReceiptLong,
                        tint = Color(0xFF0277BD),
                        onClick = {}
                    )
                }
            }

            // 3. Low-Stock Alert Banner
            if (lowStockList.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        onClick = onNavigateToInventory
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${strings.lowStockAlert} (${lowStockList.size} ${strings.itemsRunningLow})",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = lowStockList.take(2).joinToString(", ") { "${it.name} (${it.stockQty})" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }

            // 4. Recent Transactions
            item {
                Text(
                    text = strings.recentTransactions,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (invoices.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(strings.noBillsYet, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            } else {
                items(invoices.take(10)) { invoice ->
                    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                        .format(Date(invoice.createdAt))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(invoice.customerName, fontWeight = FontWeight.Bold)
                                Text(
                                    "${invoice.invoiceNumber} • $dateStr",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Text(
                                text = "₹${String.format("%.2f", invoice.totalAmount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}