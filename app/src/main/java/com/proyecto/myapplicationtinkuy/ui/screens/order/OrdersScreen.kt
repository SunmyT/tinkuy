
package com.proyecto.myapplicationtinkuy.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.proyecto.myapplicationtinkuy.AppIcon
import com.proyecto.myapplicationtinkuy.Screen
import com.proyecto.myapplicationtinkuy.TinkuyIcon
import com.proyecto.myapplicationtinkuy.soles
import com.proyecto.myapplicationtinkuy.ui.components.Header
import com.proyecto.myapplicationtinkuy.ui.components.OrderBadge
import com.proyecto.myapplicationtinkuy.ui.components.Page
import com.proyecto.myapplicationtinkuy.ui.theme.AppBackground
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Line
import com.proyecto.myapplicationtinkuy.ui.theme.Muted
import com.proyecto.myapplicationtinkuy.ui.theme.TinkuyGreen

@Composable
internal fun OrdersScreen(
    orders: List<ShopOrder>,
    filter: OrderFilter,
    onFilter: (OrderFilter) -> Unit,
    onOpen: (ShopOrder) -> Unit,
    go: (Screen) -> Unit,
    onBack: () -> Unit
) = Page(Screen.Orders, go) {
    val visibleOrders = orders.filter {
        filter.status == null || it.status == filter.status
    }

    Column(Modifier.fillMaxSize()) {
        Header("Mis pedidos", back = onBack)

        OrderFilters(
            filter,
            onFilter,
            Modifier.padding(horizontal = 16.dp)
        )

        if (visibleOrders.isEmpty()) {
            OrdersEmpty(filter)
        } else {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp, 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                visibleOrders.forEach { order ->
                    OrderCard(order) {
                        onOpen(order)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderFilters(
    active: OrderFilter,
    onFilter: (OrderFilter) -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Row(
            Modifier
                .selectableGroup()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrderFilter.entries.forEach { filter ->
                val selected = filter == active
                val shape = RoundedCornerShape(12.dp)

                Box(
                    Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .clip(shape)
                        .background(
                            if (selected) TinkuyGreen else Color.Transparent
                        )
                        .selectable(
                            selected,
                            role = Role.Tab,
                            onClick = { onFilter(filter) }
                        )
                        .padding(horizontal = 2.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        filter.label,
                        color = if (selected) Color.White else Muted,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: ShopOrder, onOpen: () -> Unit) {
    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        "Pedido #${order.id}",
                        color = Ink,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        order.date.format(dateFormat),
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OrderBadge(order.status)
            }

            HorizontalDivider(color = Line)

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        soles(order.total),
                        color = Ink,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        order.units.quantityLabel(),
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    "Ver detalle",
                    color = TinkuyGreen,
                    style = MaterialTheme.typography.labelLarge
                )

                TinkuyIcon(
                    AppIcon.ChevronRight,
                    Modifier.size(16.dp),
                    tint = TinkuyGreen
                )
            }
        }
    }
}

@Composable
private fun OrdersEmpty(filter: OrderFilter) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(AppBackground),
            contentAlignment = Alignment.Center
        ) {
            TinkuyIcon(
                AppIcon.Package,
                Modifier.size(28.dp),
                tint = TinkuyGreen
            )
        }

        Spacer(Modifier.height(14.dp))

        Text(
            if (filter == OrderFilter.All) {
                "Todavía no tienes pedidos"
            } else {
                "No hay pedidos " + filter.label.lowercase()
            },
            color = Ink,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
