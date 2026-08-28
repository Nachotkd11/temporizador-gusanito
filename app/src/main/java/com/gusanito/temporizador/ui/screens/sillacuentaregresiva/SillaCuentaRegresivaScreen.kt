package com.gusanito.temporizador.ui.screens.sillacuentaregresiva

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gusanito.temporizador.domain.Modo
import com.gusanito.temporizador.timer.TimerViewModel
import com.gusanito.temporizador.ui.components.GusanitoEstilo
import com.gusanito.temporizador.ui.components.drawGusanito
import com.gusanito.temporizador.ui.components.drawRecorrido
import com.gusanito.temporizador.ui.components.perimeterPath
import com.gusanito.temporizador.ui.theme.Baloo2
import com.gusanito.temporizador.ui.theme.Cool
import com.gusanito.temporizador.ui.theme.Nunito

/**
 * Pantalla 6 · Cuenta regresiva tranquila (README, "6 · Cuenta regresiva tranquila"):
 * mismo recorrido perimetral que la pantalla 3, pero con clima calmado — colores fríos,
 * gusanito casi sin baile, círculo de respiración de fondo y sin controles para el niño.
 * Solo un adulto puede terminar antes, manteniendo presionada la barra inferior 1.4 s.
 */
@Composable
fun SillaCuentaRegresivaScreen(
    onCompletado: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel(factory = TimerViewModel.factory(Modo.SILLA)),
) {
    val duracion by viewModel.duracionMin.collectAsStateWithLifecycle()
    val estado by viewModel.estadoCuenta.collectAsStateWithLifecycle()
    var ahoraMs by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(estado.corriendo) {
        while (estado.corriendo) {
            withFrameMillis { ahoraMs = System.currentTimeMillis() }
        }
    }

    val progreso = viewModel.progresoEn(ahoraMs)
    val completado = estado.iniciada && progreso >= 1f

    LaunchedEffect(completado) {
        if (completado) onCompletado()
    }

    val duracionSeg = duracion * 60
    val transcurridoSeg = (progreso * duracionSeg).toInt().coerceIn(0, duracionSeg)
    val restanteSeg = duracionSeg - transcurridoSeg
    val tiempoTexto = "%02d:%02d".format(restanteSeg / 60, restanteSeg % 60)
    val mensaje = if (progreso >= 0.88f) "ya falta poco" else "el gusanito está dando la vuelta"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Cool.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clipToBounds(),
    ) {
        CirculoRespiracion(modifier = Modifier.align(Alignment.Center).offset(x = (-4).dp, y = (-40).dp))

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
        ) {
            val radiusPx = 34.dp.toPx()
            val path = perimeterPath(size.width, size.height, radiusPx)
            val pathMeasure = PathMeasure().apply { setPath(path, false) }
            val distanciaRastro = progreso * pathMeasure.length

            drawRecorrido(
                pathMeasure = pathMeasure,
                trackColor = Cool.track,
                trailColor = Cool.worm,
                strokeWidthPx = 16.dp.toPx(),
                distanciaRastro = distanciaRastro,
            )
            drawGusanito(
                pathMeasure = pathMeasure,
                distanciaCabeza = distanciaRastro,
                tiempoSeg = ahoraMs / 1000f,
                estilo = GusanitoEstilo(
                    headRadiusPx = 24.dp.toPx() * 0.86f,
                    segmentos = 8,
                    separacionFactor = 0.8f,
                    frecuenciaOndulacion = 1.1f,
                    amplitudPx = 1.1.dp.toPx(),
                    conAntenas = false,
                    colorCuerpo = Cool.worm,
                    colorOjo = Cool.surface,
                    colorPupila = Cool.ink,
                ),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Quédate sentado",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Cool.inkSoft,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = tiempoTexto,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                fontSize = 80.sp,
                letterSpacing = (-1).sp,
                color = Cool.ink,
            )
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.height(22.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = mensaje,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Cool.inkMuted,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 56.dp)
                .padding(bottom = 104.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MantenerParaTerminarBarra(onTerminar = onCompletado)
            Spacer(Modifier.height(14.dp))
            Text(
                text = "solo un adulto",
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Cool.inkFaint,
            )
        }
    }
}

@Composable
private fun CirculoRespiracion(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "breathe")
    val escala by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breatheScale",
    )
    val opacidad by infinite.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breatheAlpha",
    )
    Box(
        modifier = modifier
            .size(280.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
                alpha = opacidad
            }
            .clip(CircleShape)
            .background(Cool.breathe),
    )
}

/** Barra "mantén presionado para terminar": se llena en 1.4 s, vuelve a 0 en 0.25 s al soltar. */
@Composable
private fun MantenerParaTerminarBarra(onTerminar: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val holdProgreso = remember { Animatable(0f) }

    LaunchedEffect(pressed) {
        if (pressed) {
            holdProgreso.animateTo(1f, animationSpec = tween(1400, easing = LinearEasing))
            onTerminar()
        } else {
            holdProgreso.animateTo(0f, animationSpec = tween(250, easing = LinearEasing))
        }
    }

    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(shape)
            .background(Cool.breathe)
            .border(2.dp, Cool.holdBorder, shape)
            .clickable(interactionSource = interaction, indication = null, onClick = {}),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .fillMaxWidth(holdProgreso.value.coerceIn(0f, 1f))
                .background(Cool.holdFill),
        )
        Text(
            text = "Mantén presionado para terminar",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Cool.dark,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}
