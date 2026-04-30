# Codex plan: Modernize the Java teaching renderer without OpenGL/ImGui

## Main goal

Modernize the old Java visualizer used in the architecture informatics course.

The students are not primarily interested in programming, so the viewer should look visually engaging, but the programming tasks must remain small and localized.

The project must stay easy to import in Eclipse/IntelliJ as a normal Java project.

## Hard technical decision

Use:

```text
Plain Java
Swing
JPanel
BufferedImage
Manual CPU rasterization
No external dependencies
```

Do **not** use:

```text
OpenGL
LWJGL
JOGL
ImGui
JavaFX
Maven
Gradle
native libraries
shader files
GLFW
```

Reason: these dependencies would make import and execution harder for students.

## Big implementation strategy

Do not ask Codex to "modernize the renderer" in one large task. Use the phases below one at a time.

The key technical modernization is:

```text
old Java2D per-pixel rectangle drawing
→ BufferedImage color buffer
→ flat depth buffer
→ optional normal buffer
→ shader modes
→ fake CPU AO / outline / vignette postprocess
```

---

# Phase 0 — Inspect the current project

Use this first. No edits.

```text
Read AGENTS.md and inspect the existing Java visualizer project.

Do not modify code yet.

Produce a short report with:

1. Current package structure.
2. Main class / entry point.
3. Whether the project uses OpenGL, LWJGL, JOGL, ImGui, JavaFX, native libraries, Maven, or Gradle.
4. Which classes are responsible for:
   - OBJ loading,
   - camera/projection,
   - rasterization,
   - z-buffer,
   - material/light,
   - Swing UI.
5. Which files are too coupled or risky to modify.
6. A safe incremental refactor plan.

Hard constraints:
- Do not add dependencies.
- Do not introduce OpenGL or ImGui.
- Do not modify files in this task.

Done when:
- You provide the report.
- You explicitly confirm whether the project is currently dependency-free.
```

---

# Phase 1 — Preserve importability

Goal: make the project easier to run before changing rendering.

```text
Make the project easier to run while preserving a plain Java/Eclipse-friendly structure.

Requirements:

1. Keep the project dependency-free.
2. Do not add Maven or Gradle.
3. Do not add OpenGL, LWJGL, JOGL, JavaFX, ImGui, or native libraries.
4. Ensure there is one obvious main class.
5. If possible, create or keep a main class named `app.Main` or keep the existing main class if renaming is risky.
6. On startup, open the viewer window.
7. Automatically load a default OBJ model if one exists in the project.
8. If no OBJ model exists, show a clear message in the console or viewer.
9. Keep manual OBJ loading if already present.

Done when:
- The project compiles.
- The viewer opens by running the main class.
- A default model loads automatically or a clear fallback message appears.
- No external dependencies were added.
```

---

# Phase 2 — Replace pixel drawing with `BufferedImage`

This is the most important technical modernization. Do this before fake AO/outlines.

```text
Refactor raster drawing to use a BufferedImage color buffer instead of drawing each pixel as a Java2D rectangle.

Current style to avoid:
- Drawing every pixel with `Graphics2D.draw(Rectangle2D)`.

Target style:
- Create a `BufferedImage`.
- Write pixels into the image.
- Draw the final image once in `paintComponent`.

Requirements:

1. Keep Swing/JPanel.
2. Keep Java2D only for drawing the final image and optional text overlay.
3. Keep the existing rasterization algorithm as similar as possible.
4. Keep the existing z-buffer logic.
5. Do not add dependencies.
6. Do not change visual modes yet unless needed.
7. Prefer direct pixel access through an `int[]` buffer if simple.

Suggested approach:

- Add a color buffer:
  `BufferedImage colorBuffer`

- Optionally access pixels through:
  `((DataBufferInt) colorBuffer.getRaster().getDataBuffer()).getData()`

- Replace per-pixel Graphics2D drawing with:
  `pixels[y * width + x] = rgb`

- In `paintComponent`, call:
  `g.drawImage(colorBuffer, 0, 0, null)`

Done when:
- The project compiles.
- Raster mode still works.
- Wireframe mode still works or is restored.
- Rendering is not visibly worse than before.
- No external dependencies were added.
```

---

# Phase 3 — Clean render buffers

Goal: prepare for depth/normal/fake AO.

