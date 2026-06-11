package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AuthenticatorAppContent
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AuthenticatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AuthenticatorViewModel = viewModel()
            val themeType by viewModel.darkThemeType.collectAsState()
            
            MyApplicationTheme(
                darkTheme = true, // ALWAYS force Dark Mode to prevent eye strain and maintain design integrity
                dynamicColor = false // Keep our custom Elegant/AMOLED dark colors rather than system tints
            ) {
                AuthenticatorAppContent(viewModel = viewModel)
            }
        }
    }
}
