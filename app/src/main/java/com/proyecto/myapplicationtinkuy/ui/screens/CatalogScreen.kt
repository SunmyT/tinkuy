
package com.proyecto.myapplicationtinkuy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.Accent
import com.proyecto.myapplicationtinkuy.AppIcon
import com.proyecto.myapplicationtinkuy.Category
import com.proyecto.myapplicationtinkuy.Product
import com.proyecto.myapplicationtinkuy.Screen
import com.proyecto.myapplicationtinkuy.TinkuyIcon
import com.proyecto.myapplicationtinkuy.ui.components.CategoryChips
import com.proyecto.myapplicationtinkuy.ui.components.EmptySearch
import com.proyecto.myapplicationtinkuy.ui.components.Page
import com.proyecto.myapplicationtinkuy.ui.components.Search
import com.proyecto.myapplicationtinkuy.ui.components.Thumb
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Line
import com.proyecto.myapplicationtinkuy.ui.theme.Muted
import com.proyecto.myapplicationtinkuy.data.local.entity.ProductEntity
import com.proyecto.myapplicationtinkuy.filter
import com.proyecto.myapplicationtinkuy.toProduct
@Composable
internal fun CatalogScreen(
    go: (Screen) -> Unit,
    category: Category,
    selectCategory: (Category) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    openProduct: (Product) -> Unit,
    items: List<ProductEntity>
) = Page(Screen.Catalog, go) {
    val focus = LocalFocusManager.current
    val products = remember(items, category, query) {
        category.filter(items, query).mapNotNull { it.toProduct() }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Catálogo",
            color = Ink,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Search(query, onQueryChange)

        CategoryChips(category, {
            focus.clearFocus()
            selectCategory(it)
        })

        if (items.isEmpty()) {
            //
        } else if (products.isEmpty()) {
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                products.forEach { product ->
                    CatalogRow(product) {
                        focus.clearFocus()
                        openProduct(product)
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogRow(
    product: Product,
    open: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Line, shape)
            .clickable(onClick = open)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Thumb(
            product,
            Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                product.title,
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                product.maker,
                color = Muted,
                fontSize = 13.sp
            )

            Text(
                product.price,
                color = Accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        TinkuyIcon(
            AppIcon.ChevronRight,
            Modifier.size(18.dp),
            Muted
        )
    }
}
