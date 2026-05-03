package com.phantomfiles.pro.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

class FTPServer(
    private val port: Int = 2121,
    private val rootDir: String = "/storage/emulated/0"
) {
    private var serverSocket: ServerSocket? = null
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun getLocalIpAddress(): String? {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun start() = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(port)
            _isRunning.value = true
            while (_isRunning.value) {
                try {
                    val client = serverSocket?.accept() ?: break
                    handleClient(client, rootDir)
                } catch (_: Exception) {
                    break
                }
            }
        } catch (_: Exception) {
            _isRunning.value = false
        }
    }

    fun stop() {
        _isRunning.value = false
        try {
            serverSocket?.close()
        } catch (_: Exception) { }
        serverSocket = null
    }

    private fun handleClient(socket: java.net.Socket, rootDir: String) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)
            writer.println("220 PhantomFiles FTP Server Ready")
            var currentDir = rootDir
            var running = true
            while (running) {
                val line = reader.readLine() ?: break
                val parts = line.trim().split(" ", limit = 2)
                val cmd = parts[0].uppercase()
                val arg = if (parts.size > 1) parts[1] else ""
                when (cmd) {
                    "USER" -> writer.println("230 User logged in")
                    "SYST" -> writer.println("215 UNIX Type: L8")
                    "PWD" -> writer.println("257 \"$currentDir\"")
                    "LIST" -> {
                        val files = File(currentDir).listFiles()
                        writer.println("150 Opening data connection")
                        files?.forEach { f ->
                            val perm = if (f.isDirectory) "drwxr-xr-x" else "-rw-r--r--"
                            val size = if (f.isDirectory) 0 else f.length()
                            writer.println("$perm 1 owner group $size ${f.name}")
                        }
                        writer.println("226 Transfer complete")
                    }
                    "CWD" -> {
                        val newDir = if (arg.startsWith("/")) arg else "$currentDir/$arg"
                        val canonicalNew = File(newDir).canonicalPath
                        val canonicalRoot = File(rootDir).canonicalPath
                        if (File(newDir).isDirectory && (canonicalNew == canonicalRoot || canonicalNew.startsWith(canonicalRoot + "/"))) {
                            currentDir = canonicalNew
                            writer.println("250 Directory changed to $currentDir")
                        } else {
                            writer.println("550 Directory not found")
                        }
                    }
                    "TYPE" -> writer.println("200 Type set")
                    "QUIT" -> {
                        writer.println("221 Goodbye")
                        running = false
                    }
                    else -> writer.println("502 Command not implemented")
                }
            }
            socket.close()
        } catch (_: Exception) { }
    }
}
