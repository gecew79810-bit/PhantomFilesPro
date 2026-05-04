package com.phantomfiles.pro.presentation.tools

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantomfiles.pro.presentation.theme.*

data class ToolItem(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllToolsScreen(
    onBack: () -> Unit = {},
    onToolClick: (String) -> Unit = {}
) {
    val tools = listOf(
        ToolItem("Analyze", Icons.Filled.Analytics, PfBlue, "analyze"),
        ToolItem("Deep Scan", Icons.Filled.Search, PfPurple, "deep_scan"),
        ToolItem("Recycle Bin", Icons.Filled.DeleteSweep, PfRed, "recycle_bin"),
        ToolItem("Encrypted Vault", Icons.Filled.Lock, PfGreen, "vault"),
        ToolItem("App Manager", Icons.Filled.Apps, PfAmber, "app_manager"),
        ToolItem("Cloud Drive", Icons.Filled.Cloud, PfBlue2, "cloud"),
        ToolItem("LAN Transfer", Icons.Filled.WifiTethering, PfPink, "network"),
        ToolItem("FTP Server", Icons.Filled.Dns, PfGreen, "ftp"),
        ToolItem("Disguised Finder", Icons.Filled.FindInPage, PfPurple, "disguised"),
        ToolItem("Duplicate Finder", Icons.Filled.ContentCopy, PfAmber, "duplicates"),
        ToolItem("Large Files", Icons.Filled.SdStorage, PfRed, "large_files"),
        ToolItem("Bookmarks", Icons.Filled.Bookmark, PfBlue, "bookmarks"),
        ToolItem("AI Assistant", Icons.Filled.SmartToy, PfPink, "ai"),
        ToolItem("Images", Icons.Filled.Image, PfAmber, "category_images"),
        ToolItem("Videos", Icons.Filled.VideoLibrary, PfBlue, "category_videos"),
        ToolItem("Audio", Icons.Filled.Headphones, PfPink, "category_audio"),
        ToolItem("Documents", Icons.Filled.Description, PfGreen, "category_documents"),
        ToolItem("APKs", Icons.Filled.Android, PfPurple, "category_apks"),
        ToolItem("Compressed", Icons.Filled.FolderZip, PfAmber, "category_compressed"),
        ToolItem("Downloads", Icons.Filled.Download, PfBlue2, "category_downloads"),
        ToolItem("Storage Report", Icons.Filled.PieChart, PfGreen, "storage_report"),
        ToolItem("Junk Cleaner", Icons.Filled.CleaningServices, PfRed, "junk_clean"),
        ToolItem("Empty Folders", Icons.Filled.FolderOpen, PfTextDim, "empty_folders"),
        ToolItem("Settings", Icons.Filled.Settings, PfTextDim, "settings")
    )

    Box(modifier = Modifier.fillMaxSize().background(PfBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("All Tools", color = PfText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PfText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tools) { tool ->
                    ToolCard(tool = tool, onClick = { onToolClick(tool.route) })
                }
            }
        }
    }
}

@Composable
private fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(PfPanel, PfPanelAlt)
                )
            )
            .border(1.dp, tool.color.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tool.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(tool.icon, contentDescription = tool.name, tint = tool.color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            tool.name,
            color = PfText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}
