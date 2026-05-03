package com.phantomfiles.pro.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phantomfiles.pro.presentation.theme.AmberWarning
import com.phantomfiles.pro.presentation.theme.ElectricCyan
import com.phantomfiles.pro.presentation.theme.NeonGreen
import com.phantomfiles.pro.presentation.theme.PhantomPurple
import com.phantomfiles.pro.presentation.theme.PhantomTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showRecycleDaysDialog by remember { mutableStateOf(false) }
    var showFtpPasswordDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineLarge, color = ElectricCyan)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item { SectionHeader("Access Modes") }
        item {
            SettingsCard(
                icon = Icons.Default.PhoneAndroid,
                title = "Shizuku",
                subtitle = if (state.shizukuConnected) "Connected" else if (state.shizukuAvailable) "Available" else "Not running",
                iconTint = if (state.shizukuConnected) NeonGreen else AmberWarning,
                onClick = { viewModel.requestShizukuPermission() }
            )
        }
        item {
            SettingsCard(
                icon = Icons.Default.Security,
                title = "Root Access",
                subtitle = if (state.isRooted) "Device is rooted" else "Not rooted",
                iconTint = if (state.isRooted) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item { SectionHeader("File Browser") }
        item {
            SettingsToggle(
                icon = Icons.Default.Visibility,
                title = "Show Hidden Files",
                subtitle = "Show files starting with dot (.)",
                checked = state.showHiddenFiles,
                onCheckedChange = { viewModel.setShowHiddenFiles(it) }
            )
        }

        item { SectionHeader("Security") }
        item {
            SettingsToggle(
                icon = Icons.Default.Fingerprint,
                title = "Biometric Lock",
                subtitle = "Require fingerprint for vault",
                checked = state.biometricEnabled,
                onCheckedChange = { viewModel.setBiometricEnabled(it) }
            )
        }

        item { SectionHeader("Recycle Bin") }
        item {
            SettingsCard(
                icon = Icons.Default.AutoDelete,
                title = "Auto Empty",
                subtitle = "After ${state.recycleBinDays} days",
                iconTint = AmberWarning,
                onClick = { showRecycleDaysDialog = true }
            )
        }

        item { SectionHeader("Cleanup") }
        item {
            SettingsToggle(
                icon = Icons.Default.CleaningServices,
                title = "Auto Clean Cache",
                subtitle = "Clean app cache weekly",
                checked = state.autoCleanCache,
                onCheckedChange = { viewModel.setAutoCleanCache(it) }
            )
        }

        item { SectionHeader("Network") }
        item {
            SettingsCard(
                icon = Icons.Default.Key,
                title = "FTP Password",
                subtitle = if (state.ftpPassword.isNotEmpty()) "Password set" else "No password (open access)",
                iconTint = if (state.ftpPassword.isNotEmpty()) NeonGreen else AmberWarning,
                onClick = { showFtpPasswordDialog = true }
            )
        }

        item { SectionHeader("AI") }
        item {
            SettingsCard(
                icon = Icons.Default.SmartToy,
                title = "Groq API Key",
                subtitle = if (state.groqApiKey.isNotEmpty()) "Configured" else "Not set (offline mode)",
                iconTint = if (state.groqApiKey.isNotEmpty()) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { showApiKeyDialog = true }
            )
        }

        item { SectionHeader("About") }
        item {
            SettingsCard(
                icon = Icons.Default.Info,
                title = "PhantomFiles Pro",
                subtitle = "v1.0.0 • Har file. Har raaz. Har jagah.",
                iconTint = PhantomPurple
            )
        }
        item {
            SettingsCard(
                icon = Icons.Default.Code,
                title = "Architecture",
                subtitle = "MVVM + Clean • Kotlin 100%",
                iconTint = ElectricCyan
            )
        }
    }

    if (showRecycleDaysDialog) {
        var days by remember { mutableStateOf(state.recycleBinDays.toString()) }
        AlertDialog(
            onDismissRequest = { showRecycleDaysDialog = false },
            title = { Text("Recycle Bin Auto-Empty") },
            text = {
                Column {
                    Text("Files in recycle bin will be auto-deleted after this many days", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("30") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val d = days.toIntOrNull() ?: 30
                    viewModel.setRecycleBinDays(d.coerceIn(1, 365))
                    showRecycleDaysDialog = false
                }) { Text("Save", color = ElectricCyan) }
            },
            dismissButton = { TextButton(onClick = { showRecycleDaysDialog = false }) { Text("Cancel") } }
        )
    }

    if (showFtpPasswordDialog) {
        var password by remember { mutableStateOf(state.ftpPassword) }
        AlertDialog(
            onDismissRequest = { showFtpPasswordDialog = false },
            title = { Text("FTP Server Password") },
            text = {
                Column {
                    Text("Set password for FTP file transfers. Leave blank for open access.", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter password") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setFtpPassword(password)
                    showFtpPasswordDialog = false
                }) { Text("Save", color = ElectricCyan) }
            },
            dismissButton = { TextButton(onClick = { showFtpPasswordDialog = false }) { Text("Cancel") } }
        )
    }

    if (showApiKeyDialog) {
        var key by remember { mutableStateOf(state.groqApiKey) }
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("Groq API Key") },
            text = {
                Column {
                    Text("Get key from console.groq.com", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = key,
                        onValueChange = { key = it },
                        placeholder = { Text("gsk_...") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.setGroqApiKey(key); showApiKeyDialog = false }) {
                    Text("Save", color = ElectricCyan)
                }
            },
            dismissButton = { TextButton(onClick = { showApiKeyDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = PhantomPurple,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: androidx.compose.ui.graphics.Color = ElectricCyan,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedTrackColor = ElectricCyan)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
private fun SettingsScreenPreview() {
    PhantomTheme { SettingsScreen() }
}
