package com.mas.liquidlauncher

import android.content.pm.PackageManager
import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class LaunchApp(val label: String, val packageName: String, val icon: android.graphics.drawable.Drawable)

@Composable
fun LiquidLauncherScreen(packageManager: PackageManager, onLaunchApp: (String) -> Unit) {
    var search by remember { mutableStateOf("") }
    var showAll by remember { mutableStateOf(false) }
    var apps by remember { mutableStateOf<List<LaunchApp>>(emptyList()) }

    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .mapNotNull { info ->
                runCatching {
                    LaunchApp(
                        info.loadLabel(packageManager).toString(),
                        info.activityInfo.packageName,
                        info.loadIcon(packageManager)
                    )
                }.getOrNull()
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    val filtered = remember(apps, search) {
        if (search.isBlank()) apps else apps.filter { it.label.contains(search, ignoreCase = true) }
    }
    val time = remember { SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()) }
    val date = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF101C36), Color(0xFF172D4C), Color(0xFF090F20)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(Modifier.height(24.dp))
                Text(time, color = Color.White, fontSize = 58.sp, fontWeight = FontWeight.Light)
                Text(date, color = Color(0xFFB9C9E2), fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Good day", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
                        Text("Your space. Your flow.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text("✦", color = Color(0xFFC8DFFF), fontSize = 28.sp)
                }
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (showAll) "All apps" else "Favorites", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { showAll = !showAll }) {
                        Text(if (showAll) "Home" else "See all", color = Color(0xFFBBD8FF))
                    }
                }
                if (showAll) {
                    Row(
                        Modifier.fillMaxWidth().clip(CircleShape).background(Color.White.copy(alpha = .12f)).padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.White.copy(alpha = .8f))
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = search, onValueChange = { search = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 15.sp),
                            modifier = Modifier.weight(1f),
                            decorationBox = { inner ->
                                if (search.isEmpty()) Text("Search apps", color = Color.White.copy(alpha = .5f))
                                inner()
                            }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(bottom = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filtered, key = { it.packageName }) { app ->
                            AppTile(app) { onLaunchApp(app.packageName) }
                        }
                    }
                } else {
                    val favorites = apps.take(8)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        userScrollEnabled = false,
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 250.dp)
                    ) {
                        items(favorites, key = { it.packageName }) { app ->
                            AppTile(app) { onLaunchApp(app.packageName) }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = .14f))
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                apps.take(4).forEach { app ->
                    AppIcon(app) { onLaunchApp(app.packageName) }
                }
                Text("＋", color = Color.White, fontSize = 26.sp, modifier = Modifier.clickable { showAll = true }.padding(horizontal = 8.dp))
            }
        }
    }
}

@Composable
private fun AppTile(app: LaunchApp, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(4.dp)
    ) {
        AppIcon(app, onClick)
        Spacer(Modifier.height(6.dp))
        Text(app.label, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AppIcon(app: LaunchApp, onClick: () -> Unit) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.graphics.painter.rememberDrawablePainter(app.icon),
        contentDescription = app.label,
        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).clickable(onClick = onClick)
    )
}
