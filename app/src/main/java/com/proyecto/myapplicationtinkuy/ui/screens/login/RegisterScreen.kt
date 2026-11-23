package com.proyecto.myapplicationtinkuy.ui.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.ui.theme.TinkuyGreen

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val nameValid = name.trim().isNotEmpty()
    val surnameValid = surname.trim().isNotEmpty()
    val emailValid = email.trim().matches(
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    )
    val phoneValid = phone.length == 9 && phone.all { it.isDigit() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
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
            text = "Registro de usuario",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombres") },
            singleLine = true,
            isError = submitted && !nameValid
        )
        if (submitted && !nameValid) {
            ErrorText("Por favor ingresa un nombre válido")
        }

        Spacer(Modifier.height(9.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Apellidos") },
            singleLine = true,
            isError = submitted && !surnameValid
        )
        if (submitted && !surnameValid) {
            ErrorText("Por favor ingresa un apellido válido")
        }

        Spacer(Modifier.height(9.dp))

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
            ErrorText("Por favor ingresa un correo válido")
        }

        Spacer(Modifier.height(9.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { value ->
                if (value.length <= 9 && value.all { it.isDigit() }) {
                    phone = value
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Teléfono") },
            placeholder = { Text("999999999") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            isError = submitted && !phoneValid
        )
        if (submitted && !phoneValid) {
            ErrorText("Por favor ingresa un número de teléfono válido")
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                submitted = true
                if (nameValid && surnameValid && emailValid && phoneValid) {
                    onNext()
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
            Text("Siguiente")
        }
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        fontSize = 11.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}