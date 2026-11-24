package com.proyecto.myapplicationtinkuy.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.R
import com.proyecto.myapplicationtinkuy.ui.auth.AuthUiState
import com.proyecto.myapplicationtinkuy.ui.theme.TinkuyGreen
import androidx.compose.ui.text.font.FontFamily

@Composable
fun LoginScreen(
    state: AuthUiState,
    onLogin: (email: String, password: String) -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val emailValid = email.trim().matches(
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    )
    val passwordValid = password.length >= 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp)
    ) {
        Spacer(Modifier.height(46.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.tinkuy_logo),
                contentDescription = "Logo de Tinkuy",
                modifier = Modifier.size(174.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "TINKUY",
                color = TinkuyGreen,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 5.sp
            )
        }
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = submitted && !emailValid
        )

        if (submitted && !emailValid) {
            Text(
                text = "Por favor ingresa un correo válido",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(15.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            isError = submitted && !passwordValid
        )

        if (submitted && !passwordValid) {
            Text(
                text = "Por favor ingresa una contraseña válida",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Text(
            text = "¿Has olvidado la contraseña?",
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onForgotPassword() }
                .padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(9.dp))

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
        }

        Button(
            onClick = {
                submitted = true
                if (emailValid && passwordValid) onLogin(email.trim(), password)
            },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = TinkuyGreen
            )
        ) {
            Text(if (state.loading) "Ingresando..." else "Ingresar")
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes una cuenta? ",
                fontSize = 14.sp
            )

            Text(
                text = "Regístrate",
                fontSize = 14.sp,
                color = TinkuyGreen,
                modifier = Modifier.clickable { onRegister() }
            )
        }

        Spacer(Modifier.height(44.dp))
    }
}