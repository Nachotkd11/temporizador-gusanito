package com.gusanito.temporizador.ui.screens.termino

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gusanito.temporizador.ui.components.hardShadow
import com.gusanito.temporizador.ui.theme.Baloo2
import com.gusanito.temporizador.ui.theme.Nunito
import com.gusanito.temporizador.ui.theme.Warm

/**
 * Pantalla 4 · Terminó (README, "4 · Terminó"): cierre celebratorio al completar
 * la vuelta. Confeti cayendo, gusanito grande sonriente con animación de "bob",
 * y las dos salidas: empezar otra vuelta o volver a elegir modo.
 */
@Composable
fun TerminoScreen(
    onOtraVez: () -> Unit,
    onVolverAlInicio: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Warm.paperDone)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clipToBounds(),
    ) {
        Confetti(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GusanitoGrande()
            Spacer(Modifier.height(34.dp))
            TituloPop()
            Spacer(Modifier.height(4.dp))
            Text(
                text = "el gusanito dio la vuelta completa",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = Warm.inkMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(22.dp))
            CampanitaPill()
            Spacer(Modifier.height(26.dp))
            OtraVezButton(onClick = onOtraVez)
            Spacer(Modifier.height(18.dp))
            Text(
                text = "volver al inicio",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Warm.inkPale,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onVolverAlInicio,
                    )
                    .padding(vertical = 10.dp, horizontal = 22.dp),
            )
        }
    }
}

@Composable
private fun GusanitoGrande() {
    val infinite = rememberInfiniteTransition(label = "bob")
    val bob by infinite.animateFloat(
        initialValue = -9f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bobY",
    )
    Row(
        modifier = Modifier
            .height(78.dp)
            .offset(y = bob.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(Warm.wormTints[0]))
        Box(Modifier.size(44.dp).clip(CircleShape).background(Warm.wormTints[1]))
        Box(Modifier.size(56.dp).clip(CircleShape).background(Warm.wormTints[2]))
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(CircleShape)
                .background(Warm.worm),
        ) {
            OjoGrande(left = 36.dp, top = 21.dp)
            OjoGrande(left = 57.dp, top = 21.dp)
            Boca()
        }
    }
}

@Composable
private fun OjoGrande(left: Dp, top: Dp) {
    Box(
        modifier = Modifier
            .offset(x = left, y = top)
            .size(14.dp)
            .clip(CircleShape)
            .background(Warm.card),
    )
    Box(
        modifier = Modifier
            .offset(x = left + 4.dp, y = top + 5.dp)
            .size(6.dp)
            .clip(CircleShape)
            .background(Warm.ink),
    )
}

/** Sonrisa: mitad inferior de un óvalo, trazada como arco (README: "boca"). */
@Composable
private fun Boca() {
    Canvas(
        modifier = Modifier
            .offset(x = 42.dp, y = 50.dp)
            .size(width = 28.dp, height = 13.dp),
    ) {
        val strokeWidthPx = 4.dp.toPx()
        drawArc(
            color = Warm.ink,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(0f, -size.height),
            size = Size(size.width, size.height * 2f),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
        )
    }
}

/** "¡Terminó!" con animación de entrada: escala 0.72→1 y opacidad 0→1 (README: "pop"). */
@Composable
private fun TituloPop() {
    val progreso = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progreso.animateTo(1f, animationSpec = tween(500, easing = LinearOutSlowInEasing))
    }
    Text(
        text = "¡Terminó!",
        fontFamily = Baloo2,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 56.sp,
        color = Warm.ink,
        modifier = Modifier.graphicsLayer {
            val escala = 0.72f + 0.28f * progreso.value
            scaleX = escala
            scaleY = escala
            alpha = progreso.value
        },
    )
}

@Composable
private fun CampanitaPill() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Warm.chip)
            .padding(vertical = 9.dp, horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(11.dp).clip(CircleShape).background(Warm.accent))
        Text(
            text = "sonó la campanita",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Warm.accentText,
        )
    }
}

@Composable
private fun OtraVezButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(47.dp)
    val shadowOffset: Dp = if (pressed) 4.dp else 8.dp
    val contentOffset: Dp = if (pressed) 4.dp else 0.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp)
            .hardShadow(shape, Warm.accentShadow, shadowOffset)
            .offset(y = contentOffset)
            .clip(shape)
            .background(Warm.accent)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Otra vez",
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            color = Color.White,
        )
    }
}

private data class ConfettiSpec(
    val xFraccion: Float,
    val ancho: Dp,
    val alto: Dp,
    val esquina: Dp,
    val color: Color,
    val duracionMs: Int,
    val delayMs: Int,
)

private val CONFETTI_SPECS = listOf(
    ConfettiSpec(40f / 412f, 14.dp, 19.dp, 3.dp, Warm.confetti[0], 2400, 0),
    ConfettiSpec(96f / 412f, 12.dp, 16.dp, 3.dp, Warm.confetti[1], 2900, 400),
    ConfettiSpec(150f / 412f, 15.dp, 15.dp, 8.dp, Warm.confetti[2], 2600, 900),
    ConfettiSpec(210f / 412f, 13.dp, 18.dp, 3.dp, Warm.confetti[3], 3100, 200),
    ConfettiSpec(268f / 412f, 14.dp, 14.dp, 7.dp, Warm.confetti[0], 2500, 1200),
    ConfettiSpec(322f / 412f, 12.dp, 17.dp, 3.dp, Warm.confetti[1], 2800, 600),
    ConfettiSpec(360f / 412f, 13.dp, 13.dp, 7.dp, Warm.confetti[2], 3300, 1600),
    ConfettiSpec(16f / 412f, 12.dp, 16.dp, 3.dp, Warm.confetti[3], 2700, 1900),
)

/** Confeti cayendo en loop infinito, 8 piezas con retardo y color propios (README: "fall"). */
@Composable
private fun Confetti(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier) {
        val anchoTotal = maxWidth
        val altoTotal = maxHeight
        CONFETTI_SPECS.forEach { spec ->
            ConfettiPieza(spec = spec, anchoTotal = anchoTotal, altoTotal = altoTotal)
        }
    }
}

@Composable
private fun ConfettiPieza(spec: ConfettiSpec, anchoTotal: Dp, altoTotal: Dp) {
    val infinite = rememberInfiniteTransition(label = "confetti")
    val progreso by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(spec.duracionMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(spec.delayMs, StartOffsetType.FastForward),
        ),
        label = "confettiProgreso",
    )
    Box(
        modifier = Modifier
            .offset(
                x = anchoTotal * spec.xFraccion,
                y = -40.dp + (altoTotal + 80.dp) * progreso,
            )
            .size(spec.ancho, spec.alto)
            .graphicsLayer { rotationZ = progreso * 520f }
            .clip(RoundedCornerShape(spec.esquina))
            .background(spec.color),
    )
}
