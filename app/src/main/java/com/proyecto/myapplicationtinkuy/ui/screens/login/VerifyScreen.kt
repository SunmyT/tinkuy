
package com.proyecto.myapplicationtinkuy.ui.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun VerifyScreen(
    onBack: () -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit
) {
    var code by rememberSaveable {
        mutableStateOf(List(5) { "" })
    }

    val completeCode = code.all { it.isNotEmpty() }

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

        Text(
            text = "Verifica tu número",
            fontSize = 26.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Hemos enviado un código de verificación\npor SMS",
            fontSize = 16.sp,
            lineHeight = 23.sp
        )

        Spacer(Modifier.height(47.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            code.indices.forEach { index ->
                OutlinedTextField(
                    value = code[index],
                    onValueChange = { value ->
                        if (value.all { it.isDigit() } && value.length <= 1) {
                            code = code.toMutableList().also {
                                it[index] = value
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(57.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(5.dp)
                )
            }
        }

        Spacer(Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No recibiste el código? ",
                fontSize = 14.sp
            )

            Text(
                text = "Reenviar código",
                fontSize = 14.sp,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                modifier = Modifier.clickable {
                    code = List(5) { "" }
                    onResend()
                }
            )
        }

        Spacer(Modifier.height(58.dp))

        Button(
            onClick = onVerify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            enabled = completeCode,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Verificar")
        }

        Spacer(Modifier.weight(1f))
    }
}