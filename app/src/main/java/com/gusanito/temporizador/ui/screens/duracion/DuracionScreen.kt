package com.gusanito.temporizador.ui.screens.duracion

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gusanito.temporizador.domain.Modo
import com.gusanito.temporizador.timer.TimerViewModel
import com.gusanito.temporizador.ui.components.hardShadow
import com.gusanito.temporizador.ui.theme.Baloo2
import com.gusanito.temporizador.ui.theme.Nunito
import com.gusanito.temporizador.ui.theme.Warm

private val ATAJOS = listOf(1, 3, 5, 10, 15, 30)

@Composable
fun DuracionScreen(
    onEmpezar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel(factory = TimerViewModel.factory(Modo.TEMPORIZADOR)),
) {
    val duracion by viewModel.duracionMin.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Warm.paper)
            .statusBarsPadding()
            .padding(horizontal = 26.dp)
            .padding(top = 34.dp, bottom = 30.dp),
    ) {
        Text(
            text = "¿Cuánto tiempo?",
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            color = Warm.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "de 1 a 60 minutos",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Warm.inkMuted,
        )

        Spacer(Modifier.height(22.dp))

        Stepper(
            valor = duracion,
            onDecrementar = viewModel::decrementar,
            onIncrementar = viewModel::incrementar,
        )

        Spacer(Modifier.height(26.dp))

        Text(
            text = "ATAJOS",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 1.8.sp,
            color = Warm.inkFaint,
        )
        Spacer(Modifier.height(10.dp))
        AtajosGrid(
            seleccionado = duracion,
            onSeleccionar = viewModel::setDuracion,
        )

        Spacer(Modifier.weight(1f))

        EmpezarButton(onClick = onEmpezar)
    }
}

@Composable
private fun Stepper(
    valor: Int,
    onDecrementar: () -> Unit,
    onIncrementar: () -> Unit,
) {
    val shape = RoundedCornerShape(32.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .hardShadow(shape, Warm.ink.copy(alpha = 0.07f), 7.dp)
            .clip(shape)
            .background(Warm.card)
            .border(3.dp, Warm.border, shape)
            .padding(horizontal = 26.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(
            background = Warm.cardAlt,
            backgroundActive = Warm.border,
            onClick = onDecrementar,
        ) { color ->
            Box(Modifier.size(width = 32.dp, height = 6.dp).background(color, RoundedCornerShape(3.dp)))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = valor.toString(),
                fontFamily = Baloo2,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 84.sp,
                letterSpacing = (-2).sp,
                color = Warm.ink,
            )
            Text(
                text = "MINUTOS",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 2.sp,
                color = Warm.inkFaint,
            )
        }

        StepperButton(
            background = Warm.worm,
            backgroundActive = Warm.wormShadow,
            onClick = onIncrementar,
        ) { color ->
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
                Box(Modifier.size(width = 32.dp, height = 6.dp).background(color, RoundedCornerShape(3.dp)))
                Box(Modifier.size(width = 6.dp, height = 32.dp).background(color, RoundedCornerShape(3.dp)))
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
    val iconColor = if (background == Warm.worm) Warm.card else Warm.inkSoft
    Box(
        modifier = Modifier
            .size(82.dp)
            .clip(CircleShape)
            .background(if (pressed) backgroundActive else background)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        icon(iconColor)
    }
}

@Composable
private fun AtajosGrid(seleccionado: Int, onSeleccionar: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ATAJOS.chunked(3).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                fila.forEach { minutos ->
                    AtajoChip(
                        minutos = minutos,
                        seleccionado = minutos == seleccionado,
                        onClick = { onSeleccionar(minutos) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AtajoChip(
    minutos: Int,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(22.dp)
    val resaltado = pressed || seleccionado
    Box(
        modifier = modifier
            .height(74.dp)
            .clip(shape)
            .background(if (resaltado) Warm.border else Warm.chip)
            .border(2.dp, Warm.border, shape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = minutos.toString(),
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            color = Warm.ink,
        )
    }
}

@Composable
private fun EmpezarButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(48.dp)
    val shadowOffset: Dp = if (pressed) 4.dp else 8.dp
    val contentOffset: Dp = if (pressed) 4.dp else 0.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .hardShadow(shape, Warm.wormShadow, shadowOffset)
            .offset(y = contentOffset)
            .clip(shape)
            .background(Warm.worm)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Canvas(Modifier.size(24.dp)) {
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
            Text(
                text = "Empezar",
                fontFamily = Baloo2,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = Color.White,
            )
        }
    }
}
