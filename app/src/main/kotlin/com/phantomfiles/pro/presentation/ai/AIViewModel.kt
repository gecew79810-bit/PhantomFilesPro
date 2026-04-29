package com.phantomfiles.pro.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.domain.usecase.AIAction
import com.phantomfiles.pro.domain.usecase.AICommandUseCase
import com.phantomfiles.pro.domain.usecase.AIResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val action: AIAction = AIAction.NONE,
    val files: List<FileItem> = emptyList()
)

@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiCommandUseCase: AICommandUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Main PhantomFiles AI Assistant hoon. Koi bhi command do!\n\nExamples:\n• \"Cache delete karo\"\n• \"Large files dikha\"\n• \"Duplicate photos hata do\"\n• \"Storage report dikha\"\n• \"Disguised files scan karo\"",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    fun sendCommand(command: String) {
        if (command.isBlank()) return
        val updated = _messages.value + ChatMessage(text = command, isUser = true)
        _messages.value = updated
        _isProcessing.value = true

        viewModelScope.launch {
            aiCommandUseCase.processCommand(command)
                .catch { e ->
                    _messages.value = _messages.value + ChatMessage(
                        text = "Error: ${e.message}",
                        isUser = false
                    )
                    _isProcessing.value = false
                }
                .collect { response ->
                    _messages.value = _messages.value + ChatMessage(
                        text = response.message,
                        isUser = false,
                        action = response.action,
                        files = response.files
                    )
                    _isProcessing.value = false
                }
        }
    }
}
