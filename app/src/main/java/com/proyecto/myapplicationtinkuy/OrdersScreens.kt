package com.proyecto.myapplicationtinkuy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.proyecto.myapplicationtinkuy.ui.components.Thumb

private val OrderAccent = Color(0xFF17594D)
private val OrderAccentSoft = Color(0xFFE3F2EB)
private val OrderSecondaryText = Color(0xFF5F6D67)
private val OrderBorder = Color(0xFFDCE6E0)
private val dateFormat = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' uuuu", Locale.forLanguageTag("es-PE"))

internal enum class OrderStatus(val label: String, val detail: String) {
    Pending("Pendiente", "Esperamos que el productor confirme tu pedido."),
    InProgress("En preparación", "El productor está preparando tu pedido."),
    Delivered("Entregado", "Tu pedido ya fue entregado.")
}

internal enum class OrderFilter(val label: String, val status: OrderStatus?) {
    All("Todos", null),
    Pending("Pendientes", OrderStatus.Pending),
    InProgress("En curso", OrderStatus.InProgress),
    Delivered("Entregados", OrderStatus.Delivered)
}

internal data class ShopOrderItem(
    val product: Product,
    val quantity: Int,
    val unitPrice: Int = product.unitPrice
) : Serializable {
    val subtotal: Int get() = quantity * unitPrice
}

internal data class ShopOrder(
    val id: Int,
    val date: LocalDate,
    val status: OrderStatus,
    val items: List<ShopOrderItem>,
    val shipping: Int = 0
) : Serializable {
    val subtotal: Int get() = items.sumOf { it.subtotal }
    val total: Int get() = subtotal + shipping
    val units: Int get() = items.sumOf { it.quantity }
}

internal val sampleOrders = listOf(
    ShopOrder(156156, LocalDate.of(2026, 11, 21), OrderStatus.Pending,
        listOf(ShopOrderItem(Product.Alpaca, 1), ShopOrderItem(Product.Ceramic, 1))),
    ShopOrder(156155, LocalDate.of(2026, 11, 20), OrderStatus.Delivered,
        listOf(ShopOrderItem(Product.Coffee, 1)), shipping = 11),
    ShopOrder(156154, LocalDate.of(2026, 11, 19), OrderStatus.InProgress,
        listOf(ShopOrderItem(Product.Sheep, 1)))
)

@Composable
internal fun OrdersScreen(
    orders: List<ShopOrder>,
    filter: OrderFilter,
    onFilter: (OrderFilter) -> Unit,
    onOpen: (ShopOrder) -> Unit,
    go: (Screen) -> Unit,
    onBack: () -> Unit
) = Page(Screen.Orders, go) {
    val visibleOrders = orders.filter { filter.status == null || it.status == filter.status }
    Column(Modifier.fillMaxSize()) {
        Header("Mis pedidos", back = onBack)
        OrderFilters(filter, onFilter, Modifier.padding(horizontal = 16.dp))
        if (visibleOrders.isEmpty()) {
            OrdersEmpty(filter)
        } else {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp, 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                visibleOrders.forEach { order -> OrderCard(order) { onOpen(order) } }
            }
        }
    }
}

@Composable
private fun OrderFilters(active: OrderFilter, onFilter: (OrderFilter) -> Unit, modifier: Modifier) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OrderBorder)
    ) {
        Row(
            Modifier.selectableGroup().padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrderFilter.entries.forEach { filter ->
                val selected = filter == active
                val shape = RoundedCornerShape(12.dp)
                Box(
                    Modifier.weight(1f).heightIn(min = 48.dp).clip(shape)
                        .background(if (selected) OrderAccent else Color.Transparent)
                        .selectable(selected, role = Role.Tab, onClick = { onFilter(filter) })
                        .padding(horizontal = 2.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        filter.label,
                        color = if (selected) Color.White else OrderSecondaryText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
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
        border = BorderStroke(1.dp, OrderBorder)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Pedido #" + order.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(order.date.format(dateFormat), color = OrderSecondaryText, style = MaterialTheme.typography.bodySmall)
                }
                OrderBadge(order.status)
            }
            HorizontalDivider(color = OrderBorder)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(soles(order.total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(order.units.quantityLabel(), color = OrderSecondaryText, style = MaterialTheme.typography.bodySmall)
                }
                Text("Ver detalle", color = OrderAccent, style = MaterialTheme.typography.labelLarge)
                TinkuyIcon(AppIcon.ChevronRight, Modifier.size(16.dp), tint = OrderAccent)
            }
        }
    }
}

