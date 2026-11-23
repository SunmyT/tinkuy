package com.proyecto.myapplicationtinkuy.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.ui.theme.TinkuyGreen
import com.proyecto.myapplicationtinkuy.ui.theme.PasswordWeak
import com.proyecto.myapplicationtinkuy.ui.theme.PasswordMedium
import com.proyecto.myapplicationtinkuy.ui.theme.PasswordStrong

@Composable
fun PasswordScreen(
    onBack: () -> Unit,
    onCreateAccount: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasNumber = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }

    val strengthPoints = listOf(
        hasMinLength,
        hasUppercase,
        hasLowercase,
        hasNumber,
        hasSpecial
    ).count { it }

    val strengthLabel = when {
        password.isEmpty() -> "Escribe una contraseña"
        strengthPoints <= 2 -> "Contraseña débil"
        strengthPoints <= 4 -> "Contraseña media"
        else -> "Contraseña fuerte"
    }

    val strengthColor = when {
        password.isEmpty() -> Color.LightGray
        strengthPoints <= 2 -> PasswordWeak
        strengthPoints <= 4 -> PasswordMedium
        else -> PasswordStrong
    }

    val passwordsMatch = password.isNotEmpty() && password == repeat
    val passwordValid = hasMinLength && strengthPoints >= 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Crea tu contraseña",
            fontSize = 26.sp
        )

        Spacer(Modifier.height(23.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        Text(
            text = strengthLabel,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
            color = strengthColor,
            fontSize = 12.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) { index ->
                val segmentColor = when {
                    password.isEmpty() -> Color(0xFFDCE0E4)
                    index == 0 -> strengthColor
                    index == 1 && strengthPoints >= 3 -> strengthColor
                    index == 2 && strengthPoints == 5 -> strengthColor
                    else -> Color(0xFFDCE0E4)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(
                            color = segmentColor,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = repeat,
            onValueChange = { repeat = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Repetir contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            isError = submitted && !passwordsMatch
        )

        if (submitted && !passwordsMatch) {
            Text(
                text = "Las contraseñas deben ser iguales",
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        if (submitted && !passwordValid) {
            Text(
                text = "Usa al menos 8 caracteres e incluye letras mayúsculas, minúsculas, números y símbolos para crear una contraseña más segura.",
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                submitted = true
                if (passwordValid && passwordsMatch) {
                    onCreateAccount()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TinkuyGreen
            )
        ) {
            Text("Crear cuenta")
        }

        Spacer(Modifier.weight(1f))
    }
}