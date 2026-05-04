(() => {
  const canvas = document.getElementById('rot-canvas-uza');
  const slider = document.getElementById('rot-angle-uza');
  const valLabel = document.getElementById('rot-angle-val');
  const beforeEl = document.getElementById('rot-before-uza');
  const afterEl = document.getElementById('rot-after-uza');
  const m00El = document.getElementById('rot-m00');
  const m01El = document.getElementById('rot-m01');
  const m10El = document.getElementById('rot-m10');
  const m11El = document.getElementById('rot-m11');
  if (!canvas || !slider || !valLabel || !beforeEl || !afterEl || !m00El || !m01El || !m10El || !m11El) return;

  const ctx = canvas.getContext('2d');
  const W = canvas.width;
  const H = canvas.height;
  const cx = W / 2;
  const cy = H / 2;
  const SC = 95;
  const POINT = [0.72, 0.24];
  const ORBIT = Math.hypot(POINT[0], POINT[1]);

  function tw(x, y) {
    return [cx + x * SC, cy - y * SC];
  }

  function rot(x, y, cosA, sinA) {
    return [cosA * x + sinA * y, -sinA * x + cosA * y];
  }

  function fmt(x) {
    return x.toFixed(2);
  }

  function drawArrow(x, y, color, lw) {
    const [sx, sy] = tw(x, y);
    const angle = Math.atan2(sy - cy, sx - cx);
    const head = 11;

    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.lineTo(sx, sy);
    ctx.strokeStyle = color;
    ctx.lineWidth = lw;
    ctx.lineCap = 'round';
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(sx, sy);
    ctx.lineTo(sx - head * Math.cos(angle - Math.PI / 6), sy - head * Math.sin(angle - Math.PI / 6));
    ctx.lineTo(sx - head * 0.82 * Math.cos(angle), sy - head * 0.82 * Math.sin(angle));
    ctx.lineTo(sx - head * Math.cos(angle + Math.PI / 6), sy - head * Math.sin(angle + Math.PI / 6));
    ctx.closePath();
    ctx.fillStyle = color;
    ctx.fill();
  }

  function drawDot(x, y, color, r) {
    const [sx, sy] = tw(x, y);
    ctx.beginPath();
    ctx.arc(sx, sy, r, 0, Math.PI * 2);
    ctx.fillStyle = color;
    ctx.fill();
  }

  function drawLabel(text, x, y, color, dx, dy) {
    const [sx, sy] = tw(x, y);
    ctx.save();
    ctx.font = '600 12px system-ui, -apple-system, sans-serif';
    ctx.textAlign = 'left';
    ctx.textBaseline = 'middle';
    ctx.lineWidth = 3.5;
    ctx.strokeStyle = 'rgba(255,255,255,0.96)';
    ctx.strokeText(text, sx + dx, sy + dy);
    ctx.fillStyle = color;
    ctx.fillText(text, sx + dx, sy + dy);
    ctx.restore();
  }

  function drawArc(start, end, radius, color) {
    const steps = Math.max(10, Math.round(Math.abs(end - start) * 18));

    ctx.beginPath();
    for (let i = 0; i <= steps; i++) {
      const t = i / steps;
      const angle = start + (end - start) * t;
      const [sx, sy] = tw(radius * Math.cos(angle), radius * Math.sin(angle));
      if (i === 0) {
        ctx.moveTo(sx, sy);
      } else {
        ctx.lineTo(sx, sy);
      }
    }
    ctx.strokeStyle = color;
    ctx.lineWidth = 2;
    ctx.stroke();
  }

  function draw(deg) {
    const a = deg * Math.PI / 180;
    const cosA = Math.cos(a);
    const sinA = Math.sin(a);
    const [ox, oy] = POINT;
    const [rx, ry] = rot(ox, oy, cosA, sinA);
    const startAngle = Math.atan2(oy, ox);
    const endAngle = startAngle - a;
    const arcRadius = ORBIT * 0.42;

    ctx.clearRect(0, 0, W, H);

    ctx.strokeStyle = 'rgba(148,163,184,0.16)';
    ctx.lineWidth = 1;
    ctx.beginPath();
    for (let gx = -3; gx <= 3; gx++) {
      const [px] = tw(gx, 0);
      ctx.moveTo(px, 0);
      ctx.lineTo(px, H);
    }
    for (let gy = -3; gy <= 3; gy++) {
      const [, py] = tw(0, gy);
      ctx.moveTo(0, py);
      ctx.lineTo(W, py);
    }
    ctx.stroke();

    ctx.beginPath();
    ctx.setLineDash([6, 5]);
    ctx.arc(cx, cy, ORBIT * SC, 0, Math.PI * 2);
    ctx.strokeStyle = 'rgba(100,116,139,0.38)';
    ctx.lineWidth = 1.2;
    ctx.stroke();
    ctx.setLineDash([]);

    ctx.strokeStyle = 'rgba(15,23,42,0.42)';
    ctx.lineWidth = 1.4;
    ctx.beginPath();
    ctx.moveTo(0, cy);
    ctx.lineTo(W, cy);
    ctx.moveTo(cx, 0);
    ctx.lineTo(cx, H);
    ctx.stroke();

    drawDot(0, 0, '#0f172a', 3.4);
    drawLabel('O', 0, 0, '#0f172a', 8, 10);

    ctx.fillStyle = '#64748b';
    ctx.font = '12px system-ui, -apple-system, sans-serif';
    ctx.fillText('x', W - 16, cy - 7);
    ctx.fillText('y', cx + 7, 14);

    if (deg > 0.5 && deg < 359.5) {
      drawArc(startAngle, endAngle, arcRadius, '#f59e0b');

      const midAngle = startAngle + (endAngle - startAngle) / 2;
      drawLabel('a', arcRadius * Math.cos(midAngle), arcRadius * Math.sin(midAngle), '#b45309', 8, -4);
    }

    drawArrow(ox, oy, '#64748b', 2.2);
    drawDot(ox, oy, '#64748b', 4.2);
    drawLabel('P', ox, oy, '#475569', 8, -10);

    ctx.strokeStyle = 'rgba(37,99,235,0.3)';
    ctx.lineWidth = 1.1;
    ctx.setLineDash([4, 4]);
    ctx.beginPath();
    ctx.moveTo(cx, cy);
    const [rpx, rpy] = tw(rx, ry);
    ctx.moveTo(rpx, cy);
    ctx.lineTo(rpx, rpy);
    ctx.moveTo(cx, rpy);
    ctx.lineTo(rpx, rpy);
    ctx.stroke();
    ctx.setLineDash([]);

    drawArrow(rx, ry, '#2563eb', 2.7);
    drawDot(rx, ry, '#2563eb', 4.8);
    drawLabel("P'", rx, ry, '#1d4ed8', 8, -10);

    beforeEl.textContent = `(${fmt(ox)}, ${fmt(oy)})`;
    afterEl.textContent = `(${fmt(rx)}, ${fmt(ry)})`;
    m00El.textContent = cosA.toFixed(3);
    m01El.textContent = sinA.toFixed(3);
    m10El.textContent = (-sinA).toFixed(3);
    m11El.textContent = cosA.toFixed(3);
  }

  function update() {
    valLabel.textContent = `${slider.value}\u00b0`;
    draw(Number(slider.value));
  }

  slider.addEventListener('input', update);
  update();
})();

