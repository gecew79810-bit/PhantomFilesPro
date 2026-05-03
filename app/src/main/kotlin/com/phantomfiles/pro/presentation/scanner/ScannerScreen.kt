package com.phantomfiles.pro.presentation.scanner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phantomfiles.pro.presentation.theme.AmberWarning
import com.phantomfiles.pro.presentation.theme.DangerRed
import com.phantomfiles.pro.presentation.theme.ElectricCyan
import com.phantomfiles.pro.presentation.theme.NeonGreen
import com.phantomfiles.pro.presentation.theme.PhantomPurple
import com.phantomfiles.pro.presentation.theme.PhantomTheme
import com.phantomfiles.pro.util.FormatUtils

@Composable
fun ScannerScreen(viewModel: ScannerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Deep Scanner", style = MaterialTheme.typography.headlineLarge, color = ElectricCyan)
            Text("Scan your storage for issues", style = MaterialTheme.typography.bodySmall, color = PhantomPurple)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.isScanning) {
            item { ScanAnimation(state.scanType) }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScanCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.SdStorage,
                    title = "Large Files",
                    subtitle = "100MB+",
                    color = DangerRed,
                    onClick = { viewModel.scanLargeFiles() }
                )
                ScanCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.ContentCopy,
                    title = "Duplicates",
                    subtitle = "Find copies",
                    color = PhantomPurple,
                    onClick = { viewModel.scanDuplicates() }
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScanCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Visibility,
                    title = "Disguised",
                    subtitle = "Hidden media",
                    color = AmberWarning,
                    onClick = { viewModel.scanDisguised() }
                )
                ScanCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CleaningServices,
                    title = "Junk Clean",
                    subtitle = "Cache & temp",
                    color = NeonGreen,
                    onClick = { viewModel.scanJunk() }
                )
            }
        }

        if (state.largeFiles.isNotEmpty()) {
            item {
                ResultHeader("Large Files", state.largeFiles.size, state.largeFiles.sumOf { it.size })
            }
            items(state.largeFiles.take(20)) { file ->
                ResultItem(file.name, FormatUtils.formatSize(file.size), DangerRed)
            }
        }

        if (state.duplicates.isNotEmpty()) {
            item {
                ResultHeader("Duplicate Groups", state.duplicates.size, state.duplicates.sumOf { it.totalWastedSize })
            }
            items(state.duplicates.take(10)) { group ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "${group.files.size} copies • wasted ${FormatUtils.formatSize(group.totalWastedSize)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = PhantomPurple
                        )
                        group.files.forEach { file ->
                            Text(file.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        if (state.disguisedFiles.isNotEmpty()) {
            item {
                ResultHeader("Disguised Files", state.disguisedFiles.size, state.disguisedFiles.sumOf { it.size })
            }
            items(state.disguisedFiles.take(20)) { file ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(file.name, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Extension: .${file.fakeExtension} → Real: ${file.realType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberWarning
                        )
                        Text(file.reason, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (state.junkFiles.isNotEmpty()) {
            item {
                ResultHeader("Junk Files", state.junkFiles.size, state.totalJunkSize)
                TextButton(onClick = { viewModel.deleteFiles(state.junkFiles.map { it.path }) }) {
                    Text("Clean All", color = NeonGreen)
                }
            }
            items(state.junkFiles.take(20)) { file ->
                ResultItem(file.name, FormatUtils.formatSize(file.size), NeonGreen)
            }
        }
    }
}

@Composable
private fun ScanAnimation(scanType: String) {
    val transition = rememberInfiniteTransition(label = "scan")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "rotation"
    )
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val center = this.center
            val radius = size.minDimension / 2
            drawCircle(color = ElectricCyan.copy(alpha = 0.05f), radius = radius)
            drawCircle(color = ElectricCyan.copy(alpha = 0.1f), radius = radius * 0.7f)
            drawCircle(color = ElectricCyan.copy(alpha = 0.15f), radius = radius * 0.4f)
            drawArc(
                color = ElectricCyan.copy(alpha = 0.3f),
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                style = Stroke(width = 1.5f)
            )
            drawArc(
                color = ElectricCyan.copy(alpha = 0.2f),
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = center - androidx.compose.ui.geometry.Offset(radius * 0.7f, radius * 0.7f),
                size = androidx.compose.ui.geometry.Size(radius * 1.4f, radius * 1.4f),
                style = Stroke(width = 1f)
            )
        }
        Canvas(modifier = Modifier.size(100.dp).rotate(rotation)) {
            val radius = size.minDimension / 2
            drawArc(
                brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                    0f to Color.Transparent,
                    0.3f to ElectricCyan.copy(alpha = pulseAlpha),
                    0.35f to ElectricCyan.copy(alpha = 0.8f),
                    0.36f to Color.Transparent,
                    1f to Color.Transparent
                ),
                startAngle = 0f, sweepAngle = 360f, useCenter = true,
                topLeft = center - androidx.compose.ui.geometry.Offset(radius, radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Radar, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(28.dp))
            Text("Scanning $scanType...", style = MaterialTheme.typography.labelSmall, color = ElectricCyan)
        }
    }
}

@Composable
private fun ScanCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ResultHeader(title: String, count: Int, totalSize: Long) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text("$count items • ${FormatUtils.formatSize(totalSize)}", style = MaterialTheme.typography.labelMedium, color = ElectricCyan)
    }
}

@Composable
private fun ResultItem(name: String, size: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, maxLines = 1)
            Text(size, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
private fun ScannerScreenPreview() {
    PhantomTheme { ScannerScreen() }
}
