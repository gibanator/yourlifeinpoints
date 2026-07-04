package com.example.lifeinpoints.aiScreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import com.example.lifeinpoints.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiModeScreen(
    vm: AiModeViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onSubmit: (String) -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val audioRecorder = remember(context) {
        AiAudioRecorder(context.applicationContext)
    }

    val microphonePermissionDenied =
        stringResource(R.string.ai_mode_microphone_permission_denied)

    var isTextFieldFocused by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var showVoiceSheet by remember { mutableStateOf(false) }
    var recordingError by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            runCatching {
                audioRecorder.start()
                isRecording = true
                recordingError = null
            }.onFailure { error ->
                recordingError = error.message
            }
        } else {
            recordingError = microphonePermissionDenied
        }
    }
    val voiceSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun startRecording() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            runCatching {
                audioRecorder.start()
                isRecording = true
                recordingError = null
            }.onFailure { error ->
                recordingError = error.message
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopRecordingAndTranscribe() {
        runCatching { audioRecorder.stop() }
            .onSuccess { file ->
                isRecording = false
                recordingError = null
                vm.transcribeAudio(file)
            }
            .onFailure { error ->
                isRecording = false
                recordingError = error.message
                audioRecorder.release()
            }
    }

    BackHandler(enabled = isTextFieldFocused) {
        focusManager.clearFocus()
    }

    DisposableEffect(audioRecorder) {
        onDispose {
            audioRecorder.release()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val halfHeight = maxHeight / 2
        val hPad = maxWidth * 0.04f
        val vPad = maxHeight * 0.01f
        val gapSmall = maxHeight * 0.01f
        val gapMedium = maxHeight * 0.015f
        val iconTextGap = maxWidth * 0.02f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = hPad, vertical = vPad)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.ai_mode_back_content_description)
                    )
                }
                Text(
                    text = stringResource(R.string.ai_mode_screen_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(gapSmall))
            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = vm::onInputChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = halfHeight)
                    .onFocusChanged { isTextFieldFocused = it.isFocused },
                placeholder = { Text(stringResource(R.string.ai_mode_input_placeholder)) },
                maxLines = Int.MAX_VALUE,
            )
            val displayedError = errorMessage
            if (displayedError != null) {
                Spacer(modifier = Modifier.height(gapSmall))
                Text(
                    text = displayedError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(gapMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(iconTextGap),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onSubmit(uiState.inputText) },
                    enabled = uiState.inputText.isNotBlank() && !isLoading && !uiState.isTranscribing && !isRecording,
                    modifier = Modifier.weight(2f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(iconTextGap))
                        Text(stringResource(R.string.ai_mode_send_button))
                    }
                }
                FilledIconButton(
                    onClick = { showVoiceSheet = true },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isTranscribing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = stringResource(R.string.ai_mode_mic_content_description)
                        )
                    }
                }
            }
        }
    }

    if (showVoiceSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                if (isRecording) {
                    audioRecorder.release()
                    isRecording = false
                }
                showVoiceSheet = false
            },
            sheetState = voiceSheetState
        ) {
            VoiceRecordingSheetContent(
                isRecording = isRecording,
                isTranscribing = uiState.isTranscribing,
                error = recordingError ?: uiState.transcriptionError,
                onPrimaryClick = {
                    if (isRecording) {
                        stopRecordingAndTranscribe()
                    } else {
                        startRecording()
                    }
                },
                onClose = {
                    if (isRecording) {
                        audioRecorder.release()
                        isRecording = false
                    }
                    showVoiceSheet = false
                }
            )
        }
    }
}

@Composable
private fun VoiceRecordingSheetContent(
    isRecording: Boolean,
    isTranscribing: Boolean,
    error: String?,
    onPrimaryClick: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.ai_mode_voice_sheet_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        if (isTranscribing) {
            CircularProgressIndicator()
        } else {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isRecording) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }

        Text(
            text = when {
                isRecording -> stringResource(R.string.ai_mode_recording_status)
                isTranscribing -> stringResource(R.string.ai_mode_transcribing_status)
                else -> stringResource(R.string.ai_mode_voice_sheet_ready)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = onPrimaryClick,
            enabled = !isTranscribing,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(
                    if (isRecording) {
                        R.string.ai_mode_stop_recording_button
                    } else {
                        R.string.ai_mode_start_recording_button
                    }
                )
            )
        }

        Button(
            onClick = onClose,
            enabled = !isTranscribing,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.ai_mode_close_button))
        }
    }
}

private class AiAudioRecorder(
    private val context: Context
) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun start() {
        release()
        val file = File.createTempFile("ai_mode_recording_", ".m4a", context.cacheDir)
        outputFile = file
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44_100)
            setAudioEncodingBitRate(128_000)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
    }

    fun stop(): File {
        val file = outputFile ?: error("No active recording")
        val activeRecorder = recorder ?: error("No active recording")
        runCatching { activeRecorder.stop() }
        activeRecorder.release()
        recorder = null
        outputFile = null
        return file
    }

    fun release() {
        recorder?.release()
        recorder = null
        outputFile?.delete()
        outputFile = null
    }
}
