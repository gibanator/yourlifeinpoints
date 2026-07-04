package com.example.lifeinpoints.aiScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.remote.api.AiApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

data class AiModeUiState(
    val inputText: String = "",
    val isTranscribing: Boolean = false,
    val transcriptionError: String? = null
)

@HiltViewModel
class AiModeViewModel @Inject constructor(
    private val aiApi: AiApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiModeUiState())
    val uiState: StateFlow<AiModeUiState> = _uiState.asStateFlow()

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text, transcriptionError = null) }
    }

    fun transcribeAudio(file: File) {
        if (!file.exists() || file.length() == 0L) {
            _uiState.update { it.copy(transcriptionError = "Recording is empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isTranscribing = true, transcriptionError = null) }
            try {
                val requestBody = file.asRequestBody("audio/mp4".toMediaType())
                val part = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = file.name,
                    body = requestBody
                )
                val response = aiApi.transcribe(part)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _uiState.update {
                        it.copy(
                            isTranscribing = false,
                            transcriptionError = "Transcription failed (${response.code()})"
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        inputText = mergeTranscription(it.inputText, body.text),
                        isTranscribing = false,
                        transcriptionError = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isTranscribing = false,
                        transcriptionError = e.message ?: "Transcription failed"
                    )
                }
            } finally {
                file.delete()
            }
        }
    }

    private fun mergeTranscription(currentText: String, transcription: String): String {
        val text = transcription.trim()
        if (text.isBlank()) return currentText
        if (currentText.isBlank()) return text
        return currentText.trimEnd() + "\n" + text
    }
}
