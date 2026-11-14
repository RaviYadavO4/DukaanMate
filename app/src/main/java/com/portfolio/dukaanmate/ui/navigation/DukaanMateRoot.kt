package com.portfolio.dukaanmate.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.portfolio.dukaanmate.ui.localization.AppLanguage
import com.portfolio.dukaanmate.ui.localization.EnglishStrings
import com.portfolio.dukaanmate.ui.localization.HindiStrings
import com.portfolio.dukaanmate.ui.localization.LocalStrings
import com.portfolio.dukaanmate.ui.screens.BillingScreen
import com.portfolio.dukaanmate.ui.screens.DashboardScreen
import com.portfolio.dukaanmate.ui.screens.InventoryScreen
import com.portfolio.dukaanmate.ui.viewmodel.DukaanViewModel
import com.portfolio.dukaanmate.utils.PdfInvoiceHelper
import org.koin.androidx.compose.koinViewModel

@Composable
fun DukaanMateRoot(
    viewModel: DukaanViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Live language state (English / Hindi)
    val currentLanguage by viewModel.language.collectAsState()
    val activeStrings = if (currentLanguage == AppLanguage.HINDI) HindiStrings else EnglishStrings

    // Supply Bilingual Strings to the whole app
    CompositionLocalProvider(LocalStrings provides activeStrings) {
        val strings = LocalStrings.current

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = strings.navDashboard) },
                        label = {
                            Text(
                                text = strings.navDashboard,
                                fontWeight = if (currentRoute == "dashboard") FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )

                    NavigationBarItem(
                        selected = currentRoute == "billing",
                        onClick = {
                            navController.navigate("billing") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = strings.navNewBill) },
                        label = {
                            Text(
                                text = strings.navNewBill,
                                fontWeight = if (currentRoute == "billing") FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )

                    NavigationBarItem(
                        selected = currentRoute == "inventory",
                        onClick = {
                            navController.navigate("inventory") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = strings.navStock) },
                        label = {
                            Text(
                                text = strings.navStock,
                                fontWeight = if (currentRoute == "inventory") FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("dashboard") {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToBilling = { navController.navigate("billing") },
                        onNavigateToInventory = { navController.navigate("inventory") }
                    )
                }

                composable("billing") {
                    BillingScreen(
                        viewModel = viewModel,
                        onBillGenerated = { invoice, items ->
                            PdfInvoiceHelper.generateAndShareBill(
                                context = context,
                                invoice = invoice,
                                items = items
                            )
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    )
                }

                composable("inventory") {
                    InventoryScreen(viewModel = viewModel)
                }
            }
        }
    }
}
