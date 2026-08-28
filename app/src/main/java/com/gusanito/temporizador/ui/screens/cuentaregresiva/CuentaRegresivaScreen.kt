package com.gusanito.temporizador.ui.screens.cuentaregresiva

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.gusanito.temporizador.ui.theme.Nunito
import com.gusanito.temporizador.ui.theme.Warm

@Composable
fun CuentaRegresivaScreen(
    onTerminarTemprano: () -> Unit,
    onCompletado: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel(factory = TimerViewModel.factory(Modo.TEMPORIZADOR)),
) {
    val duracion by viewModel.duracionMin.collectAsStateWithLifecycle()
    val estado by viewModel.estadoCuenta.collectAsStateWithLifecycle()
    var ahoraMs by remember { mutableStateOf(System.currentTimeMillis()) }
    val context = LocalContext.current

    // El progreso se recalcula del timestamp real en cada frame; nunca de un
    // contador acumulado (README, "Reglas de temporizador").
    LaunchedEffect(estado.corriendo) {
        while (estado.corriendo) {
            withFrameMillis { ahoraMs = System.currentTimeMillis() }
        }
    }

    val progreso = viewModel.progresoEn(ahoraMs)
    val completado = estado.iniciada && progreso >= 1f

    LaunchedEffect(completado) {
        if (completado) {
            vibrarCorto(context)
            onCompletado()
        }
    }

    val duracionSeg = duracion * 60
    val transcurridoSeg = (progreso * duracionSeg).toInt().coerceIn(0, duracionSeg)
    val restanteSeg = duracionSeg - transcurridoSeg
    val tiempoTexto = "%02d:%02d".format(restanteSeg / 60, restanteSeg % 60)
    val mensaje = mensajeProgreso(progreso)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Warm.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            // El clip va acá, en el borde real y seguro de la pantalla (ya
            // descontada la status/navigation bar), no pegado al trazo del
            // recorrido. Así el Canvas de abajo puede dibujar el gusanito con
            // su cuerpo y antenas completos —sin recortarlos— y el recorrido
            // se mantiene pegado al borde tal como pide el diseño (30dp).
            .clipToBounds(),
    ) {
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
                trackColor = Warm.border,
                trailColor = Warm.worm,
                strokeWidthPx = 20.dp.toPx(),
                distanciaRastro = distanciaRastro,
            )
            drawGusanito(
                pathMeasure = pathMeasure,
                distanciaCabeza = distanciaRastro,
                tiempoSeg = ahoraMs / 1000f,
                estilo = GusanitoEstilo(
                    headRadiusPx = 24.dp.toPx(),
                    colorCuerpo = Warm.worm,
                    colorOjo = Warm.card,
                    colorPupila = Warm.ink,
                ),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 260.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.height(30.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = mensaje,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Warm.inkMuted,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = tiempoTexto,
                fontFamily = Baloo2,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 100.sp,
                letterSpacing = (-2).sp,
                color = Warm.ink,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "MINUTOS",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 2.sp,
                color = Warm.inkFaint,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 118.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PausaReanudarBoton(corriendo = estado.corriendo, onClick = viewModel::alternarPausa)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "terminar",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Warm.inkFaint,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTerminarTemprano,
                    )
                    .padding(vertical = 10.dp, horizontal = 22.dp),
            )
        }
    }
}

private fun mensajeProgreso(progreso: Float): String = when {
    progreso < 0.02f -> "¡Vamos!"
    progreso < 0.25f -> "El gusanito camina"
    progreso < 0.50f -> "¡Un cuarto de vuelta!"
    progreso < 0.75f -> "¡Ya vas a la mitad!"
    progreso < 0.94f -> "¡Ya casi!"
    else -> "¡Última esquina!"
}

@Composable
private fun PausaReanudarBoton(corriendo: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val shape = CircleShape
    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(shape)
            .background(if (corriendo) Warm.card else Warm.worm)
            .then(if (corriendo) Modifier.border(4.dp, Warm.border, shape) else Modifier)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (corriendo) {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(Modifier.size(width = 13.dp, height = 40.dp).background(Warm.inkSoft, RoundedCornerShape(4.dp)))
                Box(Modifier.size(width = 13.dp, height = 40.dp).background(Warm.inkSoft, RoundedCornerShape(4.dp)))
            }
        } else {
            Canvas(Modifier.size(30.dp)) {
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, h / 2f)
                    lineTo(0f, h)
                    close()
                }
                drawPath(path, color = Color.White)
            }
        }
    }
}

private fun vibrarCorto(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    vibrator?.vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE))
}
