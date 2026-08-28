# Handoff: Temporizador del Gusanito (app Android para niños)

## Overview
App de temporizador para niños de 3 a 5 años. El mecanismo central: durante la cuenta
regresiva, un **gusanito recorre el perímetro de la pantalla** y completa la vuelta
exactamente cuando el tiempo termina, **dejando un rastro detrás** para que el niño vea
cuánto avanzó sin necesidad de leer números.

Dos modos con visualizaciones distintas:

1. **Temporizador** — interfaz amigable, cálida, celebración al final.
2. **Silla de pensar** (tiempo fuera / pausa) — misma mecánica, clima calmado y sobrio:
   gris, gusanito lento, círculo de respiración, cierre reparador. **No pide PIN.**
   Incluye un bloque de protocolo para adultos basado en pautas tipo PCIT.

La duración es libre (1 a 60 minutos) con stepper de 1 en 1 más atajos rápidos.
Al completar la vuelta suena un arpegio alegre.

## About the Design Files
El archivo incluido (`Temporizador Gusanito.dc.html`, más `android-frame.jsx`) es una
**referencia de diseño creada en HTML**: un prototipo que muestra la apariencia y el
comportamiento buscados, **no código de producción para copiar**.

La tarea es **recrear estos diseños en el entorno del proyecto destino** (Kotlin +
Jetpack Compose para Android nativo, o React Native / Flutter si el proyecto ya usa
uno de esos), respetando los patrones y librerías establecidos del código base. Si
todavía no existe un código base: **Jetpack Compose es la recomendación** — el
recorrido del gusanito se implementa con `Canvas` + `Path` + `PathMeasure`
(equivalente exacto de `getPointAtLength` / `stroke-dashoffset` del prototipo).

El prototipo abre en el navegador y muestra las 7 pantallas al mismo tiempo, cada una
dentro de un marco Android; las dos pantallas de cuenta regresiva se animan solas a
velocidad ×12 para poder observar la vuelta completa. Ese factor ×12 es solo del
prototipo: **en la app real el tiempo corre a ×1**.

## Fidelity
**Alta fidelidad (hifi).** Colores, tipografías, espaciados, tamaños y copys son
definitivos y están documentados abajo con valores exactos. La UI debe recrearse
fielmente. Único elemento intencionalmente aproximado: la textura de crayón y el
grano de papel, generados con filtros SVG (`feTurbulence` + `feDisplacementMap`) —
en Android se pueden lograr con un shader/`RenderEffect`, con una textura PNG en
`multiply`, o simplificarse a línea lisa sin perder el concepto.

---

## Design Tokens

### Colores — modo Temporizador (cálido)
| Token | Hex | Uso |
|---|---|---|
| paper | `#FBF3E2` | fondo de pantalla |
| paperDone | `#FDF7E6` | fondo pantalla final |
| card | `#FFFDF6` | tarjetas y botón de pausa |
| cardAlt | `#F1E9D5` | tarjeta secundaria (silla de pensar en el inicio) |
| chip | `#F6EFDD` | atajos, píldoras |
| border | `#E6D9BE` | bordes de tarjeta, track del rastro |
| borderAlt | `#DDCEAC` | borde tarjeta secundaria |
| ink | `#3A3128` | texto principal |
| inkSoft | `#8A7A5E` | texto secundario |
| inkMuted | `#9A8B70` | subtítulos |
| inkFaint | `#B6A98D` | etiquetas, texto terciario |
| worm | `#7BC96F` | gusanito y rastro |
| wormShadow | `#5FA855` | sombra dura del botón principal |
| wormTints | `#A8DDA1` `#96D48D` `#86CC7E` | segmentos decorativos del gusanito |
| accent | `#E8A33D` | botón "Otra vez", confeti |
| accentShadow | `#C6851F` | sombra dura del acento |
| sun | `#F2C14E` | círculo/sol del inicio |
| confetti | `#E8A33D` `#7BC96F` `#E8836A` `#6FB6D8` | confeti |
| grisWorm (inicio) | `#A9A08C` + tintes `#D3C9B2` `#C6BBA2` `#B8AD93` | gusanito de la tarjeta secundaria |

