package com.pocketdev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pocketdev.ui.navigation.PocketDevNavHost
import com.pocketdev.ui.theme.PocketDevTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketDevTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PocketDevNavHost()
                }
            }
        }
    }
}
