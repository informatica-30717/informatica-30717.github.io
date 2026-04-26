export function cleanSlider(Inputs, range, config) {
  const input = Inputs.range(range, config);
  const numberBox = input.querySelector("input[type=number]");
  if (numberBox) numberBox.style.display = "none";

  const rangeSlider = input.querySelector("input[type=range]");
  if (rangeSlider) rangeSlider.style.width = "100%";

  input.style.width = "100%";
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
      ? html`🔎 Probando primo impar <b>${step.p}</b>`
      : html`🔎 Probando primo <b>${step.p}</b>`;
  }

  if (step.type === "discard") {
    return oddOnly
      ? html`❌ ${step.k} es múltiplo impar de <b>${step.p}</b>`
      : html`❌ ${step.k} es múltiplo de <b>${step.p}</b>`;
  }

  return html`✅ Criba completada`;
}

function fullGridState(step, max) {
  const cols = Math.ceil(Math.sqrt(max));
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
  const cols = Math.max(1, Math.ceil(Math.sqrt(Math.max(size, 1))));
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

  return html`
    <div style="margin-top:15px;">
      ${Plot.plot({
        height: Math.ceil(state.count / state.cols) * 35 + 20,
        margin: 10,
        x: { axis: null },
        y: { axis: null, reverse: true },
        color: { type: "identity" },
        marks: [
          Plot.rect(state.grid, {
            x1: (d) => d.x - 0.45,
            x2: (d) => d.x + 0.45,
            y1: (d) => d.y - 0.45,
            y2: (d) => d.y + 0.45,
            fill: "color",
            stroke: "white",
            rx: 4,
          }),
          Plot.text(state.grid, {
            x: "x",
            y: "y",
            text: "n",
            fill: "textColor",
            fontSize: 13,
            fontWeight: "bold",
          }),
        ],
      })}
      <div style="margin-top:15px; font-size: 1.1em; height: 1.5em;">
        ${renderMessage(html, step, oddOnly)}
      </div>
    </div>
  `;
}
