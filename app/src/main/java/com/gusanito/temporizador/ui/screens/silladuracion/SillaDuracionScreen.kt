package com.gusanito.temporizador.ui.screens.silladuracion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gusanito.temporizador.domain.Modo
import com.gusanito.temporizador.timer.TimerViewModel
import com.gusanito.temporizador.ui.theme.Baloo2
import com.gusanito.temporizador.ui.theme.Cool
import com.gusanito.temporizador.ui.theme.Nunito

private val EDADES = listOf(3, 4, 5, 6, 8)

/**
 * Pantalla 5 · Silla de pensar · duración (README, "5 · Silla de pensar · duración").
 * Mismo propósito que la pantalla 2 (fijar duración) pero con clima frío/sobrio:
 * sin PIN, atajos "por edad" en vez de minutos sueltos, bloque informativo y un
 * CTA sin sombra dura (a propósito, para marcar el contraste con el temporizador).
 */
@Composable
fun SillaDuracionScreen(
    onBack: () -> Unit,
    onComenzar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel(factory = TimerViewModel.factory(Modo.SILLA)),
) {
    val duracion by viewModel.duracionMin.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Cool.paper)
            .padding(horizontal = 26.dp)
            .padding(top = 26.dp, bottom = 30.dp),
    ) {
        BackButton(onClick = onBack)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Silla de pensar",
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            color = Cool.ink,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Recomendado: un minuto por año de edad.",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Cool.inkMuted,
        )

        Spacer(Modifier.height(26.dp))

        Stepper(
            valor = duracion,
            onDecrementar = viewModel::decrementar,
            onIncrementar = viewModel::incrementar,
        )

        Spacer(Modifier.height(22.dp))

        Text(
            text = "POR EDAD",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp,
            color = Cool.inkFaint,
        )
        Spacer(Modifier.height(10.dp))
        PorEdadGrid(
            seleccionado = duracion,
            onSeleccionar = viewModel::setDuracion,
        )

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Durante el tiempo tranquilo la pantalla se queda fija. Solo un adulto " +
                "puede terminarlo antes, manteniendo el botón presionado.",
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 21.sp,
            color = Cool.inkSoft,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Cool.surfaceAlt)
                .padding(vertical = 16.dp, horizontal = 18.dp),
        )

        Spacer(Modifier.weight(1f))

        ComenzarButton(onClick = onComenzar)
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(if (pressed) Cool.pressed else Cool.surface)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(14.dp)) {
            val w = size.width
            val h = size.height
            drawLine(
                color = Cool.inkMuted,
                start = Offset(w, 0f),
                end = Offset(0f, h / 2f),
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = Cool.inkMuted,
                start = Offset(0f, h / 2f),
                end = Offset(w, h),
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun Stepper(
    valor: Int,
    onDecrementar: () -> Unit,
    onIncrementar: () -> Unit,
) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Cool.surface)
            .border(2.dp, Cool.track, shape)
            .padding(horizontal = 20.dp, vertical = 26.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(
            background = Cool.pressed,
            backgroundActive = Cool.track,
            onClick = onDecrementar,
        ) { color ->
            Box(Modifier.size(width = 30.dp, height = 5.dp).background(color, RoundedCornerShape(3.dp)))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = valor.toString(),
                fontFamily = Baloo2,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 82.sp,
                color = Cool.ink,
            )
            Text(
                text = "MINUTOS",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 2.sp,
                color = Cool.inkMuted,
            )
        }

        StepperButton(
            background = Cool.dark,
            backgroundActive = Cool.darkActive,
            onClick = onIncrementar,
        ) { color ->
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(30.dp)) {
                Box(Modifier.size(width = 30.dp, height = 5.dp).background(color, RoundedCornerShape(3.dp)))
                Box(Modifier.size(width = 5.dp, height = 30.dp).background(color, RoundedCornerShape(3.dp)))
            }
        }
    }
}

@Composable
private fun StepperButton(
    background: Color,
    backgroundActive: Color,
    onClick: () -> Unit,
    icon: @Composable (iconColor: Color) -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val iconColor = if (background == Cool.dark) Cool.surface else Cool.dark
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(if (pressed) backgroundActive else background)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        icon(iconColor)
    }
}

@Composable
private fun PorEdadGrid(seleccionado: Int, onSeleccionar: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EDADES.forEach { edad ->
            EdadChip(
                edad = edad,
                seleccionado = edad == seleccionado,
                onClick = { onSeleccionar(edad) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun EdadChip(
    edad: Int,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(18.dp)
    val resaltado = pressed || seleccionado
    Column(
        modifier = modifier
            .height(66.dp)
            .clip(shape)
            .background(if (resaltado) Cool.pressed else Cool.surface)
            .border(2.dp, Cool.track, shape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = edad.toString(),
            fontFamily = Baloo2,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Cool.ink,
        )
        Text(
            text = "años",
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = Cool.inkFaint,
        )
    }
}

@Composable
private fun ComenzarButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(shape)
            .background(if (pressed) Cool.darkActive else Cool.dark)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Comenzar",
            fontFamily = Baloo2,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Cool.surface,
        )
    }
}
