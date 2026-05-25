package com.example.lifeinpoints.daily_checkup.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.lifeinpoints.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

private const val TRANSCRIPT_DISPLAY_DELAY_MS = 1_500L
private const val COMPLETE_SILENCE_LENGTH_MS = 2_000L
private const val POSSIBLY_COMPLETE_SILENCE_LENGTH_MS = 1_500L

@Composable
fun VoiceRecognitionScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLocale = LocalConfiguration.current.locales[0]
    val scope = rememberCoroutineScope()
    val initialPermissionGranted = remember(context) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    val controller = remember(context, scope, initialPermissionGranted, currentLocale) {
        VoiceRecognitionController(
            context = context.applicationContext,
            scope = scope,
            initialPermissionGranted = initialPermissionGranted,
            recognitionLocale = currentLocale
        )
    }
    val uiState by controller.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        controller.onPermissionResult(granted)
    }

    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.voice_recognition_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        val statusText = when {
            !uiState.recognitionAvailable -> stringResource(R.string.voice_recognition_unavailable)
            !uiState.hasPermission -> stringResource(R.string.voice_recognition_permission_text)
            uiState.isListening -> stringResource(R.string.voice_recognition_listening)
            else -> stringResource(R.string.voice_recognition_ready)
        }
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (uiState.errorCode != 0) {
            Text(
                text = speechRecognitionErrorMessage(uiState.errorCode),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = uiState.transcript.ifBlank {
                    stringResource(R.string.voice_recognition_transcript_placeholder)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = if (uiState.transcript.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { controller.clearTranscript() },
                modifier = Modifier.weight(1f),
                enabled = uiState.transcript.isNotBlank()
            ) {
                Text(stringResource(R.string.voice_recognition_clear))
            }

            Button(
                onClick = {
                    when {
                        uiState.isListening -> controller.stopListening()
                        uiState.hasPermission -> controller.startListening()
                        else -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = uiState.recognitionAvailable
            ) {
                Text(
                    if (uiState.isListening) {
                        stringResource(R.string.voice_recognition_stop)
                    } else if (uiState.hasPermission) {
                        stringResource(R.string.voice_recognition_start)
                    } else {
                        stringResource(R.string.voice_recognition_request_permission)
                    }
                )
            }
        }
    }
}

private data class VoiceRecognitionUiState(
    val hasPermission: Boolean,
    val recognitionAvailable: Boolean,
    val isListening: Boolean = false,
    val transcript: String = "",
    val errorCode: Int = 0
)

private class VoiceRecognitionController(
    context: android.content.Context,
    private val scope: CoroutineScope,
    initialPermissionGranted: Boolean,
    private val recognitionLocale: Locale
) {
    private val recognitionAvailable = SpeechRecognizer.isRecognitionAvailable(context)
    private val recognizer = if (recognitionAvailable) {
        SpeechRecognizer.createSpeechRecognizer(context)
    } else {
        null
    }
    private var transcriptUpdateJob: Job? = null

    private val _uiState = MutableStateFlow(
        VoiceRecognitionUiState(
            hasPermission = initialPermissionGranted,
            recognitionAvailable = recognitionAvailable
        )
    )
    val uiState: StateFlow<VoiceRecognitionUiState> = _uiState.asStateFlow()

    init {
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _uiState.update { it.copy(errorCode = 0, isListening = true) }
            }

            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit

            override fun onEndOfSpeech() {
                _uiState.update { it.copy(isListening = false) }
            }

            override fun onError(error: Int) {
                transcriptUpdateJob?.cancel()
                _uiState.update { it.copy(isListening = false, errorCode = error) }
            }

            override fun onResults(results: Bundle?) {
                val finalText = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                _uiState.update { it.copy(isListening = false, errorCode = 0) }
                displayTranscriptAfterPause(finalText)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                displayTranscriptAfterPause(partial)
            }

            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                hasPermission = granted,
                errorCode = if (granted) 0 else SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS
            )
        }
    }

    fun startListening() {
        val state = _uiState.value
        if (!state.hasPermission || !state.recognitionAvailable) return

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_ENABLE_FORMATTING,
                RecognizerIntent.FORMATTING_OPTIMIZE_QUALITY
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, recognitionLocale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                COMPLETE_SILENCE_LENGTH_MS
            )
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                POSSIBLY_COMPLETE_SILENCE_LENGTH_MS
            )
        }

        _uiState.update { it.copy(isListening = true, errorCode = 0) }
        recognizer?.startListening(intent)
    }

    fun stopListening() {
        recognizer?.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

    fun clearTranscript() {
        transcriptUpdateJob?.cancel()
        _uiState.update { it.copy(transcript = "") }
    }

    fun release() {
        transcriptUpdateJob?.cancel()
        recognizer?.stopListening()
        recognizer?.destroy()
    }

    private fun displayTranscriptAfterPause(text: String) {
        if (text.isBlank()) return

        transcriptUpdateJob?.cancel()
        transcriptUpdateJob = scope.launch {
            delay(TRANSCRIPT_DISPLAY_DELAY_MS)
            _uiState.update { it.copy(transcript = text) }
        }
    }
}

@Composable
private fun speechRecognitionErrorMessage(errorCode: Int): String =
    when (errorCode) {
        SpeechRecognizer.ERROR_NO_MATCH,
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> stringResource(R.string.voice_recognition_error_no_match)
        SpeechRecognizer.ERROR_AUDIO -> stringResource(R.string.voice_recognition_error_audio)
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
        SpeechRecognizer.ERROR_SERVER -> stringResource(R.string.voice_recognition_error_network)
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> stringResource(R.string.voice_recognition_error_permission)
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> stringResource(R.string.voice_recognition_error_busy)
        else -> stringResource(R.string.voice_recognition_error_generic)
    }
