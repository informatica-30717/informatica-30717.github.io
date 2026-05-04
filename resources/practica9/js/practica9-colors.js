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
    !specChip ||
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

  function rgbToLabel({ r, g, b }) {
    return `RGB(${r}, ${g}, ${b})`;
  }

  function mixChannel(light, material) {
    return Math.round((light * material) / 255);
  }

  function setChipColor(node, rgb) {
    node.style.background = rgbToCss(rgb);
  }

  function update() {
    const light = hexToRgb(lightInput.value);
    const material = hexToRgb(materialInput.value);
    const result = {
      r: mixChannel(light.r, material.r),
      g: mixChannel(light.g, material.g),
      b: mixChannel(light.b, material.b),
    };

    setChipColor(lightChip, light);
    setChipColor(materialChip, material);
    setChipColor(resultChip, result);
    setChipColor(specChip, light);

    lightLabel.textContent = rgbToLabel(light);
    materialLabel.textContent = rgbToLabel(material);
    resultLabel.textContent = rgbToLabel(result);

    rFormula.textContent = `${light.r} x ${material.r} / 255 = ${result.r}`;
    gFormula.textContent = `${light.g} x ${material.g} / 255 = ${result.g}`;
    bFormula.textContent = `${light.b} x ${material.b} / 255 = ${result.b}`;
  }

  lightInput.addEventListener('input', update);
  materialInput.addEventListener('input', update);
  update();
})();
