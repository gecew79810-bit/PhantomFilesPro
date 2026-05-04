package com.phantomfiles.pro.presentation.analyzer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.presentation.theme.*
import com.phantomfiles.pro.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageAnalyzerScreen(
    viewModel: StorageAnalyzerViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onViewFiles: (List<FileItem>, String) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.startAnalysis() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Internal Storage Analyze", color = PfText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PfText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PfBlue)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PfBg),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { OverviewCard(state) }

            item {
                AnalyzerCategory(
                    title = "All Partitions",
                    icon = Icons.Filled.Storage,
                    sizeText = FormatUtils.formatSize(state.totalPartitionSize),
                    items = state.topPartitions,
                    isLoading = state.isAnalyzing,
                    onViewAndClean = { onViewFiles(state.topPartitions, "All Partitions") }
                )
            }

            item {
                AnalyzerCategory(
                    title = "Large Files",
                    icon = Icons.Filled.SdStorage,
                    sizeText = FormatUtils.formatSize(state.largeFiles.sumOf { it.size }),
                    items = state.largeFiles.take(3),
                    isLoading = state.isAnalyzing,
                    onViewAndClean = { onViewFiles(state.largeFiles, "Large Files") }
                )
            }

            item {
                AnalyzerCategory(
                    title = "Recent Files",
                    icon = Icons.Filled.History,
                    sizeText = FormatUtils.formatSize(state.recentFiles.sumOf { it.size }),
                    items = state.recentFiles.take(3),
                    isLoading = state.isAnalyzing,
                    onViewAndClean = { onViewFiles(state.recentFiles, "Recent Files") }
                )
            }

            item {
                AnalyzerCategory(
                    title = "Empty Folders",
                    icon = Icons.Filled.FolderOpen,
                    sizeText = "${state.emptyFolders.size} items",
                    items = state.emptyFolders.take(3),
                    isLoading = state.isAnalyzing,
                    onViewAndClean = { onViewFiles(state.emptyFolders, "Empty Folders") }
                )
            }

            item {
                AppCacheCategory(
                    appCaches = state.appCaches,
                    totalCacheSize = state.totalCacheSize,
                    isLoading = state.isAnalyzing,
                    onViewAndClean = { onViewFiles(state.appCaches.map { it.first }, "App Cache") }
                )
            }

            item {
                AnalyzerCategory(
                    title = "Residual Files",
                    icon = Icons.Filled.BrokenImage,
                    sizeText = "${state.residualFiles.size} items",
                    items = state.residualFiles.take(3),
                    isLoading = state.isAnalyzing,
                    onViewAndClean = { onViewFiles(state.residualFiles, "Residual Files") }
                )
            }

            item {
                AnalyzerCategory(
                    title = "Redundant Files",
                    icon = Icons.Filled.FileCopy,
                    sizeText = "${state.redundantFiles.size} items",
                    items = state.redundantFiles.take(3),
                    isLoading = state.isAnalyzing,
                    onViewAndClean = { onViewFiles(state.redundantFiles, "Redundant Files") }
                )
            }

            item {
                AnalyzerCategory(
                    title = "Duplicate Files",
                    icon = Icons.Filled.ContentCopy,
                    sizeText = FormatUtils.formatSize(state.duplicateWastedSize),
                    items = state.duplicateFiles.take(3),
                    isLoading = state.isAnalyzing,
                    onViewAndClean = { onViewFiles(state.duplicateFiles, "Duplicate Files") }
                )
            }

            if (state.totalCleanableSize > 0 && !state.isAnalyzing) {
                item {
                    TrashCleanerCard(
                        cleanableSize = state.totalCleanableSize,
                        onClean = { viewModel.cleanAll() }
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun OverviewCard(state: AnalyzerState) {
    val total = state.storageTotal
    val used = state.storageUsed
    val usedPct = if (total > 0) ((used.toDouble() / total) * 100).toInt() else 0
    var animTarget by remember { mutableFloatStateOf(0f) }
    val animPct by animateFloatAsState(animTarget, tween(1200), label = "pct")
    LaunchedEffect(usedPct) { animTarget = usedPct / 100f }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PfPanel),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Overview", color = PfText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val stroke = 14.dp.toPx()
                        drawCircle(color = PfStroke, style = Stroke(stroke))
                        drawArc(
                            brush = androidx.compose.ui.graphics.Brush.sweepGradient(listOf(PfBlue, PfPurple, PfAmber, PfBlue)),
                            startAngle = -90f,
                            sweepAngle = 360f * animPct,
                            useCenter = false,
                            style = Stroke(stroke, cap = StrokeCap.Round)
                        )
                    }
                    Text("${usedPct}%", color = PfText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    state.categoryBreakdown.forEach { (name, size, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                            Spacer(Modifier.width(8.dp))
                            Text("$name:${FormatUtils.formatSize(size)}", color = PfText, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyzerCategory(
    title: String,
    icon: ImageVector,
    sizeText: String,
    items: List<FileItem>,
    isLoading: Boolean,
    onViewAndClean: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PfPanel),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = PfBlue, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(title, color = PfText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(sizeText, color = PfTextDim, fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = PfBlue, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.ChevronRight, null, tint = PfTextDim)
                    }
                }
            }

            if (items.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                items.forEach { file ->
                    FileItemRow(file)
                    Spacer(Modifier.height(6.dp))
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onViewAndClean,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PfBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("View and clean", color = Color.Black, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun AppCacheCategory(
    appCaches: List<Pair<FileItem, Long>>,
    totalCacheSize: Long,
    isLoading: Boolean,
    onViewAndClean: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PfPanel),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Apps, null, tint = PfAmber, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("App Cache", color = PfText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(FormatUtils.formatSize(totalCacheSize), color = PfTextDim, fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = PfBlue, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.ChevronRight, null, tint = PfTextDim)
                    }
                }
            }
            if (appCaches.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                appCaches.take(3).forEach { (file, cacheSize) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Android, null, tint = PfGreen, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(file.name, color = PfText, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Text(FormatUtils.formatSize(cacheSize), color = PfTextDim, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onViewAndClean,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PfBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("View and clean", color = Color.Black, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun TrashCleanerCard(cleanableSize: Long, onClean: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PfPanel),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CleaningServices, null, tint = PfRed, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Trash Cleaner", color = PfText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Estimated another ${FormatUtils.formatSize(cleanableSize)} of junk files can be cleaned up.",
                color = PfTextDim,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onClean,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PfBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("CLEAN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun FileItemRow(file: FileItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (file.isDirectory) Icons.Filled.Folder else Icons.Filled.InsertDriveFile,
            null,
            tint = if (file.isDirectory) PfBlue else PfTextDim,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                file.name,
                color = PfText,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                file.path.substringBeforeLast("/").takeLast(40).let { if (it.length < file.path.substringBeforeLast("/").length) "...$it" else it },
                color = PfTextDim,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
        Text(FormatUtils.formatSize(file.size), color = PfText, fontSize = 13.sp)
    }
}
