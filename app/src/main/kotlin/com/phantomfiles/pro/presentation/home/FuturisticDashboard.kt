package com.phantomfiles.pro.presentation.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantomfiles.pro.presentation.theme.*
import com.phantomfiles.pro.util.FormatUtils

data class DashboardUiState(
    val storageUsedPercent: Int = 0,
    val usedStorageText: String = "0 GB",
    val freeStorageText: String = "0 GB",
    val junkText: String = "0 B",
    val junkItemsText: String = "0 items",
    val duplicatesText: String = "0 B",
    val duplicateGroupsText: String = "0 groups",
    val hiddenCountText: String = "0",
    val disguisedCountText: String = "0",
    val systemHealthText: String = "Excellent",
    val systemNote1: String = "All systems normal",
    val systemNote2: String = "No issues detected",
    val aiOnline: Boolean = true,
    val grokStatusText: String = "GROQ",
    val logLine: String = "Ready",
    val aiLines: List<String> = emptyList(),
    val recentResults: List<RecentItem> = emptyList(),
    val storageBreakdown: List<StorageBreakdownItem> = emptyList(),
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val scanStatusText: String = "",
    val scanResultText: String = ""
)

data class RecentItem(
    val title: String,
    val detail: String,
    val timeAgo: String,
    val accent: Color
)

data class StorageBreakdownItem(
    val label: String,
    val value: String,
    val accent: Color,
    val portion: Float = 0f
)

@Composable
fun FuturisticDashboard(
    state: DashboardUiState,
    onSmartScan: () -> Unit,
    onDeepScan: () -> Unit,
    onCleanAll: () -> Unit,
    onAutoFix: () -> Unit,
    onUnlock: () -> Unit,
    onPickFolder: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onMenu: () -> Unit,
    onSettings: () -> Unit,
    onViewAllRecent: () -> Unit,
    onRecentItemClick: (RecentItem) -> Unit,
    onCommandRun: (String) -> Unit,
    onAllTools: () -> Unit = {},
    onAnalyze: () -> Unit = {}
) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val glow by pulse.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    var commandInput by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(PfBg, PfPanel, Color(0xFF02040B)))
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(PfBlue.copy(alpha = 0.16f), Color.Transparent),
                        radius = 1400f
                    )
                )
                .alpha(0.75f)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                TopHeader(
                    aiOnline = state.aiOnline,
                    onMenu = onMenu,
                    onPickFolder = onPickFolder,
                    onSettings = onSettings,
                    onUnlock = onUnlock,
                    onAnalyze = onAnalyze,
                    onDeepScan = onDeepScan
                )
            }

            item {
                HeroRow(
                    storageUsedPercent = state.storageUsedPercent,
                    usedStorageText = state.usedStorageText,
                    freeStorageText = state.freeStorageText,
                    systemHealthText = state.systemHealthText,
                    systemNote1 = state.systemNote1,
                    systemNote2 = state.systemNote2,
                    glow = glow,
                    aiOnline = state.aiOnline
                )
            }

            item { StatGrid(state) }

            item {
                QuickActions(
                    onSmartScan = onSmartScan,
                    onDeepScan = onDeepScan,
                    onCleanAll = onCleanAll,
                    onAutoFix = onAutoFix,
                    onPause = onPause,
                    onResume = onResume,
                    onCancel = onCancel,
                    isScanning = state.isScanning,
                    scanProgress = state.scanProgress,
                    scanStatusText = state.scanStatusText,
                    scanResultText = state.scanResultText,
                    onAllTools = onAllTools,
                    onAnalyze = onAnalyze
                )
            }

            item { StorageBreakdownCard(state.storageBreakdown) }

            item {
                RecentResultsCard(
                    items = state.recentResults,
                    onViewAll = onViewAllRecent,
                    onItemClick = onRecentItemClick
                )
            }

            item {
                AiAssistantCard(
                    state = state,
                    commandInput = commandInput,
                    onCommandChange = { commandInput = it },
                    onCommandRun = {
                        val toRun = commandInput.trim()
                        if (toRun.isNotEmpty()) {
                            onCommandRun(toRun)
                            commandInput = ""
                        }
                    }
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun TopHeader(
    aiOnline: Boolean,
    onMenu: () -> Unit,
    onPickFolder: () -> Unit,
    onSettings: () -> Unit,
    onUnlock: () -> Unit,
    onAnalyze: () -> Unit = {},
    onDeepScan: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconShell(Icons.Filled.Menu, onClick = onMenu)
        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "PhantomFiles",
                    color = PfText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                AssistChip(
                    onClick = onUnlock,
                    label = { Text("AI", fontSize = 12.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = PfPanelAlt,
                        labelColor = PfBlue
                    )
                )
            }
            Text(
                "Next Gen Storage Guardian",
                color = PfBlue,
                fontSize = 12.sp,
                letterSpacing = 1.1.sp
            )
            Text(
                if (aiOnline) "AI Status: Online" else "AI Status: Offline",
                color = if (aiOnline) PfGreen else PfRed,
                fontSize = 11.sp
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconShell(Icons.Filled.FolderOpen, onClick = onPickFolder)
            Box {
                IconShell(Icons.Filled.MoreVert, onClick = { showMenu = true })
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Analyze Storage") },
                        onClick = { showMenu = false; onAnalyze() },
                        leadingIcon = { Icon(Icons.Filled.Analytics, contentDescription = null, tint = PfBlue) }
                    )
                    DropdownMenuItem(
                        text = { Text("Deep Scan") },
                        onClick = { showMenu = false; onDeepScan() },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = PfPurple) }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = { showMenu = false; onSettings() },
                        leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = PfTextDim) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroRow(
    storageUsedPercent: Int,
    usedStorageText: String,
    freeStorageText: String,
    systemHealthText: String,
    systemNote1: String,
    systemNote2: String,
    glow: Float,
    aiOnline: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GlassCard(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text("STORAGE", color = PfTextDim, fontSize = 10.sp, letterSpacing = 1.sp)
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                RingProgress(used = storageUsedPercent)
            }
            Text("$usedStorageText Used", color = PfText, fontSize = 11.sp)
            Text("$freeStorageText Free", color = PfGreen, fontSize = 11.sp)
        }

        GlassCard(modifier = Modifier.weight(1.2f).fillMaxHeight()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AiOrb(glow = glow, aiOnline = aiOnline)
            }
        }

        GlassCard(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text("HEALTH", color = PfTextDim, fontSize = 10.sp, letterSpacing = 1.sp)
            Text(systemHealthText, color = PfGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                HeartLine()
            }
            Text(systemNote1, color = PfTextDim, fontSize = 11.sp, maxLines = 1)
            Text(systemNote2, color = PfGreen, fontSize = 11.sp, maxLines = 1)
        }
    }
}

