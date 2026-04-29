package com.phantomfiles.pro.presentation.files

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.model.FileType
import com.phantomfiles.pro.presentation.theme.AmberWarning
import com.phantomfiles.pro.presentation.theme.DangerRed
import com.phantomfiles.pro.presentation.theme.ElectricCyan
import com.phantomfiles.pro.presentation.theme.NeonGreen
import com.phantomfiles.pro.presentation.theme.PhantomPurple
import com.phantomfiles.pro.presentation.theme.PhantomTheme
import com.phantomfiles.pro.util.FormatUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(
    viewModel: FilesViewModel = hiltViewModel(),
    onOpenViewer: (FileItem) -> Unit = {},
    onExitScreen: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pathStack by viewModel.pathStack.collectAsStateWithLifecycle()
    val selectedFiles by viewModel.selectedFiles.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val sortMode by viewModel.sortMode.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val operationProgress by viewModel.operationProgress.collectAsStateWithLifecycle()

    var showNewFolderDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<FileItem?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    BackHandler(enabled = pathStack.size > 1 || onExitScreen != null) {
        if (!viewModel.navigateBack()) {
            onExitScreen?.invoke()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search files...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            pathStack.lastOrNull()?.first ?: "Files",
                            style = MaterialTheme.typography.titleLarge,
                            color = ElectricCyan
                        )
                    }
                },
                navigationIcon = {
                    if (pathStack.size > 1 || onExitScreen != null) {
                        IconButton(onClick = {
                            if (!viewModel.navigateBack()) onExitScreen?.invoke()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSearch() }) {
                        Icon(
                            if (isSearching) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            if (viewMode == "grid") Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = "View mode"
                        )
                    }
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Sort") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Sort, null) },
                                onClick = { showMoreMenu = false; showSortMenu = true }
                            )
                            DropdownMenuItem(
                                text = { Text("New Folder") },
                                leadingIcon = { Icon(Icons.Default.CreateNewFolder, null) },
                                onClick = { showMoreMenu = false; showNewFolderDialog = true }
                            )
                            DropdownMenuItem(
                                text = { Text(if (viewModel.showHidden.value) "Hide Hidden Files" else "Show Hidden Files") },
                                leadingIcon = { Icon(if (viewModel.showHidden.value) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) },
                                onClick = { showMoreMenu = false; viewModel.toggleHiddenFiles() }
                            )
                            DropdownMenuItem(
                                text = { Text("Bookmark") },
                                leadingIcon = { Icon(Icons.Default.BookmarkBorder, null) },
                                onClick = { showMoreMenu = false; viewModel.toggleBookmark() }
                            )
                            if (selectedFiles.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Select All") },
                                    leadingIcon = { Icon(Icons.Default.SelectAll, null) },
                                    onClick = { showMoreMenu = false; viewModel.selectAll() }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = selectedFiles.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BatchActionBar(
                    selectedCount = selectedFiles.size,
                    hasClipboard = false,
                    onCopy = { viewModel.copySelected() },
                    onCut = { viewModel.cutSelected() },
                    onDelete = { showDeleteConfirm = true },
                    onPaste = { viewModel.paste() },
                    onClear = { viewModel.clearSelection() }
                )
            }
        },
        floatingActionButton = {
            if (selectedFiles.isEmpty()) {
                val clip = viewModel.clipboard
                FloatingActionButton(
                    onClick = {
                        if (clip != null) viewModel.paste()
                        else showNewFolderDialog = true
                    },
                    containerColor = ElectricCyan,
                    contentColor = MaterialTheme.colorScheme.background
                ) {
                    Icon(
                        if (clip != null) Icons.Default.ContentPaste else Icons.Default.CreateNewFolder,
                        contentDescription = null
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (!isSearching && pathStack.size > 1) {
                BreadcrumbBar(pathStack) { index -> viewModel.navigateToBreadcrumb(index) }
            }

            operationProgress?.let { (label, progress) ->
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(label, style = MaterialTheme.typography.labelSmall, color = ElectricCyan)
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = ElectricCyan
                    )
                }
            }

            when (val state = uiState) {
                is FilesUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElectricCyan)
                    }
                }
                is FilesUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = DangerRed)
                    }
                }
                is FilesUiState.Success -> {
                    if (state.files.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Empty folder", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else if (viewMode == "grid") {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.files) { file ->
                                FileGridItem(
                                    file = file,
                                    isSelected = selectedFiles.contains(file.path),
                                    onClick = {
                                        if (selectedFiles.isNotEmpty()) viewModel.toggleFileSelection(file.path)
                                        else if (file.isDirectory) viewModel.navigateToFolder(file)
                                        else onOpenViewer(file)
                                    },
                                    onLongClick = { viewModel.toggleFileSelection(file.path) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(state.files) { file ->
                                FileListItem(
                                    file = file,
                                    isSelected = selectedFiles.contains(file.path),
                                    onClick = {
                                        if (selectedFiles.isNotEmpty()) viewModel.toggleFileSelection(file.path)
                                        else if (file.isDirectory) viewModel.navigateToFolder(file)
                                        else onOpenViewer(file)
                                    },
                                    onLongClick = { viewModel.toggleFileSelection(file.path) },
                                    onRename = { showRenameDialog = file },
                                    onShare = { shareFile(file) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewFolderDialog) {
        NewItemDialog(
            title = "New Folder",
            onDismiss = { showNewFolderDialog = false },
            onConfirm = { name -> viewModel.createFolder(name); showNewFolderDialog = false }
        )
    }

    showRenameDialog?.let { file ->
        RenameDialog(
            currentName = file.name,
            onDismiss = { showRenameDialog = null },
            onConfirm = { newName -> viewModel.renameFile(file.path, newName); showRenameDialog = null }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete ${selectedFiles.size} items?") },
            text = { Text("Files will be moved to Recycle Bin") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteSelected(); showDeleteConfirm = false }) {
                    Text("Delete", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showSortMenu) {
        SortDialog(
            currentSort = sortMode,
            onDismiss = { showSortMenu = false },
            onSelect = { viewModel.setSortMode(it); showSortMenu = false }
        )
    }
}

@Composable
private fun BreadcrumbBar(pathStack: List<Pair<String, String>>, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        pathStack.forEachIndexed { index, (name, _) ->
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = if (index == pathStack.lastIndex) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onClick(index) }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
            if (index < pathStack.lastIndex) {
                Text(" / ", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileListItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRename: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    val iconColor = when (file.fileType) {
        FileType.FOLDER -> ElectricCyan
        FileType.IMAGE -> PhantomPurple
        FileType.VIDEO -> DangerRed
        FileType.AUDIO -> NeonGreen
        FileType.DOCUMENT -> AmberWarning
        FileType.APK -> NeonGreen
        FileType.ARCHIVE -> PhantomPurple
        FileType.CODE -> ElectricCyan
        FileType.OTHER -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ElectricCyan.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (file.isHidden) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    buildString {
                        if (!file.isDirectory) append(FormatUtils.formatSize(file.size))
                        if (!file.isDirectory) append(" • ")
                        append(FormatUtils.timeAgo(file.lastModified))
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            var showItemMenu by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { showItemMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", modifier = Modifier.size(18.dp))
                }
                DropdownMenu(expanded = showItemMenu, onDismissRequest = { showItemMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, null) },
                        onClick = { showItemMenu = false; onRename() }
                    )
                    if (!file.isDirectory) {
                        DropdownMenuItem(
                            text = { Text("Share") },
                            leadingIcon = { Icon(Icons.Default.Share, null) },
                            onClick = { showItemMenu = false; onShare() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileGridItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val iconColor = when (file.fileType) {
        FileType.FOLDER -> ElectricCyan
        FileType.IMAGE -> PhantomPurple
        FileType.VIDEO -> DangerRed
        FileType.AUDIO -> NeonGreen
        FileType.DOCUMENT -> AmberWarning
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ElectricCyan.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                file.name,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (!file.isDirectory) {
                Text(
                    FormatUtils.formatSize(file.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BatchActionBar(
    selectedCount: Int,
    hasClipboard: Boolean,
    onCopy: () -> Unit,
    onCut: () -> Unit,
    onDelete: () -> Unit,
    onPaste: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$selectedCount selected", modifier = Modifier.weight(1f).padding(start = 8.dp), style = MaterialTheme.typography.labelMedium)
            IconButton(onClick = onCopy) { Icon(Icons.Default.ContentCopy, "Copy", tint = ElectricCyan) }
            IconButton(onClick = onCut) { Icon(Icons.Default.ContentCut, "Cut", tint = AmberWarning) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = DangerRed) }
            if (hasClipboard) {
                IconButton(onClick = onPaste) { Icon(Icons.Default.ContentPaste, "Paste", tint = NeonGreen) }
            }
            IconButton(onClick = onClear) { Icon(Icons.Default.Close, "Clear") }
        }
    }
}

@Composable
private fun NewItemDialog(title: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Enter name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) {
                Text("Create", color = ElectricCyan)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun RenameDialog(currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) {
                Text("Rename", color = ElectricCyan)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun SortDialog(currentSort: String, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val options = listOf(
        "name_asc" to "Name (A-Z)", "name_desc" to "Name (Z-A)",
        "size_asc" to "Size (Small first)", "size_desc" to "Size (Large first)",
        "date_asc" to "Date (Old first)", "date_desc" to "Date (New first)",
        "type" to "Type"
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sort by") },
        text = {
            Column {
                options.forEach { (value, label) ->
                    TextButton(
                        onClick = { onSelect(value) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            label,
                            color = if (currentSort == value) ElectricCyan else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}

private fun shareFile(file: FileItem) { /* handled via context in real usage */ }

@Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
private fun FilesScreenPreview() {
    PhantomTheme { FilesScreen() }
}
