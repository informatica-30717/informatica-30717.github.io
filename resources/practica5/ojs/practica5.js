export function cleanSlider(Inputs, range, config) {
  const input = Inputs.range(range, config);
  input.classList.add("p5-range-widget-uza");

  const numberBox = input.querySelector("input[type=number]");
  if (numberBox) numberBox.style.display = "none";

  const rangeSlider = input.querySelector("input[type=range]");
  if (rangeSlider) rangeSlider.classList.add("p5-slider-uza");

  return input;
}

export function buildBasicSieveSteps(max) {
  const state = Array(max + 1).fill("candidate");
  state[0] = state[1] = "discarded";

  const out = [];
  for (let p = 2; p <= max; p++) {
    if (state[p] === "candidate") {
      out.push({ type: "prime", p, state: [...state] });

      for (let k = p * 2; k <= max; k += p) {
        if (state[k] === "candidate") {
          state[k] = "discarded";
          out.push({ type: "discard", p, k, state: [...state] });
        }
      }
    }
  }

  out.push({ type: "final", state: [...state] });
  return out;
}

export function buildRootSieveSteps(max) {
  const state = Array(max + 1).fill("candidate");
  state[0] = state[1] = "discarded";

  const out = [];
  for (let p = 2; p * p <= max; p++) {
    if (state[p] === "candidate") {
      out.push({ type: "prime", p, state: [...state] });

      for (let k = p * p; k <= max; k += p) {
        if (state[k] === "candidate") {
          state[k] = "discarded";
          out.push({ type: "discard", p, k, state: [...state] });
        }
      }
    }
  }

  out.push({ type: "final", state: [...state] });
  return out;
}

export function buildOddOnlySieveSteps(max) {
  if (max < 3) return [{ type: "final", state: [] }];

  const size = Math.floor((max - 3) / 2) + 1;
  const state = Array(size).fill("candidate");
  const out = [];

  for (let i = 0; ; i++) {
    const p = 2 * i + 3;
    if (p * p > max) break;

    if (state[i] === "candidate") {
      out.push({ type: "prime", p, state: [...state] });

      for (let mult = p * p; mult <= max; mult += 2 * p) {
        const idx = (mult - 3) / 2;
        if (state[idx] === "candidate") {
          state[idx] = "discarded";
          out.push({ type: "discard", p, k: mult, state: [...state] });
        }
      }
    }
  }

  out.push({ type: "final", state: [...state] });
  return out;
}

function renderMessage(html, step, oddOnly) {
  if (step.type === "prime") {
    return oddOnly
      ? html`Probando primo impar <b>${step.p}</b>`
      : html`Probando primo <b>${step.p}</b>`;
  }

  if (step.type === "discard") {
    return oddOnly
      ? html`${step.k} es múltiplo impar de <b>${step.p}</b>`
      : html`${step.k} es múltiplo de <b>${step.p}</b>`;
  }

  return html`Criba completada`;
}

function comfortableColumnCount(count) {
  // A slightly taller-than-square grid keeps cells readable in slides.
  return Math.max(3, Math.ceil(Math.sqrt(Math.max(count, 1))) - 1);
}

function fullGridState(step, max) {
  const cols = comfortableColumnCount(max + 1);
  const grid = Array.from({ length: max + 1 }, (_, i) => {
    let bgColor = "#2a9d8f";
    if (i < 2) bgColor = "#ddd";
    else if (i === step.p) bgColor = "#457b9d";
    else if (i === step.k) bgColor = "#f4a261";
    else if (step.state[i] === "discarded") bgColor = "#e63946";

    return {
      n: i,
      x: i % cols,
      y: Math.floor(i / cols),
      color: bgColor,
      textColor: i < 2 ? "#333" : "white",
    };
  });

  return { cols, count: max + 1, grid };
}

function oddGridState(step, max) {
  const size = Math.max(0, Math.floor((max - 3) / 2) + 1);
  const cols = comfortableColumnCount(size);
  const grid = Array.from({ length: size }, (_, i) => {
    const num = 2 * i + 3;
    let bgColor = "#2a9d8f";
    if (num === step.p) bgColor = "#457b9d";
    else if (num === step.k) bgColor = "#f4a261";
    else if (step.state[i] === "discarded") bgColor = "#e63946";

    return {
      n: num,
      x: i % cols,
      y: Math.floor(i / cols),
      color: bgColor,
      textColor: "white",
    };
  });

  return { cols, count: Math.max(size, 1), grid };
}

export function renderSieveState({ html, Plot, steps, stepIndex, max, oddOnly = false }) {
  if (!steps || steps.length === 0) return html`<div>Cargando...</div>`;

  const safeStep = Math.min(stepIndex, steps.length - 1);
  const step = steps[safeStep];
  if (!step) return html`<div>Cargando...</div>`;

  const state = oddOnly ? oddGridState(step, max) : fullGridState(step, max);
  const rows = Math.ceil(state.count / state.cols);
  const yGap = 1.22;
  const plotGrid = state.grid.map((d) => ({ ...d, plotY: d.y * yGap }));
  const cellHeight = 45;
  const yMax = (rows - 1) * yGap;
  const plot = Plot.plot({
    width: 920,
    height: rows * cellHeight + 24,
    margin: 10,
    x: { axis: null, domain: [-0.5, state.cols - 0.5] },
    y: { axis: null, domain: [-0.64, yMax + 0.64], reverse: true },
    color: { type: "identity" },
    marks: [
      Plot.rect(plotGrid, {
        x1: (d) => d.x - 0.48,
        x2: (d) => d.x + 0.48,
        y1: (d) => d.plotY - 0.52,
        y2: (d) => d.plotY + 0.52,
        fill: "color",
        stroke: "white",
        strokeWidth: 1.4,
        rx: 4,
      }),
      Plot.text(plotGrid, {
        x: "x",
        y: "plotY",
        text: "n",
        fill: "textColor",
        fontSize: rows > 9 ? 13 : 16,
        fontWeight: "bold",
      }),
    ],
  });

  plot.classList.add("p5-sieve-plot-uza");

  return html`
    <div class="p5-plot-panel-uza">
      ${plot}
      <div class="p5-sieve-message-uza">
        ${renderMessage(html, step, oddOnly)}
      </div>
    </div>
  `;
}
