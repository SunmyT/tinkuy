
package com.proyecto.myapplicationtinkuy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proyecto.myapplicationtinkuy.AppIcon
import com.proyecto.myapplicationtinkuy.TinkuyIcon
import com.proyecto.myapplicationtinkuy.Product
import com.proyecto.myapplicationtinkuy.soles
import com.proyecto.myapplicationtinkuy.ui.order.OrderStatus
import com.proyecto.myapplicationtinkuy.ui.order.ShopOrderItem
import com.proyecto.myapplicationtinkuy.ui.order.quantityLabel
import com.proyecto.myapplicationtinkuy.ui.theme.AppBackground
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Line
import com.proyecto.myapplicationtinkuy.ui.theme.Muted
import com.proyecto.myapplicationtinkuy.ui.theme.TinkuyGreen
import com.proyecto.myapplicationtinkuy.ui.theme.OrderPendingBackground
import com.proyecto.myapplicationtinkuy.ui.theme.OrderPendingText
import com.proyecto.myapplicationtinkuy.ui.theme.OrderInProgressBackground
import com.proyecto.myapplicationtinkuy.ui.theme.OrderInProgressText

@Composable
internal fun OrderBadge(status: OrderStatus) {
    val (background, foreground) = when (status) {
        OrderStatus.Pending ->
            OrderPendingBackground to OrderPendingText

        OrderStatus.InProgress ->
            OrderInProgressBackground to OrderInProgressText

        OrderStatus.Delivered ->
            AppBackground to TinkuyGreen
    }

    Surface(
        color = background,
        contentColor = foreground,
        shape = CircleShape
    ) {
        Text(
            status.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
internal fun OrderSection(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
internal fun OrderTimeline(status: OrderStatus) {
    val stages = listOf(
        "Pedido recibido",
        "Preparación del pedido",
        "Entrega completada"
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        stages.forEachIndexed { index, label ->
            val complete = index <= status.ordinal

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (complete) TinkuyGreen else AppBackground),
                    contentAlignment = Alignment.Center
                ) {
                    if (complete) {
                        Canvas(Modifier.size(12.dp)) {
                            val joint = Offset(size.width * 0.4f, size.height * 0.72f)
                            val stroke = 1.6.dp.toPx()

                            drawLine(
                                Color.White,
                                Offset(size.width * 0.12f, size.height * 0.46f),
                                joint,
                                stroke,
                                StrokeCap.Round
                            )

                            drawLine(
                                Color.White,
                                joint,
                                Offset(size.width * 0.9f, size.height * 0.24f),
                                stroke,
                                StrokeCap.Round
                            )
                        }
                    }
                }

                Text(
                    label,
                    color = if (complete) TinkuyGreen else Muted,
                    fontWeight = if (index == status.ordinal) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
internal fun TrackingItem(item: ShopOrderItem) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Thumb(
            item.product,
            Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Column(Modifier.weight(1f)) {
            Text(
                item.product.title,
                color = Ink,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                item.quantity.quantityLabel(),
                color = Muted,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            soles(item.subtotal),
            color = Ink,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
internal fun OrderAmount(
    label: String,
    amount: Int,
    emphasized: Boolean = false
) {
    val color = if (emphasized) Ink else Muted
    val style = if (emphasized) {
        MaterialTheme.typography.titleMedium
    } else {
        MaterialTheme.typography.bodyMedium
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = color, style = style)
        Text(soles(amount), color = color, style = style)
    }
}
