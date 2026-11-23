package com.proyecto.myapplicationtinkuy.ui.order

import com.proyecto.myapplicationtinkuy.Product
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val dateFormat = DateTimeFormatter.ofPattern(
    "d 'de' MMMM 'de' uuuu",
    Locale.forLanguageTag("es-PE")
)

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
    ShopOrder(
        156156,
        LocalDate.of(2026, 11, 21),
        OrderStatus.Pending,
        listOf(
            ShopOrderItem(Product.Alpaca, 1),
            ShopOrderItem(Product.Ceramic, 1)
        )
    ),
    ShopOrder(
        156155,
        LocalDate.of(2026, 11, 20),
        OrderStatus.Delivered,
        listOf(ShopOrderItem(Product.Coffee, 1)),
        shipping = 11
    ),
    ShopOrder(
        156154,
        LocalDate.of(2026, 11, 19),
        OrderStatus.InProgress,
        listOf(ShopOrderItem(Product.Sheep, 1))
    )
)

internal fun Int.quantityLabel(): String =
    if (this == 1) "1 unidad" else "$this unidades"
