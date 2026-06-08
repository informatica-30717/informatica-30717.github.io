"""Build the Practice 8 point-cloud Blender scene and static renders."""

from __future__ import annotations

import math
from pathlib import Path

import bpy
from mathutils import Vector

from add_sparsity_levels import add_controlled_ball_modifier, look_at, make_material


REPO_ROOT = Path(__file__).resolve().parents[3]
MODELS = REPO_ROOT / "resources" / "practica8" / "models"
IMAGES = REPO_ROOT / "resources" / "practica8" / "images"
BLENDER_DIR = REPO_ROOT / "resources" / "practica8" / "blender"
SCENE_PATH = BLENDER_DIR / "practica8_point_clouds.blend"

CLOUDS = [
    ("Goya point cloud", MODELS / "goya_point_cloud.ply", Vector((-1.05, 0.0, 0.0)), 0.0045, (0.86, 0.56, 0.31, 1.0)),
    ("Room point cloud", MODELS / "room_500k.ply", Vector((1.05, 0.0, 0.0)), 0.0038, (0.40, 0.72, 1.0, 1.0)),
]


def reset_scene() -> None:
    bpy.ops.object.select_all(action="SELECT")
    bpy.ops.object.delete()
    scene = bpy.context.scene
    scene.render.engine = "BLENDER_EEVEE_NEXT"
    scene.eevee.taa_render_samples = 64
    scene.render.resolution_x = 1800
    scene.render.resolution_y = 1100
    scene.world = bpy.data.worlds.new("Practice 8 dark world")
    scene.world.color = (0.015, 0.021, 0.035)
    scene.view_settings.view_transform = "Filmic"
    scene.view_settings.look = "Medium High Contrast"


def import_ply(path: Path) -> bpy.types.Object:
    before = set(bpy.data.objects)
    if hasattr(bpy.ops.wm, "ply_import"):
        bpy.ops.wm.ply_import(filepath=str(path))
    else:
        bpy.ops.import_mesh.ply(filepath=str(path))
    imported = [obj for obj in bpy.data.objects if obj not in before]
    if not imported:
        raise RuntimeError(f"No object imported from {path}")
    return imported[0]


def normalize_vertices(obj: bpy.types.Object) -> None:
    vertices = obj.data.vertices
    minimum = Vector((math.inf, math.inf, math.inf))
    maximum = Vector((-math.inf, -math.inf, -math.inf))
    for vertex in vertices:
        co = vertex.co
        minimum.x = min(minimum.x, co.x)
        minimum.y = min(minimum.y, co.y)
        minimum.z = min(minimum.z, co.z)
        maximum.x = max(maximum.x, co.x)
        maximum.y = max(maximum.y, co.y)
        maximum.z = max(maximum.z, co.z)

    center = (minimum + maximum) * 0.5
    size = maximum - minimum
    scale = 1.55 / max(size.x, size.y, size.z)
    for vertex in vertices:
        vertex.co = (vertex.co - center) * scale
    obj.data.update()


def add_lighting() -> None:
    bpy.ops.object.light_add(type="AREA", location=(0.0, -2.6, 3.0))
    key = bpy.context.object
    key.name = "Large soft key light"
    key.data.energy = 620
    key.data.size = 4.5
    bpy.ops.object.light_add(type="POINT", location=(-2.4, 1.4, 1.8))
    fill = bpy.context.object
    fill.name = "Warm fill light"
    fill.data.energy = 90
    fill.data.color = (1.0, 0.78, 0.55)
    bpy.ops.object.light_add(type="POINT", location=(2.5, 1.1, 1.5))
    rim = bpy.context.object
    rim.name = "Cool rim light"
    rim.data.energy = 120
    rim.data.color = (0.55, 0.72, 1.0)


def add_floor() -> None:
    bpy.ops.mesh.primitive_plane_add(size=4.9, location=(0, 0, -0.84))
    floor = bpy.context.object
    floor.name = "matte dark floor"
    floor.data.materials.append(make_material("matte graphite", (0.025, 0.032, 0.045, 1.0)))


def add_camera(name: str, location: Vector, target: Vector, lens: float) -> bpy.types.Object:
    data = bpy.data.cameras.new(name)
    data.lens = lens
    camera = bpy.data.objects.new(name, data)
    bpy.context.collection.objects.link(camera)
    camera.location = location
    look_at(camera, target)
    return camera


def render(camera: bpy.types.Object, output: Path) -> None:
    bpy.context.scene.camera = camera
    bpy.context.scene.render.filepath = str(output)
    bpy.ops.render.render(write_still=True)


def main() -> None:
    BLENDER_DIR.mkdir(parents=True, exist_ok=True)
    IMAGES.mkdir(parents=True, exist_ok=True)
    bpy.context.preferences.filepaths.save_version = 0
    reset_scene()

    for name, path, location, radius, color in CLOUDS:
        obj = import_ply(path)
        obj.name = name
        obj.data.name = f"{name} mesh"
        normalize_vertices(obj)
        obj.location = location
        add_controlled_ball_modifier(obj, make_material(f"{name} material", color), radius, 1.0, 101)

    add_floor()
    add_lighting()
    cameras = {
        "overview": add_camera("Camera overview", Vector((0.0, -3.45, 1.35)), Vector((0.0, 0.0, -0.05)), 46.0),
        "goya": add_camera("Camera Goya", Vector((-1.05, -2.35, 1.05)), Vector((-1.05, 0.0, 0.02)), 58.0),
        "room": add_camera("Camera room", Vector((1.05, -2.15, 0.95)), Vector((1.05, 0.0, 0.0)), 52.0),
    }

    bpy.ops.wm.save_as_mainfile(filepath=str(SCENE_PATH), check_existing=False)
    render(cameras["overview"], IMAGES / "point_clouds_blender_overview.png")
    render(cameras["goya"], IMAGES / "point_cloud_goya_blender.png")
    render(cameras["room"], IMAGES / "point_cloud_room_blender.png")


if __name__ == "__main__":
    main()
