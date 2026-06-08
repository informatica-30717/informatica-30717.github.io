# Practice 8 Point-Cloud Blender Scene

`practica8_point_clouds.blend` contains the two point clouds used by the web
viewer. The scene uses Geometry Nodes so points render as instanced low-poly
spheres instead of duplicated mesh data.

## Regenerate The Base Scene

From the repository root:

```powershell
& 'C:\Program Files\Blender Foundation\Blender 4.4\blender.exe' -b --python resources\practica8\blender\render_point_clouds.py
```

This recreates `practica8_point_clouds.blend` and the overview/detail PNG
renders. It starts from scratch, so do not use it after manually adjusting
cameras unless you first save those changes elsewhere.

## Add Sparsity Controls Without Touching Cameras

From the repository root:

```powershell
& 'C:\Program Files\Blender Foundation\Blender 4.4\blender.exe' -b --python resources\practica8\blender\add_sparsity_levels.py
```

This reads `practica8_point_clouds.blend`, preserves existing cameras/lights,
and writes `practica8_point_clouds_sparsity.blend`.

## Change Point Size Or Sparsity In Blender

1. Open `practica8_point_clouds_sparsity.blend`.
2. Select one of the comparison objects, such as `Goya point cloud 25 pct`.
3. Open the Modifier properties tab.
4. In `Ball per point - density controls`, adjust:
   - `Ball Radius`: visual point size.
   - `Density`: fraction of points kept, from `0.0` to `1.0`.
   - `Seed`: random sampling seed.

The comparison scene includes 100%, 25%, and 7% density examples for both point
clouds.
