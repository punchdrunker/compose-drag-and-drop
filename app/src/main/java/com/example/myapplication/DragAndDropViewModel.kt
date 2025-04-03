package com.example.myapplication

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DragAndDropViewModel @Inject constructor(): ViewModel() {
    private val _uiState = MutableStateFlow(DragAndDropUiState())
    val uiState = _uiState.asStateFlow()

    fun updateState(event: DragAndDropEvent) {
        val dragEvent = event.toAndroidDragEvent()
        val summary = "dragged x: ${dragEvent.x}, y: ${dragEvent.y}, label: ${dragEvent.clipData.description.label}"
        _uiState.update { it.copy(summary = summary) }
    }
}

data class DragAndDropUiState(val summary: String? = null)