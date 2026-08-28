package com.gusanito.temporizador.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gusanito.temporizador.domain.Modo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado del temporizador de un [Modo]. Cada modo (temporizador / silla) tiene su
 * propia instancia independiente, como describe el README ("State Management").
 */
class TimerViewModel(val modo: Modo) : ViewModel() {

    private val _duracionMin = MutableStateFlow(duracionInicialPorDefecto(modo))
    val duracionMin: StateFlow<Int> = _duracionMin.asStateFlow()

    private val _estadoCuenta = MutableStateFlow(EstadoCuenta())
    val estadoCuenta: StateFlow<EstadoCuenta> = _estadoCuenta.asStateFlow()

    fun setDuracion(minutos: Int) {
        _duracionMin.value = minutos.coerceIn(DURACION_MIN, DURACION_MAX)
    }

    fun incrementar() = setDuracion(_duracionMin.value + 1)

    fun decrementar() = setDuracion(_duracionMin.value - 1)

    /** Arranca la vuelta desde cero, con el timestamp real como única fuente de verdad. */
    fun iniciarCuenta() {
        _estadoCuenta.value = EstadoCuenta(inicioTimestamp = System.currentTimeMillis())
    }

    /** Pausa si está corriendo, reanuda si está en pausa. La silla de pensar no la usa. */
    fun alternarPausa() {
        val estado = _estadoCuenta.value
        if (estado.inicioTimestamp == null) return
        if (estado.corriendo) {
            _estadoCuenta.value = estado.copy(pausadoEnTimestamp = System.currentTimeMillis())
        } else {
            val pausadoEn = estado.pausadoEnTimestamp ?: return
            val pausaMs = System.currentTimeMillis() - pausadoEn
            _estadoCuenta.value = estado.copy(
                pausaAcumuladaMs = estado.pausaAcumuladaMs + pausaMs,
                pausadoEnTimestamp = null,
            )
        }
    }

    /** Cancela la vuelta en curso (botón "terminar" o cierre por completado). */
    fun reiniciarCuenta() {
        _estadoCuenta.value = EstadoCuenta()
    }

    /**
     * Progreso 0f–1f derivado siempre de los timestamps guardados, nunca de un
     * contador acumulado en memoria (README, "Reglas de temporizador").
     */
    fun progresoEn(ahoraMs: Long): Float {
        val estado = _estadoCuenta.value
        val inicio = estado.inicioTimestamp ?: return 0f
        val fin = estado.pausadoEnTimestamp ?: ahoraMs
        val transcurridoMs = (fin - inicio - estado.pausaAcumuladaMs).coerceAtLeast(0L)
        val duracionMs = _duracionMin.value * 60_000L
        if (duracionMs <= 0L) return 1f
        return (transcurridoMs.toFloat() / duracionMs.toFloat()).coerceIn(0f, 1f)
    }

    companion object {
        const val DURACION_MIN = 1
        const val DURACION_MAX = 60

        private fun duracionInicialPorDefecto(modo: Modo): Int = when (modo) {
            Modo.TEMPORIZADOR -> 5
            Modo.SILLA -> 4
        }

        fun factory(modo: Modo) = viewModelFactory {
            initializer { TimerViewModel(modo) }
        }
    }
}

/** Estado de una vuelta en curso. `null` en [inicioTimestamp] significa "sin empezar". */
data class EstadoCuenta(
    val inicioTimestamp: Long? = null,
    val pausadoEnTimestamp: Long? = null,
    val pausaAcumuladaMs: Long = 0L,
) {
    val iniciada: Boolean get() = inicioTimestamp != null
    val corriendo: Boolean get() = inicioTimestamp != null && pausadoEnTimestamp == null
}