### Colores — modo Silla de pensar (frío / neutro)
| Token | Hex | Uso |
|---|---|---|
| paper | `#E7E6E1` | fondo |
| surface | `#F2F1ED` | tarjetas, teclas, chips |
| surfaceAlt | `#EDECE8` | bloque informativo |
| pressed | `#E2E0DA` | estado presionado / botón − |
| track | `#D6D4CD` | track del rastro, bordes |
| breathe | `#DBDAD4` | círculo de respiración, barra de mantener |
| holdFill | `#C4C2BA` | relleno de la barra al mantener |
| ink | `#45423C` | texto principal |
| inkSoft | `#6E6A63` | texto secundario |
| inkMuted | `#8C887F` | subtítulos |
| inkFaint | `#A29E96` | terciario |
| dark | `#5F5B54` → `#4A473F` (activo) | botón "Comenzar", botón + |
| worm | `#9AA1A8` | gusanito gris |
| Cierre: bg | `#EAF3E4` | fondo pantalla final |
| Cierre: ink | `#2F4A2B` | título |
| Cierre: inkSoft | `#5A7154` | cuerpo |
| Cierre: cta | `#5FA855` → `#4E8E46` (activo) | botón "Listo" |
| Cierre: card | `#FBFDF9`, borde `#D7E5CF`, divisor `#CFDEC7` | tarjeta del protocolo |
| Cierre: chipBg / chipInk / bullet | `#DCEBD4` / `#4E7346` / `#E3EEDD` | píldora de sonido y numeración |

### Tipografía
- **Baloo 2** (Google Fonts) — números, títulos, botones. Pesos 500 / 700 / 800.
- **Nunito** (Google Fonts) — todo el resto. Pesos 400 / 700 / 800.
- Escala usada: 100 / 84 / 82 / 80 / 56 / 44 / 38 / 32 / 30 / 26 / 22 / 20 / 19 / 17 / 16 / 15 / 14 / 13 / 12 / 11 px.
- Etiquetas en mayúsculas: 11–15 px, peso 700, `letter-spacing` 1.5–2 px.
- Números grandes de cuenta regresiva: `letter-spacing: -2px`.
- Mínimo absoluto de texto: 13 px (solo en cuerpo del protocolo, pantalla de adultos).

### Espaciado, radios y sombras
- Padding de pantalla: 26 px lateral; 26–56 px superior; 30–40 px inferior.
- Radios: 999 px (píldoras) · 52/48/47 px (botón/círculo principal) · 41/40 px (steppers) · 32 px (tarjetas grandes) · 28/24/22/20/18 px (tarjetas y chips).
- Sombra dura (estilo juguete, sin blur): `0 8px 0 rgba(58,49,40,.09)` en tarjetas, `0 8px 0 #5FA855` y `0 8px 0 #C6851F` en botones; al presionar, `translateY(4px)` y sombra a `0 4px 0`.
- Toque mínimo: 66 px de alto (chips por edad); botones principales 90–96 px; steppers 80–82 px; círculo de pausa 104 px.

### Geometría del recorrido (clave)
Área de contenido del marco Android usada en el prototipo: **396 × 812 px**
(pantalla 412 × 892 menos 8 px de bisel a cada lado, 40 px de barra de estado y
24 px de barra de gestos).

- El path del recorrido está **desplazado 30 px** desde la esquina superior izquierda del área de contenido: `translate(30, 30)`.
- Rectángulo redondeado de **336 × 752 px, radio 34 px**.
- Path SVG exacto (empieza en el centro del borde superior, sentido de las agujas del reloj):

```
M168 0 H302 A34 34 0 0 1 336 34 V718 A34 34 0 0 1 302 752 H34 A34 34 0 0 1 0 718 V34 A34 34 0 0 1 34 0 H168
```

- Longitud total ≈ **2118 px**.
- Progreso del rastro: `stroke-dasharray = L`, `stroke-dashoffset = L * (1 - p)` donde `p = transcurrido / duración`.
- En Compose: `Path` idéntico + `PathMeasure.getPosition(distance)` / `getTangent(distance)`; el rastro se dibuja con `PathEffect.dashPathEffect` o midiendo un sub-path de 0 a `p·L`.
- **El punto de inicio y fin es el centro del borde superior**: la vuelta se cierra donde empezó, y eso es intencional y visible.

