package com.example.lifeinpoints.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.AppTopAppBar
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val serverClientId = stringResource(R.string.default_web_client_id)
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val googleLoginError = stringResource(R.string.login_google_error)
    var isChoosingGoogleAccount by remember { mutableStateOf(false) }

    val emailLoginEnabled = uiState.email.isNotBlank() &&
            uiState.password.isNotBlank() &&
            !uiState.isLoading &&
            !isChoosingGoogleAccount

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                LoginEvent.Success -> onLoginSuccess()
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(stringResource(R.string.login_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { vm.login() },
                        enabled = emailLoginEnabled
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Log in")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        isChoosingGoogleAccount = true
                        try {
                            val googleOption = GetSignInWithGoogleOption.Builder(
                                serverClientId = serverClientId
                            ).build()
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleOption)
                                .build()
                            val credential = credentialManager
                                .getCredential(context = context, request = request)
                                .credential

                            if (
                                credential is CustomCredential &&
                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                            ) {
                                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                vm.loginWithGoogle(googleCredential.idToken)
                            } else {
                                vm.onGoogleLoginError(googleLoginError)
                            }
                        } catch (_: GetCredentialCancellationException) {
                            // The user closed the account chooser; leave the form unchanged.
                        } catch (_: GetCredentialException) {
                            vm.onGoogleLoginError(googleLoginError)
                        } catch (_: Exception) {
                            vm.onGoogleLoginError(googleLoginError)
                        } finally {
                            isChoosingGoogleAccount = false
                        }
                    }
                },
                enabled = !uiState.isLoading && !isChoosingGoogleAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isChoosingGoogleAccount || uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_google_logo),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.login_google_button),
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = vm::onEmailChanged,
                label = { Text(stringResource(R.string.registration_email_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                isError = uiState.errorMessage != null
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = vm::onPasswordChanged,
                label = { Text(stringResource(R.string.registration_password_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (emailLoginEnabled) {
                            vm.login()
                        }
                    }
                ),
                isError = uiState.errorMessage != null
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
