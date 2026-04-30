import * as THREE from 'https://esm.sh/three@0.161.0';
import { OrbitControls } from 'https://esm.sh/three@0.161.0/examples/jsm/controls/OrbitControls.js';
import { RGBELoader } from 'https://esm.sh/three@0.161.0/examples/jsm/loaders/RGBELoader.js';

(() => {
  const vp = document.getElementById('phong-viewport');
  const statusEl = document.getElementById('phong-status');
  if (!vp || !statusEl) return;

  const scene = new THREE.Scene();
  scene.background = new THREE.Color(0x000000);

  const W = vp.clientWidth || 500;
  const H = 340;
  const camera = new THREE.PerspectiveCamera(45, W / H, 0.001, 2000);
  const renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  renderer.setSize(W, H);
  renderer.outputColorSpace = THREE.SRGBColorSpace;
  renderer.toneMapping = THREE.ACESFilmicToneMapping;
  renderer.toneMappingExposure = 1.1;
  vp.appendChild(renderer.domElement);

  const pmremGenerator = new THREE.PMREMGenerator(renderer);
  pmremGenerator.compileEquirectangularShader();

  const ambientLight = new THREE.AmbientLight(0xfff4e0, 1.0);
  const dirLight = new THREE.DirectionalLight(0xffffff, 2.8);
  dirLight.position.set(3, 6, 4);
  scene.add(ambientLight, dirLight);

  const controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.08;

  const shadedMats = [];
  let envMap = null;

  const sphereGeometry = new THREE.SphereGeometry(1.08, 128, 96);
  const sphereMaterial = new THREE.MeshStandardMaterial({
    color: new THREE.Color('#f7f7f5'),
    metalness: 1,
    roughness: 0.08,
    envMap: null,
    envMapIntensity: 2.6,
  });
  const sphere = new THREE.Mesh(sphereGeometry, sphereMaterial);
  sphere.position.set(0, 0, 0);
  scene.add(sphere);
  shadedMats.push(sphereMaterial);

  camera.position.set(0, 0.12, 3.05);
  controls.target.set(0, 0, 0);
  controls.update();
  statusEl.style.display = 'none';

  function hexToColor(hex) {
    const v = parseInt(hex.slice(1), 16);
    return new THREE.Color((v >> 16 & 255) / 255, (v >> 8 & 255) / 255, (v & 255) / 255);
  }

  function updatePhong() {
    const ka = Number(document.getElementById('ph-ka').value);
    const kd = Number(document.getElementById('ph-kd').value);
    const ks = Number(document.getElementById('ph-ks').value);
    const e = Number(document.getElementById('ph-e').value);
    const cl = hexToColor(document.getElementById('ph-cl').value);
    const ca = hexToColor(document.getElementById('ph-ca').value);

    ambientLight.color.copy(ca);
    ambientLight.intensity = ka * 5;
    dirLight.color.copy(cl);
    dirLight.intensity = kd * 5;

    shadedMats.forEach((mat) => {
      mat.color.copy(ca).lerp(new THREE.Color('#ffffff'), 0.72);
      mat.metalness = 0.82 + ks * 0.18;
      mat.roughness = Math.max(0.035, 0.22 - (e / 120) * 0.18);
      if (envMap) {
        mat.envMap = envMap;
        mat.envMapIntensity = 1.8 + ks * 1.8;
      }
      mat.needsUpdate = true;
    });
  }

  new RGBELoader().load('../resources/practica9/models/sunset.hdr', (texture) => {
    texture.mapping = THREE.EquirectangularReflectionMapping;
    envMap = pmremGenerator.fromEquirectangular(texture).texture;
    scene.environment = envMap;
    shadedMats.forEach((mat) => {
      mat.envMap = envMap;
      mat.envMapIntensity = 2.4;
      mat.needsUpdate = true;
    });
    texture.dispose();
    pmremGenerator.dispose();
  }, undefined, (err) => {
    console.warn('No se pudo cargar sunset.hdr', err);
  });

  updatePhong();

  ['ph-ka', 'ph-kd', 'ph-ks', 'ph-e', 'ph-ca', 'ph-cl'].forEach((id) => {
    document.getElementById(id)?.addEventListener('input', () => {
      const vEl = document.getElementById(`${id}-v`);
      const el = document.getElementById(id);
      if (vEl) vEl.textContent = id === 'ph-e' ? el.value : Number(el.value).toFixed(2);
      updatePhong();
    });
  });

  (function animate() {
    requestAnimationFrame(animate);
    controls.update();
    renderer.render(scene, camera);
  })();

  new ResizeObserver(() => {
    const nw = vp.clientWidth;
    if (!nw) return;
    camera.aspect = nw / H;
    camera.updateProjectionMatrix();
    renderer.setSize(nw, H);
  }).observe(vp);
})();