### El gusanito
- 9 segmentos (temporizador) / 8 (silla de pensar), dibujados de la cola a la cabeza para que la cabeza quede encima.
- Radio de cabeza: **24 px** por defecto (rango útil 16–34). Silla de pensar: 0.86 × ese valor.
- Radio de los segmentos: `max(head·0.32, head − 3 − i·1.9)`; opacidad `0.94 − i·0.035`.
- Separación entre segmentos: `head · 0.86` (temporizador) / `head · 0.8` (silla).
- Ojos: dos círculos de radio `head·0.24` en `(head·0.30, ±head·0.30)`, color `#FFFDF6` (`#F2F1ED` en gris), con pupila de radio `0.48 ×` en `+0.4 r` sobre X, color `#3A3128`.
- Antenas (solo temporizador): línea de grosor `head·0.09` desde `(head·0.5, ±head·0.7)` hasta `(head·0.35, ±head·2.2)`, con bolita de radio `head·0.11`.
- **Cada segmento se orienta según la tangente del path** (rotación = `atan2(dy, dx)`), no según la dirección global.
- Ondulación: desplazamiento perpendicular `sin(t·5 − i·0.85) · head·0.16` (la cabeza a la mitad de amplitud). En la silla de pensar: amplitud 1.1 px y frecuencia 1.1 — apenas perceptible, sin "baile".
- Los segmentos con distancia negativa (aún no han salido) se ocultan.
- Grosor del rastro: **20 px** (temporizador) / **16 px** (silla). El track de fondo usa el mismo grosor.
- Variante de rastro **"migas"**: 46 círculos equidistantes de radio `grosor·0.45` que se van encendiendo (opacidad 0.95) al ser alcanzados, con la línea sólida oculta.

---

## Screens / Views

Las 7 pantallas del prototipo, en orden de flujo.

### 1 · Inicio (`2a`)
**Propósito**: elegir entre los dos modos.
**Layout**: columna, padding 34/26/30 px.
- Encabezado: "Gusanito" (Baloo 2 800, 30 px, ink) a la izquierda; círculo sol de 50 px `#F2C14E` con sombra `0 3px 0 rgba(0,0,0,.08)` a la derecha. Debajo: "¿qué vamos a medir?" (Nunito 700, 16 px, inkMuted).
- **Dos tarjetas del mismo tamaño**: `height: 252px`, `border-radius: 32px`, borde 3 px, padding 28/26 px, separadas 18 px. Requisito explícito: idénticas en tamaño y estructura.
  - Tarjeta A → pantalla 2: fondo `#FFFDF6`, borde `#E6D9BE`, sombra `0 8px 0 rgba(58,49,40,.09)`. Fila de gusanito (altura 70 px, alineada abajo, gap 7 px): círculos de 28 / 38 / 48 px en tintes verdes + cabeza de 62 px `#7BC96F` con ojos (12 px blancos, pupila 5 px). Título "Temporizador" (Baloo 2 800, 38 px, margen superior 26 px) + "el gusanito da la vuelta" (Nunito 700, 17 px, inkMuted).
  - Tarjeta B → pantalla 5: fondo `#F1E9D5`, borde `#DDCEAC`, sombra `0 8px 0 rgba(58,49,40,.07)`. Misma fila de gusanito en tintes tierra + cabeza `#A9A08C`. Título "Silla de pensar" (color `#6E6455`) + "tiempo tranquilo" (`#A2977F`).
- Pie: fila con cuadrado de 40 px `#E6D9BE` (radio 12) + "Ajustes para adultos" (Nunito 700, 14 px, inkSoft) y "sonido, vibración, historial" (400, inkFaint), sobre fondo `#F6EFDD`, radio 22 px, padding 16/20 px.
- Overlay de grano de papel en toda la pantalla: `feTurbulence baseFrequency=0.85, numOctaves=3`, saturación 0, `opacity .16`, capa al 50 % en `multiply`.

### 2 · ¿Cuánto tiempo? (`2b`)
**Propósito**: fijar la duración. **Debe ser totalmente personalizable.**
- Botón atrás: círculo 52 px `#F1E9D5` con chevron (borde 5 px `#8A7A5E`, rotado 45°).
- Título "¿Cuánto tiempo?" (Baloo 2 800, 32 px) + "de 1 a 60 minutos" (700, 15 px, inkMuted).
- **Stepper**: tarjeta `#FFFDF6`, borde 3 px `#E6D9BE`, radio 32, padding 26/20, sombra `0 7px 0 rgba(58,49,40,.07)`. Botón − : círculo 82 px `#F1E9D5` (activo `#E6D9BE`) con barra 32×6. Centro: número (Baloo 2 800, **84 px**, `letter-spacing -2px`) + "MINUTOS" (700, 13 px, tracking 2 px, inkFaint). Botón + : círculo 82 px `#7BC96F` (activo `#5FA855`) con cruz blanca 32×6.
  - Paso de 1 minuto, límites 1–60 (clamp).
