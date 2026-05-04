(() => {
  const lightInput = document.getElementById('mix-light-uza');
  const materialInput = document.getElementById('mix-material-uza');
  const lightChip = document.getElementById('mix-light-chip-uza');
  const materialChip = document.getElementById('mix-material-chip-uza');
  const resultChip = document.getElementById('mix-result-chip-uza');
  const specChip = document.getElementById('mix-spec-chip-uza');
  const lightLabel = document.getElementById('mix-light-rgb-uza');
  const materialLabel = document.getElementById('mix-material-rgb-uza');
  const resultLabel = document.getElementById('mix-result-rgb-uza');
  const rFormula = document.getElementById('mix-r-formula-uza');
  const gFormula = document.getElementById('mix-g-formula-uza');
  const bFormula = document.getElementById('mix-b-formula-uza');

  if (
    !lightInput ||
    !materialInput ||
    !lightChip ||
    !materialChip ||
    !resultChip ||
    !lightLabel ||
    !materialLabel ||
    !resultLabel ||
    !rFormula ||
    !gFormula ||
    !bFormula
  ) {
    return;
  }

  function hexToRgb(hex) {
    const clean = hex.replace('#', '');
    const value = parseInt(clean, 16);
    return {
      r: (value >> 16) & 255,
      g: (value >> 8) & 255,
      b: value & 255,
    };
  }

  function rgbToCss({ r, g, b }) {
    return `rgb(${r}, ${g}, ${b})`;
  }

  function toUnitRgb({ r, g, b }) {
    return {
      r: r / 255,
      g: g / 255,
      b: b / 255,
    };
  }

  function formatUnit(value) {
    return value.toFixed(2);
  }

  function unitRgbToCss({ r, g, b }) {
    return `rgb(${Math.round(r * 255)}, ${Math.round(g * 255)}, ${Math.round(b * 255)})`;
  }

  function unitRgbToLabel({ r, g, b }) {
    return `RGB(${formatUnit(r)}, ${formatUnit(g)}, ${formatUnit(b)})`;
  }

  function mixUnitRgb(light, material) {
    return {
      r: light.r * material.r,
      g: light.g * material.g,
      b: light.b * material.b,
    };
  }

  function setChipColor(node, cssColor) {
    node.style.background = cssColor;
  }

  function update() {
    const lightBytes = hexToRgb(lightInput.value);
    const materialBytes = hexToRgb(materialInput.value);
    const light = toUnitRgb(lightBytes);
    const material = toUnitRgb(materialBytes);
    const result = mixUnitRgb(light, material);

    setChipColor(lightChip, rgbToCss(lightBytes));
    setChipColor(materialChip, rgbToCss(materialBytes));
    setChipColor(resultChip, unitRgbToCss(result));
    if (specChip) {
      setChipColor(specChip, rgbToCss(lightBytes));
    }

    lightLabel.textContent = unitRgbToLabel(light);
    materialLabel.textContent = unitRgbToLabel(material);
    resultLabel.textContent = unitRgbToLabel(result);

    rFormula.textContent = `${formatUnit(light.r)} x ${formatUnit(material.r)} = ${formatUnit(result.r)}`;
    gFormula.textContent = `${formatUnit(light.g)} x ${formatUnit(material.g)} = ${formatUnit(result.g)}`;
    bFormula.textContent = `${formatUnit(light.b)} x ${formatUnit(material.b)} = ${formatUnit(result.b)}`;
  }

  lightInput.addEventListener('input', update);
  materialInput.addEventListener('input', update);
  update();
})();
