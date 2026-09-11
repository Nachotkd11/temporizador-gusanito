package com.gusanito.temporizador

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gusanito.temporizador.nav.GusanitoNavHost
import com.gusanito.temporizador.ui.theme.GusanitoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // La pantalla nunca debe apagarse mientras la app está en primer plano
        // (el sistema quita esta flag automáticamente al pasar a segundo plano).
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            GusanitoTheme {
                GusanitoNavHost()
            }
        }
    }
}
