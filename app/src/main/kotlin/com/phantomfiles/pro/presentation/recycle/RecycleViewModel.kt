package com.phantomfiles.pro.presentation.recycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.model.RecycleBinItem
import com.phantomfiles.pro.data.repository.RecycleBinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecycleUiState {
    data object Loading : RecycleUiState()
    data class Success(val items: List<RecycleBinItem>, val totalSize: Long) : RecycleUiState()
    data class Error(val message: String) : RecycleUiState()
}

@HiltViewModel
class RecycleViewModel @Inject constructor(
    private val recycleBinRepository: RecycleBinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecycleUiState>(RecycleUiState.Loading)
    val uiState: StateFlow<RecycleUiState> = _uiState

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = RecycleUiState.Loading
            recycleBinRepository.getAllItems().collect { items ->
                val totalSize = recycleBinRepository.getTotalSize()
                _uiState.value = RecycleUiState.Success(items, totalSize)
            }
        }
    }

    fun restoreItem(item: RecycleBinItem) {
        viewModelScope.launch {
            recycleBinRepository.restoreItem(item)
        }
    }

    fun permanentlyDelete(item: RecycleBinItem) {
        viewModelScope.launch {
            recycleBinRepository.permanentlyDelete(item)
        }
    }

    fun emptyRecycleBin() {
        viewModelScope.launch {
            recycleBinRepository.emptyRecycleBin()
        }
    }
}
