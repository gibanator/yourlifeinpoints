package com.example.lifeinpoints.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun RegistrationScreen(
    vm: RegisterViewModel = hiltViewModel()
) {
    val uiState = vm.uiState.collectAsState()

    Column {
        OutlinedTextField(
            value = uiState.value.email,
            onValueChange = vm::onEmailChanged,
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = uiState.value.password,
            onValueChange = vm::onPasswordChanged,
            label = { Text("Password") }
        )

        Button(
            onClick = { vm.register() },
            enabled = !uiState.value.isLoading
        ) {
            Text("Register")
        }

        uiState.value.errorMessage?.let {
            Text("Error: $it")
        }

        uiState.value.successMessage?.let {
            Text(it)
        }
    }
}