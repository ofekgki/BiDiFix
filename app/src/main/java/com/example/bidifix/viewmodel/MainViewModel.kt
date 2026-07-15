package com.example.bidifix.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bidifix.bidi.BidiTransformer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Immutable UI state for the single main screen. */
data class MainUiState(
    val originalText: String = "",
    val transformedText: String = "",
    val isTransformEnabled: Boolean = false,
) {
    /** The copy button is only enabled once there is output. */
    val isCopyEnabled: Boolean get() = transformedText.isNotEmpty()

    /** The swap/reuse action only makes sense once there is output. */
    val isSwapEnabled: Boolean get() = transformedText.isNotEmpty()
}

/**
 * Holds the screen state and delegates all bidi work to [BidiTransformer]. The UI never
 * performs transformation logic itself; it only reads [uiState] and calls these methods.
 */
class MainViewModel(
    private val transformer: BidiTransformer = BidiTransformer(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    /** Called on every edit of the input field. */
    fun updateOriginalText(text: String) {
        _uiState.update { it.copy(originalText = text, isTransformEnabled = text.isNotBlank()) }
    }

    /** Runs the transformation and publishes the result to the output panel. */
    fun transformText() {
        val result = transformer.transform(_uiState.value.originalText)
        _uiState.update { it.copy(transformedText = result) }
    }

    /**
     * Places text received from a share/process-text intent into the input field without
     * transforming it; the user still presses Transform explicitly.
     */
    fun setSharedText(text: String) {
        _uiState.update {
            it.copy(originalText = text, transformedText = "", isTransformEnabled = text.isNotBlank())
        }
    }

    /** Resets the screen to its initial state. */
    fun clearAll() {
        _uiState.value = MainUiState()
    }

    /** Moves the transformed output back into the input field for another pass. */
    fun reuseTransformed() {
        _uiState.update { state ->
            if (state.transformedText.isEmpty()) {
                state
            } else {
                MainUiState(
                    originalText = state.transformedText,
                    isTransformEnabled = state.transformedText.isNotBlank(),
                )
            }
        }
    }
}
