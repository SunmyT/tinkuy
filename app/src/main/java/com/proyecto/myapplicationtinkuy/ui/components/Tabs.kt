package com.proyecto.myapplicationtinkuy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Line
import com.proyecto.myapplicationtinkuy.ui.theme.Muted

@Composable
internal fun Tabs(
    labels: List<String>,
    selected: Int
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(47.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEachIndexed { index, label ->
            Text(
                text = label,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 13.dp),
                color = if (index == selected) Ink else Muted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    Row(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .weight(1f / labels.size)
                .height(4.dp)
                .background(Color.Black)
        )
        Spacer(Modifier.weight(1f - 1f / labels.size))
    }

    Divider(color = Line)
}