@Composable
private fun StatGrid(state: DashboardUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                title = "Junk Files",
                value = state.junkText,
                subtitle = state.junkItemsText,
                tint = PfRed,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Duplicates",
                value = state.duplicatesText,
                subtitle = state.duplicateGroupsText,
                tint = PfPurple,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                title = "Hidden Files",
                value = state.hiddenCountText,
                subtitle = "Detected",
                tint = PfAmber,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Disguised Files",
                value = state.disguisedCountText,
                subtitle = "Detected",
                tint = PfGreen,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActions(
    onSmartScan: () -> Unit,
    onDeepScan: () -> Unit,
    onCleanAll: () -> Unit,
    onAutoFix: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    isScanning: Boolean = false,
    scanProgress: Float = 0f,
    scanStatusText: String = "",
    scanResultText: String = "",
    onAllTools: () -> Unit = {},
    onAnalyze: () -> Unit = {}
) {
    GlassCard {
        Text("QUICK ACTIONS", color = PfTextDim, fontSize = 12.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            NeonActionButton("Smart Scan", Icons.Filled.Psychology, PfBlue, Modifier.weight(1f), onSmartScan)
            NeonActionButton("Deep Scan", Icons.Filled.AutoAwesome, PfPurple, Modifier.weight(1f), onDeepScan)
            NeonActionButton("Clean All", Icons.Filled.CleaningServices, PfGreen, Modifier.weight(1f), onCleanAll)
            NeonActionButton("AI Auto Fix", Icons.Filled.Bolt, PfBlue2, Modifier.weight(1f), onAutoFix)
        }

        if (isScanning) {
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { scanProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = PfBlue,
                trackColor = PfStroke
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(scanStatusText, color = PfBlue, fontSize = 12.sp)
                Text("${(scanProgress * 100).toInt()}%", color = PfText, fontSize = 12.sp)
            }
        }

        if (scanResultText.isNotEmpty() && !isScanning) {
            Spacer(Modifier.height(8.dp))
            Text(scanResultText, color = PfGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            if (isScanning) {
                NeonActionButton("Pause", Icons.Filled.Pause, PfAmber, Modifier.weight(1f), onPause)
                NeonActionButton("Cancel", Icons.Filled.Close, PfRed, Modifier.weight(1f), onCancel)
            }
            NeonActionButton("Analyze", Icons.Filled.Analytics, PfAmber, Modifier.weight(1f), onAnalyze)
            NeonActionButton("All Tools", Icons.Filled.GridView, PfPink, Modifier.weight(1f), onAllTools)
        }
    }
}

@Composable
private fun StorageBreakdownCard(items: List<StorageBreakdownItem>) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1.3f)) {
                Text("STORAGE BREAKDOWN", color = PfTextDim, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))
                WaveChart(modifier = Modifier.fillMaxWidth().height(160.dp))
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                items.forEach { item ->
                    BreakdownRow(item.label, item.value, item.accent)
                }
            }

            DonutChart(
                items = items,
                modifier = Modifier.size(132.dp)
            )
        }
    }
}