- **Atajos**: etiqueta "ATAJOS" + grid 3 columnas, gap 12, celdas de 74 px alto, radio 22, fondo `#F6EFDD`, borde 2 px, número en Baloo 2 800 30 px. Valores: **1, 3, 5, 10, 15, 30**.
- CTA "Empezar": 96 px de alto, radio 48, `#7BC96F`, sombra `0 8px 0 #5FA855`; triángulo blanco (24 px de base) + texto Baloo 2 800 32 px; al presionar `translateY(4px)` y sombra `0 4px 0`.

### 3 · Cuenta regresiva (`2c`)
**Propósito**: la pantalla principal; el niño la mira mientras corre el tiempo.
- Track del recorrido en `#E6D9BE`, grosor 20, `stroke-linecap: round`, con filtro crayón (`baseFrequency 0.026`, `numOctaves 2`, `scale 3.4`).
- Rastro en `#7BC96F` sobre el track, mismo grosor, revelado por progreso.
- Gusanito encima, **fuera del filtro de crayón** (se mantiene nítido).
- Centro (top 260 px): mensaje de progreso (Nunito 700, 22 px, inkMuted, alto fijo 30 px), tiempo `MM:SS` (Baloo 2 800, **100 px**), etiqueta "MINUTOS" (700, 15 px, tracking 2).
  - Mensajes por progreso: `<2 %` "¡Vamos!" · `<25 %` "El gusanito camina" · `<50 %` "¡Un cuarto de vuelta!" · `<75 %` "¡Ya vas a la mitad!" · `<94 %` "¡Ya casi!" · resto "¡Última esquina!".
- Controles (bottom 118 px): círculo de 104 px — pausa (fondo `#FFFDF6`, borde 4 px `#E6D9BE`, dos barras 13×40 `#8A7A5E`) que alterna con reanudar (fondo `#7BC96F`, triángulo blanco de 30 px). Debajo, texto "terminar" (Nunito 700, 16 px, inkFaint) con área de toque de 10/22 px.

### 4 · Terminó (`2d`)
**Propósito**: cierre celebratorio.
- Fondo `#FDF7E6`. Confeti: 8 piezas de 12–15 px (rect radio 3 y círculos), colores del set de confeti, animación `fall` de 2.4–3.3 s lineal infinita con retardos 0–1.9 s (caída de −40 px a 900 px con rotación 520°).
- Gusanito grande centrado: círculos de 34 / 44 / 56 px + cabeza de 78 px con ojos de 14 px, pupilas de 6 px y **boca** (arco de 28×13, borde inferior 4 px `#3A3128`, radio `0 0 15px 15px`); animación `bob` 1.6 s ease-in-out infinita (±9 px).
- "¡Terminó!" (Baloo 2 800, 56 px) con animación `pop` 0.5 s ease-out (escala 0.72→1, opacidad 0→1). Subtítulo "el gusanito dio la vuelta completa" (700, 19 px, inkMuted).
- Píldora "sonó la campanita": fondo `#F6EFDD`, radio 999, punto de 11 px `#E8A33D`, texto 700 14 px `#8A6A2F`.
- CTA "Otra vez": 94 px, radio 47, `#E8A33D`, sombra `0 8px 0 #C6851F`. Debajo, "volver al inicio" (700, 15 px, `#C0B49A`).

