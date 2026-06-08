"""Add density and point-size controls to the Practice 8 point-cloud scene.

This script preserves existing cameras/lights by reading a base .blend and
writing a derivative scene with comparison objects at 100%, 25%, and 7% density.
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

import bpy
from mathutils import Vector


REPO_ROOT = Path(__file__).resolve().parents[3]
BLENDER_DIR = REPO_ROOT / "resources" / "practica8" / "blender"
IMAGES = REPO_ROOT / "resources" / "practica8" / "images"
DEFAULT_INPUT = BLENDER_DIR / "practica8_point_clouds.blend"
DEFAULT_OUTPUT = BLENDER_DIR / "practica8_point_clouds_sparsity.blend"

DENSITY_LEVELS = [
    ("100 pct", 1.0, 0.0042, -1.30),
    ("25 pct", 0.25, 0.0064, 0.0),
    ("7 pct", 0.07, 0.0105, 1.30),
]

CLOUDS = [
    ("Goya point cloud", "Goya sparsity comparison", "point_cloud_goya_sparsity.png", (0.86, 0.56, 0.31, 1.0)),
    ("Room point cloud", "Room sparsity comparison", "point_cloud_room_sparsity.png", (0.40, 0.72, 1.0, 1.0)),
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", type=Path, default=DEFAULT_INPUT)
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    parser.add_argument("--no-render", action="store_true")
    return parser.parse_args(sys.argv[sys.argv.index("--") + 1 :] if "--" in sys.argv else [])


def make_material(name: str, color: tuple[float, float, float, float]) -> bpy.types.Material:
    material = bpy.data.materials.get(name) or bpy.data.materials.new(name)
    material.diffuse_color = color
    material.use_nodes = True
    bsdf = material.node_tree.nodes.get("Principled BSDF")
    if bsdf is not None:
        if "Base Color" in bsdf.inputs:
            bsdf.inputs["Base Color"].default_value = color
        if "Roughness" in bsdf.inputs:
            bsdf.inputs["Roughness"].default_value = 0.55
    return material


def clear_modifiers(obj: bpy.types.Object) -> None:
    for modifier in list(obj.modifiers):
        obj.modifiers.remove(modifier)


def add_controlled_ball_modifier(
    obj: bpy.types.Object,
    material: bpy.types.Material,
    radius: float,
    density: float,
    seed: int,
) -> None:
    clear_modifiers(obj)
    modifier = obj.modifiers.new("Ball per point - density controls", "NODES")
    group = bpy.data.node_groups.new(f"{obj.name} density controls", "GeometryNodeTree")
    modifier.node_group = group

    geometry_in = group.interface.new_socket("Geometry", in_out="INPUT", socket_type="NodeSocketGeometry")
    radius_in = group.interface.new_socket("Ball Radius", in_out="INPUT", socket_type="NodeSocketFloat")
    radius_in.default_value = radius
    radius_in.min_value = 0.0005
    radius_in.max_value = 0.04
    density_in = group.interface.new_socket("Density", in_out="INPUT", socket_type="NodeSocketFloat")
    density_in.default_value = density
    density_in.min_value = 0.0
    density_in.max_value = 1.0
    seed_in = group.interface.new_socket("Seed", in_out="INPUT", socket_type="NodeSocketInt")
    seed_in.default_value = seed
    seed_in.min_value = 0
    seed_in.max_value = 10000
    group.interface.new_socket("Geometry", in_out="OUTPUT", socket_type="NodeSocketGeometry")

    nodes = group.nodes
    links = group.links
    nodes.clear()
    group_in = nodes.new("NodeGroupInput")
    group_in.location = (-900, 0)
    mesh_to_points = nodes.new("GeometryNodeMeshToPoints")
    mesh_to_points.location = (-650, 0)
    random = nodes.new("FunctionNodeRandomValue")
    random.location = (-650, -250)
    random.data_type = "BOOLEAN"
    index = nodes.new("GeometryNodeInputIndex")
    index.location = (-900, -320)
    invert = nodes.new("FunctionNodeBooleanMath")
    invert.location = (-420, -130)
    invert.operation = "NOT"
    delete = nodes.new("GeometryNodeDeleteGeometry")
    delete.location = (-220, 0)
    ico = nodes.new("GeometryNodeMeshIcoSphere")
    ico.location = (-220, -280)
    ico.inputs["Subdivisions"].default_value = 1
    instance = nodes.new("GeometryNodeInstanceOnPoints")
    instance.location = (40, 0)
    set_material = nodes.new("GeometryNodeSetMaterial")
    set_material.location = (300, 0)
    set_material.inputs["Material"].default_value = material
    group_out = nodes.new("NodeGroupOutput")
    group_out.location = (540, 0)

    links.new(group_in.outputs[geometry_in.identifier], mesh_to_points.inputs["Mesh"])
    links.new(group_in.outputs[radius_in.identifier], mesh_to_points.inputs["Radius"])
    links.new(group_in.outputs[density_in.identifier], random.inputs["Probability"])
    links.new(group_in.outputs[seed_in.identifier], random.inputs["Seed"])
    links.new(index.outputs["Index"], random.inputs["ID"])
    links.new(next(output for output in random.outputs if output.type == "BOOLEAN"), invert.inputs["Boolean"])
    links.new(mesh_to_points.outputs["Points"], delete.inputs["Geometry"])
    links.new(invert.outputs["Boolean"], delete.inputs["Selection"])
    links.new(group_in.outputs[radius_in.identifier], ico.inputs["Radius"])
    links.new(delete.outputs["Geometry"], instance.inputs["Points"])
    links.new(ico.outputs["Mesh"], instance.inputs["Instance"])
    links.new(instance.outputs["Instances"], set_material.inputs["Geometry"])
    links.new(set_material.outputs["Geometry"], group_out.inputs["Geometry"])

    modifier[radius_in.identifier] = radius
    modifier[density_in.identifier] = density
    modifier[seed_in.identifier] = seed


def collection_for(name: str) -> bpy.types.Collection:
    collection = bpy.data.collections.get(name)
    if collection is None:
        collection = bpy.data.collections.new(name)
        bpy.context.scene.collection.children.link(collection)
    else:
        for obj in list(collection.objects):
            bpy.data.objects.remove(obj, do_unlink=True)
    return collection


def add_comparison_objects(base_name: str, collection_name: str, color: tuple[float, float, float, float]) -> None:
    base = bpy.data.objects.get(base_name)
    if base is None:
        raise RuntimeError(f"Missing base object: {base_name}")
    collection = collection_for(collection_name)
    material = make_material(f"{base_name} sparsity material", color)

    for index, (label, density, radius, x_offset) in enumerate(DENSITY_LEVELS):
        obj = base.copy()
        obj.data = base.data
        obj.animation_data_clear()
        obj.name = f"{base_name} {label}"
        obj.location = (x_offset, 0.0, 0.0)
        obj.rotation_euler = (0.0, 0.0, 0.0)
        obj.scale = (1.0, 1.0, 1.0)
        collection.objects.link(obj)
        add_controlled_ball_modifier(obj, material, radius, density, 137 + index)


def look_at(obj: bpy.types.Object, target: Vector) -> None:
    direction = target - obj.location
    obj.rotation_euler = direction.to_track_quat("-Z", "Y").to_euler()


def ensure_camera() -> bpy.types.Object:
    camera = bpy.data.objects.get("Camera sparsity comparison")
    if camera is None:
        data = bpy.data.cameras.new("Camera sparsity comparison")
        camera = bpy.data.objects.new("Camera sparsity comparison", data)
        bpy.context.scene.collection.objects.link(camera)
    camera.location = (0.0, -4.05, 1.05)
    look_at(camera, Vector((0.0, 0.0, -0.05)))
    camera.data.type = "ORTHO"
    camera.data.ortho_scale = 2.9
    camera.data.dof.use_dof = False
    return camera


def set_render_settings() -> None:
    scene = bpy.context.scene
    scene.render.engine = "BLENDER_EEVEE_NEXT"
    scene.eevee.taa_render_samples = 64
    scene.render.resolution_x = 1800
    scene.render.resolution_y = 1000
    scene.view_settings.view_transform = "Filmic"
    scene.view_settings.look = "Medium High Contrast"


def render_collection(collection_name: str, camera: bpy.types.Object, output: Path) -> None:
    previous = {obj.name: obj.hide_render for obj in bpy.data.objects}
    try:
        for obj in bpy.data.objects:
            obj.hide_render = obj.type not in {"CAMERA", "LIGHT"}
        for obj in bpy.data.collections[collection_name].objects:
            obj.hide_render = False
        floor = bpy.data.objects.get("matte dark floor")
        if floor is not None:
            floor.hide_render = False
        bpy.context.scene.camera = camera
        bpy.context.scene.render.filepath = str(output)
        bpy.ops.render.render(write_still=True)
    finally:
        for obj in bpy.data.objects:
            if obj.name in previous:
                obj.hide_render = previous[obj.name]


def main() -> None:
    args = parse_args()
    if not args.input.exists():
        raise FileNotFoundError(args.input)
    bpy.ops.wm.open_mainfile(filepath=str(args.input))
    bpy.context.preferences.filepaths.save_version = 0
    set_render_settings()

    for base_name, collection_name, _render_name, color in CLOUDS:
        add_comparison_objects(base_name, collection_name, color)

    camera = ensure_camera()
    args.output.parent.mkdir(parents=True, exist_ok=True)
    bpy.ops.wm.save_as_mainfile(filepath=str(args.output), check_existing=False)

    if not args.no_render:
        IMAGES.mkdir(parents=True, exist_ok=True)
        for _base_name, collection_name, render_name, _color in CLOUDS:
            render_collection(collection_name, camera, IMAGES / render_name)

    print(f"Saved sparsity scene: {args.output}")


if __name__ == "__main__":
    main()