@Composable
private fun OrderBadge(status: OrderStatus) {
    val (background, foreground) = when (status) {
        OrderStatus.Pending -> Color(0xFFFFF0D7) to Color(0xFF754606)
        OrderStatus.InProgress -> Color(0xFFE6EFFB) to Color(0xFF265381)
        OrderStatus.Delivered -> OrderAccentSoft to OrderAccent
    }
    Surface(color = background, contentColor = foreground, shape = CircleShape) {
        Text(
            status.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun OrdersEmpty(filter: OrderFilter) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(58.dp).clip(CircleShape).background(OrderAccentSoft), contentAlignment = Alignment.Center) {
            TinkuyIcon(AppIcon.Package, Modifier.size(28.dp), tint = OrderAccent)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            if (filter == OrderFilter.All) "Todavía no tienes pedidos"
            else "No hay pedidos " + filter.label.lowercase(Locale.ROOT),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
internal fun TrackingScreen(order: ShopOrder, go: (Screen) -> Unit, onBack: () -> Unit) = Page(Screen.Tracking, go) {
    Column(Modifier.fillMaxSize()) {
        Header("Seguimiento", back = onBack)
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OrderSection {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Pedido #" + order.id, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text(order.date.format(dateFormat), color = OrderSecondaryText, style = MaterialTheme.typography.bodySmall)
                    }
                    OrderBadge(order.status)
                }
                Text(order.status.detail, color = OrderSecondaryText, style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider(color = OrderBorder)
                OrderTimeline(order.status)
            }
            OrderSection {
                Text("Productos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                order.items.forEach { item -> TrackingItem(item) }
                HorizontalDivider(color = OrderBorder)
                OrderAmount("Subtotal", order.subtotal)
                if (order.shipping > 0) OrderAmount("Envío", order.shipping)
                OrderAmount("Total", order.total, emphasized = true)
            }
        }
    }
}

@Composable
private fun OrderSection(content: @Composable () -> Unit) {
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OrderBorder)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { content() }
    }
}

@Composable
private fun OrderTimeline(status: OrderStatus) {
    val stages = listOf("Pedido recibido", "Preparación del pedido", "Entrega completada")
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        stages.forEachIndexed { index, label ->
            val complete = index <= status.ordinal
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(if (complete) OrderAccent else OrderAccentSoft),
                    contentAlignment = Alignment.Center
                ) {
                    if (complete) {
                        Canvas(Modifier.size(12.dp)) {
                            val joint = Offset(size.width * 0.4f, size.height * 0.72f)
                            val stroke = 1.6.dp.toPx()
                            drawLine(Color.White, Offset(size.width * 0.12f, size.height * 0.46f), joint, stroke, StrokeCap.Round)
                            drawLine(Color.White, joint, Offset(size.width * 0.9f, size.height * 0.24f), stroke, StrokeCap.Round)
                        }
                    }
                }
                Text(
                    label,
                    color = if (complete) OrderAccent else OrderSecondaryText,
                    fontWeight = if (index == status.ordinal) FontWeight.SemiBold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun TrackingItem(item: ShopOrderItem) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Thumb(item.product, Modifier.size(54.dp).clip(RoundedCornerShape(8.dp)))
        Column(Modifier.weight(1f)) {
            Text(item.product.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(item.quantity.quantityLabel(), color = OrderSecondaryText, style = MaterialTheme.typography.bodySmall)
        }
        Text(soles(item.subtotal), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun OrderAmount(label: String, amount: Int, emphasized: Boolean = false) {
    val color = if (emphasized) OrderAccent else OrderSecondaryText
    val style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = color, style = style)
        Text(soles(amount), color = color, style = style)
    }
}

private fun Int.quantityLabel(): String = if (this == 1) "1 unidad" else "$this unidades"
