package com.gusanito.temporizador.ui.screens.sillacierre

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gusanito.temporizador.ui.theme.Baloo2
import com.gusanito.temporizador.ui.theme.Cierre
import com.gusanito.temporizador.ui.theme.Nunito

/**
 * Pantalla 7 · Ya puedes levantarte (README, "7 · Ya puedes levantarte"): cierre
 * reparador de la silla de pensar, sin celebración —solo calma— más un bloque de
 * protocolo para el adulto basado en pautas tipo PCIT. Es la única pantalla que
 * hace scroll (README, requisito explícito).
 */
@Composable
fun SillaCierreScreen(
    onListo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Cierre.bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp)
            .padding(top = 56.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GusanitoCierre()

        Spacer(Modifier.height(32.dp))
        Text(
            text = "Ya puedes\nlevantarte",
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 44.sp,
            lineHeight = 50.sp,
            color = Cierre.ink,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "El gusanito terminó su vuelta. ¿Hablamos un momento de lo que pasó?",
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 27.sp,
            color = Cierre.inkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 290.dp),
        )

        Spacer(Modifier.height(22.dp))
        CampanitaPill()

        Spacer(Modifier.height(26.dp))
        ListoButton(onClick = onListo)

        Spacer(Modifier.height(34.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Cierre.divider),
        )
        Spacer(Modifier.height(26.dp))

        ProtocoloCard()
    }
}

@Composable
private fun GusanitoCierre() {
    Row(
        modifier = Modifier.height(66.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(Modifier.size(28.dp).clip(CircleShape).background(Cierre.wormTints[0]))
        Box(Modifier.size(36.dp).clip(CircleShape).background(Cierre.wormTints[1]))
        Box(Modifier.size(48.dp).clip(CircleShape).background(Cierre.wormTints[2]))
        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(Cierre.wormHead),
        ) {
            OjoCierre(left = 30.dp, top = 18.dp)
            OjoCierre(left = 48.dp, top = 18.dp)
        }
    }
}

@Composable
private fun OjoCierre(left: Dp, top: Dp) {
    Box(
        modifier = Modifier
            .offset(x = left, y = top)
            .size(13.dp)
            .clip(CircleShape)
            .background(Cierre.wormEye),
    )
    Box(
        modifier = Modifier
            .offset(x = left + 4.dp, y = top + 4.dp)
            .size(5.dp)
            .clip(CircleShape)
            .background(Cierre.wormPupil),
    )
}

@Composable
private fun CampanitaPill() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Cierre.chipBg)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(Cierre.cta))
        Text(
            text = "sonó la campanita",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Cierre.chipInk,
        )
    }
}

@Composable
private fun ListoButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(shape)
            .background(if (pressed) Cierre.ctaActive else Cierre.cta)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Listo",
            fontFamily = Baloo2,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Cierre.wormEye,
        )
    }
}

@Composable
private fun ProtocoloCard() {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Cierre.card)
            .border(1.dp, Cierre.cardBorder, shape)
            .padding(vertical = 24.dp, horizontal = 22.dp),
    ) {
        Text(
            text = "PARA LOS ADULTOS",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.6.sp,
            color = Cierre.eyebrow,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Protocolo para que el tiempo fuera sea efectivo",
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            lineHeight = 31.sp,
            color = Cierre.ink,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Si decides utilizar la pausa como herramienta de gestión de conducta, la " +
                "evidencia en modificación de conducta (como los protocolos de Parent-Child " +
                "Interaction Therapy — PCIT) sugiere las siguientes pautas:",
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 23.sp,
            color = Cierre.protocoloIntro,
        )
        Spacer(Modifier.height(22.dp))
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            PAUTAS.forEachIndexed { index, pauta ->
                PautaItem(numero = index + 1, pauta = pauta)
            }
        }
    }
}

private data class Pauta(val guia: String, val cuerpo: String)

private val PAUTAS = listOf(
    Pauta(
        guia = "Objetivo de desescalada, no de castigo moral.",
        cuerpo = " El propósito debe ser la regulación fisiológica (bajar la activación del " +
            "sistema nervioso/amígdala), no la reflexión abstracta.",
    ),
    Pauta(
        guia = "Consistencia e inmediatez.",
        cuerpo = " Debe aplicarse inmediatamente después de la conducta límite (por ejemplo, " +
            "agresión física o destrucción de objetos), sin advertencias repetitivas que " +
            "diluyan el límite.",
    ),
    Pauta(
        guia = "Criterio de finalización predecible.",
        cuerpo = " Utiliza un temporizador visual o auditivo. La duración debe ser breve (2 a " +
            "4 minutos). Si el niño sigue en desborde motor o llanto intenso al sonar la " +
            "alarma, la pausa se extiende únicamente hasta que logre entre 30 y 60 segundos " +
            "de calma relativa.",
    ),
    Pauta(
        guia = "Cierre sin sermones ni sobreexplicación.",
        cuerpo = " Una vez cumplido el tiempo, el ciclo se cierra de inmediato. No se exige " +
            "una disculpa forzada ni un debate largo; se restablece la actividad normal " +
            "reforzando la conducta alternativa esperada: «las manos se usan para jugar, no " +
            "para golpear; volvamos a armar los bloques».",
    ),
)

@Composable
private fun PautaItem(numero: Int, pauta: Pauta) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier.size(26.dp).clip(CircleShape).background(Cierre.bullet),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = numero.toString(),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Cierre.chipInk,
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = Cierre.ink)) {
                    append(pauta.guia)
                }
                append(pauta.cuerpo)
            },
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = Cierre.protocoloIntro,
        )
    }
}
