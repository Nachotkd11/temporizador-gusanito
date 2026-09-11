"""Genera una fanfarria corta estilo chiptune (onda cuadrada, look 8/4-bit)
como WAV PCM de 8 bits sin firmar, para usar como sonido de "terminó" del
temporizador. Se ejecuta una sola vez para producir el recurso binario;
no forma parte del build de la app.
"""
import math
import struct
import wave

SAMPLE_RATE = 22050
AMPLITUDE = 90  # sobre un bias de 128 (8-bit unsigned): deja margen sin clip
BIAS = 128
QUANT_LEVELS = 16  # cuantiza la onda a pocos escalones -> textura "lo-fi" de 4 bits


def square_wave(freq, t):
    phase = (t * freq) % 1.0
    return 1.0 if phase < 0.5 else -1.0


def quantize(value_float):
    """Reduce la resolución de amplitud para dar textura retro tipo 4 bits."""
    step = 2.0 / QUANT_LEVELS
    return round(value_float / step) * step


def envelope(t, dur, attack=0.006, release=0.03):
    if t < attack:
        return t / attack
    if t > dur - release:
        return max(0.0, (dur - t) / release)
    return 1.0


NOTES = [
    # (frecuencia Hz, inicio s, duración s)
    (523.25, 0.00, 0.10),   # Do5
    (659.25, 0.10, 0.10),   # Mi5
    (783.99, 0.20, 0.10),   # Sol5
    (1046.50, 0.30, 0.34),  # Do6 (nota larga)
    (783.99, 0.66, 0.10),   # Sol5
    (1046.50, 0.78, 0.46),  # Do6 final, sostenida
]

TOTAL_DUR = max(start + dur for _, start, dur in NOTES)
N_SAMPLES = int(TOTAL_DUR * SAMPLE_RATE) + 1

samples = bytearray(N_SAMPLES)
for i in range(N_SAMPLES):
    t = i / SAMPLE_RATE
    value = 0.0
    for freq, start, dur in NOTES:
        if start <= t < start + dur:
            local_t = t - start
            raw = square_wave(freq, t) * envelope(local_t, dur)
            value = quantize(raw)
            break
    sample = int(BIAS + value * AMPLITUDE)
    samples[i] = max(0, min(255, sample))

out_path = r"C:\dev\Gusanito\app\src\main\res\raw\fanfarria_final.wav"
with wave.open(out_path, "wb") as wf:
    wf.setnchannels(1)
    wf.setsampwidth(1)  # 8 bits
    wf.setframerate(SAMPLE_RATE)
    wf.writeframes(bytes(samples))

print(f"Escrito {out_path}: {N_SAMPLES} muestras, {TOTAL_DUR:.2f}s @ {SAMPLE_RATE}Hz 8-bit mono")
