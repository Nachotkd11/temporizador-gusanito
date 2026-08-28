package com.gusanito.temporizador.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp

/**
 * Sombra "dura" de juguete (sin blur): una copia sólida de la forma, desplazada
 * hacia abajo `offsetY`. Equivalente a `box-shadow: 0 Npx 0 color` del prototipo.
 */
fun Modifier.hardShadow(shape: Shape, color: Color, offsetY: Dp): Modifier =
    drawBehind {
        val offsetPx = offsetY.toPx()
        if (offsetPx <= 0f) return@drawBehind
        val outline = shape.createOutline(size, layoutDirection, this)
        translate(top = offsetPx) {
            drawOutline(outline, color = color)
        }
    }
