package com.gusanito.temporizador.ui.theme

import androidx.compose.ui.graphics.Color

/** Paleta modo Temporizador (cálido). Ver README, tabla "Colores — modo Temporizador". */
object Warm {
    val paper = Color(0xFFFBF3E2)
    val paperDone = Color(0xFFFDF7E6)
    val card = Color(0xFFFFFDF6)
    val cardAlt = Color(0xFFF1E9D5)
    val chip = Color(0xFFF6EFDD)
    val border = Color(0xFFE6D9BE)
    val borderAlt = Color(0xFFDDCEAC)
    val ink = Color(0xFF3A3128)
    val inkSoft = Color(0xFF8A7A5E)
    val inkMuted = Color(0xFF9A8B70)
    val inkFaint = Color(0xFFB6A98D)
    val worm = Color(0xFF7BC96F)
    val wormShadow = Color(0xFF5FA855)
    val wormTints = listOf(Color(0xFFA8DDA1), Color(0xFF96D48D), Color(0xFF86CC7E))
    val accent = Color(0xFFE8A33D)
    val accentShadow = Color(0xFFC6851F)
    val accentText = Color(0xFF8A6A2F)
    val sun = Color(0xFFF2C14E)
    val confetti = listOf(Color(0xFFE8A33D), Color(0xFF7BC96F), Color(0xFFE8836A), Color(0xFF6FB6D8))
    val inkPale = Color(0xFFC0B49A)

    // Vista previa "silla de pensar" en la tarjeta de Inicio: tonos tierra sobre
    // el fondo cálido (no es la paleta fría de la silla, es solo su miniatura).
    val sillaPreviewTints = listOf(Color(0xFFD3C9B2), Color(0xFFC6BBA2), Color(0xFFB8AD93))
    val sillaPreviewHead = Color(0xFFA9A08C)
    val sillaPreviewPupil = Color(0xFF59524A)
    val sillaPreviewTitle = Color(0xFF6E6455)
    val sillaPreviewSubtitle = Color(0xFFA2977F)
}

/** Paleta modo Silla de pensar (frío / neutro). Ver README, tabla "Colores — modo Silla de pensar". */
object Cool {
    val paper = Color(0xFFE7E6E1)
    val surface = Color(0xFFF2F1ED)
    val surfaceAlt = Color(0xFFEDECE8)
    val pressed = Color(0xFFE2E0DA)
    val track = Color(0xFFD6D4CD)
    val holdBorder = Color(0xFFCDCBC4)
    val breathe = Color(0xFFDBDAD4)
    val holdFill = Color(0xFFC4C2BA)
    val ink = Color(0xFF45423C)
    val inkSoft = Color(0xFF6E6A63)
    val inkMuted = Color(0xFF8C887F)
    val inkFaint = Color(0xFFA29E96)
    val dark = Color(0xFF5F5B54)
    val darkActive = Color(0xFF4A473F)
    val worm = Color(0xFF9AA1A8)
}

/** Paleta pantalla 7 · "Ya puedes levantarte" (cierre reparador, verde). Ver README. */
object Cierre {
    val bg = Color(0xFFEAF3E4)
    val ink = Color(0xFF2F4A2B)
    val inkSoft = Color(0xFF5A7154)
    val cta = Color(0xFF5FA855)
    val ctaActive = Color(0xFF4E8E46)
    val card = Color(0xFFFBFDF9)
    val cardBorder = Color(0xFFD7E5CF)
    val divider = Color(0xFFCFDEC7)
    val chipBg = Color(0xFFDCEBD4)
    val chipInk = Color(0xFF4E7346)
    val bullet = Color(0xFFE3EEDD)
    val eyebrow = Color(0xFF8AA382)
    val protocoloIntro = Color(0xFF54634F)

    // Gusanito de cierre: tintes propios (más claros que Warm.wormTints), cabeza
    // y ojos reutilizan los tonos alegres del gusanito verde (README, pantalla 7).
    val wormTints = listOf(Color(0xFFBFE0B6), Color(0xFFA8DDA1), Color(0xFF93D289))
    val wormHead = Color(0xFF7BC96F)
    val wormEye = Color(0xFFFFFDF6)
    val wormPupil = Color(0xFF3A3128)
}
