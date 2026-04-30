function cleanSlider(Inputs, range, config, accentColor = null) {
  const input = Inputs.range(range, config);
  const numberBox = input.querySelector("input[type=number]");
  if (numberBox) numberBox.style.display = "none";

  const rangeSlider = input.querySelector("input[type=range]");
  if (rangeSlider) {
    rangeSlider.style.width = "100%";
    if (accentColor) rangeSlider.style.accentColor = accentColor;
  }

  input.style.width = "100%";
  return input;
}

function labeledControl({ html, input, label, formatter, color, padded = false }) {
  const value = html`<div style="
    margin-top: ${padded ? 8 : 4}px;
    display: inline-block;
    padding: ${padded ? "4px 10px" : "0"};
    border-radius: ${padded ? "999px" : "0"};
    background: ${padded ? color : "transparent"};
    color: ${padded ? "white" : "#555"};
    font-family: monospace;
    font-size: ${padded ? "0.90rem" : "inherit"};
    font-weight: ${padded ? "700" : "400"};
  ">${formatter(input.value)}</div>`;

  input.addEventListener("input", () => {
    value.textContent = formatter(input.value);
  });

  if (!padded) {
    return html`
      <div style="display: flex; flex-direction: column; margin-bottom: 20px;">
        <div style="font-weight: bold; font-size: 0.9rem; margin-bottom: 5px;">
          ${label}
        </div>
        ${input}
        ${value}
      </div>
    `;
  }

  return html`
    <div style="
      margin-bottom: 18px;
      padding: 14px 14px 12px 14px;
      border: 1px solid #e5e7eb;
      border-radius: 14px;
      background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
    ">
      <div style="
        font-weight: 700;
        font-size: 0.93rem;
        margin-bottom: 8px;
        color: #1f2937;
      ">${label}</div>
      ${input}
      ${value}
    </div>
  `;
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
  const xmin = 3.1;
  const xmax = 3.2;

  if (catenaryResidual(xmin, a, b) * catenaryResidual(xmax, a, b) > 0) return null;
  return { xmin, xmax };
}

export function createCatenaryControls({ Inputs, html }) {
  const aInput = cleanSlider(Inputs, [0.1, 5], { value: 1, step: 0.1 });
  const bInput = cleanSlider(Inputs, [0, 6], { value: 2, step: 0.1 });

  const form = html`
    <div style="width: 100%;">
      ${labeledControl({
        html,
        input: aInput,
        label: "Constante a",
        formatter: (x) => Number(x).toFixed(1),
        color: "#2563eb",
      })}
      ${labeledControl({
        html,
        input: bInput,
        label: "Nivel b",
        formatter: (x) => Number(x).toFixed(1),
        color: "#dc2626",
      })}
    </div>
  `;

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

  const points = a !== 0 ? [{ x: 0, y: a, label: `V=(0, ${a.toFixed(2)})` }] : [];
  const yline = [{ x: -4, y: b }, { x: 4, y: b }];

  const plot = Plot.plot({
    height: 300,
    grid: true,
    marginLeft: 40,
    x: { domain: [-4, 4], label: "x axis" },
    y: { domain: [0, 6], label: "y axis" },
    marks: [
      Plot.ruleY([0], { stroke: "#888" }),
      Plot.ruleX([0], { stroke: "#888" }),
      Plot.line(curve, { x: "x", y: "y", stroke: "steelblue", strokeWidth: 3 }),
      Plot.dot(points, { x: "x", y: "y", fill: "#f49e61", r: 6 }),
      Plot.text(points, { x: "x", y: "y", text: "label", dy: -15 }),
      Plot.line(yline, { x: "x", y: "y", stroke: "red", strokeDasharray: "4 4" }),
      Plot.dot(catenaryIntersections(a, b), { x: "x", y: "y", fill: "red", r: 5 }),
    ],
  });

  plot.style.width = "100%";
  return plot;
}

