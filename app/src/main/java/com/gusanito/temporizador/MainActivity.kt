package com.gusanito.temporizador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gusanito.temporizador.nav.GusanitoNavHost
import com.gusanito.temporizador.ui.theme.GusanitoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GusanitoTheme {
                GusanitoNavHost()
            }
        }
    }
}
