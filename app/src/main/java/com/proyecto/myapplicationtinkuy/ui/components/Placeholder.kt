
package com.proyecto.myapplicationtinkuy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun Placeholder(
    modifier: Modifier,
    pale: Boolean = false
) {
    Canvas(modifier) {
        val color = if (pale) {
            Color(0xFFD8D8D8)
        } else {
            Color(0xFF222222)
        }

        val inset = size.minDimension * 0.2f
        val side = size.minDimension - inset * 2
        val strokeWidth = if (pale) 1f else 1.5f

        drawRect(
            color = color,
            topLeft = Offset(inset, inset),
            size = Size(side, side),
            style = Stroke(width = strokeWidth)
        )

        drawLine(
            color = color,
            start = Offset(inset, inset),
            end = Offset(inset + side, inset + side),
            strokeWidth = strokeWidth
        )

        drawLine(
            color = color,
            start = Offset(inset + side, inset),
            end = Offset(inset, inset + side),
            strokeWidth = strokeWidth
        )
    }
}
