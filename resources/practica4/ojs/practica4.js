const BISECTION_CASE = {
  a: 2,
  b: 5,
  xmin: 3.1,
  xmax: 3.2,
};

function cleanSlider(Inputs, range, config, accentColor = null) {
  const input = Inputs.range(range, config);
  input.classList.add("p4-range-widget-uza");

  const numberBox = input.querySelector("input[type=number]");
  if (numberBox) numberBox.style.display = "none";

  const rangeSlider = input.querySelector("input[type=range]");
  if (rangeSlider) {
    rangeSlider.classList.add("p4-slider-uza");
    if (accentColor) rangeSlider.style.accentColor = accentColor;
  }

  return input;
}

function labeledControl({ html, input, label, formatter, color, padded = false }) {
  const value = html`<output class=${padded ? "p4-control-value-uza p4-control-value--pill-uza" : "p4-control-value-uza"} style=${padded ? `--p4-control-color: ${color}` : ""}>${formatter(input.value)}</output>`;

  input.addEventListener("input", () => {
    value.value = formatter(input.value);
    value.textContent = formatter(input.value);
  });

  return html`<label class=${padded ? "p4-control-uza p4-control--card-uza" : "p4-control-uza"}>
    <span>${label}</span>
    ${input}
    ${value}
  </label>`;
}

function catenaryY(x, a) {
  return a * Math.cosh(x / a);
}

function catenaryResidual(x, a, b) {
  return catenaryY(x, a) - b;
}

function catenaryIntersections(a, b) {
  if (b < a) return [];

  const xsol = a * Math.acosh(b / a);
  const intersections = [{ x: xsol, y: b }];
  if (xsol !== 0) intersections.push({ x: -xsol, y: b });
  return intersections;
}

function bracketRoot(a, b) {
  const xmin = BISECTION_CASE.xmin;
  const xmax = BISECTION_CASE.xmax;

  if (catenaryResidual(xmin, a, b) * catenaryResidual(xmax, a, b) > 0) return null;
  return { xmin, xmax };
}

export function createCatenaryControls({ Inputs, html }) {
  const aInput = cleanSlider(Inputs, [0.1, 5], { value: 1, step: 0.1 }, "#1f5f8b");
  const bInput = cleanSlider(Inputs, [0, 6], { value: 2, step: 0.1 }, "#d94841");

  const form = html`<div class="p4-controls-uza">
    ${labeledControl({
      html,
      input: aInput,
      label: "Constante a",
      formatter: (x) => Number(x).toFixed(1),
      color: "#1f5f8b",
    })}
    ${labeledControl({
      html,
      input: bInput,
      label: "Nivel b",
      formatter: (x) => Number(x).toFixed(1),
      color: "#d94841",
    })}
  </div>`;

  form.oninput = () => {
    form.value = { a: +aInput.value, b: +bInput.value };
  };

  form.value = { a: +aInput.value, b: +bInput.value };
  return form;
}

export function renderCatenaryPlot({ Plot, a, b }) {
  const curve = Array.from({ length: 401 }, (_, i) => {
    const x = -10 + i * 0.05;
    return { x, y: Math.min(20, catenaryY(x, a)) };
  });

  const vertex = [{ x: 0, y: a, label: `V=(0, ${a.toFixed(2)})` }];
  const intersections = catenaryIntersections(a, b);
  const yline = [{ x: -4, y: b }, { x: 4, y: b }];

  const plot = Plot.plot({
    height: 330,
    grid: true,
    marginLeft: 42,
    marginRight: 18,
    marginTop: 18,
    marginBottom: 38,
    x: { domain: [-4, 4], label: "x" },
    y: { domain: [0, 6], label: "y" },
    marks: [
      Plot.ruleY([0], { stroke: "#94a3b8", strokeWidth: 1.2 }),
      Plot.ruleX([0], { stroke: "#94a3b8", strokeWidth: 1.2 }),
      Plot.line(curve, { x: "x", y: "y", stroke: "#1f5f8b", strokeWidth: 3 }),
      Plot.dot(vertex, { x: "x", y: "y", fill: "#f59e0b", r: 6, stroke: "white", strokeWidth: 2 }),
      Plot.text(vertex, { x: "x", y: "y", text: "label", dy: -15, fill: "#172033", stroke: "white", strokeWidth: 4, fontWeight: 700 }),
      Plot.line(yline, { x: "x", y: "y", stroke: "#d94841", strokeDasharray: "6 5", strokeWidth: 2 }),
      Plot.dot(intersections, { x: "x", y: "y", fill: "#d94841", r: 5.5, stroke: "white", strokeWidth: 2 }),
    ],
  });

  plot.classList.add("p4-plot-uza");
  return plot;
}

export function renderCatenaryExamplePlot({ Plot, a = 2, b = 5 }) {
  const curve = Array.from({ length: 401 }, (_, i) => {
    const x = -6 + i * 0.03;
    return { x, y: Math.min(20, catenaryY(x, a)) };
  });

  const yline = [{ x: -6, y: b }, { x: 6, y: b }];
  const plot = Plot.plot({
    height: 390,
    grid: true,
    marginLeft: 42,
    marginRight: 16,
    marginTop: 18,
    marginBottom: 36,
    x: { domain: [-6, 6], label: "x" },
    y: { domain: [0, 8], label: "y" },
    marks: [
      Plot.rect(
        [{ x1: 2, x2: 4, y1: 0, y2: 8 }],
        {
          x1: "x1",
          x2: "x2",
          y1: "y1",
          y2: "y2",
          fill: "#f59e0b",
          fillOpacity: 0.16,
        }
      ),
      Plot.ruleY([0], { stroke: "#94a3b8", strokeWidth: 1.2 }),
      Plot.ruleX([0], { stroke: "#94a3b8", strokeWidth: 1.2 }),
      Plot.line(curve, { x: "x", y: "y", stroke: "#1f5f8b", strokeWidth: 3 }),
      Plot.line(yline, { x: "x", y: "y", stroke: "#d94841", strokeDasharray: "6 5", strokeWidth: 2 }),
      Plot.dot(catenaryIntersections(a, b), { x: "x", y: "y", fill: "#d94841", r: 5.5, stroke: "white", strokeWidth: 2 }),
    ],
  });

  plot.classList.add("p4-plot-uza");
  return plot;
}

