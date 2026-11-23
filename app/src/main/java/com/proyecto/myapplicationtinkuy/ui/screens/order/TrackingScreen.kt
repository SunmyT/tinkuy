
package com.proyecto.myapplicationtinkuy.ui.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proyecto.myapplicationtinkuy.Screen
import com.proyecto.myapplicationtinkuy.ui.components.Header
import com.proyecto.myapplicationtinkuy.ui.components.OrderAmount
import com.proyecto.myapplicationtinkuy.ui.components.OrderBadge
import com.proyecto.myapplicationtinkuy.ui.components.OrderSection
import com.proyecto.myapplicationtinkuy.ui.components.OrderTimeline
import com.proyecto.myapplicationtinkuy.ui.components.Page
import com.proyecto.myapplicationtinkuy.ui.components.TrackingItem
import com.proyecto.myapplicationtinkuy.ui.theme.Line
import com.proyecto.myapplicationtinkuy.ui.theme.Muted

@Composable
internal fun TrackingScreen(
    order: ShopOrder,
    go: (Screen) -> Unit,
    onBack: () -> Unit
) = Page(Screen.Tracking, go) {
    Column(Modifier.fillMaxSize()) {
        Header("Seguimiento", back = onBack)

        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OrderSection {
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
                            "Pedido #" + order.id,
                            style = MaterialTheme.typography.titleLarge,
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

                Text(
                    order.status.detail,
                    color = Muted,
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(color = Line)

                OrderTimeline(order.status)
            }

            OrderSection {
                Text(
                    "Productos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                order.items.forEach { item ->
                    TrackingItem(item)
                }

                HorizontalDivider(color = Line)

                OrderAmount("Subtotal", order.subtotal)

                if (order.shipping > 0) {
                    OrderAmount("Envío", order.shipping)
                }

                OrderAmount(
                    "Total",
                    order.total,
                    emphasized = true
                )
            }
        }
    }
}