(() => {
  const canvas = document.getElementById('perspective-canvas-uza');
  const wInput = document.getElementById('persp-w-uza');
  const hInput = document.getElementById('persp-h-uza');
  const fInput = document.getElementById('persp-f-uza');
  if (!canvas || !wInput || !hInput || !fInput) return;

  const ctx = canvas.getContext('2d');
  const W = canvas.width;
  const H = canvas.height;
  const OBJ_W = 70;
  const OBJ_H = 50;
  const OBJ_DIST = 180;
  const LABEL_FONT = '600 14px "Aptos", "Segoe UI", "Helvetica Neue", Arial, sans-serif';
  const LEGEND_FONT = '12px "Aptos", "Segoe UI", "Helvetica Neue", Arial, sans-serif';

  const ids = {
    w: wInput,
    h: hInput,
    f: fInput,
    wVal: document.getElementById('persp-w-val-uza'),
    hVal: document.getElementById('persp-h-val-uza'),
    fVal: document.getElementById('persp-f-val-uza'),
    ar: document.getElementById('persp-ar-uza'),
    fovx: document.getElementById('persp-fovx-uza'),
    fovy: document.getElementById('persp-fovy-uza'),
    fit: document.getElementById('persp-fit-uza'),
    fitNote: document.getElementById('persp-fit-note-uza'),
  };

  const CY = Math.cos(0.72);
  const SY = Math.sin(0.72);
  const CP = Math.cos(0.46);
  const SP = Math.sin(0.46);

  function mm(v) {
    return v / 5;
  }

  function project(point) {
    const x1 = CY * point.x + SY * point.z;
    const z1 = -SY * point.x + CY * point.z;
    const y1 = CP * point.y - SP * z1;
    return { x: 80 + x1 * 18, y: 40 - y1 * 18 };
  }

  function seg(a, b, color, lw, dash) {
    ctx.save();
    ctx.strokeStyle = color;
    ctx.lineWidth = lw;
    if (dash) ctx.setLineDash(dash);
    ctx.beginPath();
    ctx.moveTo(a.x, a.y);
    ctx.lineTo(b.x, b.y);
    ctx.stroke();
    ctx.restore();
  }

  function quad(pts, fill, stroke, lw) {
    ctx.beginPath();
    pts.forEach((p, i) => {
      if (i) {
        ctx.lineTo(p.x, p.y);
      } else {
        ctx.moveTo(p.x, p.y);
      }
    });
    ctx.closePath();
    if (fill) {
      ctx.fillStyle = fill;
      ctx.fill();
    }
    if (stroke && lw > 0) {
      ctx.strokeStyle = stroke;
      ctx.lineWidth = lw;
      ctx.stroke();
    }
  }

  function clamp(v, min, max) {
    return Math.max(min, Math.min(max, v));
  }

  function roundedRect(x, y, width, height, radius) {
    const r = Math.min(radius, width / 2, height / 2);
    ctx.beginPath();
    ctx.moveTo(x + r, y);
    ctx.arcTo(x + width, y, x + width, y + height, r);
    ctx.arcTo(x + width, y + height, x, y + height, r);
    ctx.arcTo(x, y + height, x, y, r);
    ctx.arcTo(x, y, x + width, y, r);
    ctx.closePath();
  }

  function drawLabel(text, x, y, options = {}) {
    const {
      align = 'left',
      baseline = 'middle',
      font = LABEL_FONT,
      textColor = '#0f172a',
      fill = 'rgba(255,255,255,0.92)',
      stroke = 'rgba(148,163,184,0.45)',
      height = 26,
      paddingX = 9,
      margin = 12,
    } = options;

    ctx.save();
    ctx.font = font;
    ctx.textAlign = 'left';
    ctx.textBaseline = 'middle';

    const width = Math.ceil(ctx.measureText(text).width + paddingX * 2);
    let left = x;
    let top = y;

    if (align === 'center') left -= width / 2;
    if (align === 'right') left -= width;
    if (baseline === 'middle') top -= height / 2;
    if (baseline === 'bottom' || baseline === 'alphabetic') top -= height;

    left = clamp(left, margin, W - margin - width);
    top = clamp(top, margin, H - margin - height);

    roundedRect(left, top, width, height, 9);
    ctx.fillStyle = fill;
    ctx.fill();
    if (stroke) {
      ctx.strokeStyle = stroke;
      ctx.lineWidth = 1;
      ctx.stroke();
    }

    ctx.fillStyle = textColor;
    ctx.fillText(text, left + paddingX, top + height / 2 + 0.5);
    ctx.restore();
  }

  function draw() {
    const sW = Number(ids.w.value);
    const sH = Number(ids.h.value);
    const f = Number(ids.f.value);
    const fovX = 2 * Math.atan(sW / (2 * f)) * 180 / Math.PI;
    const fovY = 2 * Math.atan(sH / (2 * f)) * 180 / Math.PI;

    ids.wVal.textContent = `${sW} mm`;
    ids.hVal.textContent = `${sH} mm`;
    ids.fVal.textContent = `${f} mm`;
    ids.ar.textContent = (sW / sH).toFixed(2);
    ids.fovx.textContent = `${fovX.toFixed(2)}\u00b0`;
    ids.fovy.textContent = `${fovY.toFixed(2)}\u00b0`;

    ctx.clearRect(0, 0, W, H);
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';

    const sZ = mm(f);
    const oZ = mm(OBJ_DIST);
    const hw = mm(sW / 2);
    const hh = mm(sH / 2);

    const cam = project({ x: 0, y: 0, z: 0 });
    const sC = project({ x: 0, y: 0, z: sZ });
    const axE = project({ x: 0, y: 0, z: oZ + mm(18) });
    const sc = [[-hw, -hh], [hw, -hh], [hw, hh], [-hw, hh]].map(([x, y]) => project({ x, y, z: sZ }));

    const hW = mm(OBJ_W / 2);
    const hD = 5;
    const hB = -mm(OBJ_H / 2);
    const hT = 2;
    const hR = mm(OBJ_H / 2);

    const houseVertices = [
      { x: -hW, y: hB, z: oZ - hD },
      { x: hW, y: hB, z: oZ - hD },
      { x: -hW, y: hT, z: oZ - hD },
      { x: hW, y: hT, z: oZ - hD },
      { x: 0, y: hR, z: oZ - hD },
      { x: -hW, y: hB, z: oZ + hD },
      { x: hW, y: hB, z: oZ + hD },
      { x: -hW, y: hT, z: oZ + hD },
      { x: hW, y: hT, z: oZ + hD },
      { x: 0, y: hR, z: oZ + hD },
    ];

    const sensorImagePoints = houseVertices.map(({ x, y, z }) => ({ x: x * sZ / z, y: y * sZ / z }));
    const minProjX = Math.min(...sensorImagePoints.map(p => p.x));
    const maxProjX = Math.max(...sensorImagePoints.map(p => p.x));
    const minProjY = Math.min(...sensorImagePoints.map(p => p.y));
    const maxProjY = Math.max(...sensorImagePoints.map(p => p.y));
    const projectedWidthMm = (maxProjX - minProjX) * 5;
    const projectedHeightMm = (maxProjY - minProjY) * 5;
    const fitsSensor = projectedWidthMm <= sW && projectedHeightMm <= sH;
    const pc = [
      project({ x: minProjX, y: minProjY, z: sZ }),
      project({ x: maxProjX, y: minProjY, z: sZ }),
      project({ x: maxProjX, y: maxProjY, z: sZ }),
      project({ x: minProjX, y: maxProjY, z: sZ }),
    ];

    ids.fit.textContent = fitsSensor ? 'Cabe completa' : 'Se recorta';
    ids.fit.style.color = fitsSensor ? '#166534' : '#b45309';
    ids.fitNote.textContent = fitsSensor
      ? `La imagen proyectada mide ${projectedWidthMm.toFixed(1)} x ${projectedHeightMm.toFixed(1)} mm y cabe dentro del sensor.`
      : `La imagen proyectada mide ${projectedWidthMm.toFixed(1)} x ${projectedHeightMm.toFixed(1)} mm: es mayor que el sensor en al menos una dimension, asi que el objeto queda recortado.`;

    const bfl = project({ x: -hW, y: hB, z: oZ - hD });
    const bfr = project({ x: hW, y: hB, z: oZ - hD });
    const tfl = project({ x: -hW, y: hT, z: oZ - hD });
    const tfr = project({ x: hW, y: hT, z: oZ - hD });
    const rpf = project({ x: 0, y: hR, z: oZ - hD });
    const bbl = project({ x: -hW, y: hB, z: oZ + hD });
    const bbr = project({ x: hW, y: hB, z: oZ + hD });
    const tbl = project({ x: -hW, y: hT, z: oZ + hD });
    const tbr = project({ x: hW, y: hT, z: oZ + hD });
    const rpb = project({ x: 0, y: hR, z: oZ + hD });

    seg(cam, axE, 'rgba(148,163,184,0.45)', 1.2, [5, 4]);

    quad([bbl, bbr, tbr, tbl], 'rgba(180,160,130,0.55)', null, 0);
    quad([tbl, tbr, rpb], 'rgba(160,140,110,0.55)', null, 0);
    quad([bfl, bfr, bbr, bbl], 'rgba(155,140,120,0.45)', null, 0);
    quad([bfr, bbr, tbr, tfr], 'rgba(190,170,140,0.60)', null, 0);
    quad([tfr, tbr, rpb, rpf], 'rgba(185,165,130,0.60)', null, 0);
    quad([bfl, bfr, tfr, tfl], 'rgba(240,225,195,0.80)', null, 0);
    quad([tfl, tfr, rpf], 'rgba(220,200,165,0.80)', null, 0);

    const hDim = 'rgba(100,80,50,0.35)';
    seg(bbl, bbr, hDim, 1.4);
    seg(bbl, tbl, hDim, 1.4);
    seg(bbr, tbr, hDim, 1.4);
    seg(tbl, tbr, hDim, 1.4);
    seg(tbl, rpb, hDim, 1.4);
    seg(tbr, rpb, hDim, 1.4);

    const hMid = 'rgba(100,80,50,0.55)';
    seg(bfl, bbl, hMid, 1.5);
    seg(bfr, bbr, hMid, 1.5);
    seg(tfl, tbl, hMid, 1.5);
    seg(tfr, tbr, hMid, 1.5);
    seg(rpf, rpb, hMid, 1.5);

    sc.forEach(c => seg(cam, c, 'rgba(29,78,216,0.4)', 1.5));
    quad(sc, 'rgba(14,165,233,0.14)', '#0ea5e9', 2);
    quad(pc, 'rgba(245,158,11,0.28)', '#f59e0b', 1.8);

    function perspPt(wx, wy, wz) {
      return project({ x: wx * sZ / wz, y: wy * sZ / wz, z: sZ });
    }

    const ipbfl = perspPt(-hW, hB, oZ - hD);
    const ipbfr = perspPt(hW, hB, oZ - hD);
    const iptfl = perspPt(-hW, hT, oZ - hD);
    const iptfr = perspPt(hW, hT, oZ - hD);
    const iprpf = perspPt(0, hR, oZ - hD);

    ctx.save();
    ctx.beginPath();
    pc.forEach((p, i) => {
      if (i) {
        ctx.lineTo(p.x, p.y);
      } else {
        ctx.moveTo(p.x, p.y);
      }
    });
    ctx.closePath();
    ctx.clip();
    quad([ipbfl, ipbfr, iptfr, iptfl], 'rgba(230,215,185,0.82)', null, 0);
    quad([iptfl, iptfr, iprpf], 'rgba(205,185,150,0.82)', null, 0);
    const ie = 'rgba(80,58,25,0.55)';
    seg(ipbfl, ipbfr, ie, 0.9);
    seg(ipbfl, iptfl, ie, 0.9);
    seg(ipbfr, iptfr, ie, 0.9);
    seg(iptfl, iptfr, ie, 0.9);
    seg(iptfl, iprpf, ie, 0.9);
    seg(iptfr, iprpf, ie, 0.9);
    ctx.restore();

    const hFront = 'rgba(80,55,20,0.85)';
    seg(bfl, bfr, hFront, 2.2);
    seg(bfl, tfl, hFront, 2.2);
    seg(bfr, tfr, hFront, 2.2);
    seg(tfl, tfr, hFront, 2.2);
    seg(tfl, rpf, hFront, 2.2);
    seg(tfr, rpf, hFront, 2.2);

    seg(cam, sC, '#ef4444', 2);

    ctx.fillStyle = '#0f172a';
    ctx.beginPath();
    ctx.arc(cam.x, cam.y, 5.5, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = '#0ea5e9';
    ctx.beginPath();
    ctx.arc(sC.x, sC.y, 3.8, 0, Math.PI * 2);
    ctx.fill();

    drawLabel(`camara · f = ${f} mm`, cam.x + 16, cam.y - 18, {
      align: 'left',
      baseline: 'bottom',
      textColor: '#991b1b',
      fill: 'rgba(254,242,242,0.96)',
      stroke: 'rgba(248,113,113,0.45)',
    });
    drawLabel('sensor', sc[2].x + 8, sc[2].y + 5, {
      textColor: '#075985',
      fill: 'rgba(240,249,255,0.96)',
      stroke: 'rgba(14,165,233,0.4)',
    });
    drawLabel('objeto', rpf.x + 10, rpf.y - 14, {
      textColor: '#713f12',
      fill: 'rgba(255,251,235,0.96)',
      stroke: 'rgba(245,158,11,0.42)',
    });

    const lx = 14;
    const ly = H - 38;
    ctx.font = LEGEND_FONT;
    ctx.textAlign = 'left';
    ctx.textBaseline = 'middle';
    roundedRect(lx - 7, ly - 8, 164, 42, 10);
    ctx.fillStyle = 'rgba(255,255,255,0.88)';
    ctx.fill();
    ctx.strokeStyle = 'rgba(148,163,184,0.28)';
    ctx.lineWidth = 1;
    ctx.stroke();
    ctx.fillStyle = 'rgba(14,165,233,0.22)';
    ctx.fillRect(lx, ly, 22, 12);
    ctx.strokeStyle = '#0ea5e9';
    ctx.lineWidth = 1.5;
    ctx.strokeRect(lx, ly, 22, 12);
    ctx.fillStyle = '#0369a1';
    ctx.fillText('plano del sensor', lx + 27, ly + 6);
    ctx.fillStyle = 'rgba(245,158,11,0.30)';
    ctx.fillRect(lx, ly + 18, 22, 12);
    ctx.strokeStyle = '#f59e0b';
    ctx.lineWidth = 1.5;
    ctx.strokeRect(lx, ly + 18, 22, 12);
    ctx.fillStyle = '#92400e';
    ctx.fillText('imagen proyectada', lx + 27, ly + 24);
    ctx.textBaseline = 'alphabetic';
  }

  ['input', 'change'].forEach(ev => {
    ids.w.addEventListener(ev, draw);
    ids.h.addEventListener(ev, draw);
    ids.f.addEventListener(ev, draw);
  });

  draw();
})();

(() => {
  const canvas = document.getElementById('bounce-canvas-uza');
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  const W = canvas.width;
  const H = canvas.height;
  const BEAM_RADIUS = 5;
  const SCATTER = 0.18;

  const ids = {
    count: document.getElementById('bounce-count-uza'),
    depth: document.getElementById('bounce-depth-uza'),
    countVal: document.getElementById('bounce-count-val-uza'),
    depthVal: document.getElementById('bounce-depth-val-uza'),
    total: document.getElementById('bounce-rays-total-uza'),
    hit: document.getElementById('bounce-rays-hit-uza'),
  };

  const ROOM = { x0: 30, y0: 16, x1: 604, y1: 304 };
  const LIGHT = { x0: 438, x1: 566, y: ROOM.y0 };
  const CAMERA = { x: 76, y: 176 };
  const CIRCLE = { x: 322, y: 168, r: 48 };
  const BLOCK = { x0: 480, y0: 214, x1: 560, y1: 274 };
  const HIT_PALETTE = ['#ea580c', '#f97316', '#f59e0b', '#facc15', '#fde047'];
  const MISS_COLOR = 'rgba(37, 99, 235, 0.26)';
  const MISS_FOCUS_COLOR = 'rgba(30, 64, 175, 0.84)';
  let seed = 7;

  function mulberry32(a) {
    return function() {
      let t = a += 0x6D2B79F5;
      t = Math.imul(t ^ t >>> 15, t | 1);
      t ^= t + Math.imul(t ^ t >>> 7, t | 61);
      return ((t ^ t >>> 14) >>> 0) / 4294967296;
    };
  }

  function raySeed(index) {
    return seed + index * 1013904223;
  }

  function vec(x, y) { return { x, y }; }
  function add(a, b) { return { x: a.x + b.x, y: a.y + b.y }; }
  function sub(a, b) { return { x: a.x - b.x, y: a.y - b.y }; }
  function scale(a, k) { return { x: a.x * k, y: a.y * k }; }
  function dot(a, b) { return a.x * b.x + a.y * b.y; }
  function cross(a, b) { return a.x * b.y - a.y * b.x; }
  function length(a) { return Math.hypot(a.x, a.y); }
  function normalize(a) {
    const len = length(a) || 1;
    return { x: a.x / len, y: a.y / len };
  }
  function reflect(direction, normal) {
    const factor = 2 * dot(direction, normal);
    return normalize(sub(direction, scale(normal, factor)));
  }
  function rotate(v, angle) {
    const ca = Math.cos(angle);
    const sa = Math.sin(angle);
    return { x: v.x * ca - v.y * sa, y: v.x * sa + v.y * ca };
  }
  function mixDirections(a, b, t) {
    return normalize(add(scale(a, 1 - t), scale(b, t)));
  }

  const segments = [
    { a: vec(ROOM.x0, ROOM.y0), b: vec(LIGHT.x0, ROOM.y0), kind: 'wall' },
    { a: vec(LIGHT.x0, ROOM.y0), b: vec(LIGHT.x1, ROOM.y0), kind: 'light', emissive: true },
    { a: vec(LIGHT.x1, ROOM.y0), b: vec(ROOM.x1, ROOM.y0), kind: 'wall' },
    { a: vec(ROOM.x1, ROOM.y0), b: vec(ROOM.x1, ROOM.y1), kind: 'wall' },
    { a: vec(ROOM.x1, ROOM.y1), b: vec(ROOM.x0, ROOM.y1), kind: 'wall' },
    { a: vec(ROOM.x0, ROOM.y1), b: vec(ROOM.x0, ROOM.y0), kind: 'wall' },
    { a: vec(BLOCK.x0, BLOCK.y0), b: vec(BLOCK.x1, BLOCK.y0), kind: 'block' },
    { a: vec(BLOCK.x1, BLOCK.y0), b: vec(BLOCK.x1, BLOCK.y1), kind: 'block' },
    { a: vec(BLOCK.x1, BLOCK.y1), b: vec(BLOCK.x0, BLOCK.y1), kind: 'block' },
    { a: vec(BLOCK.x0, BLOCK.y1), b: vec(BLOCK.x0, BLOCK.y0), kind: 'block' },
  ];

  function segmentHit(origin, direction, segment) {
    const span = sub(segment.b, segment.a);
    const den = cross(direction, span);
    if (Math.abs(den) < 1e-6) return null;
    const delta = sub(segment.a, origin);
    const t = cross(delta, span) / den;
    const u = cross(delta, direction) / den;
    if (t <= 0.35 || u < -1e-5 || u > 1 + 1e-5) return null;
    const point = add(origin, scale(direction, t));
    let normal = normalize(vec(span.y, -span.x));
    if (dot(direction, normal) > 0) normal = scale(normal, -1);
    return { t, point, normal, emissive: Boolean(segment.emissive), kind: segment.kind };
  }

  function circleHit(origin, direction) {
    const oc = sub(origin, CIRCLE);
    const b = 2 * dot(oc, direction);
    const c = dot(oc, oc) - CIRCLE.r * CIRCLE.r;
    const disc = b * b - 4 * c;
    if (disc <= 0) return null;
    const root = Math.sqrt(disc);
    const t1 = (-b - root) / 2;
    const t2 = (-b + root) / 2;
    const t = t1 > 0.35 ? t1 : (t2 > 0.35 ? t2 : null);
    if (!t) return null;
    const point = add(origin, scale(direction, t));
    let normal = normalize(sub(point, CIRCLE));
    if (dot(direction, normal) > 0) normal = scale(normal, -1);
    return { t, point, normal, emissive: false, kind: 'pillar' };
  }

  function firstHit(origin, direction) {
    let closest = null;
    segments.forEach((segment) => {
      const hit = segmentHit(origin, direction, segment);
      if (!hit) return;
      if (!closest || hit.t < closest.t) closest = hit;
    });
    const cHit = circleHit(origin, direction);
    if (cHit && (!closest || cHit.t < closest.t)) closest = cHit;
    return closest;
  }

  function sampleHemisphere(normal, rng) {
    const base = Math.atan2(normal.y, normal.x);
    const angle = base + (rng() * 2 - 1) * (Math.PI / 2);
    let dir = vec(Math.cos(angle), Math.sin(angle));
    if (dot(dir, normal) < 0) dir = scale(dir, -1);
    return normalize(dir);
  }

  function traceRay(origin, direction, maxDepth, scatter, rng) {
    const points = [origin];
    const bounces = [];
    let ro = origin;
    let rd = normalize(direction);
    let reachedLight = false;

    for (let depth = 0; depth < maxDepth; depth++) {
      const hit = firstHit(ro, rd);
      if (!hit) {
        points.push(add(ro, scale(rd, 90)));
        break;
      }

      points.push(hit.point);
      if (hit.emissive) {
        reachedLight = true;
        break;
      }

      bounces.push(hit.point);
      const reflected = reflect(rd, hit.normal);
      const diffuse = sampleHemisphere(hit.normal, rng);
      rd = mixDirections(reflected, diffuse, scatter);
      ro = add(hit.point, scale(hit.normal, 0.65));
    }

    return {
      points,
      bounces,
      reachedLight,
      segments: Math.max(points.length - 1, 0),
    };
  }

  function drawScene(radius, hits) {
    ctx.clearRect(0, 0, W, H);
    ctx.fillStyle = '#fcfcfb';
    ctx.fillRect(0, 0, W, H);

    ctx.fillStyle = '#f7f7f5';
    ctx.fillRect(ROOM.x0, ROOM.y0, ROOM.x1 - ROOM.x0, ROOM.y1 - ROOM.y0);

    ctx.fillStyle = 'rgba(251, 191, 36, 0.22)';
    ctx.fillRect(LIGHT.x0, LIGHT.y - 1, LIGHT.x1 - LIGHT.x0, 18);
    ctx.fillStyle = 'rgba(251, 191, 36, 0.34)';
    ctx.fillRect(LIGHT.x0, LIGHT.y - 1, LIGHT.x1 - LIGHT.x0, 8);
    if (hits > 0) {
      ctx.fillStyle = 'rgba(250, 204, 21, 0.16)';
      ctx.fillRect(LIGHT.x0 - 14, LIGHT.y - 10, LIGHT.x1 - LIGHT.x0 + 28, 32);
      ctx.fillStyle = 'rgba(249, 115, 22, 0.10)';
      ctx.fillRect(LIGHT.x0 - 6, LIGHT.y - 4, LIGHT.x1 - LIGHT.x0 + 12, 18);
    }

    ctx.fillStyle = '#ded7cd';
    ctx.fillRect(BLOCK.x0, BLOCK.y0, BLOCK.x1 - BLOCK.x0, BLOCK.y1 - BLOCK.y0);
    ctx.fillStyle = '#e8e1d6';
    ctx.beginPath();
    ctx.arc(CIRCLE.x, CIRCLE.y, CIRCLE.r, 0, Math.PI * 2);
    ctx.fill();

    ctx.strokeStyle = '#c7d1dc';
    ctx.lineWidth = 2;
    ctx.strokeRect(ROOM.x0, ROOM.y0, ROOM.x1 - ROOM.x0, ROOM.y1 - ROOM.y0);
    ctx.strokeStyle = '#0f172a';
    ctx.lineWidth = 1.4;
    ctx.strokeRect(BLOCK.x0, BLOCK.y0, BLOCK.x1 - BLOCK.x0, BLOCK.y1 - BLOCK.y0);
    ctx.beginPath();
    ctx.arc(CIRCLE.x, CIRCLE.y, CIRCLE.r, 0, Math.PI * 2);
    ctx.stroke();

    ctx.strokeStyle = '#94a3b8';
    ctx.lineWidth = 2.2;
    ctx.beginPath();
    ctx.moveTo(CAMERA.x, CAMERA.y - radius);
    ctx.lineTo(CAMERA.x, CAMERA.y + radius);
    ctx.stroke();

    ctx.fillStyle = '#0f172a';
    ctx.beginPath();
    ctx.arc(CAMERA.x, CAMERA.y, 4.5, 0, Math.PI * 2);
    ctx.fill();

    ctx.font = '600 13px "Aptos", "Segoe UI", Arial, sans-serif';
    ctx.textBaseline = 'middle';
    ctx.fillStyle = '#334155';
    ctx.fillText('camara', CAMERA.x - 28, CAMERA.y - radius - 14);
    ctx.fillStyle = '#b45309';
    ctx.fillText('fuente emisiva', LIGHT.x0 - 8, LIGHT.y + 16);
  }

  function drawPath(path, highlighted) {
    const reachedLight = Boolean(path.reachedLight);
    for (let i = 0; i < path.points.length - 1; i++) {
      const a = path.points[i];
      const b = path.points[i + 1];
      ctx.beginPath();
      ctx.moveTo(a.x, a.y);
      ctx.lineTo(b.x, b.y);
      if (reachedLight) {
        ctx.strokeStyle = highlighted
          ? HIT_PALETTE[i % HIT_PALETTE.length]
          : `rgba(249, 115, 22, ${0.18 + i * 0.05})`;
        ctx.lineWidth = highlighted ? 2.6 : 1.2;
      } else {
        ctx.strokeStyle = highlighted ? MISS_FOCUS_COLOR : MISS_COLOR;
        ctx.lineWidth = highlighted ? 2.2 : 1.1;
      }
      ctx.setLineDash(highlighted ? [8, 6] : []);
      ctx.stroke();
      ctx.setLineDash([]);
    }
  }

  function drawGuide(path) {
    drawPath(path, true);
    path.bounces.forEach((point, index) => {
      ctx.fillStyle = path.reachedLight ? '#fff7ed' : '#f8fafc';
      ctx.beginPath();
      ctx.arc(point.x, point.y, 9, 0, Math.PI * 2);
      ctx.fill();
      ctx.strokeStyle = path.reachedLight ? HIT_PALETTE[index % HIT_PALETTE.length] : '#2563eb';
      ctx.lineWidth = 1.7;
      ctx.stroke();
      ctx.fillStyle = path.reachedLight ? '#7c2d12' : '#1e3a8a';
      ctx.font = '700 11px "Aptos", "Segoe UI", Arial, sans-serif';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(String(index + 1), point.x, point.y + 0.5);
    });

    const end = path.points[path.points.length - 1];
    if (path.reachedLight) {
      ctx.fillStyle = 'rgba(251, 191, 36, 0.28)';
      ctx.beginPath();
      ctx.arc(end.x, end.y, 11, 0, Math.PI * 2);
      ctx.fill();
    } else {
      ctx.fillStyle = 'rgba(37, 99, 235, 0.22)';
      ctx.beginPath();
      ctx.arc(end.x, end.y, 6, 0, Math.PI * 2);
      ctx.fill();
    }
  }

  function render() {
    const radius = BEAM_RADIUS;
    const rayCount = Number(ids.count.value);
    const maxDepth = Number(ids.depth.value);
    const traced = [];
    const focus = vec(CIRCLE.x - 14, CIRCLE.y - 20);
    const centerIndex = Math.floor(rayCount / 2);

    ids.countVal.textContent = String(rayCount);
    ids.depthVal.textContent = String(maxDepth);
    ids.total.textContent = String(rayCount);

    for (let i = 0; i < rayCount; i++) {
      // Each ray gets its own RNG so changing maxDepth extends/truncates the
      // same path instead of reshuffling the rest of the beam.
      const rayRng = mulberry32(raySeed(i));
      const t = rayCount === 1 ? 0.5 : i / (rayCount - 1);
      const offset = (t - 0.5) * radius * 2 + (rayRng() - 0.5) * 1.4;
      const origin = vec(CAMERA.x, CAMERA.y + offset);
      const aim = vec(focus.x, focus.y + offset * 0.45);
      const initial = rotate(normalize(sub(aim, origin)), (rayRng() - 0.5) * 0.05);
      traced.push(traceRay(origin, initial, maxDepth, SCATTER, rayRng));
    }

    const hits = traced.filter((path) => path.reachedLight).length;
    // Keep the guide stable: always follow the central ray of the beam.
    // Its color already tells whether that particular path reaches the light.
    const guide = traced[centerIndex];

    ids.hit.textContent = String(hits);

    drawScene(radius, hits);
    traced.forEach((path) => drawPath(path, false));
    drawGuide(guide);
  }

  ['input', 'change'].forEach((eventName) => {
    ids.count.addEventListener(eventName, render);
    ids.depth.addEventListener(eventName, render);
  });

  render();
})();

(() => {
  const mainC = document.getElementById('mc-canvas');
  const graphC = document.getElementById('mc-graph');
  const btn = document.getElementById('mc-btn');
  const resetBtn = document.getElementById('mc-reset');
  const speed = document.getElementById('mc-speed');
  if (!mainC || !graphC || !btn || !resetBtn || !speed) return;

  const ctx = mainC.getContext('2d');
  const gctx = graphC.getContext('2d');
  const W = mainC.width;
  const H = mainC.height;
  const GW = graphC.width;
  const GH = graphC.height;

  let total = 0;
  let inside = 0;
  let running = false;
  let rafId = null;

  const RECORD_AT = new Set([1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 30000]);
  const history = [];
  const MAX_N = 30000;
  const SPEED = [1, 5, 25, 100, 500];

  function overlay() {
    ctx.strokeStyle = '#94a3b8';
    ctx.lineWidth = 1.5;
    ctx.strokeRect(0.75, 0.75, W - 1.5, H - 1.5);
    ctx.strokeStyle = '#3b82f6';
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.arc(W / 2, H / 2, W / 2 - 1, 0, Math.PI * 2);
    ctx.stroke();
  }

  function bg() {
    ctx.fillStyle = '#f8fafc';
    ctx.fillRect(0, 0, W, H);
    overlay();
  }

  function addBatch(n) {
    for (let i = 0; i < n && total < MAX_N; i++) {
      const x = Math.random() * 2 - 1;
      const y = Math.random() * 2 - 1;
      const hit = x * x + y * y <= 1;
      if (hit) inside++;
      total++;
      const px = (x + 1) / 2 * W;
      const py = (1 - (y + 1) / 2) * H;
      ctx.fillStyle = hit ? 'rgba(239,68,68,0.7)' : 'rgba(147,197,253,0.6)';
      ctx.beginPath();
      ctx.arc(px, py, 1.5, 0, Math.PI * 2);
      ctx.fill();
      if (RECORD_AT.has(total)) history.push({ n: total, v: inside / total * 4 });
    }
    overlay();
  }

  function stats() {
    document.getElementById('mc-total').textContent = total.toLocaleString('es');
    document.getElementById('mc-inside').textContent = inside.toLocaleString('es');
    document.getElementById('mc-pi').textContent = total > 0 ? (inside / total * 4).toFixed(5) : '\u2014';
  }

  function graph() {
    gctx.fillStyle = '#f8fafc';
    gctx.fillRect(0, 0, GW, GH);
    const pl = 22;
    const pr = 4;
    const pt = 5;
    const pb = 14;
    const pw = GW - pl - pr;
    const ph = GH - pt - pb;
    const yMin = 2.4;
    const yMax = 4.2;
    const logMax = Math.log10(MAX_N);
    const gx = n => pl + pw * Math.log10(Math.max(1, n)) / logMax;
    const gy = v => pt + ph * (1 - (Math.min(Math.max(v, yMin), yMax) - yMin) / (yMax - yMin));

    gctx.strokeStyle = '#e2e8f0';
    gctx.lineWidth = 1;
    [3.0, Math.PI, 3.5, 4.0].forEach((y) => {
      gctx.beginPath();
      gctx.moveTo(pl, gy(y));
      gctx.lineTo(GW - pr, gy(y));
      gctx.stroke();
    });

    const piY = gy(Math.PI);
    gctx.strokeStyle = '#64748b';
    gctx.lineWidth = 1;
    gctx.setLineDash([3, 3]);
    gctx.beginPath();
    gctx.moveTo(pl, piY);
    gctx.lineTo(GW - pr, piY);
    gctx.stroke();
    gctx.setLineDash([]);
    gctx.fillStyle = '#64748b';
    gctx.font = '9px sans-serif';
    gctx.fillText('\u03c0', 2, piY + 3);
    gctx.fillStyle = '#94a3b8';
    [10, 100, 1000, 10000].forEach((n) => {
      gctx.fillText(n >= 1000 ? `${n / 1000}k` : String(n), gx(n) - 4, GH - 2);
    });

    if (history.length >= 2) {
      gctx.strokeStyle = '#ef4444';
      gctx.lineWidth = 1.8;
      gctx.beginPath();
      history.forEach(({ n, v }, i) => {
        if (i === 0) {
          gctx.moveTo(gx(n), gy(v));
        } else {
          gctx.lineTo(gx(n), gy(v));
        }
      });
      gctx.stroke();
      const last = history[history.length - 1];
      gctx.fillStyle = '#ef4444';
      gctx.beginPath();
      gctx.arc(gx(last.n), gy(last.v), 3, 0, Math.PI * 2);
      gctx.fill();
    }
  }

  function frame() {
    addBatch(SPEED[Number(speed.value)]);
    stats();
    graph();
    if (total >= MAX_N) {
      running = false;
      btn.textContent = 'Reiniciar';
      return;
    }
    if (running) rafId = requestAnimationFrame(frame);
  }

  function toggle() {
    if (total >= MAX_N) {
      reset();
      return;
    }
    if (running) {
      running = false;
      cancelAnimationFrame(rafId);
      btn.textContent = 'Continuar';
    } else {
      running = true;
      btn.textContent = 'Pausar';
      rafId = requestAnimationFrame(frame);
    }
  }

  function reset() {
    running = false;
    cancelAnimationFrame(rafId);
    total = 0;
    inside = 0;
    history.length = 0;
    bg();
    stats();
    graph();
    btn.textContent = 'Iniciar';
  }

  btn.addEventListener('click', toggle);
  resetBtn.addEventListener('click', reset);
  bg();
  stats();
  graph();
})();

(() => {
  const init = (el) => {
    const setPos = (clientX) => {
      const r = el.getBoundingClientRect();
      const pct = Math.max(0, Math.min(100, ((clientX - r.left) / r.width) * 100));
      el.style.setProperty('--pos', `${pct}%`);
      el.setAttribute('aria-valuenow', Math.round(pct));
    };

    const onDown = (e) => {
      el.classList.add('is-dragging');
      el.setPointerCapture?.(e.pointerId);
      setPos(e.clientX);
    };

    const onMove = (e) => {
      if (el.classList.contains('is-dragging')) setPos(e.clientX);
    };

    const onUp = (e) => {
      el.classList.remove('is-dragging');
      el.releasePointerCapture?.(e.pointerId);
    };

    el.addEventListener('pointerdown', onDown);
    el.addEventListener('pointermove', onMove);
    el.addEventListener('pointerup', onUp);
    el.addEventListener('pointercancel', onUp);
    el.addEventListener('keydown', (e) => {
      const cur = parseFloat(getComputedStyle(el).getPropertyValue('--pos')) || 50;
      const step = e.shiftKey ? 10 : 2;
      if (e.key === 'ArrowLeft') {
        el.style.setProperty('--pos', `${Math.max(0, cur - step)}%`);
        e.preventDefault();
      }
      if (e.key === 'ArrowRight') {
        el.style.setProperty('--pos', `${Math.min(100, cur + step)}%`);
        e.preventDefault();
      }
    });
  };

  const ready = () => document.querySelectorAll('.img-compare').forEach(init);
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', ready);
  } else {
    ready();
  }
})();
