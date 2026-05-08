(() => {
  function onReady(fn) {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', fn, { once: true });
    } else {
      fn();
    }
  }

  function initPixelDemo(root) {
    const canvas = root.querySelector('canvas');
    const readout = root.querySelector('[data-readout]');
    if (!canvas || !readout) return;

    const ctx = canvas.getContext('2d');
    const cols = 10;
    const rows = 8;
    const state = { x: 4, y: 3 };
    const board = {
      cell: 0,
      left: 0,
      top: 0,
      width: 0,
      height: 0,
    };

    function clamp(value, min, max) {
      return Math.max(min, Math.min(max, value));
    }

    function measureBoard() {
      const marginX = 30;
      const marginY = 28;
      board.cell = Math.floor(Math.min((canvas.width - marginX * 2) / cols, (canvas.height - marginY * 2) / rows));
      board.width = board.cell * cols;
      board.height = board.cell * rows;
      board.left = Math.round((canvas.width - board.width) / 2);
      board.top = Math.round((canvas.height - board.height) / 2) + 4;
    }

    function placeReadout() {
      const canvasRect = canvas.getBoundingClientRect();
      const rootRect = root.getBoundingClientRect();
      if (!canvasRect.width || !canvasRect.height) return;

      const centerX = board.left + (state.x + 0.5) * board.cell;
      const centerY = board.top + (state.y + 0.5) * board.cell;
      const localX = (centerX / canvas.width) * canvasRect.width + canvasRect.left - rootRect.left;
      const localY = (centerY / canvas.height) * canvasRect.height + canvasRect.top - rootRect.top;

      readout.style.left = `${localX}px`;
      readout.style.top = `${localY}px`;
      root.classList.toggle('is-near-right', localX > rootRect.width * 0.68);
      root.classList.toggle('is-near-top', localY < 46);
      readout.textContent = `pixel (${state.x}, ${state.y})`;
    }

    function draw() {
      measureBoard();
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      const panel = ctx.createLinearGradient(0, 0, 0, canvas.height);
      panel.addColorStop(0, '#ffffff');
      panel.addColorStop(1, '#f4f8fb');
      ctx.fillStyle = panel;
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      ctx.save();
      ctx.shadowColor = 'rgba(15, 23, 42, 0.12)';
      ctx.shadowBlur = 18;
      ctx.shadowOffsetY = 8;
      ctx.fillStyle = '#ffffff';
      ctx.fillRect(board.left - 1, board.top - 1, board.width + 2, board.height + 2);
      ctx.restore();

      for (let y = 0; y < rows; y += 1) {
        for (let x = 0; x < cols; x += 1) {
          const tone = 245 - Math.round((x / (cols - 1)) * 22 + (y / (rows - 1)) * 18);
          ctx.fillStyle = `rgb(${tone}, ${Math.min(tone + 5, 255)}, ${Math.min(tone + 10, 255)})`;
          ctx.fillRect(board.left + x * board.cell, board.top + y * board.cell, board.cell, board.cell);
        }
      }

      ctx.strokeStyle = '#cbd5e1';
      ctx.lineWidth = 1;
      for (let i = 0; i <= cols; i += 1) {
        const px = board.left + i * board.cell;
        ctx.beginPath();
        ctx.moveTo(px, board.top);
        ctx.lineTo(px, board.top + rows * board.cell);
        ctx.stroke();
      }
      for (let j = 0; j <= rows; j += 1) {
        const py = board.top + j * board.cell;
        ctx.beginPath();
        ctx.moveTo(board.left, py);
        ctx.lineTo(board.left + cols * board.cell, py);
        ctx.stroke();
      }

      const selectedLeft = board.left + state.x * board.cell;
      const selectedTop = board.top + state.y * board.cell;
      ctx.fillStyle = 'rgba(31, 95, 139, 0.2)';
      ctx.fillRect(selectedLeft, selectedTop, board.cell, board.cell);
      ctx.strokeStyle = '#1f5f8b';
      ctx.lineWidth = 3;
      ctx.strokeRect(selectedLeft + 1.5, selectedTop + 1.5, board.cell - 3, board.cell - 3);

      ctx.fillStyle = '#172033';
      ctx.font = '600 13px system-ui, -apple-system, sans-serif';
      ctx.fillText('(0, 0)', board.left - 2, board.top - 14);
      ctx.fillText('x', board.left + board.width + 14, board.top + board.height + 4);
      ctx.fillText('y', board.left - 16, board.top + board.height + 20);

      ctx.strokeStyle = '#64748b';
      ctx.lineWidth = 2;
      ctx.beginPath();
      ctx.moveTo(board.left, board.top);
      ctx.lineTo(board.left + board.width + 20, board.top);
      ctx.moveTo(board.left, board.top);
      ctx.lineTo(board.left, board.top + board.height + 20);
      ctx.stroke();

      ctx.fillStyle = '#1f5f8b';
      ctx.beginPath();
      ctx.arc(
        board.left + (state.x + 0.5) * board.cell,
        board.top + (state.y + 0.5) * board.cell,
        5,
        0,
        Math.PI * 2,
      );
      ctx.fill();

      placeReadout();
    }

    function pickFromEvent(event) {
      const rect = canvas.getBoundingClientRect();
      const canvasX = ((event.clientX - rect.left) / rect.width) * canvas.width;
      const canvasY = ((event.clientY - rect.top) / rect.height) * canvas.height;
      state.x = clamp(Math.floor((canvasX - board.left) / board.cell), 0, cols - 1);
      state.y = clamp(Math.floor((canvasY - board.top) / board.cell), 0, rows - 1);
      draw();
    }

    canvas.addEventListener('pointerdown', (event) => {
      canvas.setPointerCapture(event.pointerId);
      pickFromEvent(event);
    });
    canvas.addEventListener('pointermove', (event) => {
      if (event.buttons) pickFromEvent(event);
    });
    if ('ResizeObserver' in window) {
      const resizeObserver = new ResizeObserver(placeReadout);
      resizeObserver.observe(canvas);
    }
    draw();
  }

  function initModeDemo(root) {
    const canvas = root.querySelector('canvas');
    const rectSelect = root.querySelector('select[data-shape="rect"]');
    const ellipseSelect = root.querySelector('select[data-shape="ellipse"]');
    if (!canvas || !rectSelect || !ellipseSelect) return;

    const ctx = canvas.getContext('2d');

    function drawPanel(opts) {
      const { x, y, w, h, shape, mode, title } = opts;
      ctx.save();
      ctx.translate(opts.left, 0);

      ctx.fillStyle = '#153e5c';
      ctx.font = '700 15px system-ui, -apple-system, sans-serif';
      ctx.fillText(title, 22, 30);
      ctx.font = '600 12px system-ui, -apple-system, sans-serif';
      ctx.fillStyle = '#64748b';
      ctx.fillText(`${shape}Mode(${mode})`, 22, 50);

      const left = mode === 'CENTER' ? x - w / 2 : x;
      const top = mode === 'CENTER' ? y - h / 2 : y;

      ctx.setLineDash([5, 5]);
      ctx.strokeStyle = '#94a3b8';
      ctx.beginPath();
      ctx.moveTo(x, 64);
      ctx.lineTo(x, 230);
      ctx.moveTo(45, y);
      ctx.lineTo(opts.width - 35, y);
      ctx.stroke();
      ctx.setLineDash([]);

      ctx.fillStyle = 'rgba(31, 95, 139, 0.14)';
      ctx.strokeStyle = '#1f5f8b';
      ctx.lineWidth = 2.5;
      if (shape === 'rect') {
        ctx.fillRect(left, top, w, h);
        ctx.strokeRect(left, top, w, h);
      } else {
        ctx.beginPath();
        ctx.ellipse(left + w / 2, top + h / 2, w / 2, h / 2, 0, 0, Math.PI * 2);
        ctx.fill();
        ctx.stroke();
      }

      ctx.fillStyle = '#e11d48';
      ctx.beginPath();
      ctx.arc(x, y, 5, 0, Math.PI * 2);
      ctx.fill();
      ctx.font = '600 12px system-ui, -apple-system, sans-serif';
      ctx.fillText('(x, y)', x + 9, y - 9);
      ctx.restore();
    }

    function draw() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.fillStyle = '#f8fafc';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      drawPanel({
        left: 24,
        width: 306,
        x: 138,
        y: 145,
        w: 130,
        h: 82,
        shape: 'rect',
        mode: rectSelect.value,
        title: 'Rectángulo',
      });
      drawPanel({
        left: 390,
        width: 306,
        x: 138,
        y: 145,
        w: 130,
        h: 82,
        shape: 'ellipse',
        mode: ellipseSelect.value,
        title: 'Elipse',
      });
    }

    [rectSelect, ellipseSelect].forEach((select) => select.addEventListener('change', draw));
    draw();
  }

  function initRgbDemo(root) {
    const field = root.querySelector('[data-color-field]');
    const hueStrip = root.querySelector('[data-hue-strip]');
    const chip = root.querySelector('[data-color-chip]');
    const code = root.querySelector('[data-code]');
    const hex = root.querySelector('[data-hex]');
    const values = {
      r: root.querySelector('[data-channel-value="r"]'),
      g: root.querySelector('[data-channel-value="g"]'),
      b: root.querySelector('[data-channel-value="b"]'),
    };
    if (!field || !hueStrip || !chip || !code || !hex || !values.r || !values.g || !values.b) return;

    const fieldCtx = field.getContext('2d');
    const hueCtx = hueStrip.getContext('2d');
    const state = { h: 0.09, s: 0.84, v: 1 };

    function clamp(value, min, max) {
      return Math.max(min, Math.min(max, value));
    }

    function hsvToRgb(h, s, v) {
      const i = Math.floor(h * 6);
      const f = h * 6 - i;
      const p = v * (1 - s);
      const q = v * (1 - f * s);
      const t = v * (1 - (1 - f) * s);
      const mod = i % 6;
      const channels = [
        [v, t, p],
        [q, v, p],
        [p, v, t],
        [p, q, v],
        [t, p, v],
        [v, p, q],
      ][mod];

      return channels.map((channel) => Math.round(channel * 255));
    }

    function hexPair(value) {
      return value.toString(16).padStart(2, '0').toUpperCase();
    }

    function drawField() {
      const [r, g, b] = hsvToRgb(state.h, 1, 1);
      fieldCtx.clearRect(0, 0, field.width, field.height);
      fieldCtx.fillStyle = `rgb(${r}, ${g}, ${b})`;
      fieldCtx.fillRect(0, 0, field.width, field.height);

      const white = fieldCtx.createLinearGradient(0, 0, field.width, 0);
      white.addColorStop(0, '#ffffff');
      white.addColorStop(1, 'rgba(255,255,255,0)');
      fieldCtx.fillStyle = white;
      fieldCtx.fillRect(0, 0, field.width, field.height);

      const black = fieldCtx.createLinearGradient(0, 0, 0, field.height);
      black.addColorStop(0, 'rgba(0,0,0,0)');
      black.addColorStop(1, '#000000');
      fieldCtx.fillStyle = black;
      fieldCtx.fillRect(0, 0, field.width, field.height);

      const x = state.s * field.width;
      const y = (1 - state.v) * field.height;
      fieldCtx.lineWidth = 2.5;
      fieldCtx.strokeStyle = '#ffffff';
      fieldCtx.beginPath();
      fieldCtx.arc(x, y, 7, 0, Math.PI * 2);
      fieldCtx.stroke();
      fieldCtx.lineWidth = 1.5;
      fieldCtx.strokeStyle = '#172033';
      fieldCtx.beginPath();
      fieldCtx.arc(x, y, 8.5, 0, Math.PI * 2);
      fieldCtx.stroke();
    }

    function drawHue() {
      const gradient = hueCtx.createLinearGradient(0, 0, 0, hueStrip.height);
      for (let i = 0; i <= 6; i += 1) {
        const [r, g, b] = hsvToRgb(i / 6, 1, 1);
        gradient.addColorStop(i / 6, `rgb(${r}, ${g}, ${b})`);
      }
      hueCtx.fillStyle = gradient;
      hueCtx.fillRect(0, 0, hueStrip.width, hueStrip.height);

      const y = state.h * hueStrip.height;
      hueCtx.lineWidth = 3;
      hueCtx.strokeStyle = '#ffffff';
      hueCtx.beginPath();
      hueCtx.moveTo(0, y);
      hueCtx.lineTo(hueStrip.width, y);
      hueCtx.stroke();
      hueCtx.lineWidth = 1.4;
      hueCtx.strokeStyle = '#172033';
      hueCtx.beginPath();
      hueCtx.moveTo(0, y);
      hueCtx.lineTo(hueStrip.width, y);
      hueCtx.stroke();
    }

    function draw() {
      drawField();
      drawHue();
      const [r, g, b] = hsvToRgb(state.h, state.s, state.v);
      const hexValue = `#${hexPair(r)}${hexPair(g)}${hexPair(b)}`;
      chip.style.background = `rgb(${r}, ${g}, ${b})`;
      values.r.textContent = r;
      values.g.textContent = g;
      values.b.textContent = b;
      hex.textContent = hexValue;
      code.textContent = `fill(${r}, ${g}, ${b});`;
    }

    function setFieldFromEvent(event) {
      const rect = field.getBoundingClientRect();
      state.s = clamp((event.clientX - rect.left) / rect.width, 0, 1);
      state.v = 1 - clamp((event.clientY - rect.top) / rect.height, 0, 1);
      draw();
    }

    function setHueFromEvent(event) {
      const rect = hueStrip.getBoundingClientRect();
      state.h = clamp((event.clientY - rect.top) / rect.height, 0, 1);
      draw();
    }

    field.addEventListener('pointerdown', (event) => {
      field.setPointerCapture(event.pointerId);
      setFieldFromEvent(event);
    });
    field.addEventListener('pointermove', (event) => {
      if (event.buttons) setFieldFromEvent(event);
    });
    hueStrip.addEventListener('pointerdown', (event) => {
      hueStrip.setPointerCapture(event.pointerId);
      setHueFromEvent(event);
    });
    hueStrip.addEventListener('pointermove', (event) => {
      if (event.buttons) setHueFromEvent(event);
    });
    draw();
  }

  function initGraySampler(root) {
    const image = root.querySelector('img');
    const marker = root.querySelector('[data-sampler-marker]');
    const readout = root.querySelector('[data-gray-readout]');
    if (!image || !marker || !readout) return;

    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d', { willReadFrequently: true });
    const currentRatio = { x: 0.62, y: 0.42 };

    function clamp(value, min, max) {
      return Math.max(min, Math.min(max, value));
    }

    function syncCanvas() {
      if (!image.naturalWidth || !image.naturalHeight) return false;

      if (canvas.width !== image.naturalWidth || canvas.height !== image.naturalHeight) {
        canvas.width = image.naturalWidth;
        canvas.height = image.naturalHeight;
      }

      ctx.drawImage(image, 0, 0, canvas.width, canvas.height);
      return true;
    }

    function sampleAt(clientX, clientY) {
      const rect = image.getBoundingClientRect();
      if (!rect.width || !rect.height) return;

      const localX = clamp(clientX - rect.left, 0, rect.width);
      const localY = clamp(clientY - rect.top, 0, rect.height);
      currentRatio.x = localX / rect.width;
      currentRatio.y = localY / rect.height;

      marker.style.left = `${localX}px`;
      marker.style.top = `${localY}px`;
      readout.style.left = `${localX}px`;
      readout.style.top = `${localY}px`;
      root.classList.toggle('is-near-right', localX > rect.width * 0.7);
      root.classList.toggle('is-near-top', localY < 48);

      if (!syncCanvas()) return;

      const imageX = clamp(Math.round((localX / rect.width) * (canvas.width - 1)), 0, canvas.width - 1);
      const imageY = clamp(Math.round((localY / rect.height) * (canvas.height - 1)), 0, canvas.height - 1);
      const [r, g, b] = ctx.getImageData(imageX, imageY, 1, 1).data;
      const gray = Math.round((r + g + b) / 3);

      readout.textContent = `gris: ${gray}`;
    }

    function sampleRatio(xRatio, yRatio) {
      const rect = image.getBoundingClientRect();
      if (!rect.width || !rect.height) return;

      sampleAt(rect.left + rect.width * xRatio, rect.top + rect.height * yRatio);
    }

    root.addEventListener('pointerdown', (event) => {
      root.setPointerCapture(event.pointerId);
      sampleAt(event.clientX, event.clientY);
    });
    root.addEventListener('pointermove', (event) => {
      if (event.buttons) sampleAt(event.clientX, event.clientY);
    });

    if (image.complete) {
      sampleRatio(currentRatio.x, currentRatio.y);
    } else {
      image.addEventListener('load', () => sampleRatio(currentRatio.x, currentRatio.y), { once: true });
    }

    if ('ResizeObserver' in window) {
      const resizeObserver = new ResizeObserver(() => sampleRatio(currentRatio.x, currentRatio.y));
      resizeObserver.observe(image);
    }
  }

  onReady(() => {
    document.querySelectorAll('[data-p1-pixel-demo]').forEach(initPixelDemo);
    document.querySelectorAll('[data-p1-mode-demo]').forEach(initModeDemo);
    document.querySelectorAll('[data-p1-rgb-demo]').forEach(initRgbDemo);
    document.querySelectorAll('[data-p1-gray-sampler]').forEach(initGraySampler);
  });
})();
