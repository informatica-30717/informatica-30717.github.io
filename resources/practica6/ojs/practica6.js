export function randomIntArray(length, min = 10, max = 99) {
  const span = max - min + 1;
  return Array.from({ length }, () => Math.floor(Math.random() * span) + min);
}

export function computeBubbleSortSteps(baseArr) {
  const arr = [...baseArr];
  const out = [];
  const n = arr.length;

  let sortedBound = n;
  out.push({ type: "start", arr: [...arr], i: null, j: null, sortedBound });

  for (let i = 0; i < n - 1; i++) {
    let swapped = false;
    for (let j = 0; j < n - i - 1; j++) {
      out.push({ type: "compare", arr: [...arr], i: j, j: j + 1, sortedBound });

      if (arr[j] > arr[j + 1]) {
        const temp = arr[j];
        arr[j] = arr[j + 1];
        arr[j + 1] = temp;
        swapped = true;

        out.push({ type: "swap", arr: [...arr], i: j, j: j + 1, sortedBound });
      }
    }

    sortedBound = n - i - 1;
    if (!swapped) break;
  }

  out.push({ type: "done", arr: [...arr], i: null, j: null, sortedBound: 0 });
  return out;
}

export function computeSelectionSortSteps(baseArr) {
  const arr = [...baseArr];
  const out = [];
  const n = arr.length;

  let sortedBound = 0;
  out.push({ type: "start", arr: [...arr], curr: null, minIdx: null, i: null, j: null, sortedBound });

  for (let i = 0; i < n - 1; i++) {
    let minIdx = i;
    out.push({ type: "new_min", arr: [...arr], curr: i, minIdx, i: null, j: null, sortedBound });

    for (let j = i + 1; j < n; j++) {
      out.push({ type: "compare", arr: [...arr], curr: j, minIdx, i: null, j: null, sortedBound });

      if (arr[j] < arr[minIdx]) {
        minIdx = j;
        out.push({ type: "new_min", arr: [...arr], curr: j, minIdx, i: null, j: null, sortedBound });
      }
    }

    if (minIdx !== i) {
      const temp = arr[i];
      arr[i] = arr[minIdx];
      arr[minIdx] = temp;
      out.push({ type: "swap", arr: [...arr], curr: null, minIdx: null, i, j: minIdx, sortedBound });
    }

    sortedBound = i + 1;
  }

  out.push({ type: "done", arr: [...arr], curr: null, minIdx: null, i: null, j: null, sortedBound: n });
  return out;
}

export function createStepPlayer({ html, max, onRegenerate }) {
  const safeMax = Math.max(0, max);
  const form = html`<div class="p6-control-panel-uza">
    <form class="p6-step-form-uza">
      <button type="button" class="btn btn-secondary btn-sm rand-btn" style="color:#eee;font-weight:700;">Regenerar</button>
      <button type="button" class="btn btn-primary btn-sm play-btn" style="color:#eee;font-weight:700;">Reproducir</button>
      <label class="p6-step-label-uza">Paso</label>
      <input type="range" min="0" max="${safeMax}" value="0">
      <output>0</output>
    </form>
  </div>`;

  const innerForm = form.querySelector("form");
  const randBtn = form.querySelector(".rand-btn");
  const playBtn = form.querySelector(".play-btn");
  const range = form.querySelector("input[type=range]");
  const output = form.querySelector("output");

  let timer = null;
  let isPlaying = false;

  const stopPlayback = () => {
    isPlaying = false;
    if (timer !== null) {
      clearInterval(timer);
      timer = null;
    }
    playBtn.innerHTML = "Reproducir";
    playBtn.classList.add("btn-primary");
    playBtn.classList.remove("btn-danger");
  };

  const update = () => {
    output.value = range.value;
    output.textContent = range.value;
    form.value = Number(range.value);
    form.dispatchEvent(new Event("input", { bubbles: true }));
  };

  randBtn.onclick = () => {
    stopPlayback();
    range.value = 0;
    update();
    if (onRegenerate) onRegenerate();
  };

  range.oninput = () => {
    if (isPlaying) stopPlayback();
    update();
  };

  playBtn.onclick = () => {
    if (isPlaying) {
      stopPlayback();
      return;
    }

    isPlaying = true;
    playBtn.innerHTML = "Pausar";
    playBtn.classList.remove("btn-primary");
    playBtn.classList.add("btn-danger");

    if (Number(range.value) >= safeMax) {
      range.value = 0;
      update();
    }

    timer = setInterval(() => {
      const value = Number(range.value);
      if (value < safeMax) {
        range.value = value + 1;
        update();
      } else {
        stopPlayback();
      }
    }, 1100);
  };

  innerForm.onsubmit = (event) => event.preventDefault();
  form.value = 0;
  return form;
}

