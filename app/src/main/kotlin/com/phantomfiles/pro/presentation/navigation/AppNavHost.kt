package com.phantomfiles.pro.presentation.navigation

import android.net.Uri
import android.os.Environment
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.presentation.ai.AIScreen
import com.phantomfiles.pro.presentation.appmanager.AppManagerScreen
import com.phantomfiles.pro.presentation.files.FilesScreen
import com.phantomfiles.pro.presentation.home.HomeScreen
import com.phantomfiles.pro.presentation.network.NetworkScreen
import com.phantomfiles.pro.presentation.permission.PermissionScreen
import com.phantomfiles.pro.presentation.recycle.RecycleScreen
import com.phantomfiles.pro.presentation.scanner.ScannerScreen
import com.phantomfiles.pro.presentation.settings.SettingsScreen
import com.phantomfiles.pro.presentation.vault.VaultScreen
import com.phantomfiles.pro.presentation.viewer.FileViewerScreen
import com.phantomfiles.pro.presentation.theme.ElectricCyan

data class NavItem(val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val route: String)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    var hasPermission by rememberSaveable { mutableStateOf(Environment.isExternalStorageManager()) }

    if (!hasPermission) {
        PermissionScreen(onPermissionGranted = { hasPermission = true })
    } else {
        MainScaffold(navController)
    }
}

@Composable
private fun MainScaffold(navController: NavHostController) {
    val items = listOf(
        NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, "home"),
        NavItem("Files", Icons.Filled.Folder, Icons.Outlined.Folder, "files"),
        NavItem("Scanner", Icons.Filled.Radar, Icons.Outlined.Radar, "scanner"),
        NavItem("AI", Icons.Filled.SmartToy, Icons.Outlined.SmartToy, "ai"),
        NavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "settings"),
    )
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in items.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    items.forEachIndexed { index, item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                selectedTab = index
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElectricCyan,
                                selectedTextColor = ElectricCyan,
                                indicatorColor = ElectricCyan.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToFolder = { path, name ->
                        navController.navigate("folder_files/${Uri.encode(path)}/${Uri.encode(name)}")
                    },
                    onNavigateToRecycleBin = { navController.navigate("recycle_bin") },
                    onNavigateToVault = { navController.navigate("vault") },
                    onNavigateToAppManager = { navController.navigate("app_manager") },
                    onNavigateToNetwork = { navController.navigate("network") },
                    onNavigateToScanner = {
                        navController.navigate("scanner") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToAI = {
                        navController.navigate("ai") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable("files") {
                FilesScreen(
                    onOpenViewer = { file -> navigateToViewer(navController, file) }
                )
            }

            composable("scanner") { ScannerScreen() }
            composable("ai") { AIScreen() }
            composable("settings") { SettingsScreen() }

            composable(
                "folder_files/{path}/{name}",
                arguments = listOf(
                    navArgument("path") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val path = Uri.decode(backStackEntry.arguments?.getString("path") ?: "")
                val name = Uri.decode(backStackEntry.arguments?.getString("name") ?: "Files")
                FilesScreen(
                    onOpenViewer = { file -> navigateToViewer(navController, file) },
                    onExitScreen = { navController.popBackStack() },
                    initialPath = path,
                    initialName = name
                )
            }

            composable("recycle_bin") {
                RecycleScreen(onBack = { navController.popBackStack() })
            }

            composable("vault") {
                VaultScreen(onBack = { navController.popBackStack() })
            }

            composable("app_manager") {
                AppManagerScreen(onBack = { navController.popBackStack() })
            }

            composable("network") {
                NetworkScreen(onBack = { navController.popBackStack() })
            }

            composable(
                "viewer/{path}/{name}/{type}",
                arguments = listOf(
                    navArgument("path") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType },
                    navArgument("type") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val path = Uri.decode(backStackEntry.arguments?.getString("path") ?: "")
                val name = Uri.decode(backStackEntry.arguments?.getString("name") ?: "")
                val type = backStackEntry.arguments?.getString("type") ?: "other"
                FileViewerScreen(filePath = path, fileName = name, fileType = type, onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun navigateToViewer(navController: NavHostController, file: FileItem) {
    val ext = file.extension.lowercase()
    val type = when {
        ext in listOf("jpg", "jpeg", "png", "gif", "webp", "heic", "bmp", "svg", "heif") -> "image"
        ext in listOf("mp4", "mkv", "avi", "mov", "3gp", "webm", "flv", "wmv", "m4v") -> "video"
        ext in listOf("mp3", "wav", "flac", "aac", "ogg", "m4a", "wma", "opus") -> "audio"
        ext in listOf("pdf") -> "pdf"
        ext in listOf("kt", "java", "py", "js", "ts", "html", "css", "xml", "json", "c", "cpp", "h", "rs", "go", "rb", "php", "sh", "yml", "yaml", "toml", "gradle", "swift", "dart") -> "code"
        ext in listOf("txt", "log", "md", "csv", "ini", "cfg", "conf", "properties", "env", "gitignore", "dockerfile") -> "text"
        ext in listOf("apk", "xapk") -> "apk"
        else -> "other"
    }
    navController.navigate("viewer/${Uri.encode(file.path)}/${Uri.encode(file.name)}/$type")
}
