package com.proyecto.myapplicationtinkuy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.*
import com.proyecto.myapplicationtinkuy.ui.components.CategoryChips
import com.proyecto.myapplicationtinkuy.ui.components.EmptySearch
import com.proyecto.myapplicationtinkuy.ui.components.Grid
import com.proyecto.myapplicationtinkuy.ui.components.Search
import com.proyecto.myapplicationtinkuy.ui.components.Page
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Muted
import com.proyecto.myapplicationtinkuy.ui.theme.TinkuyGreen

@Composable
internal fun HomeScreen(
    go: (Screen) -> Unit,
    category: Category,
    selectCategory: (Category) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    openProduct: (Product) -> Unit
) = Page(Screen.Home, go) {
    val focus = LocalFocusManager.current
    val products = remember(category, query) {
        category.products(query)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Buenas tardes",
                    color = Muted,
                    fontSize = 15.sp
                )

                Text(
                    "Usuario",
                    color = Ink,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = {
                        go(Screen.Favorites)
                    },
                    modifier = Modifier.background(Color.White, CircleShape)
                ) {
                    TinkuyIcon(
                        AppIcon.Heart,
                        contentDescription = "Favoritos",
                        tint = TinkuyGreen
                    )
                }

                IconButton(
                    onClick = {
                        go(Screen.Notifications)
                    },
                    modifier = Modifier.background(Color.White, CircleShape)
                ) {
                    TinkuyIcon(
                        AppIcon.Bell,
                        contentDescription = "Notificaciones",
                        tint = TinkuyGreen
                    )
                }
            }
        }

        Search(query, onQueryChange)

        CategoryChips(
            category = category,
            onSelect = { selectedCategory ->
                focus.clearFocus()
                selectCategory(selectedCategory)
            }
        )

        Text(
            if (query.isBlank()) {
                "Productos destacados"
            } else {
                "Resultados de búsqueda"
            },
            color = Ink,
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (products.isEmpty()) {
            EmptySearch {
                onQueryChange("")
            }
        } else {
            Grid(
                products = products,
                openProduct = { product ->
                    focus.clearFocus()
                    openProduct(product)
                }
            )
        }

        Spacer(Modifier.height(4.dp))
    }
}