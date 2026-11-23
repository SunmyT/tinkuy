package com.proyecto.myapplicationtinkuy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime

import com.proyecto.myapplicationtinkuy.Screen
import com.proyecto.myapplicationtinkuy.AppIcon
import com.proyecto.myapplicationtinkuy.TinkuyIcon
import com.proyecto.myapplicationtinkuy.ui.components.BottomNav
import com.proyecto.myapplicationtinkuy.ui.theme.AppBackground
import com.proyecto.myapplicationtinkuy.ui.theme.Ink

@Composable
internal fun Header(
    title: String,
    modifier: Modifier = Modifier,
    back: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = back) {
            TinkuyIcon(
                AppIcon.Back,
                contentDescription = "Volver",
                tint = Ink
            )
        }

        Text(
            title,
            Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = Ink,
            fontWeight = FontWeight.SemiBold,
            fontSize = 23.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.width(48.dp))
    }
}

@Composable
internal fun Page(
    screen: Screen,
    go: (Screen) -> Unit,
    bottom: Boolean = true,
    body: @Composable BoxScope.() -> Unit
) {
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Column(
        Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Box(
            Modifier.weight(1f),
            content = body
        )

        if (bottom && !keyboardVisible) {
            BottomNav(screen, go)
        }
    }
}