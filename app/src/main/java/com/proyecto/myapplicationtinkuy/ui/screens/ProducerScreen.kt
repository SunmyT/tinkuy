package com.proyecto.myapplicationtinkuy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.Product
import com.proyecto.myapplicationtinkuy.Product.Alpaca
import com.proyecto.myapplicationtinkuy.Screen
import com.proyecto.myapplicationtinkuy.TinkuyIcon
import com.proyecto.myapplicationtinkuy.AppIcon
import com.proyecto.myapplicationtinkuy.ui.components.Grid
import com.proyecto.myapplicationtinkuy.ui.components.Page
import com.proyecto.myapplicationtinkuy.ui.components.Placeholder
import com.proyecto.myapplicationtinkuy.ui.components.Tabs
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Muted
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.proyecto.myapplicationtinkuy.ProducerImages
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

@Composable
internal fun ProducerScreen(
    product: Product,
    go: (Screen) -> Unit,
    openProduct: (Product) -> Unit
) {
    Page(Screen.Producer, go) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 36.dp)
        ) {
            IconButton(
                onClick = { go(Screen.Detail) },
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                TinkuyIcon(
                    AppIcon.Back,
                    tint = Ink,
                    contentDescription = "Volver"
                )
            }

            Spacer(Modifier.height(3.dp))

            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = ProducerImages.defaultImage
                    ),
                    contentDescription = "Foto de ${product.producer}",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(product.producer, fontSize = 23.sp)
                Text(product.region, fontSize = 11.sp, color = Muted)

                Row {
                    Text("4.0", fontSize = 22.sp)
                    Text(" | ", fontSize = 22.sp)
                    Text(
                        "★★★★★",
                        color = Color(0xFFFFC526),
                        fontSize = 18.sp
                    )
                }

                Text("(122 ratings)", color = Muted, fontSize = 14.sp)
                Spacer(Modifier.height(17.dp))

                if (product.producer == Alpaca.producer) {
                    Text(
                        "Asociación de 18 mujeres tejedoras que preservan técnicas ancestrales y trabajan con fibra natural de alpaca.",
                        Modifier.padding(horizontal = 24.dp),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF595959),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(19.dp))

            Tabs(
                listOf("Productos", "Opiniones", "Acerca de"),
                0
            )

            Grid(
                Product.entries.filter { it.producer == product.producer },
                openProduct,
                Modifier.padding(18.dp)
            )
        }
    }
}