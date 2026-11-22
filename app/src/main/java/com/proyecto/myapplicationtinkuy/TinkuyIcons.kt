package com.proyecto.myapplicationtinkuy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

internal enum class AppIcon {
    Home, Search, Cart, Profile, Heart, Bell, Back, ChevronRight, Close, Package
}

@Composable
internal fun TinkuyIcon(
    icon: AppIcon,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null
) {
    val path = remember(icon) { icon.outline() }
    val semantics = if (contentDescription == null) Modifier else {
        Modifier.semantics { this.contentDescription = contentDescription }
    }

    Canvas(modifier.then(semantics).size(24.dp)) {
        val edge = size.minDimension
        val unit = edge / 24f
        val left = (size.width - edge) / 2f
        val top = (size.height - edge) / 2f
        withTransform({
            translate(left, top)
            scale(unit, unit, pivot = Offset.Zero)
        }) {
            drawPath(path, tint, style = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

private fun AppIcon.outline(): Path = Path().apply {
    when (this@outline) {
        AppIcon.Home -> {
            moveTo(3f, 10.5f)
            lineTo(12f, 3f)
            lineTo(21f, 10.5f)
            lineTo(21f, 20f)
            lineTo(15f, 20f)
            lineTo(15f, 14f)
            lineTo(9f, 14f)
            lineTo(9f, 20f)
            lineTo(3f, 20f)
            close()
        }
        AppIcon.Search -> {
            addOval(Rect(3f, 3f, 16f, 16f))
            moveTo(14.2f, 14.2f)
            lineTo(20.5f, 20.5f)
        }
        AppIcon.Cart -> {
            moveTo(2.5f, 3.5f)
            lineTo(5.5f, 3.5f)
            lineTo(8.2f, 14.2f)
            quadraticBezierTo(8.4f, 15f, 9.4f, 15f)
            lineTo(18.7f, 15f)
            quadraticBezierTo(19.6f, 15f, 19.8f, 14.2f)
            lineTo(21.5f, 7f)
            lineTo(6.4f, 7f)
            addOval(Rect(8f, 18.2f, 11.3f, 21.5f))
            addOval(Rect(16.4f, 18.2f, 19.7f, 21.5f))
        }
        AppIcon.Profile -> {
            addOval(Rect(8f, 3f, 16f, 11f))
            moveTo(4f, 21f)
            lineTo(4f, 19f)
            cubicTo(4f, 15.8f, 6.7f, 14f, 12f, 14f)
            cubicTo(17.3f, 14f, 20f, 15.8f, 20f, 19f)
            lineTo(20f, 21f)
            close()
        }
        AppIcon.Heart -> {
            moveTo(12f, 20.5f)
            lineTo(4.2f, 12.9f)
            cubicTo(0.4f, 9.1f, 3.1f, 3.8f, 7.2f, 3.8f)
            cubicTo(9.1f, 3.8f, 10.8f, 4.8f, 12f, 6.2f)
            cubicTo(13.2f, 4.8f, 14.9f, 3.8f, 16.8f, 3.8f)
            cubicTo(20.9f, 3.8f, 23.6f, 9.1f, 19.8f, 12.9f)
            close()
        }
        AppIcon.Bell -> {
            moveTo(5.8f, 9f)
            cubicTo(5.8f, 5.6f, 8.6f, 3f, 12f, 3f)
            cubicTo(15.4f, 3f, 18.2f, 5.6f, 18.2f, 9f)
            lineTo(18.2f, 13f)
            quadraticBezierTo(18.2f, 15.1f, 20.5f, 17f)
            lineTo(3.5f, 17f)
            quadraticBezierTo(5.8f, 15.1f, 5.8f, 13f)
            close()
            moveTo(9.5f, 20f)
            quadraticBezierTo(12f, 22.5f, 14.5f, 20f)
        }
        AppIcon.Back -> {
            moveTo(19.5f, 12f)
            lineTo(4.5f, 12f)
            moveTo(11f, 5.5f)
            lineTo(4.5f, 12f)
            lineTo(11f, 18.5f)
        }
        AppIcon.ChevronRight -> {
            moveTo(9f, 5.5f)
            lineTo(15.5f, 12f)
            lineTo(9f, 18.5f)
        }
        AppIcon.Close -> {
            moveTo(6f, 6f)
            lineTo(18f, 18f)
            moveTo(18f, 6f)
            lineTo(6f, 18f)
        }
        AppIcon.Package -> {
            moveTo(12f, 2.7f)
            lineTo(20.7f, 7.2f)
            lineTo(20.7f, 16.8f)
            lineTo(12f, 21.3f)
            lineTo(3.3f, 16.8f)
            lineTo(3.3f, 7.2f)
            close()
            moveTo(3.3f, 7.2f)
            lineTo(12f, 11.7f)
            lineTo(20.7f, 7.2f)
            moveTo(12f, 11.7f)
            lineTo(12f, 21.3f)
            moveTo(7.7f, 5f)
            lineTo(16.3f, 9.5f)
        }
    }
}