```text
Introduce simple render buffers to support later visual effects.

Create a small class if useful, for example:

- `renderer/RenderBuffers.java`

It should contain:

- width
- height
- color buffer / `int[] pixels`
- depth buffer / `double[]` or `float[]`
- optional normal buffer / flat `float[] normalX`, `normalY`, `normalZ` if the rasterizer already computes/interpolates normals
- optional world position buffer only if already available and not too expensive

Requirements:

1. Keep it simple.
2. Do not overengineer.
3. Do not introduce dependencies.
4. Preserve the current renderer behavior.
5. Store depth in a form that can later be visualized.
6. Store normals only if the rasterizer already computes/interpolates normals.
7. Use flat arrays in hot loops. Avoid `double[][]` and object allocation per pixel.

Done when:
- Raster mode still works.
- Z-buffer still works.
- Render buffers are centralized enough that postprocessing can use them later.
```

---

# Phase 4 — Create student-facing material and light files

```text
Refactor material and light configuration so that students can change visible rendering properties in one or two obvious files.

Target files:
- `scene/Material.java`
- `scene/Light.java`

Requirements:

1. Material should expose readable fields such as:
   - baseColor
   - ambient
   - diffuse
   - specular
   - shininess
   - rimStrength

2. Light should expose readable fields such as:
   - color
   - direction or position
   - intensity
   - ambientIntensity

3. Add simple Spanish comments explaining what each value changes visually.

4. Keep compatibility with the existing UI if possible.
5. If UI compatibility is difficult, prioritize a clear code-based path for the practice.

6. Do not add dependencies.

Done when:
- Changing values in `Material.java` visibly changes the object.
- Changing values in `Light.java` visibly changes the object.
- The project compiles.
```

---

# Phase 5 — Extract shading into `renderer/Shader.java`

```text
Extract the lighting calculation from the rasterizer/UI class into a dedicated shader class.

Target:
- `renderer/Shader.java`

Optional:
- `renderer/PhongShader.java`

Requirements:

1. The shader should shade one fragment/pixel.
2. It should receive, directly or through a small object:
   - position if available,
   - normal,
   - material,
   - light,
   - camera/view direction if available,
   - depth if useful.

3. Move the existing Phong-style lighting calculation into this shader.
4. Add simple comments for:
   - ambient term,
   - diffuse term,
   - specular term,
   - rim term if added.

5. Keep the visual result similar to the previous raster mode.
6. Do not modify OBJ loading or projection in this phase.
7. Do not add dependencies.

Done when:
- Raster mode still works.
- The main lighting formula is localized in `renderer/Shader.java`.
- The project compiles.
```

---

# Phase 6 — Add render modes

```text
Add several simple render modes that can be toggled at runtime.

Create:
- `renderer/RenderMode.java`

Required modes:

- WIREFRAME
- PHONG
- TOON
- CLAY
- NORMALS
- DEPTH

Keyboard shortcuts:

- 1 = WIREFRAME
- 2 = PHONG
- 3 = TOON
- 4 = CLAY
- 5 = NORMALS
- 6 = DEPTH

Requirements:

1. PHONG should use the extracted existing shader.
2. TOON should quantize diffuse lighting into a few bands.
3. CLAY should give a pleasant grey/clay/matcap-like look without loading textures.
4. NORMALS should visualize normal directions as colors.
5. DEPTH should visualize the z-buffer as grayscale.
6. Show the current mode in the window title or with a small overlay.
7. Do not add dependencies.

Done when:
- All modes can be toggled.
- The viewer remains interactive with small models.
- The project compiles.
```

---

# Phase 7 — Add fake CPU AO and other postprocessing effects

```text
Add a simple CPU postprocessing stage.

Target:
- `renderer/PostProcess.java`

Effects:

1. Vignette
   - Slightly darkens image borders.

2. Fake AO / screen-space depth darkening
   - Uses neighboring depth differences.
   - It is approximate.
   - Do not call it real SSAO in comments or UI.

3. Outline
   - Uses depth and/or normal discontinuities to darken edges.

Keyboard shortcuts:

- V toggles vignette
- A toggles fake AO
- O toggles outline

Requirements:

1. Operate on `BufferedImage` / `int[] pixels` and render buffers.
2. Keep kernels small.
3. Keep code simple.
4. Add constants at the top of `PostProcess.java`:
   - `ENABLE_VIGNETTE`
   - `ENABLE_FAKE_AO`
   - `ENABLE_OUTLINE`
   - `AO_STRENGTH`
   - `AO_RADIUS_PIXELS`
   - `AO_BIAS`
   - `AO_QUALITY`
   - `OUTLINE_STRENGTH`
   - `VIGNETTE_STRENGTH`

5. Do not add dependencies.
6. Avoid slow algorithms with large sample counts.
7. Avoid per-pixel object allocation.
8. Use flat arrays in hot loops.

Suggested AO quality levels:

```java
public enum AOQuality {
    OFF,
    LOW,     // 4 samples
    MEDIUM,  // 8 samples
    HIGH     // 16 samples
}
```

Suggested AO idea:

```text
For each visible pixel:
  read current depth
  sample a few neighboring pixels
  if a neighbor is closer than the current pixel by more than AO_BIAS,
      increase occlusion
  convert occlusion count to a darkening factor
  multiply the color by that factor
