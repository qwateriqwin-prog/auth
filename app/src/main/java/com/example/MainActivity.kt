package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.AuthenticatorAppContent
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AuthenticatorViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AuthenticatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeType by viewModel.darkThemeType.collectAsState()
            
            MyApplicationTheme(
                darkTheme = (themeType != 2), // Enable Light Theme when themeType is 2, otherwise keep Elegant or AMOLED dark colors
                dynamicColor = false // Keep our custom Elegant/AMOLED dark colors rather than system tints
            ) {
                AuthenticatorAppContent(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.lockApp()
    }
}
