package com.proyecto.myapplicationtinkuy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.AppIcon
import com.proyecto.myapplicationtinkuy.Category
import com.proyecto.myapplicationtinkuy.Product
import com.proyecto.myapplicationtinkuy.TinkuyIcon
import com.proyecto.myapplicationtinkuy.ui.theme.AppBackground
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Line
import com.proyecto.myapplicationtinkuy.ui.theme.Muted
import com.proyecto.myapplicationtinkuy.ui.theme.TinkuyGreen

@Composable
internal fun Search(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val focus = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Buscar productos"
            },
        placeholder = {
            Text("Buscar productos", color = Muted)
        },
        leadingIcon = {
            TinkuyIcon(
                AppIcon.Search,
                tint = TinkuyGreen
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    TinkuyIcon(
                        AppIcon.Close,
                        tint = Muted,
                        contentDescription = "Borrar búsqueda"
                    )
                }
            }
        } else {
            null
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { focus.clearFocus() }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TinkuyGreen,
            unfocusedBorderColor = Line,
            focusedContainerColor = AppBackground,
            unfocusedContainerColor = AppBackground,
            cursorColor = TinkuyGreen
        )
    )
}

@Composable
internal fun EmptySearch(clearQuery: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(
                AppBackground,
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                Line,
                RoundedCornerShape(18.dp)
            )
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        TinkuyIcon(
            AppIcon.Search,
            tint = TinkuyGreen
        )

        Text(
            "No encontramos productos",
            fontWeight = FontWeight.SemiBold,
            color = Ink
        )

        Text(
            "Prueba otro nombre o cambia de categoría.",
            color = Muted,
            textAlign = TextAlign.Center
        )

        TextButton(
            onClick = clearQuery,
            colors = ButtonDefaults.textButtonColors(
                contentColor = TinkuyGreen
            )
        ) {
            Text("Borrar búsqueda")
        }
    }
}

@Composable
internal fun CategoryChips(
    category: Category,
    onSelect: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier
            .selectableGroup()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Category.entries.forEach { item ->
            val selected = category == item

            Box(
                Modifier
                    .heightIn(min = 48.dp)
                    .clip(shape)
                    .background(
                        if (selected) TinkuyGreen else AppBackground
                    )
                    .border(
                        1.dp,
                        if (selected) TinkuyGreen else Line,
                        shape
                    )
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onSelect(item) }
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    item.label,
                    color = if (selected) Color.White else Ink,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
internal fun Grid(
    products: List<Product>,
    openProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        products.chunked(2).forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { product ->
                    Card(
                        product,
                        Modifier.weight(1f)
                    ) {
                        openProduct(product)
                    }
                }

                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun Card(
    product: Product,
    modifier: Modifier,
    open: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier
            .clip(shape)
            .background(AppBackground)
            .border(1.dp, Line, shape)
            .clickable(onClick = open)
    ) {
        Thumb(
            product,
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.12f)
        )

        Column(
            Modifier.padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                product.title,
                color = Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                product.maker,
                color = Muted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                product.price,
                Modifier.padding(top = 3.dp),
                color = TinkuyGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
internal fun Thumb(
    product: Product,
    modifier: Modifier
) {
    Image(
        painter = painterResource(product.photo),
        contentDescription = null,
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
    )
}