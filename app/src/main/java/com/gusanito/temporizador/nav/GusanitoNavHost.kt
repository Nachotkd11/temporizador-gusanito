package com.gusanito.temporizador.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.gusanito.temporizador.domain.Modo
import com.gusanito.temporizador.timer.TimerViewModel
import com.gusanito.temporizador.ui.screens.cuentaregresiva.CuentaRegresivaScreen
import com.gusanito.temporizador.ui.screens.duracion.DuracionScreen
import com.gusanito.temporizador.ui.screens.inicio.InicioScreen
import com.gusanito.temporizador.ui.screens.sillacierre.SillaCierreScreen
import com.gusanito.temporizador.ui.screens.sillacuentaregresiva.SillaCuentaRegresivaScreen
import com.gusanito.temporizador.ui.screens.silladuracion.SillaDuracionScreen
import com.gusanito.temporizador.ui.screens.termino.TerminoScreen

/**
 * Grafo de navegación. Se completa a medida que se entregan las pantallas
 * (README: Inicio → Duración → Cuenta regresiva → Terminó, y el equivalente en silla).
 *
 * Duración y Cuenta regresiva comparten una sola instancia de [TimerViewModel] (modo
 * Temporizador), con alcance al sub-grafo [Rutas.GRAFO_TEMPORIZADOR], para que el
 * progreso sobreviva a la navegación entre ambas pantallas.
 */
object Rutas {
    const val INICIO = "inicio"
    const val GRAFO_TEMPORIZADOR = "grafo_temporizador"
    const val DURACION_TEMPORIZADOR = "duracion_temporizador"
    const val CUENTA_REGRESIVA = "cuenta_regresiva"
    const val TERMINO = "termino"

    const val GRAFO_SILLA = "grafo_silla"
    const val DURACION_SILLA = "duracion_silla"
    const val CUENTA_REGRESIVA_SILLA = "cuenta_regresiva_silla"
    const val CIERRE_SILLA = "cierre_silla"
}

@Composable
fun GusanitoNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Rutas.INICIO) {
        composable(Rutas.INICIO) {
            InicioScreen(
                onElegirTemporizador = { navController.navigate(Rutas.GRAFO_TEMPORIZADOR) },
                onElegirSilla = { navController.navigate(Rutas.GRAFO_SILLA) },
            )
        }
        navigation(startDestination = Rutas.DURACION_TEMPORIZADOR, route = Rutas.GRAFO_TEMPORIZADOR) {
            composable(Rutas.DURACION_TEMPORIZADOR) { entry ->
                val grafo = remember(entry) { navController.getBackStackEntry(Rutas.GRAFO_TEMPORIZADOR) }
                val viewModel: TimerViewModel = viewModel(grafo, factory = TimerViewModel.factory(Modo.TEMPORIZADOR))
                DuracionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEmpezar = {
                        viewModel.iniciarCuenta()
                        navController.navigate(Rutas.CUENTA_REGRESIVA)
                    },
                )
            }
            composable(Rutas.CUENTA_REGRESIVA) { entry ->
                val grafo = remember(entry) { navController.getBackStackEntry(Rutas.GRAFO_TEMPORIZADOR) }
                val viewModel: TimerViewModel = viewModel(grafo, factory = TimerViewModel.factory(Modo.TEMPORIZADOR))
                CuentaRegresivaScreen(
                    viewModel = viewModel,
                    onTerminarTemprano = {
                        viewModel.reiniciarCuenta()
                        navController.popBackStack(Rutas.DURACION_TEMPORIZADOR, inclusive = false)
                    },
                    onCompletado = {
                        navController.navigate(Rutas.TERMINO)
                    },
                )
            }
            composable(Rutas.TERMINO) { entry ->
                val grafo = remember(entry) { navController.getBackStackEntry(Rutas.GRAFO_TEMPORIZADOR) }
                val viewModel: TimerViewModel = viewModel(grafo, factory = TimerViewModel.factory(Modo.TEMPORIZADOR))
                TerminoScreen(
                    onOtraVez = {
                        viewModel.iniciarCuenta()
                        navController.navigate(Rutas.CUENTA_REGRESIVA) {
                            popUpTo(Rutas.TERMINO) { inclusive = true }
                        }
                    },
                    onVolverAlInicio = {
                        viewModel.reiniciarCuenta()
                        navController.popBackStack(Rutas.INICIO, inclusive = false)
                    },
                )
            }
        }
        navigation(startDestination = Rutas.DURACION_SILLA, route = Rutas.GRAFO_SILLA) {
            composable(Rutas.DURACION_SILLA) { entry ->
                val grafo = remember(entry) { navController.getBackStackEntry(Rutas.GRAFO_SILLA) }
                val viewModel: TimerViewModel = viewModel(grafo, factory = TimerViewModel.factory(Modo.SILLA))
                SillaDuracionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onComenzar = {
                        viewModel.iniciarCuenta()
                        navController.navigate(Rutas.CUENTA_REGRESIVA_SILLA)
                    },
                )
            }
            composable(Rutas.CUENTA_REGRESIVA_SILLA) { entry ->
                val grafo = remember(entry) { navController.getBackStackEntry(Rutas.GRAFO_SILLA) }
                val viewModel: TimerViewModel = viewModel(grafo, factory = TimerViewModel.factory(Modo.SILLA))
                SillaCuentaRegresivaScreen(
                    viewModel = viewModel,
                    onCompletado = {
                        navController.navigate(Rutas.CIERRE_SILLA) {
                            popUpTo(Rutas.CUENTA_REGRESIVA_SILLA) { inclusive = true }
                        }
                    },
                )
            }
            composable(Rutas.CIERRE_SILLA) { entry ->
                val grafo = remember(entry) { navController.getBackStackEntry(Rutas.GRAFO_SILLA) }
                val viewModel: TimerViewModel = viewModel(grafo, factory = TimerViewModel.factory(Modo.SILLA))
                SillaCierreScreen(
                    onListo = {
                        viewModel.reiniciarCuenta()
                        navController.popBackStack(Rutas.INICIO, inclusive = false)
                    },
                )
            }
        }
    }
}
