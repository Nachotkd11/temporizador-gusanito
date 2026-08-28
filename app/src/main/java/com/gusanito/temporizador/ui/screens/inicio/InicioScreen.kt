package com.gusanito.temporizador.ui.screens.inicio

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gusanito.temporizador.ui.components.hardShadow
import com.gusanito.temporizador.ui.theme.Baloo2
import com.gusanito.temporizador.ui.theme.Nunito
import com.gusanito.temporizador.ui.theme.Warm

/**
 * Pantalla 1 · Inicio (README, "1 · Inicio"): elegir entre los dos modos.
 * Dos tarjetas idénticas en tamaño y estructura, cada una con su propia
 * miniatura del gusanito (verde para Temporizador, tierra para Silla de pensar).
 */
@Composable
fun InicioScreen(
    onElegirTemporizador: () -> Unit,
    onElegirSilla: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Warm.paper)
            .padding(horizontal = 26.dp)
            .padding(top = 34.dp, bottom = 30.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Gusanito",
                fontFamily = Baloo2,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                color = Warm.ink,
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .hardShadow(CircleShape, Color.Black.copy(alpha = 0.08f), 3.dp)
                    .clip(CircleShape)
                    .background(Warm.sun),
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = "¿qué vamos a medir?",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Warm.inkMuted,
        )

        Spacer(Modifier.height(24.dp))

        ModoCard(
            fondo = Warm.card,
            borde = Warm.border,
            sombra = Warm.ink.copy(alpha = 0.09f),
            tintes = Warm.wormTints,
            colorCabeza = Warm.worm,
            colorOjo = Warm.card,
            colorPupila = Warm.ink,
            titulo = "Temporizador",
            colorTitulo = Warm.ink,
            subtitulo = "el gusanito da la vuelta",
            colorSubtitulo = Warm.inkMuted,
            onClick = onElegirTemporizador,
        )

        Spacer(Modifier.height(18.dp))

        ModoCard(
            fondo = Warm.cardAlt,
            borde = Warm.borderAlt,
            sombra = Warm.ink.copy(alpha = 0.07f),
            tintes = Warm.sillaPreviewTints,
            colorCabeza = Warm.sillaPreviewHead,
            colorOjo = Warm.cardAlt,
            colorPupila = Warm.sillaPreviewPupil,
            titulo = "Silla de pensar",
            colorTitulo = Warm.sillaPreviewTitle,
            subtitulo = "tiempo tranquilo",
            colorSubtitulo = Warm.sillaPreviewSubtitle,
            onClick = onElegirSilla,
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Warm.chip)
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Warm.border),
            )
            Column {
                Text(
                    text = "Ajustes para adultos",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Warm.inkSoft,
                )
                Text(
                    text = "sonido, vibración, historial",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Warm.inkFaint,
                )
            }
        }
    }
}

@Composable
private fun ModoCard(
    fondo: Color,
    borde: Color,
    sombra: Color,
    tintes: List<Color>,
    colorCabeza: Color,
    colorOjo: Color,
    colorPupila: Color,
    titulo: String,
    colorTitulo: Color,
    subtitulo: String,
    colorSubtitulo: Color,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(32.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(252.dp)
            .hardShadow(shape, sombra, 8.dp)
            .clip(shape)
            .background(fondo)
            .border(3.dp, borde, shape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 26.dp, vertical = 28.dp),
    ) {
        GusanitoMini(tintes = tintes, colorCabeza = colorCabeza, colorOjo = colorOjo, colorPupila = colorPupila)
        Spacer(Modifier.height(26.dp))
        Text(
            text = titulo,
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 38.sp,
            color = colorTitulo,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitulo,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = colorSubtitulo,
        )
    }
}

/** Miniatura del gusanito: 3 segmentos crecientes + cabeza con ojos, alineados abajo. */
@Composable
private fun GusanitoMini(
    tintes: List<Color>,
    colorCabeza: Color,
    colorOjo: Color,
    colorPupila: Color,
) {
    Row(
        modifier = Modifier.height(70.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(Modifier.size(28.dp).clip(CircleShape).background(tintes[0]))
        Box(Modifier.size(38.dp).clip(CircleShape).background(tintes[1]))
        Box(Modifier.size(48.dp).clip(CircleShape).background(tintes[2]))
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(colorCabeza),
        ) {
            Ojo(colorOjo, colorPupila, left = 30.dp, top = 16.dp)
            Ojo(colorOjo, colorPupila, left = 46.dp, top = 16.dp)
        }
    }
}

@Composable
private fun Ojo(colorOjo: Color, colorPupila: Color, left: Dp, top: Dp) {
    Box(
        modifier = Modifier
            .offset(x = left, y = top)
            .size(12.dp)
            .clip(CircleShape)
            .background(colorOjo),
    )
    Box(
        modifier = Modifier
            .offset(x = left + 4.dp, y = top + 4.dp)
            .size(5.dp)
            .clip(CircleShape)
            .background(colorPupila),
    )
}