export function createBisectionControls({ Inputs, html }) {
  const stepInput = cleanSlider(Inputs, [0, 20], { value: 0, step: 1 }, "#6d5bd0");

  const form = html`<div class="p4-controls-uza p4-controls--stacked-uza">
    <div class="p4-controls-title-uza">Método de bisección</div>
    <div class="p4-bisection-status-uza">
      <span>a = ${BISECTION_CASE.a.toFixed(1)}</span>
      <span>b = ${BISECTION_CASE.b.toFixed(1)}</span>
      <span>[${BISECTION_CASE.xmin.toFixed(1)}, ${BISECTION_CASE.xmax.toFixed(1)}]</span>
    </div>
    ${labeledControl({
      html,
      input: stepInput,
      label: "Iteración",
      formatter: (x) => `${x}`,
      color: "#6d5bd0",
      padded: true,
    })}
  </div>`;

  form.oninput = () => {
    form.value = {
      a: BISECTION_CASE.a,
      b: BISECTION_CASE.b,
      step: +stepInput.value,
    };
  };

  form.value = {
    a: BISECTION_CASE.a,
    b: BISECTION_CASE.b,
    step: +stepInput.value,
  };

  return form;
}

export function renderBisectionPlot({ Plot, html, a, b, step }) {
  const bracket = bracketRoot(a, b);

  if (!bracket) {
    return html`<div class="p4-warning-uza">
      No hay cambio de signo en el intervalo <code>[3.1, 3.2]</code>.
    </div>`;
  }

  let xmin = bracket.xmin;
  let xmax = bracket.xmax;

  for (let i = 0; i < step; ++i) {
    const xmidIter = 0.5 * (xmin + xmax);
    const fxmin = catenaryResidual(xmin, a, b);
    const fxmid = catenaryResidual(xmidIter, a, b);

    if (fxmin * fxmid <= 0) xmax = xmidIter;
    else xmin = xmidIter;
  }

  const xmid = 0.5 * (xmin + xmax);
  const curveXMin = 3.05;
  const curveXMax = 3.25;
  const curve = Array.from({ length: 400 }, (_, i) => {
    const x = curveXMin + ((curveXMax - curveXMin) * i) / 399;
    return { x, y: catenaryResidual(x, a, b) };
  });

  const yValues = curve.map((d) => Math.abs(d.y));
  const yLim = Math.max(...yValues, 1e-3) * 1.1;

  const band = [{ x1: xmin, x2: xmax, y1: -yLim, y2: yLim }];
  const endpointGuides = [{ x: xmin }, { x: xmax }];
  const midpointGuide = [{ x: xmid }];
  const endPoints = [
    { x: xmin, y: catenaryResidual(xmin, a, b) },
    { x: xmax, y: catenaryResidual(xmax, a, b) },
  ];
  const midPoint = [{ x: xmid, y: catenaryResidual(xmid, a, b) }];
  const overlayLabels = [
    { x: curveXMin + 0.025, y: yLim * 0.88, text: `xmin = ${xmin.toFixed(4)}`, color: "#b45309" },
    { x: xmid, y: -yLim * 0.82, text: `xmedio = ${xmid.toFixed(4)}`, color: "#6d5bd0" },
    { x: curveXMax - 0.025, y: yLim * 0.88, text: `xmax = ${xmax.toFixed(4)}`, color: "#b45309" },
  ];

  const plot = Plot.plot({
    height: 360,
    marginLeft: 54,
    marginRight: 22,
    marginTop: 20,
    marginBottom: 44,
    grid: true,
    x: { domain: [curveXMin, curveXMax], label: "x" },
    y: { domain: [-yLim, yLim], label: "f(x)" },
    marks: [
      Plot.rect(band, {
        x1: "x1",
        x2: "x2",
        y1: "y1",
        y2: "y2",
        fill: "#f59e0b",
        fillOpacity: 0.14,
      }),
      Plot.ruleY([0], { stroke: "#94a3b8", strokeWidth: 1.2 }),
      Plot.line(curve, { x: "x", y: "y", stroke: "#1f5f8b", strokeWidth: 3 }),
      Plot.ruleX(endpointGuides, { x: "x", stroke: "#f59e0b", strokeDasharray: "5 5", strokeWidth: 1.5 }),
      Plot.ruleX(midpointGuide, { x: "x", stroke: "#6d5bd0", strokeDasharray: "7 4", strokeWidth: 1.5 }),
      Plot.dot(endPoints, { x: "x", y: "y", fill: "#b45309", r: 4.8, stroke: "white", strokeWidth: 2 }),
      Plot.dot(midPoint, { x: "x", y: "y", fill: "#1d4ed8", r: 5.8, stroke: "white", strokeWidth: 2 }),
      Plot.text(overlayLabels, {
        x: "x",
        y: "y",
        text: "text",
        fill: "color",
        fontSize: 12,
        fontWeight: 700,
        textAnchor: (d, i) => (i === 0 ? "start" : i === 2 ? "end" : "middle"),
      }),
    ],
  });

  plot.classList.add("p4-plot-uza");
  return plot;
}
