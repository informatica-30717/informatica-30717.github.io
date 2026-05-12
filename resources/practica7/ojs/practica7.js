function cleanSlider(input, accentColor = null) {
  input.classList.add("p7-range-widget-uza");

  const numberBox = input.querySelector("input[type=number]");
  if (numberBox) numberBox.style.display = "none";

  const rangeSlider = input.querySelector("input[type=range]");
  if (rangeSlider) {
    rangeSlider.classList.add("p7-slider-uza");
    if (accentColor) rangeSlider.style.accentColor = accentColor;
  }

  return input;
}

function control({ html, label, input, formatter }) {
  const value = html`<output class="p7-control-value-uza">${formatter(input.value)}</output>`;

  input.addEventListener("input", () => {
    value.value = formatter(input.value);
    value.textContent = formatter(input.value);
  });

  return html`<label class="p7-control-uza">
    <span>${label}</span>
    ${input}
    <span class="p7-control-value-uza">${value}</span>
  </label>`;
}

function lcg(seed) {
  let state = Math.max(1, Math.floor(seed * 9973)) % 2147483647;
  return () => {
    state = (state * 48271) % 2147483647;
    return (state - 1) / 2147483646;
  };
}

function makeVoronoiPoints(count, seed) {
  const random = lcg(seed);
  const margin = 0.055;
  const palette = [
    "#dbeafe",
    "#dcfce7",
    "#fef3c7",
    "#fee2e2",
    "#e0e7ff",
    "#cffafe",
    "#fce7f3",
    "#ede9fe",
    "#ffedd5",
    "#ccfbf1"
  ];

  return Array.from({ length: count }, (_, index) => ({
    id: index + 1,
    x: margin + random() * (1 - 2 * margin),
    y: margin + random() * (1 - 2 * margin),
    color: palette[index % palette.length]
  }));
}

export function createVoronoiControls({ Inputs, html }) {
  const countInput = cleanSlider(Inputs.range([8, 90], { value: 34, step: 1 }), "#1f5f8b");
  const seedInput = cleanSlider(Inputs.range([1, 25], { value: 7, step: 1 }), "#237a57");

  const form = html`<div class="p7-control-panel-uza">
    <div class="p7-controls-uza">
      ${control({ html, label: "Puntos", input: countInput, formatter: (x) => `${Math.round(Number(x))}` })}
      ${control({ html, label: "Semilla", input: seedInput, formatter: (x) => `${Math.round(Number(x))}` })}
    </div>
  </div>`;

  form.value = {
    count: Math.round(+countInput.value),
    seed: Math.round(+seedInput.value)
  };

  form.oninput = () => {
    form.value = {
      count: Math.round(+countInput.value),
      seed: Math.round(+seedInput.value)
    };
  };

  return form;
}

export function renderVoronoiExplorer({ Plot, html, params }) {
  const points = makeVoronoiPoints(params.count, params.seed);
  const plot = Plot.plot({
    width: 640,
    height: 360,
    margin: 16,
    x: { domain: [0, 1], axis: null },
    y: { domain: [0, 1], axis: null },
    marks: [
      Plot.frame({ stroke: "#d7e2ea" }),
      Plot.voronoi(points, {
        x: "x",
        y: "y",
        fill: "color",
        fillOpacity: 0.82,
        stroke: "white",
        strokeWidth: 1.25
      }),
      Plot.voronoiMesh(points, { x: "x", y: "y", stroke: "#1f5f8b", strokeOpacity: 0.42 }),
      Plot.dot(points, { x: "x", y: "y", r: 3.2, fill: "#a65f00", stroke: "white", strokeWidth: 1 })
    ]
  });
  plot.classList.add("p7-plot-uza");

  return html`<div class="p7-plot-panel-uza p7-voronoi-output-uza">
    ${plot}
    <div class="p7-plot-note-uza">${params.count} generadores. Más puntos producen celdas más pequeñas y una perforación más densa.</div>
  </div>`;
}
