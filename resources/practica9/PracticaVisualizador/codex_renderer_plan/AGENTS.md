# AGENTS.md

## Project goal

This repository contains a Java teaching renderer for second-year architecture students.

Students are not graphics programmers. They are architecture students taking an informatics course. The renderer should produce attractive visual results, but the student-facing programming work must be small, localized, and easy to understand.

The final viewer should feel more modern than the old Java2D visualizer, but it must remain easy to import and run.

## Hard constraints

- Use plain Java.
- Use Swing and `BufferedImage`.
- Keep the project dependency-free.
- Do not add Maven.
- Do not add Gradle.
- Do not add JavaFX.
- Do not add OpenGL.
- Do not add LWJGL.
- Do not add JOGL.
- Do not add ImGui or imgui-java.
- Do not add GLFW.
- Do not add native libraries.
- Do not add shader files.
- Do not fetch assets from the internet.
- Do not require students to configure VM native library paths.
- Do not require students to install anything beyond a normal JDK and IDE.

## Rendering approach

This is a CPU software renderer.

The renderer may use:

- OBJ loading.
- Matrices.
- Projection.
- Z-buffer.
- Wireframe rendering.
- Triangle rasterization.
- Per-pixel or per-fragment shading.
- Postprocessing on a `BufferedImage`.

The renderer should not try to become a real modern GPU renderer.

Approximate visual effects are acceptable if they look good and are easy to explain.

## Desired visual features

Add visually attractive but simple CPU effects:

- Phong or Blinn-Phong shading.
- Toon shading.
- Clay or matcap-like shading without textures.
- Rim lighting.
- Normal visualization.
- Depth visualization.
- Wireframe overlay.
- Fake ambient occlusion using depth differences.
- Outline using depth/normal discontinuities.
- Vignette.

Fake effects must be named clearly as fake or approximate. For example, use "fake AO" or "screen-space depth darkening" instead of claiming it is production SSAO.

## CPU fake AO / SSAO policy

A production-quality SSAO implementation is not the goal. The desired effect is a simple CPU postprocess based on the z-buffer.

Acceptable implementation:

- Use flat arrays: `int[] pixels`, `double[] depth`, optionally normal buffers.
- Use a small fixed sample kernel: 4, 8, or 16 samples.
- Compare neighboring depth values with a bias and radius.
- Darken pixels when nearby samples appear to occlude them.
- Keep constants easy to edit.

Avoid:

- Large random kernels.
- Per-pixel object allocations.
- `Color` objects per pixel.
- `double[][]` buffers for hot loops.
- Claiming the effect is physically correct.

Recommended UI wording:

- `Fake AO`
- `Depth AO`
- `Screen-space depth darkening`

Spanish wording for the practice:

- `oclusión ambiental aproximada`
- `sombreado de contacto aproximado usando el z-buffer`

## Student-facing editable files

Students should mainly interact with these files:

- `scene/Material.java`
- `scene/Light.java`
- `renderer/Shader.java`
- `renderer/PostProcess.java`

Everything else can be treated as infrastructure.

The practice statement should explicitly say:

> No es necesario entender todo el visualizador. En esta práctica trabajaremos solo con algunas clases concretas.

## Code style

- Keep code readable for beginners.
- Prefer simple classes.
- Avoid clever abstractions.
- Avoid excessive generics.
- Avoid concurrency.
- Avoid premature optimization.
- Avoid long methods when easy to split.
- Use clear names.
- Prefer Spanish comments in student-facing files.
- English comments are acceptable in infrastructure code.

## Verification

After every code change:

- Compile the project.
- Run the main class if possible.
- Confirm the viewer opens.
- Confirm no external dependencies were added.
- Mention exactly how the change was verified.

## Important warning

Do not replace the renderer with OpenGL, LWJGL, JOGL, JavaFX, ImGui, or any GPU API. This would defeat the teaching goal.
