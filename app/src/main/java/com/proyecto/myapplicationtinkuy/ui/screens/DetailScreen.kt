
package com.proyecto.myapplicationtinkuy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.AppIcon
import com.proyecto.myapplicationtinkuy.ui.components.BlackButton
import com.proyecto.myapplicationtinkuy.Product
import com.proyecto.myapplicationtinkuy.Screen
import com.proyecto.myapplicationtinkuy.TinkuyIcon
import com.proyecto.myapplicationtinkuy.ui.components.Avatar
import com.proyecto.myapplicationtinkuy.ui.components.Page
import com.proyecto.myapplicationtinkuy.ui.components.Placeholder
import com.proyecto.myapplicationtinkuy.ui.components.Thumb
import com.proyecto.myapplicationtinkuy.ui.theme.AppBackground
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Line
import com.proyecto.myapplicationtinkuy.ui.theme.Muted
import com.proyecto.myapplicationtinkuy.ui.theme.TinkuyGreen

@Composable
internal fun DetailScreen(
    product: Product,
    go: (Screen) -> Unit,
    back: () -> Unit,
    add: () -> Unit
) = Page(Screen.Detail, go) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Contenido desplazable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 82.dp)
        ) {

            // Imagen del producto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(352.dp)
            ) {
                Thumb(
                    product,
                    Modifier.matchParentSize()
                )

                IconButton(
                    onClick = back,
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Color.White, CircleShape)
                        .semantics {
                            contentDescription = "Volver"
                        }
                ) {
                    TinkuyIcon(
                        AppIcon.Back,
                        tint = Ink
                    )
                }

                IconButton(
                    onClick = { go(Screen.Favorites) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.White, CircleShape)
                        .semantics {
                            contentDescription = "Favoritos"
                        }
                ) {
                    TinkuyIcon(
                        AppIcon.Heart,
                        tint = TinkuyGreen
                    )
                }

                Text(
                    text = "Foto referencial",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(
                            Color.White,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                    fontSize = 11.sp,
                    color = Ink
                )
            }

            // Información del producto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 19.dp,
                            topEnd = 19.dp
                        )
                    )
                    .background(Color.White)
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    )
            ) {

                // Nombre y valoración
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.title,
                        modifier = Modifier.weight(1f),
                        fontSize = 19.sp,
                        color = Ink,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "★ ★ ★ ★",
                        fontSize = 11.sp,
                        color = Ink
                    )

                    Text(
                        text = " (5.0)",
                        fontSize = 10.sp,
                        color = Muted
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Precio
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = product.price,
                        fontSize = 21.sp,
                        color = Ink,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = " por unidad",
                        modifier = Modifier.padding(bottom = 3.dp),
                        fontSize = 10.sp,
                        color = Muted
                    )
                }

                Spacer(Modifier.height(15.dp))

                Text(
                    text = "Disponible: 20 unidades",
                    fontSize = 11.sp,
                    color = Ink
                )

                Spacer(Modifier.height(7.dp))

                // Descripción
                Text(
                    text = product.description,
                    fontSize = 11.sp,
                    color = Muted
                )

                Spacer(Modifier.height(16.dp))

                // Información del productor
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppBackground)
                        .clickable {
                            go(Screen.Producer)
                        }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Placeholder(
                        modifier = Modifier.size(42.dp),
                        pale = true
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = "Producido por",
                            fontSize = 9.sp,
                            color = Muted
                        )

                        Spacer(Modifier.height(3.dp))

                        Text(
                            text = product.producer,
                            fontSize = 12.sp,
                            color = Ink,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = product.region,
                            fontSize = 10.sp,
                            color = Muted
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Opiniones de compradores
                Text(
                    text = "Opiniones de compradores",
                    fontSize = 15.sp,
                    color = Ink,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                Divider(
                    color = Line,
                    thickness = 1.dp
                )

                ReviewItem(
                    name = "Jane Doe",
                    comment = "Excelente calidad y acabado."
                )

                ReviewItem(
                    name = "María López",
                    comment = "El producto llegó en buen estado."
                )

                ReviewItem(
                    name = "Carlos Ruiz",
                    comment = "Muy buen trabajo artesanal."
                )

                ReviewItem(
                    name = "Ana Torres",
                    comment = "Me gustó mucho el diseño."
                )

                ReviewItem(
                    name = "Pedro García",
                    comment = "Buen producto y buena presentación."
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        // Barra fija de acciones
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BlackButton(
                label = "Cotizar",
                modifier = Modifier.weight(1f)
            ) {
                go(Screen.Quote)
            }

            BlackButton(
                label = "Agregar",
                modifier = Modifier.weight(1f)
            ) {
                add()
                go(Screen.Cart)
            }
        }
    }
}

// Componente de opinión
@Composable
private fun ReviewItem(
    name: String,
    comment: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Avatar(
            modifier = Modifier.size(32.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                color = Ink,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = comment,
                fontSize = 10.sp,
                color = Muted
            )
        }

        Text(
            text = "★ ★ ★ ★ ★",
            fontSize = 10.sp,
            color = Ink
        )
    }

    Divider(
        color = Line,
        thickness = 0.7.dp
    )
}
