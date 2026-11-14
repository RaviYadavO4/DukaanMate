package com.portfolio.dukaanmate

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.portfolio.dukaanmate.ui.navigation.DukaanMateRoot
import com.portfolio.dukaanmate.ui.theme.DukaanMateTheme
import com.portfolio.dukaanmate.ui.viewmodel.DukaanViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: DukaanViewModel = koinViewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            LaunchedEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                )
            }

            DukaanMateTheme(darkTheme = isDarkTheme) {
                DukaanMateRoot(viewModel = viewModel)
            }
        }
    }
}