@Composable
private fun RecentResultsCard(
    items: List<RecentItem>,
    onViewAll: () -> Unit,
    onItemClick: (RecentItem) -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("RECENT SCAN RESULTS", color = PfTextDim, fontSize = 12.sp, letterSpacing = 1.sp)
            Text(
                "View All",
                color = PfBlue,
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onViewAll)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        Spacer(Modifier.height(10.dp))
        if (items.isEmpty()) {
            Text(
                "No scans yet. Run a Smart Scan to see results here.",
                color = PfTextDim,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEach { item -> RecentRow(item, onClick = { onItemClick(item) }) }
            }
        }
    }
}

@Composable
private fun AiAssistantCard(
    state: DashboardUiState,
    commandInput: String,
    onCommandChange: (String) -> Unit,
    onCommandRun: () -> Unit
) {
    GlassCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("AI ASSISTANT", color = PfTextDim, fontSize = 12.sp, letterSpacing = 1.sp)
            AssistChip(
                onClick = { },
                label = { Text(state.grokStatusText, fontSize = 12.sp) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = PfPanelAlt,
                    labelColor = PfText
                ),
                trailingIcon = {
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (state.aiOnline) PfGreen else PfRed)
                    )
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PfPanelAlt)
                    .border(1.dp, PfStroke, RoundedCornerShape(18.dp))
                    .padding(12.dp)
            ) {
                if (state.aiLines.isEmpty()) {
                    Text(
                        "> AI terminal idle. Type a command below.",
                        color = PfTextDim,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                } else {
                    state.aiLines.forEach { line ->
                        val lineColor = when {
                            line.startsWith("[AI]") -> PfGreen
                            line.startsWith(">") -> PfText
                            line.startsWith("[ERR]") -> PfRed
                            else -> PfTextDim
                        }
                        Text(
                            line,
                            color = lineColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "> ${state.logLine}",
                    color = PfTextDim,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .height(180.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF121A2E), PfPanel))
                    )
                    .border(1.dp, Color(0xFF23477A), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                AiOrb(glow = 1f, aiOnline = state.aiOnline)
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commandInput,
                onValueChange = onCommandChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a command...", color = PfTextDim) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onCommandRun() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PfBlue,
                    unfocusedBorderColor = PfStroke,
                    focusedTextColor = PfText,
                    unfocusedTextColor = PfText,
                    cursorColor = PfBlue
                )
            )
            FloatingActionButton(
                onClick = onCommandRun,
                containerColor = PfBlue,
                contentColor = Color.Black
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.height(100.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Dns, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(value, color = PfText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(title, color = PfTextDim, fontSize = 12.sp)
                Text(subtitle, color = tint, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun NeonActionButton(
    text: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(62.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PfPanelAlt),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, tint.copy(alpha = 0.75f), RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(listOf(tint.copy(alpha = 0.18f), Color.Transparent)),
                    RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
                Spacer(Modifier.height(2.dp))
                Text(text, color = PfText, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun AiOrb(glow: Float, aiOnline: Boolean) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(170.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = this.center
            val radius = (size.minDimension / 2f - 16.dp.toPx()) * glow

            repeat(3) { idx ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PfBlue.copy(alpha = 0.16f - idx * 0.03f),
                            Color.Transparent
                        ),
                        radius = radius * (1.05f + idx * 0.15f)
                    ),
                    radius = radius * (1.12f + idx * 0.09f)
                )
            }

            drawCircle(
                brush = Brush.sweepGradient(listOf(PfBlue, PfPurple, PfPink, PfBlue)),
                radius = radius * 1.08f,
                style = Stroke(10.dp.toPx(), cap = StrokeCap.Round)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF081022), Color(0xFF02040B)),
                    center = center,
                    radius = radius
                ),
                radius = radius
            )

            repeat(12) { i ->
                val angle = Math.toRadians((i * 30).toDouble())
                val inner = radius * 0.9f
                val outer = radius * (1.28f + 0.06f * kotlin.math.sin(i.toFloat()))
                val sx = center.x + kotlin.math.cos(angle).toFloat() * inner
                val sy = center.y + kotlin.math.sin(angle).toFloat() * inner
                val ex = center.x + kotlin.math.cos(angle).toFloat() * outer
                val ey = center.y + kotlin.math.sin(angle).toFloat() * outer
                drawLine(
                    color = PfBlue.copy(alpha = 0.35f),
                    start = Offset(sx, sy),
                    end = Offset(ex, ey),
                    strokeWidth = 1.6.dp.toPx()
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.BubbleChart, null, tint = PfBlue, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(8.dp))
            Text("AI CORE", color = PfText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(
                if (aiOnline) "Online" else "Offline",
                color = if (aiOnline) PfGreen else PfRed,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun RingProgress(used: Int) {
    val safe = used.coerceIn(0, 100)
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(132.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = 14.dp.toPx()
            drawCircle(color = PfStroke, style = Stroke(stroke))
            drawArc(
                brush = Brush.sweepGradient(listOf(PfBlue, PfPurple, PfPink, PfBlue)),
                startAngle = -90f,
                sweepAngle = 360f * (safe / 100f),
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$safe%", color = PfText, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("Used", color = PfTextDim, fontSize = 13.sp)
        }
    }
}

@Composable
private fun HeartLine() {
    Canvas(modifier = Modifier.fillMaxWidth().height(42.dp)) {
        val points = listOf(
            0.0f to 0.6f, 0.1f to 0.6f, 0.16f to 0.3f, 0.22f to 0.9f, 0.28f to 0.35f,
            0.36f to 0.5f, 0.44f to 0.45f, 0.53f to 0.2f, 0.61f to 0.7f, 0.69f to 0.4f,
            0.76f to 0.55f, 0.83f to 0.52f, 1.0f to 0.52f
        )
        val path = Path()
        points.forEachIndexed { index, (x, y) ->
            val px = x * size.width
            val py = y * size.height
            if (index == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        drawPath(path = path, color = PfGreen, style = Stroke(3.5f))
    }
}

@Composable
private fun WaveChart(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val colors = listOf(PfBlue2, PfPurple, PfBlue)
        colors.forEachIndexed { idx, c ->
            val path = Path()
            for (x in 0..100) {
                val fx = x / 100f * size.width
                val fy = size.height * (0.55f + 0.12f * kotlin.math.sin((x / 8f) + idx))
                if (x == 0) path.moveTo(fx, fy) else path.lineTo(fx, fy)
            }
            drawPath(path = path, color = c.copy(alpha = 0.7f - idx * 0.16f), style = Stroke(3f))
        }
    }
}

@Composable
private fun DonutChart(
    items: List<StorageBreakdownItem>,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = 16.dp.toPx()
            drawCircle(color = PfStroke, style = Stroke(stroke))

            if (items.isNotEmpty()) {
                val total = items.sumOf { it.portion.toDouble() }.toFloat()
                if (total > 0f) {
                    var start = -90f
                    items.forEach { item ->
                        val sweep = 360f * (item.portion / total)
                        drawArc(
                            color = item.accent,
                            startAngle = start,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(stroke, cap = StrokeCap.Butt)
                        )
                        start += sweep
                    }
                } else {
                    val sweep = 360f / items.size
                    var start = -90f
                    items.forEach { item ->
                        drawArc(
                            color = item.accent.copy(alpha = 0.7f),
                            startAngle = start,
                            sweepAngle = sweep - 2f,
                            useCenter = false,
                            style = Stroke(stroke, cap = StrokeCap.Butt)
                        )
                        start += sweep
                    }
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("USAGE", color = PfTextDim, fontSize = 10.sp, letterSpacing = 1.sp)
            Text(
                "${items.size}",
                color = PfText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text("types", color = PfTextDim, fontSize = 10.sp)
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accent))
        Text(label, color = PfText, fontSize = 12.sp)
        Spacer(Modifier.weight(1f))
        Text(value, color = PfText, fontSize = 12.sp)
    }
}

@Composable
private fun RecentRow(item: RecentItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(PfPanelAlt)
            .border(1.dp, PfStroke, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(item.accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Folder, null, tint = item.accent, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, color = PfText, fontSize = 13.sp)
            Text(item.detail, color = PfGreen, fontSize = 12.sp)
        }
        Text(item.timeAgo, color = PfTextDim, fontSize = 12.sp)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Filled.ChevronRight, null, tint = PfTextDim)
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(PfPanel)
            .border(1.dp, PfStroke, RoundedCornerShape(22.dp))
            .padding(14.dp),
        content = content
    )
}

@Composable
private fun IconShell(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(PfPanel)
            .border(1.dp, PfStroke, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = PfText)
    }
}