### 5 · Silla de pensar · duración (`2e`)
**Propósito**: el adulto fija la duración del tiempo fuera. **Sin PIN** (requisito explícito).
- Atrás igual al de 2b pero en `#F2F1ED` / `#8C887F`.
- Título "Silla de pensar" (Baloo 2 800, 30 px, ink) + "Recomendado: un minuto por año de edad." (700, 15 px, inkMuted).
- Stepper: tarjeta `#F2F1ED`, borde 2 px `#D6D4CD`, radio 28. Botón − circular de 80 px `#E2E0DA`; número en Baloo 2 800 82 px; botón + circular de 80 px `#5F5B54` con cruz `#F2F1ED`. Rango 1–60.
- "POR EDAD": grid de 5 columnas, gap 10, celdas de 66 px con número (Baloo 2 700, 22 px) sobre "años" (400, 10 px). Valores **3, 4, 5, 6, 8** → minutos = edad.
- Bloque informativo `#EDECE8`, radio 18, texto 400 13 px/1.6: "Durante el tiempo tranquilo la pantalla se queda fija. Solo un adulto puede terminarlo antes, manteniendo el botón presionado."
- CTA "Comenzar": 92 px, radio 24, `#5F5B54` (activo `#4A473F`), texto Baloo 2 700 26 px `#F2F1ED`. Sin sombra dura: el modo es sobrio a propósito.

### 6 · Cuenta regresiva tranquila (`2f`)
**Propósito**: el niño espera sentado.
- Mismo recorrido perimetral, pero track `#D6D4CD` y rastro `#9AA1A8` de 16 px; filtro crayón más suave (`baseFrequency 0.024`, `scale 2.8`).
- Círculo de respiración detrás del contenido: 280 px, `#DBDAD4`, centrado en (198, 406), animación `breathe` 6 s ease-in-out infinita (escala 1→1.12, opacidad .4→.75). Marca el ritmo respiratorio, sin texto que exija leer.
- Centro (top 300 px): "Quédate sentado" (Nunito 700, 20 px, inkSoft), tiempo `MM:SS` (Baloo 2 700, 80 px) — ocultable por configuración — y línea de estado (400, 15 px, inkMuted): "el gusanito está dando la vuelta", que cambia a "ya falta poco" al 88 % del progreso.
- **Terminar solo el adulto**: barra de 78 px, radio 20, fondo `#DBDAD4`, borde 2 px `#CDCBC4`; al mantener presionado, un relleno `#C4C2BA` crece de 0 a 100 % en **1.4 s** (transición lineal) y al completarse termina el temporizador; al soltar antes, vuelve a 0 en 0.25 s. Texto "Mantén presionado para terminar" (700, 17 px) y debajo "solo un adulto" (400, 13 px, inkFaint).
- Sin confeti, sin baile, sin sombras duras: el contraste con el modo temporizador es el punto.

### 7 · Ya puedes levantarte (`2g`)
**Propósito**: cierre reparador + material para el adulto. **Esta pantalla hace scroll.**
- Fondo `#EAF3E4`, padding 56/26/40. Gusanito verde de cierre: 28 / 36 / 48 px + cabeza de 66 px con ojos (sin boca, tono sereno).
- "Ya puedes / levantarte" (Baloo 2 800, 44 px, línea 1.15, `#2F4A2B`, dos líneas) + "El gusanito terminó su vuelta. ¿Hablamos un momento de lo que pasó?" (400, 17 px/1.6, `#5A7154`, ancho máx. 290 px).
- Píldora "sonó la campanita" (`#DCEBD4` / `#4E7346`, punto `#5FA855`).
- CTA "Listo": 90 px, radio 24, `#5FA855` (activo `#4E8E46`).
- Divisor de 1 px `#CFDEC7` (márgenes 34/26) y **tarjeta "Para los adultos"**: fondo `#FBFDF9`, borde 1 px `#D7E5CF`, radio 22, padding 24/22.
  - Eyebrow "PARA LOS ADULTOS" (700, 11 px, tracking 1.6, `#8AA382`).
  - Título "Protocolo para que el tiempo fuera sea efectivo" (Baloo 2 800, 26 px/1.2, `#2F4A2B`).
  - Intro (400, 14 px/1.65, `#54634F`): "Si decides utilizar la pausa como herramienta de gestión de conducta, la evidencia en modificación de conducta (como los protocolos de Parent-Child Interaction Therapy — PCIT) sugiere las siguientes pautas:"
  - Cuatro pautas numeradas (círculo de 26 px `#E3EEDD` con número 700 13 px `#4E7346`, gap 14 px, separación 18 px), cada una con guía en negrita `#2F4A2B` y cuerpo `#54634F`:
    1. **Objetivo de desescalada, no de castigo moral.** El propósito debe ser la regulación fisiológica (bajar la activación del sistema nervioso/amígdala), no la reflexión abstracta.
    2. **Consistencia e inmediatez.** Debe aplicarse inmediatamente después de la conducta límite (por ejemplo, agresión física o destrucción de objetos), sin advertencias repetitivas que diluyan el límite.
    3. **Criterio de finalización predecible.** Utiliza un temporizador visual o auditivo. La duración debe ser breve (2 a 4 minutos). Si el niño sigue en desborde motor o llanto intenso al sonar la alarma, la pausa se extiende únicamente hasta que logre entre 30 y 60 segundos de calma relativa.
    4. **Cierre sin sermones ni sobreexplicación.** Una vez cumplido el tiempo, el ciclo se cierra de inmediato. No se exige una disculpa forzada ni un debate largo; se restablece la actividad normal reforzando la conducta alternativa esperada: «las manos se usan para jugar, no para golpear; volvamos a armar los bloques».

