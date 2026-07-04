package com.example.lifeinpoints.aiScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.remote.api.AiApi
import com.example.lifeinpoints.data.remote.auth.AuthTokenProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/** Вариант модели для меню выбора: [id] уходит в запрос (provider), [label] показываем в UI. */
data class AiProviderOption(
    val id: String,
    val label: String
)

data class AiModeUiState(
    val inputText: String = "",
    // По умолчанию — GigaChat, чтобы UI всегда что-то показывал до ответа /models.
    val availableProviders: List<AiProviderOption> = listOf(AiProviderOption("gigachat", "GigaChat")),
    val selectedProvider: String = "gigachat",
    val isModelMenuVisible: Boolean = false
    val isTranscribing: Boolean = false,
    val transcriptionError: String? = null
)

@HiltViewModel
class AiModeViewModel @Inject constructor(
    private val aiApi: AiApi,
    private val authTokenProvider: AuthTokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiModeUiState())
    val uiState: StateFlow<AiModeUiState> = _uiState.asStateFlow()

    // Дата, для которой сейчас набирается текст. Нужна, чтобы чистить поле при смене дня.
    private var currentDay: String? = null

    init {
        loadModels()
    }

    /**
     * Сообщает VM активный день. Если день сменился — очищаем поле ввода,
     * чтобы каждый день AI-экран открывался пустым (текст не переносится со вчера).
     */
    fun onDayChanged(day: String) {
        if (currentDay != null && currentDay != day) {
            _uiState.update { it.copy(inputText = "") }
        }
        currentDay = day
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text, transcriptionError = null) }
    }

    fun onModelMenuVisibilityChanged(visible: Boolean) {
        _uiState.update { it.copy(isModelMenuVisible = visible) }
    }

    fun onProviderSelected(providerId: String) {
        _uiState.update { it.copy(selectedProvider = providerId, isModelMenuVisible = false) }
    }

    /** Тянет список реально доступных на бэкенде провайдеров (GET /api/v1/ai/models). */
    private fun loadModels() {
        viewModelScope.launch {
            try {
                val auth = authTokenProvider.getAuthorizationHeader()
                val response = aiApi.getModels(auth)
                val body = response.body()
                if (response.isSuccessful && !body.isNullOrEmpty()) {
                    val options = body.map { it.toProviderOption() }
                    _uiState.update { state ->
                        val stillAvailable = options.any { it.id == state.selectedProvider }
                        state.copy(
                            availableProviders = options,
                            selectedProvider = if (stillAvailable) state.selectedProvider else options.first().id
                        )
                    }
                }
            } catch (_: Exception) {
                // Нет сети / ошибка — оставляем дефолтный список (GigaChat).
            }
        }
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

/** Маппинг enum-имени с бэкенда ("GIGACHAT") в опцию меню (id для запроса + человекочитаемый label). */
private fun String.toProviderOption(): AiProviderOption {
    val id = lowercase()
    val label = when (id) {
        "gigachat" -> "GigaChat"
        "claude" -> "Claude"
        "qwen" -> "Qwen"
        "deepseek" -> "DeepSeek"
        else -> replaceFirstChar { it.uppercase() }
    }
    return AiProviderOption(id, label)
}