```

Important:
- Depending on the renderer depth convention, the comparison sign may need flipping.
- Tune empirically.
- Prefer a visible and stable effect over physical correctness.

Spanish explanation for comments/student guide:
- "sombreado de contacto aproximado usando el z-buffer"
- "no es SSAO físico; es una aproximación visual barata"

Done when:
- Effects visibly change the render.
- Effects can be toggled.
- Fake AO is visibly useful on the default model.
- The viewer remains usable at 640x480 or 800x600 with LOW/MEDIUM AO.
- The project compiles.
```

---

# Phase 8 — Add screenshot export

```text
Add a screenshot/export function.

Requirements:

1. Pressing S saves the current render as a PNG.
2. Use standard Java ImageIO.
3. Save to:
   `screenshots/render_YYYYMMDD_HHMMSS.png`
4. Create the screenshots folder if needed.
5. Print a console message when the screenshot is saved.
6. Do not add dependencies.

Done when:
- Pressing S saves a PNG.
- The app continues running.
- The file can be opened normally.
```

---

# Phase 9 — Add student guide

```text
Create a student-facing guide in Spanish.

Add:
- `STUDENT_GUIDE.md`

The guide should explain:

1. How to run the project.
2. Which files students are expected to edit:
   - `scene/Material.java`
   - `scene/Light.java`
   - `renderer/Shader.java`
   - `renderer/PostProcess.java`

3. Which files they should not edit.
4. Keyboard controls.
5. Short tasks:
   - change material color,
   - disable specular,
   - increase shininess,
   - enable/disable rim lighting,
   - switch between Phong, Toon, Clay, Normals and Depth,
   - enable/disable fake AO,
   - enable/disable outline,
   - export a screenshot,
   - compare two render modes.

6. Include this exact idea:
   "No es necesario entender todo el visualizador. En esta práctica trabajaremos solo con algunas clases concretas."

7. Include questions about:
   - packages,
   - classes,
   - attributes,
   - methods,
   - parameters,
   - return types,
   - projection,
   - z-buffer,
   - material,
   - light,
   - ambient/diffuse/specular terms,
   - fake AO as an approximate z-buffer effect.

Use accessible Spanish for architecture students.

Do not modify rendering code in this phase unless needed for documentation accuracy.
```

---

# Phase 10 — Teacher notes

```text
Create `TEACHER_NOTES.md`.

The notes should explain:

1. Why this project intentionally avoids OpenGL, LWJGL, JOGL, ImGui, JavaFX, Maven and Gradle.
2. Why Swing + BufferedImage is enough for this practice.
3. Why the project uses fake CPU AO instead of real GPU SSAO.
4. Which files are suitable for student modification.
5. Which files are infrastructure.
6. Which effects are approximate:
   - fake AO,
   - clay/matcap-like shading,
   - outline.
7. Suggested grading checklist.
8. Common troubleshooting:
   - model does not load,
   - black screen,
   - Java version problem,
   - screenshot folder not created,
   - render mode keys do not work.

Use Spanish or English, whichever fits the repository style.
```

---

# Suggested first prompt to Codex

Use this after adding `AGENTS.md`:

```text
Read AGENTS.md and inspect the existing Java visualizer project.

Do not modify code yet.

I want to modernize this old teaching renderer for architecture students. The final project must remain plain Java, dependency-free, easy to import in Eclipse/IntelliJ, and based on Swing + BufferedImage.

Do not add OpenGL, LWJGL, JOGL, JavaFX, ImGui, Maven, Gradle, native libraries, GLFW, or shader files.

For this first task, produce a short report with:

1. Current package structure.
2. Main class / entry point.
3. Whether the project currently uses any external dependencies.
4. Whether it uses OpenGL or any graphics binding.
5. Classes responsible for OBJ loading, projection, rasterization, z-buffer, material/light, and UI.
6. Which files are most coupled or risky to modify.
7. A safe incremental refactor plan.

Do not change files in this task.
```

Then give Codex one phase at a time.