---

## Interactions & Behavior

### Navegación
`Inicio → Cuánto tiempo → Cuenta regresiva → Terminó → Inicio`
`Inicio → Silla: duración → Silla: cuenta regresiva → Ya puedes levantarte → Inicio`

En el prototipo las 7 pantallas se muestran a la vez (para revisión) y las tarjetas del
inicio son enlaces de ancla. En la app real es navegación normal entre pantallas.

### Reglas de temporizador
- Duración libre 1–60 min, paso 1; atajos que fijan el valor directamente.
- La vuelta del gusanito **siempre** dura exactamente lo que dure el temporizador: `p = transcurrido / duración`, sin importar los minutos elegidos.
- Pausa/reanudar en el modo temporizador. La silla de pensar **no tiene pausa**: solo el gesto de mantener presionado, 1.4 s, para terminar antes.
- La pantalla debe permanecer encendida durante la cuenta (`keepScreenOn` / wake lock) y el temporizador debe seguir corriendo en segundo plano (servicio + notificación); al volver, la posición del gusanito se recalcula desde el timestamp de inicio, nunca desde un contador acumulado en memoria.
- Recomendado en producción: bloqueo de pantalla / modo kiosco durante la silla de pensar, para que el niño no pueda cerrar la app.

### Sonido al terminar (ambos modos)
Al completar la vuelta suena un arpegio alegre generado sintéticamente:
- **Temporizador**: do-mi-sol-do (523.25 / 659.25 / 783.99 / 1046.5 Hz), onda triangular, un tono cada 0.11 s, volumen 0.22, ataque 0.02 s, caída exponencial de 0.3 s (0.85 s la última nota).
- **Silla de pensar**: do-mi-sol (sin la octava), un tono cada 0.2 s, volumen 0.14 — misma familia sonora, más suave y lenta.
- Debe poder desactivarse en ajustes. Acompañar con vibración corta. En el prototipo se usa Web Audio; en Android, `ToneGenerator`/`AudioTrack` o un archivo de audio corto pre-renderizado.

### Animaciones
| Animación | Duración / easing | Detalle |
|---|---|---|
| avance del gusanito | continua, lineal | ~60 fps, posición derivada del tiempo real |
| ondulación | continua | seno; amplitud `head·0.16` (temporizador), 1.1 px (silla) |
| `breathe` | 6 s ease-in-out infinita | escala 1→1.12, opacidad .4→.75 |
| `bob` | 1.6 s ease-in-out infinita | ±9 px |
| `pop` | 0.5 s ease-out | escala 0.72→1 |
| `fall` (confeti) | 2.4–3.3 s lineal infinita | −40 px → 900 px, rotación 520° |
| mantener para terminar | 1.4 s lineal (0.25 s al soltar) | relleno de 0 a 100 % |
| presión de botón | instantánea | `translateY(4px)` + sombra a la mitad |

## State Management
Por temporizador (dos instancias independientes: `temporizador` y `silla`):
- `duracionMin: Int` (1–60)
- `inicioTimestamp: Long?` y `pausadoEn: Long?` → `transcurridoSeg` derivado
- `corriendo: Boolean`
- `progreso: Float` = `transcurrido / (duracionMin·60)`, clamp 0–1
- `pantalla` / ruta de navegación
- Al llegar a `progreso == 1`: detener, disparar sonido + vibración, navegar a la pantalla de cierre.

Ajustes persistidos (preferencias): color del gusanito, tamaño del gusanito, grosor del
rastro, estilo de rastro (`solido` | `migas`), sonido on/off, mostrar u ocultar los
minutos en la silla de pensar, última duración usada por modo.