function renderLegend(html, items) {
  return html`
    <div class="p6-legend-uza">
      ${items.map(
        (item) => html`<span class="p6-legend-chip-uza"><span class="p6-legend-swatch-uza" style="background:${item.color};"></span>${item.label}</span>`
      )}
    </div>
  `;
}

function renderArrayState({ html, Plot, steps, stepIndex, gridBuilder, messageBuilder, legendItems }) {
  if (!steps || steps.length === 0) return html`<div>Cargando...</div>`;

  const safeStep = Math.min(stepIndex, steps.length - 1);
  const step = steps[safeStep];
  if (!step) return html`<div>Cargando...</div>`;

  const grid = gridBuilder(step);
  const arr = step.arr;
  const plot = Plot.plot({
    width: 920,
    height: 100,
    margin: 10,
    x: { axis: null, domain: [-0.5, arr.length - 0.5] },
    y: { axis: null, domain: [0, 1] },
    color: { type: "identity" },
    marks: [
      Plot.rect(grid, {
        x1: (d) => d.n - 0.48,
        x2: (d) => d.n + 0.48,
        y1: 0.1,
        y2: 0.9,
        fill: "color",
        stroke: "white",
        strokeWidth: 1.5,
        rx: 4,
      }),
      Plot.text(grid, {
        x: "n",
        y: 0.5,
        text: "value",
        fill: "white",
        fontSize: 17,
        fontWeight: "bold",
      }),
    ],
  });

  plot.classList.add("p6-sort-plot-uza");

  return html`
    <div class="p6-plot-panel-uza">
      ${plot}
      <div class="p6-sort-message-uza">
        ${messageBuilder(html, step, arr)}
      </div>
      ${renderLegend(html, legendItems)}
    </div>
  `;
}

export function renderBubbleSortState({ html, Plot, steps, stepIndex }) {
  return renderArrayState({
    html,
    Plot,
    steps,
    stepIndex,
    gridBuilder: (step) =>
      step.arr.map((value, idx) => {
        let bgColor = "#2a9d8f";
        if (idx >= step.sortedBound) bgColor = "#457b9d";
        if (idx === step.i || idx === step.j) {
          bgColor = step.type === "swap" ? "#e63946" : "#f4a261";
        }
        if (step.type === "done") bgColor = "#457b9d";

        return { n: idx, value, color: bgColor };
      }),
    messageBuilder: (htmlTag, step, arr) => {
      if (step.type === "start") return htmlTag`Vector inicial. Comenzando ordenación de burbuja`;
      if (step.type === "compare") return htmlTag`Comparando índices: ¿es <b>${arr[step.i]}</b> mayor que <b>${arr[step.j]}</b>?`;
      if (step.type === "swap") return htmlTag`Intercambiando posiciones`;
      return htmlTag`Ordenamiento completado`;
    },
    legendItems: [
      { color: "#2a9d8f", label: "No ordenado" },
      { color: "#f4a261", label: "Comparando" },
      { color: "#e63946", label: "Intercambiando" },
      { color: "#457b9d", label: "Ordenado" },
    ],
  });
}

export function renderSelectionSortState({ html, Plot, steps, stepIndex }) {
  return renderArrayState({
    html,
    Plot,
    steps,
    stepIndex,
    gridBuilder: (step) =>
      step.arr.map((value, idx) => {
        let bgColor = "#2a9d8f";
        if (idx < step.sortedBound) bgColor = "#457b9d";

        if (step.type === "swap" && (idx === step.i || idx === step.j)) {
          bgColor = "#e63946";
        } else if (step.type !== "swap") {
          if (idx === step.minIdx) bgColor = "#e9c46a";
          else if (idx === step.curr) bgColor = "#f4a261";
        }

        if (step.type === "done") bgColor = "#457b9d";
        return { n: idx, value, color: bgColor };
      }),
    messageBuilder: (htmlTag, step, arr) => {
      if (step.type === "start") return htmlTag`Vector inicial. Comenzando ordenación por selección`;
      if (step.type === "new_min") return htmlTag`Mínimo provisional marcado: <b>${arr[step.minIdx]}</b>`;
      if (step.type === "compare") return htmlTag`¿Es <b>${arr[step.curr]}</b> menor que el mínimo actual <b>${arr[step.minIdx]}</b>?`;
      if (step.type === "swap") return htmlTag`Mínimo encontrado. Intercambiando posiciones`;
      return htmlTag`Ordenamiento completado`;
    },
    legendItems: [
      { color: "#2a9d8f", label: "No ordenado" },
      { color: "#e9c46a", label: "Mínimo actual" },
      { color: "#f4a261", label: "Comparando" },
      { color: "#e63946", label: "Intercambiando" },
      { color: "#457b9d", label: "Ordenado" },
    ],
  });
}
