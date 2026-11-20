package com.mas.liquidlauncher

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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
                    },
                    onSetAsDefaultHome = ::requestDefaultHome
                )
            }
        }
    }

    private fun requestDefaultHome() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                startActivity(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
            } else {
                startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
            }
        } else {
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        }
    }
}
