package com.proyecto.myapplicationtinkuy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.Screen
import com.proyecto.myapplicationtinkuy.AppIcon
import com.proyecto.myapplicationtinkuy.TinkuyIcon
import com.proyecto.myapplicationtinkuy.ui.theme.*

@Composable
internal fun BottomNav(
    screen: Screen,
    go: (Screen) -> Unit
) {
    val choices = listOf(
        Triple(Screen.Home, "Inicio", AppIcon.Home),
        Triple(Screen.Catalog, "Catálogo", AppIcon.Search),
        Triple(Screen.Cart, "Carrito", AppIcon.Cart),
        Triple(Screen.Profile, "Perfil", AppIcon.Profile)
    )

    HorizontalDivider(color = Line)

    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .selectableGroup()
            .padding(horizontal = 8.dp)
    ) {
        choices.forEach { (destination, label, icon) ->
            val selected = when (destination) {
                Screen.Home -> screen == Screen.Home
                Screen.Catalog -> screen in listOf(
                    Screen.Catalog,
                    Screen.Detail,
                    Screen.Producer,
                    Screen.Favorites
                )
                Screen.Cart -> screen in listOf(
                    Screen.Cart,
                    Screen.OrderDone
                )
                else -> screen in listOf(
                    Screen.Profile,
                    Screen.Account,
                    Screen.History,
                    Screen.Notifications,
                    Screen.Settings,
                    Screen.Help,
                    Screen.Sync,
                    Screen.About,
                    Screen.Orders,
                    Screen.Tracking,
                    Screen.Requests,
                    Screen.Proposals
                )
            }

            val tint = if (selected) TinkuyGreen else Muted

            Column(
                Modifier
                    .weight(1f)
                    .heightIn(min = 64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { go(destination) }
                    )
                    .semantics { contentDescription = label }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .size(48.dp, 30.dp)
                        .background(
                            if (selected) TinkuyGreen.copy(alpha = 0.10f)
                            else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    TinkuyIcon(
                        icon,
                        Modifier.size(21.dp),
                        tint
                    )
                }

                Text(
                    label,
                    color = tint,
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.SemiBold
                    else FontWeight.Medium
                )
            }
        }
    }
}