export function renderCatenaryExamplePlot({ Plot, a = 2, b = 5 }) {
  const curve = Array.from({ length: 401 }, (_, i) => {
    const x = -6 + i * 0.03;
    return { x, y: Math.min(20, catenaryY(x, a)) };
  });

  const yline = [{ x: -6, y: b }, { x: 6, y: b }];

  return Plot.plot({
    height: 450,
    grid: true,
    marginLeft: 40,
    x: { domain: [-6, 6] },
    y: { domain: [0, 8] },
    style: { fontSize: "1.4em" },
    marks: [
      Plot.rect(
        [{ x1: 2, x2: 4, y1: 0, y2: 8 }],
        {
          x1: "x1",
          x2: "x2",
          y1: "y1",
          y2: "y2",
          fill: "orange",
          fillOpacity: 0.15,
        }
      ),
      Plot.ruleY([0], { stroke: "#888" }),
      Plot.ruleX([0], { stroke: "#888" }),
      Plot.line(curve, { x: "x", y: "y", stroke: "steelblue", strokeWidth: 3 }),
      Plot.line(yline, { x: "x", y: "y", stroke: "red", strokeDasharray: "4 4" }),
      Plot.dot(catenaryIntersections(a, b), { x: "x", y: "y", fill: "red", r: 5 }),
    ],
  });
}

export function createBisectionControls({ Inputs, html }) {
  const aInput = cleanSlider(Inputs, [0.5, 5], { value: 2.0, step: 0.1 }, "#2563eb");
  const bInput = cleanSlider(Inputs, [0.5, 8], { value: 5.0, step: 0.1 }, "#dc2626");
  const stepInput = cleanSlider(Inputs, [0, 5], { value: 0, step: 1 }, "#7c3aed");

  const form = html`
    <div style="width: 100%;">
      <div style="
        font-size: 1rem;
        font-weight: 800;
        margin-bottom: 14px;
        color: #111827;
      ">
        Método de bisección
      </div>

      ${labeledControl({
        html,
        input: aInput,
        label: "Constante a",
        formatter: (x) => Number(x).toFixed(1),
        color: "#2563eb",
        padded: true,
      })}
      ${labeledControl({
        html,
        input: bInput,
        label: "Nivel b",
        formatter: (x) => Number(x).toFixed(1),
        color: "#dc2626",
        padded: true,
      })}
      ${labeledControl({
        html,
        input: stepInput,
        label: "Iteración",
        formatter: (x) => `${x}`,
        color: "#7c3aed",
        padded: true,
      })}
    </div>
  `;

  form.oninput = () => {
    form.value = {
      a: +aInput.value,
      b: +bInput.value,
      step: +stepInput.value,
    };
  };

  form.value = {
    a: +aInput.value,
    b: +bInput.value,
    step: +stepInput.value,
  };

  return form;
}

export function renderBisectionPlot({ Plot, html, a, b, step }) {
  const bracket = bracketRoot(a, b);

  if (!bracket) {
    return html`<div style="
      padding: 18px;
      border-radius: 14px;
      background: #fff7ed;
      border: 1px solid #fed7aa;
      color: #9a3412;
      font-weight: 600;
    ">
      No hay cambio de signo en el intervalo <span style="font-family: monospace;">[2,4]</span>.
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
    const x = curveXMin + (curveXMax - curveXMin) * i / 399;
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
    { x: 2.18, y: yLim * 0.88, text: `xmin = ${xmin.toFixed(3)}`, color: "#b45309" },
    { x: 2.7, y: yLim * 0.88, text: `f(xMedio) = ${catenaryResidual(xmid, a, b).toExponential(2)}`, color: "#1d4ed8" },
    { x: 3.68, y: yLim * 0.88, text: `xmax = ${xmax.toFixed(3)}`, color: "#b45309" },
  ];

  return Plot.plot({
    height: 350,
    marginLeft: 52,
    marginRight: 18,
    marginTop: 18,
    marginBottom: 40,
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
        fillOpacity: 0.12,
      }),
      Plot.ruleY([0], { stroke: "#9ca3af", strokeWidth: 1.2 }),
      Plot.line(curve, { x: "x", y: "y", stroke: "#2563eb", strokeWidth: 3 }),
      Plot.ruleX(endpointGuides, { x: "x", stroke: "#f59e0b", strokeDasharray: "4 4" }),
      Plot.ruleX(midpointGuide, { x: "x", stroke: "#7c3aed", strokeDasharray: "6 4" }),
      Plot.dot(endPoints, { x: "x", y: "y", fill: "#b45309", r: 4.5 }),
      Plot.dot(midPoint, { x: "x", y: "y", fill: "#1d4ed8", r: 5.5 }),
      Plot.text(overlayLabels, {
        x: "x",
        y: "y",
        text: "text",
        fill: "color",
        fontSize: 13,
        fontWeight: 700,
      }),
    ],
  });
}
