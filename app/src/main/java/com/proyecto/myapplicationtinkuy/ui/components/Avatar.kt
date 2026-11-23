
package com.proyecto.myapplicationtinkuy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun Avatar(
    modifier: Modifier,
    person: Boolean = true
) {
    Box(
        modifier
            .clip(CircleShape)
            .background(Color(0xFFDCE0E4)),
        contentAlignment = Alignment.Center
    ) {
        if (person) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    Color.White,
                    size.minDimension * 0.18f,
                    Offset(size.width / 2, size.height * 0.35f)
                )

                drawCircle(
                    Color.White,
                    size.minDimension * 0.34f,
                    Offset(size.width / 2, size.height * 0.87f)
                )
            }
        } else {
            Placeholder(
                Modifier
                    .fillMaxSize()
                    .padding(29.dp),
                true
            )
        }
    }
}
