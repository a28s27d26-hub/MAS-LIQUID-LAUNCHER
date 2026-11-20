package com.mas.liquidlauncher

import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class LaunchApp(val label: String, val packageName: String, val icon: android.graphics.drawable.Drawable)

private val Glass = Color(0x24FFFFFF)
private val GlassStrong = Color(0x38FFFFFF)
private val Warm = Color(0xFF9A7655)
private val Muted = Color(0xFFBEBAB6)

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
                    LaunchApp(info.loadLabel(packageManager).toString(), info.activityInfo.packageName, info.loadIcon(packageManager))
                }.getOrNull()
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    val filtered = remember(apps, search) {
        if (search.isBlank()) apps else apps.filter { it.label.contains(search, ignoreCase = true) }
    }
    val time = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    val date = remember { SimpleDateFormat("EEEE  •  d MMMM", Locale.getDefault()).format(Date()) }

    Box(
        Modifier.fillMaxSize().background(
            Brush.linearGradient(
                0f to Color(0xFF090909),
                .42f to Color(0xFF252525),
                .72f to Color(0xFF101010),
                1f to Color(0xFF050505)
            )
        )
    ) {
        // Soft monochrome light pools create depth without expensive real-time blur.
        Box(Modifier.fillMaxWidth().height(330.dp).align(Alignment.TopCenter)
            .background(Brush.radialGradient(listOf(Color(0x337C7C7C), Color.Transparent))))
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(time, color = Color.White, fontSize = 46.sp, fontWeight = FontWeight.Light, letterSpacing = (-2).sp)
                        Text(date.uppercase(Locale.getDefault()), color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
                    }
                    GlassCircleButton(Icons.Rounded.MoreHoriz) { showAll = !showAll }
                }
                Spacer(Modifier.height(22.dp))

                AnimatedVisibility(visible = !showAll, enter = fadeIn(), exit = fadeOut()) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            GlassPanel(Modifier.weight(1f).height(142.dp), radius = 28) {
                                Column(Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Cloud, null, tint = Muted, modifier = Modifier.size(17.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("WEATHER", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp)
                                    }
                                    Column {
                                        Text("--°", color = Color.White, fontSize = 37.sp, fontWeight = FontWeight.Light)
                                        Text("Add your city", color = Muted, fontSize = 10.sp)
                                    }
                                }
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                    MiniGlassTile("43°", true, Modifier.weight(1f))
                                    MiniGlassTile("31°", false, Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                    MiniGlassTile("43°", false, Modifier.weight(1f))
                                    MiniGlassTile("31°", true, Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                    GlassCircleButton(Icons.Rounded.Settings, Modifier.weight(1f)) { showAll = true }
                                    GlassCircleButton(Icons.Rounded.Navigation, Modifier.weight(1f)) { showAll = true }
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            GlassPanel(Modifier.weight(1f).height(112.dp), radius = 24) {
                                Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Text("YOUR SPACE", color = Muted, fontSize = 8.sp, letterSpacing = 1.5.sp)
                                    Text("Quiet moments.\nClear mind.", color = Color.White, fontSize = 15.sp, lineHeight = 19.sp, fontWeight = FontWeight.Light)
                                }
                            }
                            GlassPanel(Modifier.weight(1f).height(112.dp), radius = 24) {
                                Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                                    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                                        val bars = 38
                                        val gap = size.width / (bars * 1.7f)
                                        for (i in 0 until bars) {
                                            val h = (0.18f + ((i * 37 % 83) / 100f)) * size.height
                                            drawRoundRect(
                                                color = Color(0xFFE4E1DD).copy(alpha = .82f),
                                                topLeft = androidx.compose.ui.geometry.Offset(i * gap * 1.7f, (size.height - h) / 2),
                                                size = androidx.compose.ui.geometry.Size(gap, h),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(gap)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        GlassPanel(Modifier.fillMaxWidth().height(106.dp), radius = 25) {
                            Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("NOW PLAYING", color = Muted, fontSize = 8.sp, letterSpacing = 1.7.sp)
                                        Text("Your music will appear here", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Icon(Icons.Rounded.GraphicEq, null, tint = Color(0xFFD9D4CE), modifier = Modifier.size(21.dp))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.weight(1f).height(3.dp).clip(CircleShape).background(Color.White.copy(alpha = .17f))) {
                                        Box(Modifier.fillMaxWidth(.18f).fillMaxHeight().clip(CircleShape).background(Warm))
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(23.dp))
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = showAll, enter = fadeIn(), exit = fadeOut()) {
                    Column {
                        Text("APPLICATIONS", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Light, letterSpacing = 2.sp)
                        Spacer(Modifier.height(14.dp))
                        Row(Modifier.fillMaxWidth().clip(CircleShape).background(Glass).padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Search, null, tint = Muted)
                            Spacer(Modifier.width(10.dp))
                            BasicTextField(
                                value = search, onValueChange = { search = it }, singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                                modifier = Modifier.weight(1f),
                                decorationBox = { inner -> if (search.isEmpty()) Text("Search apps", color = Muted, fontSize = 14.sp); inner() }
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            contentPadding = PaddingValues(bottom = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(19.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            items(filtered, key = { it.packageName }) { app -> AppTile(app) { onLaunchApp(app.packageName) } }
                        }
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(Brush.verticalGradient(listOf(GlassStrong, Color(0x1AFFFFFF))))
                    .padding(horizontal = 13.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dockApps = apps.take(4)
                dockApps.forEach { app -> AppIcon(app, onLaunch = { onLaunchApp(app.packageName) }, size = 43) }
                GlassCircleButton(if (showAll) Icons.Rounded.Close else Icons.Rounded.Apps, Modifier.size(43.dp)) {
                    showAll = !showAll
                    if (!showAll) search = ""
                }
            }
        }
    }
}

@Composable
private fun GlassPanel(modifier: Modifier, radius: Int, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(radius.dp))
            .background(Brush.linearGradient(listOf(Color(0x38FFFFFF), Color(0x14FFFFFF), Color(0x0CFFFFFF))))
            .background(Color(0x0AFFFFFF))
            .padding(1.dp),
        content = content
    )
}

@Composable
private fun MiniGlassTile(value: String, bright: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier.height(47.dp).clip(RoundedCornerShape(18.dp))
            .background(if (bright) Color(0xFFEAE8E5) else Color(0xFF70543D)),
        contentAlignment = Alignment.Center
    ) {
        Text(value, color = if (bright) Color(0xFF282522) else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun GlassCircleButton(icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier.size(43.dp), onClick: () -> Unit) {
    Box(
        modifier.size(43.dp).clip(CircleShape).background(GlassStrong).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(19.dp))
    }
}

@Composable
private fun AppTile(app: LaunchApp, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(4.dp)) {
        AppIcon(app, onLaunch = onClick, size = 49)
        Spacer(Modifier.height(7.dp))
        Text(app.label, color = Color.White, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AppIcon(app: LaunchApp, onLaunch: () -> Unit, size: Int) {
    AndroidView(
        factory = { context -> android.widget.ImageView(context).apply { scaleType = android.widget.ImageView.ScaleType.FIT_CENTER } },
        update = { it.setImageDrawable(app.icon) },
        modifier = Modifier.size(size.dp).clip(RoundedCornerShape(15.dp)).clickable(onClick = onLaunch)
    )
}
