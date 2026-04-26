import * as THREE from 'https://esm.sh/three@0.161.0';
import { OrbitControls } from 'https://esm.sh/three@0.161.0/examples/jsm/controls/OrbitControls.js';
import { PLYLoader } from 'https://esm.sh/three@0.161.0/examples/jsm/loaders/PLYLoader.js';

function createViewer(id, url, options = {}) {
  const container = document.getElementById(id);
  if (!container) return;

  const status = document.createElement('div');
  status.className = 'lidar-viewer-status';
  status.textContent = 'Cargando…';
  container.parentElement.appendChild(status);

  const scene = new THREE.Scene();
  scene.background = new THREE.Color(0x0b1221);

  const initW = container.clientWidth || 400;
  const initH = container.clientHeight || initW * 0.75;
  const camera = new THREE.PerspectiveCamera(45, initW / initH, 0.001, 100);
  camera.position.set(2.5, 1.8, 2.5);

  const renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.setSize(initW, initH);
  container.appendChild(renderer.domElement);

  const controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;

  scene.add(new THREE.HemisphereLight(0xffffff, 0x444466, 1.0));
  const dir = new THREE.DirectionalLight(0xffffff, 0.6);
  dir.position.set(1, 2, 1);
  scene.add(dir);

  const frameCamera = (sphere) => {
    const radius = Math.max(0.001, sphere.radius);
    if (options.startInside) {
      camera.position.set(radius * 0.08, radius * 0.03, radius * 0.08);
      controls.target.set(0, radius * 0.02, -radius * 0.45);
      camera.near = Math.max(0.0005, radius / 2000);
      camera.far = radius * 20;
      controls.minDistance = radius * 0.01;
      controls.maxDistance = radius * 4;
    } else {
      const dist = radius / Math.tan((camera.fov * Math.PI / 180) / 2) * 0.85;
      camera.position.set(dist * 0.7, dist * 0.5, dist * 0.7);
      controls.target.set(0, 0, 0);
      camera.near = Math.max(0.001, dist / 1000);
      camera.far = dist * 100;
    }
    camera.updateProjectionMatrix();
    controls.update();
  };

  (async () => {
    try {
      const response = await fetch(url);
      if (!response.ok) throw new Error(response.status + ' ' + response.statusText);
      const buffer = await response.arrayBuffer();
      const geometry = new PLYLoader().parse(buffer);

      geometry.computeBoundingBox();
      const bbox = geometry.boundingBox;
      const size = new THREE.Vector3();
      bbox.getSize(size);
      const center = new THREE.Vector3();
      bbox.getCenter(center);
      geometry.translate(-center.x, -center.y, -center.z);
      const maxDim = Math.max(size.x, size.y, size.z) || 1;
      const scale = 1.5 / maxDim;
      geometry.scale(scale, scale, scale);

      const hasColor = geometry.hasAttribute('color');
      const vertCount = geometry.attributes.position?.count ?? 0;
      console.log('[lidar-viewer]', id, { format: 'ply', vertCount, hasColor });

      // Screen-space points stay readable from far away without swelling on zoom-in.
      const pointSize = vertCount > 200000 ? 1.2 : vertCount > 50000 ? 1.45 : 1.7;
      const material = new THREE.PointsMaterial({
        size: pointSize,
        vertexColors: hasColor,
        color: hasColor ? 0xffffff : 0x88c0ff,
        sizeAttenuation: false,
      });
      const object = new THREE.Points(geometry, material);
      scene.add(object);

      const sphere = new THREE.Sphere();
      geometry.boundingBox.getBoundingSphere(sphere);
      frameCamera(sphere);

      status.remove();
    } catch (err) {
      console.error('[lidar-viewer] load error for ' + url, err);
      status.textContent = 'Modelo no disponible';
    }
  })();

  function animate() {
    requestAnimationFrame(animate);
    controls.update();
    renderer.render(scene, camera);
  }
  animate();

  // Quarto may resize containers after first paint, so keep the viewer responsive.
  const resize = () => {
    const nw = container.clientWidth;
    const nh = container.clientHeight || nw * 0.75;
    if (!nw || !nh) return;
    camera.aspect = nw / nh;
    camera.updateProjectionMatrix();
    renderer.setSize(nw, nh);
  };
  new ResizeObserver(resize).observe(container);
  window.addEventListener('resize', resize);
}

createViewer('lidar-viewer-cloud', '../resources/practica8/models/goya_point_cloud.ply');
createViewer('lidar-viewer-room', '../resources/practica8/models/room_500k.ply', { startInside: true });
