package com.proyecto.myapplicationtinkuy.ui.screens.welcome

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.R
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF4F4F4)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bienvenida),
                contentDescription = "Imagen de bienvenida",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 28.dp,
                            bottomEnd = 28.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.padding(
                horizontal = 17.dp,
                vertical = 42.dp
            )
        ) {
            Text(
                text = "TINKUY",
                fontSize = 36.sp,
                letterSpacing = 1.sp
            )

            Text(
                text = "Conecta. Descubre. Compra.",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(21.dp))

            Text(
                text = "Explora productos, solicita cotizaciones y\nconecta directamente con productores",
                color = Color(0xFF777777),
                fontSize = 16.sp,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(37.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Iniciar Sesión")
                }

                Button(
                    onClick = onCreateAccountClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Crear Cuenta")
                }
            }
        }
    }
}
