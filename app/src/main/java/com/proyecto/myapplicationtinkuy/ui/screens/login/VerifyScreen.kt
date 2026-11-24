package com.proyecto.myapplicationtinkuy.ui.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.ui.auth.AuthUiState

@Composable
fun VerifyScreen(
    state: AuthUiState,
    email: String,
    onBack: () -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit
) {
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

        Spacer(Modifier.height(70.dp))

        Text(text = "Verifica tu correo", fontSize = 26.sp)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Enviamos un enlace de verificación a\n$email\nÁbrelo y luego pulsa el botón.",
            fontSize = 16.sp,
            lineHeight = 23.sp
        )

        Spacer(Modifier.height(36.dp))

        (state.info ?: state.error)?.let {
            Text(text = it, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = onVerify,
            enabled = !state.loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (state.loading) "Comprobando..." else "Ya verifiqué mi correo")
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "¿No te llegó? ", fontSize = 14.sp)
            Text(
                text = "Reenviar correo",
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onResend() }
            )
        }

        Spacer(Modifier.weight(1f))
    }
}