Sin backend ni fetching. Opcional a futuro (no diseñado aún): historial para el adulto.

## Assets
Ninguna imagen ni icono externo. Todo son formas geométricas (círculos, rectángulos
redondeados, triángulos con bordes CSS) y filtros SVG generados por código. Fuentes:
**Baloo 2** y **Nunito** desde Google Fonts (licencia OFL) — empaquetarlas en la app.

## Screenshots
Capturas 2x en `screenshots/`, una por pantalla:
`01-inicio.png` · `02-cuanto-tiempo.png` · `03-cuenta-regresiva.png` · `04-termino.png` · `05-silla-duracion.png` · `06-silla-cuenta-regresiva.png` · `07-ya-puedes-levantarte.png` · `07b-protocolo-adultos.png` (la tarjeta del protocolo completa, que en la pantalla 7 queda bajo el pliegue).

Las capturas incluyen el marco del dispositivo y la barra de estado del prototipo: **no son parte del diseño**, son solo el contexto del teléfono. En `03` y `06` el gusanito está congelado en un punto arbitrario de su recorrido — la posición real depende del progreso.

## Files
- `Temporizador Gusanito.dc.html` — prototipo completo con las 7 pantallas, la
  animación del gusanito, los estados y el sonido. Abrir en un navegador.
- `android-frame.jsx` — marco de dispositivo Android usado solo para presentar el
  prototipo. **No es parte de la app**: no hay que portarlo.

Notas de lectura del prototipo: el recorrido y el gusanito se dibujan en un `<svg>`
por pantalla; la lógica vive en la clase al final del archivo (`build()` construye
gusanito y migas, `paint()` posiciona todo cada frame, `chime()` genera el sonido).


---

## Cómo usar este paquete con Claude Code

1. **Descomprime la carpeta dentro de tu proyecto** (o en una carpeta vacía si empiezas de cero):
   ```
   mi-app/design_handoff_temporizador_gusanito/
   ```

2. **Abre el prototipo en el navegador** — `Temporizador Gusanito.dc.html`. Ver el gusanito moviéndose vale más que cualquier descripción: es la referencia de movimiento.

3. **Arranca Claude Code en la raíz del proyecto** y dale el contexto:
   ```
   claude
   ```
   Primer mensaje sugerido:
   > Lee design_handoff_temporizador_gusanito/README.md completo y mira las capturas de
   > screenshots/. Es el diseño de una app Android de temporizador para niños. Antes de
   > escribir código, dime tu plan de implementación: estructura de pantallas, cómo vas a
   > dibujar el recorrido del gusanito y cómo vas a manejar el tiempo en segundo plano.

4. **Revisa el plan antes de dejarlo codear.** Los dos puntos donde conviene estar de acuerdo desde el principio:
   - el recorrido perimetral (`Path` + `PathMeasure` en un `Canvas` de Compose),
   - el tiempo derivado de un timestamp real, nunca de un contador acumulado.

5. **Pídele las pantallas de una en una**, en este orden — cada una es verificable por separado:
   ```
   Implementa solo la pantalla 2 (¿Cuánto tiempo?) con los valores exactos del README.
   ```
   Luego 3 (la cuenta regresiva, la más difícil), 1, 4, y por último 5-6-7.

6. **Para iterar, cita el README.** Funciona mejor referirse a la fuente que describir de nuevo:
   ```
   El gusanito va desfasado del rastro. Revisa la sección "El gusanito" del README:
   cada segmento se orienta con la tangente del path, no con la dirección global.
   ```

7. **Criterios de aceptación** que conviene pedirle explícitamente:
   - la vuelta se cierra en el centro del borde superior exactamente al llegar a cero, con 1 minuto y con 60;
   - el rastro deja ver el avance sin mirar los números;
   - la silla de pensar no tiene pausa y solo termina antes con el gesto de mantener 1.4 s;
   - al terminar suena el arpegio y vibra;
   - si la app se va a segundo plano y vuelve, el gusanito aparece en la posición correcta.

**Consejo**: si el proyecto ya existe, pídele primero que resuma los patrones del código base
(navegación, tema, tipografías) y que adapte el diseño a ellos en lugar de introducir un
sistema nuevo. Si empieza de cero, que cree el tema con los tokens de la sección Design Tokens
antes de la primera pantalla.
