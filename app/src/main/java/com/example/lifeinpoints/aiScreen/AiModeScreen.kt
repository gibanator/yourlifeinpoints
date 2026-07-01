package com.example.lifeinpoints.aiScreen

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeinpoints.R

@Composable
fun AiModeScreen(
    vm: AiModeViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onSubmit: (String) -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    var isTextFieldFocused by remember { mutableStateOf(false) }

    BackHandler(enabled = isTextFieldFocused) {
        focusManager.clearFocus()
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
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(gapSmall))
                Text(
                    text = errorMessage,
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
                    enabled = uiState.inputText.isNotBlank() && !isLoading,
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
                    onClick = { /* TODO: запись голоса */ },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = stringResource(R.string.ai_mode_mic_content_description)
                    )
                }
            }
        }
    }
}
