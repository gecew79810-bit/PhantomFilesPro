package com.phantomfiles.pro.presentation.vault

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phantomfiles.pro.presentation.theme.DangerRed
import com.phantomfiles.pro.presentation.theme.ElectricCyan
import com.phantomfiles.pro.presentation.theme.NeonGreen
import com.phantomfiles.pro.presentation.theme.PhantomPurple
import com.phantomfiles.pro.presentation.theme.PhantomTheme
import com.phantomfiles.pro.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    viewModel: VaultViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Private Vault", color = PhantomPurple) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is VaultUiState.Unlocked) {
                        IconButton(onClick = { viewModel.lock() }) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock", tint = DangerRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is VaultUiState.Locked -> LockScreen(
                modifier = Modifier.padding(padding),
                onUnlock = { pin -> viewModel.unlock(pin) },
                onBiometricSuccess = { viewModel.unlockViaBiometric() }
            )
            is VaultUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricCyan)
                }
            }
            is VaultUiState.Error -> {
                LockScreen(
                    modifier = Modifier.padding(padding),
                    error = state.message,
                    onUnlock = { pin -> viewModel.unlock(pin) },
                    onBiometricSuccess = { viewModel.unlockViaBiometric() }
                )
            }
            is VaultUiState.Unlocked -> {
                if (state.files.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = PhantomPurple, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Vault is empty", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Import files to encrypt them", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                "${state.files.size} files • ${FormatUtils.formatSize(state.totalSize)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = PhantomPurple
                            )
                        }
                        items(state.files) { file ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = PhantomPurple)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(file.originalName, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${FormatUtils.formatSize(file.fileSize)} • ${FormatUtils.timeAgo(file.addedAt)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { viewModel.removeFile(file) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DangerRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LockScreen(
    modifier: Modifier = Modifier,
    error: String = "",
    onUnlock: (String) -> Unit,
    onBiometricSuccess: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val biometricAvailable = remember {
        val mgr = BiometricManager.from(context)
        mgr.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
    }

    LaunchedEffect(biometricAvailable) {
        if (biometricAvailable && activity != null) {
            showBiometricPrompt(activity, onBiometricSuccess)
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PhantomPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = PhantomPurple, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Enter PIN", style = MaterialTheme.typography.headlineSmall)
            if (error.isNotEmpty()) {
                Text(error, color = DangerRed, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 6) pin = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                placeholder = { Text("4-6 digit PIN") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { if (pin.length >= 4) onUnlock(pin) },
                colors = ButtonDefaults.buttonColors(containerColor = PhantomPurple),
                enabled = pin.length >= 4
            ) {
                Text("Unlock")
            }
            if (biometricAvailable) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        if (activity != null) showBiometricPrompt(activity, onBiometricSuccess)
                    }
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Use Fingerprint", color = NeonGreen)
                }
            }
        }
    }
}

private fun showBiometricPrompt(activity: FragmentActivity, onSuccess: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            onSuccess()
        }
    }
    val prompt = BiometricPrompt(activity, executor, callback)
    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle("PhantomFiles Vault")
        .setSubtitle("Authenticate to unlock your vault")
        .setNegativeButtonText("Use PIN instead")
        .build()
    try {
        prompt.authenticate(info)
    } catch (_: Exception) { }
}

@Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
private fun VaultScreenPreview() {
    PhantomTheme { VaultScreen() }
}
