package com.proyecto.myapplicationtinkuy.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.ui.auth.AuthUiState

@Composable
fun ForgotPasswordScreen(
    state: AuthUiState,
    onBack: () -> Unit,
    onSendInstructions: (email: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }

    val validEmail = android.util.Patterns.EMAIL_ADDRESS
        .matcher(email.trim())
        .matches()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }

        Spacer(Modifier.height(75.dp))

        Text(text = "Recupera tu contraseña", fontSize = 26.sp)

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Ingresa tu correo electrónico y te enviaremos instrucciones para recuperar tu acceso.",
            fontSize = 16.sp,
            lineHeight = 23.sp
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(8.dp),
            isError = email.isNotEmpty() && !validEmail
        )

        if (email.isNotEmpty() && !validEmail) {
            Text(
                text = "Ingresa un correo electrónico válido.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(22.dp))

        if (state.resetSent) {
            Text(
                "Si el correo está registrado, recibirás las instrucciones. Revisa también la carpeta de spam.",
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        state.error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                if (state.resetSent) onBackToLogin() else onSendInstructions(email.trim())
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = validEmail && !state.loading,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                when {
                    state.loading -> "Enviando..."
                    state.resetSent -> "Volver a iniciar sesión"
                    else -> "Enviar instrucciones"
                }
            )
        }

        Spacer(Modifier.weight(1f))
    }
}