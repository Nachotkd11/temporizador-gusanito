(() => {
  'use strict';

  // ---------- Escala del "stage" (1280x720, formato 16:9 adaptado a cualquier pantalla) ----------
  const STAGE_W = 1280, STAGE_H = 720;
  const stage = document.getElementById('stage');
  function ajustarEscala() {
    const escala = Math.min(window.innerWidth / STAGE_W, window.innerHeight / STAGE_H);
    stage.style.transform = `scale(${escala})`;
  }
  window.addEventListener('resize', ajustarEscala);
  ajustarEscala();

  // ---------- Navegación ----------
  const screens = {};
  document.querySelectorAll('.screen').forEach(el => { screens[el.id] = el; });
  function goto(id) {
    Object.values(screens).forEach(el => el.classList.remove('active'));
    screens[id].classList.add('active');
    if (id === 'screen-termino') {
      const t = document.getElementById('titulo-pop');
      t.style.animation = 'none';
      void t.offsetWidth;
      t.style.animation = '';
    }
  }

  // El navegador no tiene teclas de navegación físicas: usamos la History API para que
  // Atrás/Adelante del navegador funcionen de forma consistente con la navegación interna.
  // Las transiciones automáticas (fin del temporizador, fin de la silla) reemplazan la entrada
  // (espejo de popUpTo(inclusive) en GusanitoNavHost.kt), así "atrás" no puede reanudar una
  // cuenta regresiva ya terminada.
  let currentScreenId = 'screen-inicio';
  function irA(id, { replace = false } = {}) {
    goto(id);
    currentScreenId = id;
    const state = { screen: id };
    if (replace) history.replaceState(state, '', '#' + id);
    else history.pushState(state, '', '#' + id);
  }
  function limpiarAlSalir(idSaliente) {
    if (idSaliente === 'screen-cuenta-t') { timerT.reiniciarCuenta(); completadoT = false; renderDuracionT(); }
    if (idSaliente === 'screen-cierre-s') { timerS.reiniciarCuenta(); completadoS = false; }
  }
  history.replaceState({ screen: 'screen-inicio' }, '', '#screen-inicio');
  window.addEventListener('popstate', (ev) => {
    // Bloqueo intencional: "solo un adulto puede terminar" la silla de pensar.
    if (currentScreenId === 'screen-cuenta-s') {
      history.pushState({ screen: 'screen-cuenta-s' }, '', '#screen-cuenta-s');
      return;
    }
    limpiarAlSalir(currentScreenId);
    let destino = (ev.state && ev.state.screen) || 'screen-inicio';
    // Las cuentas regresivas no se pueden reanudar navegando con atrás/adelante.
    if (destino === 'screen-cuenta-t') destino = 'screen-duracion-t';
    if (destino === 'screen-cuenta-s') destino = 'screen-duracion-s';
    goto(destino);
    currentScreenId = destino;
  });

  // ---------- TimerEngine: espejo exacto de TimerViewModel.kt (timestamps reales, nunca acumuladores) ----------
  class TimerEngine {
    constructor(duracionDefault) {
      this.duracionMin = duracionDefault;
      this.estado = { inicio: null, pausadoEn: null, pausaAcum: 0 };
    }
    setDuracion(min) { this.duracionMin = Math.min(60, Math.max(1, min)); }
    incrementar() { this.setDuracion(this.duracionMin + 1); }
    decrementar() { this.setDuracion(this.duracionMin - 1); }
    iniciarCuenta() { this.estado = { inicio: Date.now(), pausadoEn: null, pausaAcum: 0 }; }
    alternarPausa() {
      if (this.estado.inicio == null) return;
      if (this.corriendo()) {
        this.estado = { ...this.estado, pausadoEn: Date.now() };
      } else if (this.estado.pausadoEn != null) {
        const pausaMs = Date.now() - this.estado.pausadoEn;
        this.estado = { ...this.estado, pausaAcum: this.estado.pausaAcum + pausaMs, pausadoEn: null };
      }
    }
    reiniciarCuenta() { this.estado = { inicio: null, pausadoEn: null, pausaAcum: 0 }; }
    progresoEn(ahoraMs) {
      const e = this.estado;
      if (e.inicio == null) return 0;
      const fin = e.pausadoEn != null ? e.pausadoEn : ahoraMs;
      const transcurrido = Math.max(0, fin - e.inicio - e.pausaAcum);
      const duracionMs = this.duracionMin * 60000;
      if (duracionMs <= 0) return 1;
      return Math.min(1, Math.max(0, transcurrido / duracionMs));
    }
    corriendo() { return this.estado.inicio != null && this.estado.pausadoEn == null; }
    iniciada() { return this.estado.inicio != null; }
  }

  const timerT = new TimerEngine(5);
  const timerS = new TimerEngine(4);

  // ---------- Sonido: chime() vía Web Audio API ----------
  let audioCtx = null;
  function getCtx() {
    if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    if (audioCtx.state === 'suspended') audioCtx.resume();
    return audioCtx;
  }
  function chime(modo) {
    const ctx = getCtx();
    const notas = modo === 'temporizador'
      ? [523.25, 659.25, 783.99, 1046.5]
      : [523.25, 659.25, 783.99];
    const intervalo = modo === 'temporizador' ? 0.11 : 0.2;
    const volumen = modo === 'temporizador' ? 0.22 : 0.14;
    const ahora = ctx.currentTime;
    notas.forEach((freq, i) => {
      const t0 = ahora + i * intervalo;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'triangle';
      osc.frequency.value = freq;
      gain.gain.setValueAtTime(0.0001, t0);
      gain.gain.exponentialRampToValueAtTime(volumen, t0 + 0.02);
      gain.gain.exponentialRampToValueAtTime(0.0001, t0 + 0.4);
      osc.connect(gain).connect(ctx.destination);
      osc.start(t0);
      osc.stop(t0 + 0.42);
    });
  }

  // ---------- Geometría del recorrido perimetral (espejo de PerimeterPath.kt) ----------
  function perimeterPathD(w, h, radius) {
    const r = Math.min(radius, Math.min(w, h) / 2);
    const midX = w / 2;
    return `M ${midX} 0 H ${w - r} A ${r} ${r} 0 0 1 ${w} ${r} V ${h - r} A ${r} ${r} 0 0 1 ${w - r} ${h} H ${r} A ${r} ${r} 0 0 1 0 ${h - r} V ${r} A ${r} ${r} 0 0 1 ${r} 0 H ${midX} Z`;
  }

  const NS = 'http://www.w3.org/2000/svg';
  function svgEl(tag, attrs) {
    const el = document.createElementNS(NS, tag);
    for (const k in attrs) el.setAttribute(k, attrs[k]);
    return el;
  }

  /**
   * Construye un recorrido perimetral animado dentro de un <svg>: track, rastro y gusanito,
   * espejo exacto de perimeterPath()/drawRecorrido()/drawGusanito() en PerimeterPath.kt.
   */
  function crearRecorrido(svg, opts) {
    const w = STAGE_W - 60, h = STAGE_H - 60; // padding 30px en cada borde
    const d = perimeterPathD(w, h, 34);
    const g = svgEl('g', { transform: 'translate(30,30)' });
    svg.appendChild(g);

    const track = svgEl('path', {
      d, fill: 'none', stroke: opts.trackColor, 'stroke-width': opts.strokeWidth, 'stroke-linecap': 'round',
    });
    const trail = svgEl('path', {
      d, fill: 'none', stroke: opts.trailColor, 'stroke-width': opts.strokeWidth, 'stroke-linecap': 'round',
    });
    g.appendChild(track);
    g.appendChild(trail);

    const refPath = svgEl('path', { d });
    const length = refPath.getTotalLength ? refPath.getTotalLength() : 0;
    trail.setAttribute('stroke-dasharray', String(length));
    trail.setAttribute('stroke-dashoffset', String(length));

    // Medición de posición/tangente: se usa refPath (no montado) vía un clon montado invisible.
    const medidor = refPath.cloneNode();
    medidor.setAttribute('stroke', 'none');
    medidor.setAttribute('fill', 'none');
    medidor.setAttribute('opacity', '0');
    svg.appendChild(medidor);

    const segmentos = opts.segmentos;
    const bodies = [];
    for (let i = 0; i < segmentos; i++) {
      const c = svgEl('circle', { r: 0, fill: opts.colorCuerpo, cx: 0, cy: 0 });
      g.appendChild(c);
      bodies.push(c);
    }

    const headGroup = svgEl('g');
    g.appendChild(headGroup);

    if (opts.conAntenas) {
      for (const signo of [-1, 1]) {
        const linea = svgEl('line', {
          x1: opts.headRadius * 0.5, y1: signo * opts.headRadius * 0.7,
          x2: opts.headRadius * 0.35, y2: signo * opts.headRadius * 2.2,
          stroke: opts.colorCuerpo, 'stroke-width': opts.headRadius * 0.09, 'stroke-linecap': 'round',
        });
        const punta = svgEl('circle', {
          cx: opts.headRadius * 0.35, cy: signo * opts.headRadius * 2.2, r: opts.headRadius * 0.11,
          fill: opts.colorCuerpo,
        });
        headGroup.appendChild(linea);
        headGroup.appendChild(punta);
      }
    }

    const eyeRadius = opts.headRadius * 0.24;
    for (const signo of [-1, 1]) {
      const ojo = svgEl('circle', {
        cx: opts.headRadius * 0.30, cy: signo * opts.headRadius * 0.30, r: eyeRadius, fill: opts.colorOjo,
      });
      const pupila = svgEl('circle', {
        cx: opts.headRadius * 0.30 + eyeRadius * 0.4, cy: signo * opts.headRadius * 0.30, r: eyeRadius * 0.48,
        fill: opts.colorPupila,
      });
      headGroup.appendChild(ojo);
      headGroup.appendChild(pupila);
    }

    function medirEn(d) {
      const dc = Math.min(length, Math.max(0, d));
      return medidor.getPointAtLength(dc);
    }

    function actualizar(progreso, tiempoSeg) {
      const distanciaRastro = progreso * length;
      trail.setAttribute('stroke-dashoffset', String(length - distanciaRastro));

      const separacionPx = opts.headRadius * opts.separacionFactor;
      const amplitud = opts.amplitudPx != null ? opts.amplitudPx : opts.headRadius * 0.16;

      for (let i = segmentos - 1; i >= 0; i--) {
        const distancia = distanciaRastro - i * separacionPx;
        const circle = bodies[i];
        if (distancia < 0) { circle.setAttribute('r', 0); continue; }
        const dClamp = Math.min(length, Math.max(0, distancia));
        const pos = medirEn(dClamp);
        const back = Math.max(0, dClamp - 0.5);
        const fwd = Math.min(length, dClamp + 0.5);
        const pBack = medirEn(back);
        const pFwd = medirEn(fwd);
        const angulo = Math.atan2(pFwd.y - pBack.y, pFwd.x - pBack.x);

        const perpX = -Math.sin(angulo);
        const perpY = Math.cos(angulo);
        const onda = Math.sin(tiempoSeg * opts.frecuenciaOndulacion - i * 0.85) * amplitud;
        const cx = pos.x + perpX * onda;
        const cy = pos.y + perpY * onda;

        const radio = Math.max(opts.headRadius * 0.32, opts.headRadius - 3 - i * 1.9);
        const opacidad = Math.min(1, Math.max(0, 0.94 - i * 0.035));
        circle.setAttribute('cx', cx);
        circle.setAttribute('cy', cy);
        circle.setAttribute('r', radio);
        circle.setAttribute('opacity', opacidad);

        if (i === 0) {
          const deg = angulo * 180 / Math.PI;
          headGroup.setAttribute('transform', `translate(${cx},${cy}) rotate(${deg})`);
        }
      }
    }

    return { actualizar };
  }

  const svgT = document.getElementById('canvas-t');
  const recorridoT = crearRecorrido(svgT, {
    trackColor: 'var(--warm-border)', trailColor: 'var(--warm-worm)', strokeWidth: 20,
    segmentos: 9, separacionFactor: 0.86, frecuenciaOndulacion: 5, amplitudPx: null,
    conAntenas: true, headRadius: 24, colorCuerpo: '#7BC96F', colorOjo: '#FFFDF6', colorPupila: '#3A3128',
  });

  const svgS = document.getElementById('canvas-s');
  const recorridoS = crearRecorrido(svgS, {
    trackColor: 'var(--cool-track)', trailColor: 'var(--cool-worm)', strokeWidth: 16,
    segmentos: 8, separacionFactor: 0.8, frecuenciaOndulacion: 1.1, amplitudPx: 1.1,
    conAntenas: false, headRadius: 24 * 0.86, colorCuerpo: '#9AA1A8', colorOjo: '#F2F1ED', colorPupila: '#45423C',
  });

  // ---------- Pantalla 1 · Inicio ----------
  document.getElementById('card-temporizador').addEventListener('click', () => irA('screen-duracion-t'));
  document.getElementById('card-silla').addEventListener('click', () => irA('screen-duracion-s'));

  // ---------- Pantalla 2 · Duración Temporizador ----------
  const ATAJOS = [1, 3, 5, 10, 15, 30];
  const atajosGrid = document.getElementById('atajos-grid');
  const atajoChips = ATAJOS.map(min => {
    const btn = document.createElement('button');
    btn.className = 'atajo-chip';
    btn.textContent = min;
    btn.addEventListener('click', () => { timerT.setDuracion(min); renderDuracionT(); });
    atajosGrid.appendChild(btn);
    return { min, btn };
  });

  function renderDuracionT() {
    document.getElementById('t-valor').textContent = timerT.duracionMin;
    atajoChips.forEach(({ min, btn }) => btn.classList.toggle('selected', min === timerT.duracionMin));
  }
  document.getElementById('t-minus').addEventListener('click', () => { timerT.decrementar(); renderDuracionT(); });
  document.getElementById('t-plus').addEventListener('click', () => { timerT.incrementar(); renderDuracionT(); });
  document.getElementById('back-duracion-t').addEventListener('click', () => history.back());
  document.getElementById('empezar-btn').addEventListener('click', () => {
    timerT.iniciarCuenta();
    irA('screen-cuenta-t');
  });

  // ---------- Pantalla 3 · Cuenta regresiva Temporizador ----------
  function mensajeProgresoT(p) {
    if (p < 0.02) return '¡Vamos!';
    if (p < 0.25) return 'El gusanito camina';
    if (p < 0.50) return '¡Un cuarto de vuelta!';
    if (p < 0.75) return '¡Ya vas a la mitad!';
    if (p < 0.94) return '¡Ya casi!';
    return '¡Última esquina!';
  }
  const pausaBtn = document.getElementById('t-pausa');
  pausaBtn.addEventListener('click', () => { timerT.alternarPausa(); });
  document.getElementById('t-terminar').addEventListener('click', () => {
    timerT.reiniciarCuenta();
    renderDuracionT();
    irA('screen-duracion-t');
  });

  let completadoT = false;
  function loopCuentaT(ahoraMs) {
    const progreso = timerT.progresoEn(ahoraMs);
    const duracionSeg = timerT.duracionMin * 60;
    const transcurridoSeg = Math.min(duracionSeg, Math.max(0, Math.round(progreso * duracionSeg)));
    const restanteSeg = duracionSeg - transcurridoSeg;
    const mm = String(Math.floor(restanteSeg / 60)).padStart(2, '0');
    const ss = String(restanteSeg % 60).padStart(2, '0');
    document.getElementById('t-tiempo').textContent = `${mm}:${ss}`;
    document.getElementById('t-mensaje').textContent = mensajeProgresoT(progreso);
    recorridoT.actualizar(progreso, ahoraMs / 1000);

    pausaBtn.classList.toggle('pausado', !timerT.corriendo());
    pausaBtn.innerHTML = timerT.corriendo()
      ? '<div class="pausa-icono-bars"><div></div><div></div></div>'
      : '<svg viewBox="0 0 24 24" width="30" height="30"><path d="M0,0 L24,12 L0,24 Z" fill="#FFFFFF"/></svg>';

    const completado = timerT.iniciada() && progreso >= 1;
    if (completado && !completadoT) {
      completadoT = true;
      if (navigator.vibrate) navigator.vibrate(180);
      chime('temporizador');
      irA('screen-termino', { replace: true });
    }
    if (!completado) completadoT = false;
  }

  // ---------- Pantalla 4 · Terminó ----------
  const CONFETTI_SPECS = [
    { x: 40 / 412, w: 14, h: 19, r: 3, color: 'var(--warm-confetti0)', dur: 2400, delay: 0 },
    { x: 96 / 412, w: 12, h: 16, r: 3, color: 'var(--warm-confetti1)', dur: 2900, delay: 400 },
    { x: 150 / 412, w: 15, h: 15, r: 8, color: 'var(--warm-confetti2)', dur: 2600, delay: 900 },
    { x: 210 / 412, w: 13, h: 18, r: 3, color: 'var(--warm-confetti3)', dur: 3100, delay: 200 },
    { x: 268 / 412, w: 14, h: 14, r: 7, color: 'var(--warm-confetti0)', dur: 2500, delay: 1200 },
    { x: 322 / 412, w: 12, h: 17, r: 3, color: 'var(--warm-confetti1)', dur: 2800, delay: 600 },
    { x: 360 / 412, w: 13, h: 13, r: 7, color: 'var(--warm-confetti2)', dur: 3300, delay: 1600 },
    { x: 16 / 412, w: 12, h: 16, r: 3, color: 'var(--warm-confetti3)', dur: 2700, delay: 1900 },
  ];
  const confettiLayer = document.getElementById('confetti-layer');
  CONFETTI_SPECS.forEach(spec => {
    const el = document.createElement('div');
    el.className = 'confetti-pieza';
    el.style.left = `${spec.x * STAGE_W}px`;
    el.style.width = `${spec.w}px`;
    el.style.height = `${spec.h}px`;
    el.style.borderRadius = `${spec.r}px`;
    el.style.background = spec.color;
    el.style.animation = `fall ${spec.dur}ms linear ${spec.delay}ms infinite`;
    confettiLayer.appendChild(el);
  });

  document.getElementById('otra-vez-btn').addEventListener('click', () => {
    timerT.iniciarCuenta();
    completadoT = false;
    irA('screen-cuenta-t');
  });
  document.getElementById('volver-inicio-btn').addEventListener('click', () => {
    timerT.reiniciarCuenta();
    completadoT = false;
    irA('screen-inicio', { replace: true });
  });

  // ---------- Pantalla 5 · Silla · duración ----------
  const EDADES = [3, 4, 5, 6, 8];
  const edadGrid = document.getElementById('edad-grid');
  const edadChips = EDADES.map(edad => {
    const btn = document.createElement('button');
    btn.className = 'edad-chip';
    btn.innerHTML = `<div class="edad-numero">${edad}</div><div class="edad-label">años</div>`;
    btn.addEventListener('click', () => { timerS.setDuracion(edad); renderDuracionS(); });
    edadGrid.appendChild(btn);
    return { edad, btn };
  });

  function renderDuracionS() {
    document.getElementById('s-valor').textContent = timerS.duracionMin;
    edadChips.forEach(({ edad, btn }) => btn.classList.toggle('selected', edad === timerS.duracionMin));
  }
  document.getElementById('s-minus').addEventListener('click', () => { timerS.decrementar(); renderDuracionS(); });
  document.getElementById('s-plus').addEventListener('click', () => { timerS.incrementar(); renderDuracionS(); });
  document.getElementById('back-duracion-s').addEventListener('click', () => history.back());
  document.getElementById('comenzar-btn').addEventListener('click', () => {
    timerS.iniciarCuenta();
    irA('screen-cuenta-s');
  });

  // ---------- Pantalla 6 · Silla · cuenta regresiva ----------
  let completadoS = false;
  function completarSilla() {
    if (completadoS) return;
    completadoS = true;
    chime('silla');
    irA('screen-cierre-s', { replace: true });
  }

  function loopCuentaS(ahoraMs) {
    const progreso = timerS.progresoEn(ahoraMs);
    const duracionSeg = timerS.duracionMin * 60;
    const transcurridoSeg = Math.min(duracionSeg, Math.max(0, Math.round(progreso * duracionSeg)));
    const restanteSeg = duracionSeg - transcurridoSeg;
    const mm = String(Math.floor(restanteSeg / 60)).padStart(2, '0');
    const ss = String(restanteSeg % 60).padStart(2, '0');
    document.getElementById('s-tiempo').textContent = `${mm}:${ss}`;
    document.getElementById('s-mensaje').textContent = progreso >= 0.88 ? 'ya falta poco' : 'el gusanito está dando la vuelta';
    recorridoS.actualizar(progreso, ahoraMs / 1000);

    const completado = timerS.iniciada() && progreso >= 1;
    if (completado) completarSilla();
  }

  // Barra "mantén presionado para terminar": llena en 1.4s, retrocede a 0 en 0.25s al soltar.
  const holdBar = document.getElementById('hold-bar');
  const holdFill = document.getElementById('hold-fill');
  let holdTimeoutId = null;
  function holdStart(ev) {
    ev.preventDefault();
    if (holdTimeoutId) clearTimeout(holdTimeoutId);
    holdFill.style.transition = 'width 1.4s linear';
    void holdFill.offsetWidth;
    holdFill.style.width = '100%';
    holdTimeoutId = setTimeout(() => {
      holdTimeoutId = null;
      completarSilla();
    }, 1400);
  }
  function holdEnd() {
    if (holdTimeoutId) { clearTimeout(holdTimeoutId); holdTimeoutId = null; }
    const fracion = holdFill.getBoundingClientRect().width / holdBar.getBoundingClientRect().width;
    holdFill.style.transition = 'none';
    holdFill.style.width = `${fracion * 100}%`;
    void holdFill.offsetWidth;
    holdFill.style.transition = 'width 0.25s linear';
    holdFill.style.width = '0%';
  }
  holdBar.addEventListener('pointerdown', holdStart);
  holdBar.addEventListener('pointerup', holdEnd);
  holdBar.addEventListener('pointerleave', holdEnd);
  holdBar.addEventListener('pointercancel', holdEnd);

  // ---------- Pantalla 7 · Silla · cierre ----------
  const PAUTAS = [
    { guia: 'Objetivo de desescalada, no de castigo moral.', cuerpo: ' El propósito debe ser la regulación fisiológica (bajar la activación del sistema nervioso/amígdala), no la reflexión abstracta.' },
    { guia: 'Consistencia e inmediatez.', cuerpo: ' Debe aplicarse inmediatamente después de la conducta límite (por ejemplo, agresión física o destrucción de objetos), sin advertencias repetitivas que diluyan el límite.' },
    { guia: 'Criterio de finalización predecible.', cuerpo: ' Utiliza un temporizador visual o auditivo. La duración debe ser breve (2 a 4 minutos). Si el niño sigue en desborde motor o llanto intenso al sonar la alarma, la pausa se extiende únicamente hasta que logre entre 30 y 60 segundos de calma relativa.' },
    { guia: 'Cierre sin sermones ni sobreexplicación.', cuerpo: ' Una vez cumplido el tiempo, el ciclo se cierra de inmediato. No se exige una disculpa forzada ni un debate largo; se restablece la actividad normal reforzando la conducta alternativa esperada: «las manos se usan para jugar, no para golpear; volvamos a armar los bloques».' },
  ];
  const pautasLista = document.getElementById('pautas-lista');
  PAUTAS.forEach((p, i) => {
    const item = document.createElement('div');
    item.className = 'pauta-item';
    item.innerHTML = `<div class="pauta-bullet">${i + 1}</div><div class="pauta-texto"><b>${p.guia}</b>${p.cuerpo}</div>`;
    pautasLista.appendChild(item);
  });

  document.getElementById('listo-btn').addEventListener('click', () => {
    timerS.reiniciarCuenta();
    completadoS = false;
    irA('screen-inicio', { replace: true });
  });

  // ---------- Bucle principal ----------
  renderDuracionT();
  renderDuracionS();
  function tick() {
    const ahoraMs = Date.now();
    if (screens['screen-cuenta-t'].classList.contains('active')) loopCuentaT(ahoraMs);
    if (screens['screen-cuenta-s'].classList.contains('active')) loopCuentaS(ahoraMs);
    requestAnimationFrame(tick);
  }
  requestAnimationFrame(tick);
})();
