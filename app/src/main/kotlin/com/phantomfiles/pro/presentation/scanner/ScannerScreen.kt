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
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
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
            item { ScanAnimation(state.scanType, state.scanProgress, state.scanStatus) }
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
                DeletableResultItem(
                    name = file.name,
                    size = FormatUtils.formatSize(file.size),
                    color = DangerRed,
                    onDelete = { viewModel.deleteFile(file.path) }
                )
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    file.name,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                IconButton(onClick = { viewModel.deleteFile(file.path) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
                                }
                            }
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
                val typeIcon = when {
                    file.realType.contains("image", true) || file.realType.contains("jpeg", true) || file.realType.contains("png", true) -> Icons.Default.Image
                    file.realType.contains("video", true) || file.realType.contains("mp4", true) || file.realType.contains("avi", true) -> Icons.Default.VideoFile
                    file.realType.contains("audio", true) || file.realType.contains("mp3", true) -> Icons.Default.AudioFile
                    else -> Icons.Default.Description
                }
                val typeColor = when {
                    file.realType.contains("image", true) || file.realType.contains("jpeg", true) || file.realType.contains("png", true) -> ElectricCyan
                    file.realType.contains("video", true) || file.realType.contains("mp4", true) -> PhantomPurple
                    file.realType.contains("audio", true) || file.realType.contains("mp3", true) -> NeonGreen
                    else -> AmberWarning
                }
                val typeLabel = when {
                    file.realType.contains("image", true) || file.realType.contains("jpeg", true) || file.realType.contains("png", true) -> "IMAGE"
                    file.realType.contains("video", true) || file.realType.contains("mp4", true) -> "VIDEO"
                    file.realType.contains("audio", true) || file.realType.contains("mp3", true) -> "AUDIO"
                    file.realType.contains("zip", true) || file.realType.contains("pk", true) -> "ARCHIVE"
                    else -> file.realType.uppercase()
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(typeColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(typeIcon, contentDescription = null, tint = typeColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(file.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                            Row {
                                Text(
                                    ".${file.fakeExtension}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DangerRed
                                )
                                Text(
                                    " → Actually: $typeLabel",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = typeColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                "${FormatUtils.formatSize(file.size)} • ${file.reason}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.deleteFile(file.path) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                        }
                    }
                }
            }
        }

        if (state.junkFiles.isNotEmpty()) {
            item {
                ResultHeader("Junk Files", state.junkFiles.size, state.totalJunkSize)
                TextButton(onClick = { viewModel.deleteFiles(state.junkFiles.map { it.path }) }) {
                    Text("Clean All", color = NeonGreen, fontWeight = FontWeight.Bold)
                }
            }
            items(state.junkFiles.take(50)) { file ->
                DeletableResultItem(
                    name = file.name,
                    size = FormatUtils.formatSize(file.size),
                    color = NeonGreen,
                    onDelete = { viewModel.deleteFile(file.path) }
                )
            }
        }

        if (state.emptyFolders.isNotEmpty()) {
            item {
                ResultHeader("Empty Folders", state.emptyFolders.size, 0L)
                TextButton(onClick = { viewModel.deleteFiles(state.emptyFolders.map { it.path }) }) {
                    Text("Delete All Empty", color = AmberWarning)
                }
            }
        }

        if (state.oldApks.isNotEmpty()) {
            item {
                ResultHeader("Old APKs", state.oldApks.size, state.oldApks.sumOf { it.size })
                TextButton(onClick = { viewModel.deleteFiles(state.oldApks.map { it.path }) }) {
                    Text("Delete All", color = DangerRed)
                }
            }
            items(state.oldApks.take(10)) { apk ->
                DeletableResultItem(
                    name = apk.name,
                    size = FormatUtils.formatSize(apk.size),
                    color = DangerRed,
                    onDelete = { viewModel.deleteFile(apk.path) }
                )
            }
        }
    }
}

@Composable
private fun ScanAnimation(scanType: String, progress: Int, status: String) {
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

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(80.dp)) {
                val radius = size.minDimension / 2
                drawCircle(color = ElectricCyan.copy(alpha = 0.05f), radius = radius)
                drawCircle(color = ElectricCyan.copy(alpha = 0.1f), radius = radius * 0.7f)
                drawCircle(color = ElectricCyan.copy(alpha = 0.15f), radius = radius * 0.4f)
                drawArc(
                    color = ElectricCyan.copy(alpha = 0.3f),
                    startAngle = 0f, sweepAngle = 360f, useCenter = false,
                    style = Stroke(width = 1.5f)
                )
            }
            Canvas(modifier = Modifier.size(80.dp).rotate(rotation)) {
                val radius = size.minDimension / 2
                drawArc(
                    color = ElectricCyan.copy(alpha = pulseAlpha),
                    startAngle = 0f, sweepAngle = 60f, useCenter = true,
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Radar, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(24.dp))
                Text("$progress%", style = MaterialTheme.typography.labelMedium, color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
        }
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = ElectricCyan,
            trackColor = ElectricCyan.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(status, style = MaterialTheme.typography.labelSmall, color = PhantomPurple)
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
        Text(
            "$count items" + if (totalSize > 0) " • ${FormatUtils.formatSize(totalSize)}" else "",
            style = MaterialTheme.typography.labelMedium,
            color = ElectricCyan
        )
    }
}

@Composable
private fun DeletableResultItem(name: String, size: String, color: Color, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, maxLines = 1)
            Text(size, style = MaterialTheme.typography.labelSmall, color = color)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
private fun ScannerScreenPreview() {
    PhantomTheme { ScannerScreen() }
}
