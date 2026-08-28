package com.gusanito.temporizador.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Recorrido perimetral del gusanito (README, "Geometría del recorrido"): un
 * rectángulo redondeado que empieza y termina en el centro del borde superior,
 * recorrido en sentido horario, para que la vuelta se cierre donde empezó.
 */
fun perimeterPath(width: Float, height: Float, radius: Float): Path {
    val r = radius.coerceAtMost(min(width, height) / 2f)
    val midTopX = width / 2f
    return Path().apply {
        moveTo(midTopX, 0f)
        lineTo(width - r, 0f)
        arcTo(Rect(width - 2 * r, 0f, width, 2 * r), -90f, 90f, false)
        lineTo(width, height - r)
        arcTo(Rect(width - 2 * r, height - 2 * r, width, height), 0f, 90f, false)
        lineTo(r, height)
        arcTo(Rect(0f, height - 2 * r, 2 * r, height), 90f, 90f, false)
        lineTo(0f, r)
        arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
        lineTo(midTopX, 0f)
    }
}

/** Dibuja el track de fondo completo y el rastro recorrido hasta [distanciaRastro]. */
fun DrawScope.drawRecorrido(
    pathMeasure: PathMeasure,
    trackColor: Color,
    trailColor: Color,
    strokeWidthPx: Float,
    distanciaRastro: Float,
) {
    val track = Path()
    pathMeasure.getSegment(0f, pathMeasure.length, track, true)
    drawPath(path = track, color = trackColor, style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round))

    if (distanciaRastro > 0f) {
        val trail = Path()
        pathMeasure.getSegment(0f, distanciaRastro, trail, true)
        drawPath(path = trail, color = trailColor, style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round))
    }
}

/** Estilo geométrico y de color del gusanito (README, "El gusanito"). */
data class GusanitoEstilo(
    val headRadiusPx: Float,
    val segmentos: Int = 9,
    val separacionFactor: Float = 0.86f,
    val frecuenciaOndulacion: Float = 5f,
    /** Amplitud fija de la ondulación en px; si es `null` se deriva de `headRadiusPx·0.16`
     * (temporizador). La silla de pensar usa un valor fijo muy pequeño ("apenas
     * perceptible, sin baile", README). */
    val amplitudPx: Float? = null,
    val conAntenas: Boolean = true,
    val colorCuerpo: Color,
    val colorOjo: Color,
    val colorPupila: Color,
)

/**
 * Dibuja los segmentos del gusanito, de la cola a la cabeza, con orientación según
 * la tangente del recorrido y una ondulación perpendicular continua.
 */
fun DrawScope.drawGusanito(
    pathMeasure: PathMeasure,
    distanciaCabeza: Float,
    tiempoSeg: Float,
    estilo: GusanitoEstilo,
) {
    val length = pathMeasure.length
    if (length <= 0f) return
    val separacionPx = estilo.headRadiusPx * estilo.separacionFactor
    val amplitud = estilo.amplitudPx ?: (estilo.headRadiusPx * 0.16f)

    for (i in estilo.segmentos - 1 downTo 0) {
        val distancia = distanciaCabeza - i * separacionPx
        if (distancia < 0f) continue
        val d = distancia.coerceIn(0f, length)

        val pos = pathMeasure.getPosition(d)
        val tangente = pathMeasure.getTangent(d)
        val angulo = atan2(tangente.y, tangente.x)

        val perpX = -sin(angulo)
        val perpY = cos(angulo)
        val onda = sin(tiempoSeg * estilo.frecuenciaOndulacion - i * 0.85f) * amplitud
        val centro = Offset(pos.x + perpX * onda, pos.y + perpY * onda)

        val radio = max(estilo.headRadiusPx * 0.32f, estilo.headRadiusPx - 3f - i * 1.9f)
        val opacidad = (0.94f - i * 0.035f).coerceIn(0f, 1f)
        drawCircle(color = estilo.colorCuerpo.copy(alpha = opacidad), radius = radio, center = centro)

        if (i == 0) {
            dibujarCabeza(centro, angulo, estilo)
        }
    }
}

private fun DrawScope.dibujarCabeza(centro: Offset, angulo: Float, estilo: GusanitoEstilo) {
    val head = estilo.headRadiusPx
    val cosA = cos(angulo)
    val sinA = sin(angulo)
    fun local(lx: Float, ly: Float) = Offset(
        centro.x + lx * cosA - ly * sinA,
        centro.y + lx * sinA + ly * cosA,
    )

    if (estilo.conAntenas) {
        for (signo in floatArrayOf(-1f, 1f)) {
            val inicio = local(head * 0.5f, signo * head * 0.7f)
            val fin = local(head * 0.35f, signo * head * 2.2f)
            drawLine(
                color = estilo.colorCuerpo,
                start = inicio,
                end = fin,
                strokeWidth = head * 0.09f,
                cap = StrokeCap.Round,
            )
            drawCircle(color = estilo.colorCuerpo, radius = head * 0.11f, center = fin)
        }
    }

    val eyeRadius = head * 0.24f
    for (signo in floatArrayOf(-1f, 1f)) {
        val ojoCentro = local(head * 0.30f, signo * head * 0.30f)
        drawCircle(color = estilo.colorOjo, radius = eyeRadius, center = ojoCentro)
        val pupilaCentro = local(head * 0.30f + eyeRadius * 0.4f, signo * head * 0.30f)
        drawCircle(color = estilo.colorPupila, radius = eyeRadius * 0.48f, center = pupilaCentro)
    }
}
