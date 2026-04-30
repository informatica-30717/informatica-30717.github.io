# Teacher notes — Java teaching renderer modernization

## Rationale

The goal is not to teach a modern GPU graphics API. The goal is to keep the existing pedagogical value of the old Java visualizer while making the output more visually engaging for architecture students.

The original practice already covers useful concepts:

- package organization,
- Java classes,
- object-oriented programming,
- OBJ loading,
- camera and projection,
- transformation matrices,
- z-buffer,
- Phong-style lighting,
- material and light parameters.

The modernization should preserve these ideas while reducing friction.

## Why not OpenGL / LWJGL / JOGL / ImGui?

They would make the project harder to import and run.

They may require:

- extra JARs,
- native libraries,
- VM arguments,
- platform-specific configuration,
- driver-related issues,
- Maven or Gradle,
- additional IDE configuration.

For a short informatics practice with architecture students, this setup cost is not worth it.

## Why Swing + BufferedImage?

Swing and `BufferedImage` are enough for this practice because the goal is visual engagement, not high-performance rendering.

A CPU renderer can still show:

- wireframe,
- Phong or Blinn-Phong,
- toon shading,
- clay/matcap-like shading,
- normal visualization,
- depth visualization,
- fake ambient occlusion,
- outlines,
- vignette.

The most important technical improvement over the old project is to stop drawing individual pixels as Java2D rectangles and instead render into a `BufferedImage`.

## Fake AO vs real SSAO

Real SSAO is a GPU-oriented screen-space effect with sample kernels, depth reconstruction, normals, noise patterns, and blur passes.

For this practice, we only need a CPU-friendly approximation:

- read the z-buffer,
- compare neighboring depth values,
- darken pixels near likely contact/depth discontinuity regions.

This should be called:

- fake AO,
- approximate ambient occlusion,
- screen-space depth darkening,
- sombreado de contacto aproximado usando el z-buffer.

Do not present it as physically correct SSAO.

## Student-facing files

The preferred student-editable files are:

- `scene/Material.java`
- `scene/Light.java`
- `renderer/Shader.java`
- `renderer/PostProcess.java`

The practice should tell students explicitly that they do not need to understand the entire renderer.

Suggested sentence:

> No es necesario entender todo el visualizador. En esta práctica trabajaremos solo con algunas clases concretas.

## Infrastructure files

The following areas should mostly remain infrastructure:

- OBJ loader,
- matrix classes,
- rasterizer inner loops,
- render buffers,
- Swing window management,
- screenshot export.

Students may inspect these files, but they should not be the main editing target.

## Suggested grading checklist

Possible low-friction grading items:

- screenshot of the project running,
- screenshot of Phong mode,
- screenshot of Toon or Clay mode,
- screenshot with fake AO enabled/disabled,
- modified material color,
- modified specular or shininess value,
- short explanation of z-buffer,
- short explanation of ambient/diffuse/specular terms,
- identification of packages/classes/attributes/methods,
- explanation of which files were modified.

## Common troubleshooting

### Model does not load

Check that the model path is relative to the project root or packaged resources folder. Print a clear error message if the default OBJ is missing.

### Black screen

Common causes:

- camera is inside or behind the object,
- model scale is too large/small,
- z-buffer initialized incorrectly,
- light intensity or material color is zero,
- all render modes disabled by mistake.

### Java version problem

Keep the project compatible with a common JDK version used in the course. Avoid unnecessary modern Java features if students use older IDE installations.

### Screenshot folder not created

Use `Files.createDirectories(...)` or equivalent before saving.

### Render mode keys do not work

Check keyboard focus. The `JPanel` may need:

```java
setFocusable(true);
requestFocusInWindow();
```

or key bindings instead of a raw key listener.

## Recommended development order

1. Inspect project.
2. Ensure clean startup.
3. Move rendering to `BufferedImage`.
4. Centralize render buffers.
5. Extract shader.
6. Add render modes.
7. Add fake AO / outline / vignette.
8. Add screenshot export.
9. Write student guide.
10. Write teacher notes.
