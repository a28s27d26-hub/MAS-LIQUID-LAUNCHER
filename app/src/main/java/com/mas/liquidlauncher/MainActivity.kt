package com.mas.liquidlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent {
            val scheme = darkColorScheme(
                primary = Color(0xFFB7D9FF),
                secondary = Color(0xFFC7B8FF),
                background = Color(0xFF080D1B),
                surface = Color(0xFF172238)
            )
            MaterialTheme(colorScheme = scheme) {
                LiquidLauncherScreen(
                    packageManager = packageManager,
                    onLaunchApp = { packageName ->
                        packageManager.getLaunchIntentForPackage(packageName)?.let(::startActivity)
                    }
                )
            }
        }
    }
}
