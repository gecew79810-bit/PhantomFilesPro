package com.phantomfiles.pro.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phantomfiles.pro.presentation.theme.*
import com.phantomfiles.pro.util.FormatUtils

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToFolder: (String, String) -> Unit = { _, _ -> },
    onNavigateToRecycleBin: () -> Unit = {},
    onNavigateToVault: () -> Unit = {},
    onNavigateToAppManager: () -> Unit = {},
    onNavigateToNetwork: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAI: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val dashboardState = remember(state) {
        val total = state.storageInfo.totalBytes
        val used = state.storageInfo.usedBytes
        val free = state.storageInfo.freeBytes
        val usedPercent = if (total > 0) ((used.toDouble() / total) * 100).toInt() else 0

        val healthText = when {
            usedPercent > 90 -> "Critical"
            usedPercent > 75 -> "Warning"
            usedPercent > 50 -> "Good"
            else -> "Excellent"
        }
        val note1 = when {
            usedPercent > 90 -> "Storage almost full!"
            usedPercent > 75 -> "Consider cleaning up"
            else -> "All systems normal"
        }
        val note2 = when {
            state.recycleBinCount > 0 -> "${state.recycleBinCount} items in recycle bin"
            else -> "No issues detected"
        }

        val breakdown = state.categories.mapIndexed { idx, cat ->
            val colors = listOf(PfBlue, PfPurple, PfGreen, PfAmber, PfRed)
            StorageBreakdownItem(
                label = cat.name,
                value = cat.formattedSize,
                accent = colors[idx % colors.size],
                portion = if (total > 0) (cat.size.toFloat() / total) else 0f
            )
        }

        val recent = state.recentFiles.take(5).map { file ->
            RecentItem(
                title = file.name,
                detail = FormatUtils.formatSize(file.size),
                timeAgo = FormatUtils.timeAgo(file.lastModified),
                accent = PfBlue
            )
        }

        DashboardUiState(
            storageUsedPercent = usedPercent,
            usedStorageText = FormatUtils.formatSize(used),
            freeStorageText = FormatUtils.formatSize(free),
            junkText = if (state.largeFiles.isNotEmpty()) FormatUtils.formatSize(state.largeFiles.sumOf { it.size }) else "0 B",
            junkItemsText = "${state.largeFiles.size} items",
            duplicatesText = "Scan to find",
            duplicateGroupsText = "0 groups",
            hiddenCountText = "0",
            disguisedCountText = "0",
            systemHealthText = healthText,
            systemNote1 = note1,
            systemNote2 = note2,
            aiOnline = true,
            grokStatusText = "GROQ",
            logLine = "Ready",
            aiLines = emptyList(),
            recentResults = recent,
            storageBreakdown = breakdown
        )
    }

    FuturisticDashboard(
        state = dashboardState,
        onSmartScan = onNavigateToScanner,
        onDeepScan = onNavigateToScanner,
        onCleanAll = onNavigateToScanner,
        onAutoFix = onNavigateToAI,
        onUnlock = onNavigateToVault,
        onPickFolder = { onNavigateToFolder("/storage/emulated/0", "Internal Storage") },
        onPause = { },
        onResume = { },
        onCancel = { },
        onMenu = { },
        onSettings = onNavigateToSettings,
        onViewAllRecent = { onNavigateToFolder("/storage/emulated/0", "Internal Storage") },
        onRecentItemClick = { },
        onCommandRun = { onNavigateToAI() }
    )
}
