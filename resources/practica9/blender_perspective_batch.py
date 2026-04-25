"""Batch helper to vary Blender camera FOV while preserving framing.

The script changes one or more perspective cameras while leaving the rest of
the scene untouched. It keeps each camera orientation, measures a reference
depth in front of the camera, and moves the camera only along its current
viewing direction so the framing stays comparable when the FOV changes. Dolly
travel is also clamped against nearby geometry so interior cameras do not walk
through walls or drift outside a small room.

Usage examples:

	blender scene.blend --python resources/practica9/blender_perspective_batch.py -- \
		--fovs 30 45 70 --render --output-dir //renders

	blender scene.blend --python resources/practica9/blender_perspective_batch.py -- \
		--fovs 35 55 85 --target-object Cube

	blender scene.blend --python resources/practica9/blender_perspective_batch.py -- \
		--fovs 35 55 85 --camera Camera_A Camera_B --render --output-dir //renders

	blender scene.blend --python resources/practica9/blender_perspective_batch.py -- \
		--fovs 35 55 85 --all-cameras

Reference point priority:
1. ``--target-object``
2. A single selected non-camera object
3. The active non-camera object
4. The depth-of-field focus object
5. The 3D cursor if ``--use-3d-cursor`` is set
6. The first center-view geometry hit, if any
7. A point ``--focus-distance`` meters in front of the camera
"""

from __future__ import annotations

import argparse
import math
from pathlib import Path
import sys

import bpy
from mathutils import Vector


def parse_args(argv: list[str]) -> argparse.Namespace:
	parser = argparse.ArgumentParser(description=__doc__)
	parser.add_argument(
		"--fovs",
		nargs="+",
		type=float,
		required=True,
		help="Target field-of-view values in degrees.",
	)
	parser.add_argument(
		"--camera",
		nargs="+",
		action="append",
		dest="camera_names",
		help="Camera object name(s). Can be supplied more than once. Defaults to selected cameras, else the scene camera.",
	)
	parser.add_argument(
		"--all-cameras",
		action="store_true",
		help="Process all perspective cameras in the scene.",
	)
	parser.add_argument(
		"--target-object",
		help="Object to keep framed. Uses its world-space bounding-box center.",
	)
	parser.add_argument(
		"--focus-distance",
		type=float,
		help="Fallback distance in meters if no target object can be inferred.",
	)
	parser.add_argument(
		"--max-dolly-distance",
		type=float,
		default=None,
		help="Maximum camera travel in meters from the original position for each FOV. Defaults to an automatic interior-safe clamp.",
	)
	parser.add_argument(
		"--clearance-margin",
		type=float,
		default=0.2,
		help="Minimum clearance to preserve from hit geometry when auto-limiting dolly travel.",
	)
	parser.add_argument(
		"--use-3d-cursor",
		action="store_true",
		help="Use the 3D cursor as the framing reference point.",
	)
	parser.add_argument(
		"--render",
		action="store_true",
		help="Render a still image for each FOV.",
	)
	parser.add_argument(
		"--output-dir",
		default="//",
		help="Output directory for renders when --render is used. Blender // paths are supported.",
	)
	parser.add_argument(
		"--prefix",
		default="perspective",
		help="Filename prefix used with --render.",
	)
	parser.add_argument(
		"--keep-last-view",
		action="store_true",
		help="Keep the final camera transform after batch rendering instead of restoring the original view.",
	)
	return parser.parse_args(argv)


def world_bbox_center(obj: bpy.types.Object) -> Vector:
	if not getattr(obj, "bound_box", None):
		return obj.matrix_world.translation.copy()
	corners = [obj.matrix_world @ Vector(corner) for corner in obj.bound_box]
	return sum(corners, Vector((0.0, 0.0, 0.0))) / len(corners)


def camera_forward(camera_obj: bpy.types.Object) -> Vector:
	return (camera_obj.matrix_world.to_quaternion() @ Vector((0.0, 0.0, -1.0))).normalized()


def raycast_target(scene: bpy.types.Scene, camera_obj: bpy.types.Object) -> Vector | None:
	depsgraph = bpy.context.evaluated_depsgraph_get()
	origin = camera_obj.matrix_world.translation.copy()
	forward = camera_forward(camera_obj)
	hit, location, _normal, _index, obj, _matrix = scene.ray_cast(depsgraph, origin, forward)
	if hit and obj is not None and obj != camera_obj:
		return location.copy()
	return None


def raycast_distance(
	scene: bpy.types.Scene,
	origin: Vector,
	direction: Vector,
	ignore_obj: bpy.types.Object | None = None,
) -> float | None:
	depsgraph = bpy.context.evaluated_depsgraph_get()
	direction = direction.normalized()
	hit, location, _normal, _index, obj, _matrix = scene.ray_cast(depsgraph, origin, direction)
	if hit and obj is not None and obj != ignore_obj:
		return (location - origin).length
	return None


def validate_perspective_camera(camera_obj: bpy.types.Object | None, label: str) -> bpy.types.Object:
	if camera_obj is None or camera_obj.type != "CAMERA":
		raise SystemExit(f"{label} is not a valid camera.")
	if camera_obj.data.type != "PERSP":
		raise SystemExit(f"Camera '{camera_obj.name}' is not perspective.")
	return camera_obj


def resolve_cameras(scene: bpy.types.Scene, args: argparse.Namespace) -> list[bpy.types.Object]:
	if args.all_cameras and args.camera_names:
		raise SystemExit("Use either --camera or --all-cameras, not both.")

	if args.camera_names:
		cameras = []
		seen = set()
		for batch in args.camera_names:
			for camera_name in batch:
				camera_obj = validate_perspective_camera(bpy.data.objects.get(camera_name), f"Camera '{camera_name}'")
				if camera_obj.name not in seen:
					cameras.append(camera_obj)
					seen.add(camera_obj.name)
		return cameras

	if args.all_cameras:
		cameras = [obj for obj in bpy.data.objects if obj.type == "CAMERA" and obj.data.type == "PERSP"]
		if not cameras:
			raise SystemExit("No perspective cameras were found in the scene.")
		return cameras

	selected_cameras = [
		obj for obj in bpy.context.selected_objects if obj.type == "CAMERA" and obj.data.type == "PERSP"
	]
	if selected_cameras:
		return selected_cameras

	if scene.camera is not None:
		return [validate_perspective_camera(scene.camera, "The scene camera")]

	perspective_cameras = [obj for obj in bpy.data.objects if obj.type == "CAMERA" and obj.data.type == "PERSP"]
	if len(perspective_cameras) == 1:
		return perspective_cameras
	if len(perspective_cameras) > 1:
		raise SystemExit(
			"Multiple perspective cameras were found. Select the cameras to process, pass --camera, or use --all-cameras."
		)
	raise SystemExit("No perspective cameras were found in the scene.")


def resolve_target(
	scene: bpy.types.Scene,
	view_layer: bpy.types.ViewLayer,
	camera_obj: bpy.types.Object,
	args: argparse.Namespace,
) -> tuple[Vector, str]:
	if args.target_object:
		obj = bpy.data.objects.get(args.target_object)
		if obj is None:
			raise SystemExit(f"Target object '{args.target_object}' was not found.")
		return world_bbox_center(obj), f"object '{obj.name}'"

	selected = [obj for obj in bpy.context.selected_objects if obj.type != "CAMERA"]
	if len(selected) == 1:
		return world_bbox_center(selected[0]), f"selected object '{selected[0].name}'"

	active = view_layer.objects.active
	if active is not None and active.type != "CAMERA":
		return world_bbox_center(active), f"active object '{active.name}'"

	dof = camera_obj.data.dof
	if dof.focus_object is not None:
		return world_bbox_center(dof.focus_object), f"focus object '{dof.focus_object.name}'"

	if args.use_3d_cursor:
		return scene.cursor.location.copy(), "3D cursor"

	hit_target = raycast_target(scene, camera_obj)
	if hit_target is not None:
		return hit_target, "first center-view geometry hit"

	focus_distance = args.focus_distance
	if focus_distance is None and dof.focus_distance > 0:
		focus_distance = dof.focus_distance
	if focus_distance is None:
		focus_distance = 5.0

	forward = camera_forward(camera_obj)
	target = camera_obj.matrix_world.translation + forward * focus_distance
	return target, f"{focus_distance:.3f} m along the current view direction"


def apply_fov(
	camera_obj: bpy.types.Object,
	original_location: Vector,
	base_angle: float,
	base_distance: float,
	fov_deg: float,
	max_forward_dolly: float,
	max_backward_dolly: float,
) -> float:
	new_angle = math.radians(fov_deg)
	forward = camera_forward(camera_obj)
	new_distance = base_distance * math.tan(base_angle / 2.0) / math.tan(new_angle / 2.0)
	# Preserve the original lateral placement and only move along the viewing axis.
	delta_distance = base_distance - new_distance
	if delta_distance >= 0.0:
		delta_distance = min(delta_distance, max_forward_dolly)
	else:
		delta_distance = max(delta_distance, -max_backward_dolly)
	camera_obj.location = original_location + forward * delta_distance
	camera_obj.data.angle = new_angle
	return delta_distance


def safe_name(name: str) -> str:
	return "".join(ch if ch.isalnum() or ch in "-_" else "_" for ch in name)


def render_path(output_dir: str, prefix: str, camera_name: str, fov_deg: float) -> str:
	output_root = Path(bpy.path.abspath(output_dir))
	output_root.mkdir(parents=True, exist_ok=True)
	file_name = f"{prefix}_{safe_name(camera_name)}_{int(round(fov_deg)):02d}deg.png"
	return str(output_root / file_name)


def main(argv: list[str]) -> None:
	args = parse_args(argv)
	scene = bpy.context.scene
	view_layer = bpy.context.view_layer
	camera_objects = resolve_cameras(scene, args)

	original_filepath = scene.render.filepath
	original_scene_camera = scene.camera

	for camera_obj in camera_objects:
		target, target_label = resolve_target(scene, view_layer, camera_obj, args)
		original_location = camera_obj.location.copy()
		original_angle = camera_obj.data.angle

		forward = camera_forward(camera_obj)
		base_distance = (target - original_location).dot(forward)
		if base_distance <= 0:
			raise SystemExit(
				f"Camera '{camera_obj.name}' inferred a target behind the camera. Use --target-object, --use-3d-cursor, or --focus-distance."
			)

		print(f"Using camera: {camera_obj.name}")
		print(f"Reference point: {target_label}")
		print(f"Base FOV: {math.degrees(original_angle):.2f} deg")
		print(f"Base distance to reference: {base_distance:.3f} m")
		auto_dolly_limit = args.max_dolly_distance
		if auto_dolly_limit is None:
			auto_dolly_limit = min(1.5, max(0.35, base_distance * 0.25))
		clearance_margin = max(0.0, args.clearance_margin)
		forward_clearance = raycast_distance(scene, original_location, forward, camera_obj)
		backward_clearance = raycast_distance(scene, original_location, -forward, camera_obj)
		max_forward_dolly = auto_dolly_limit
		if forward_clearance is not None:
			max_forward_dolly = min(max_forward_dolly, max(0.0, forward_clearance - clearance_margin))
		max_backward_dolly = min(auto_dolly_limit, 0.25)
		if backward_clearance is not None:
			max_backward_dolly = min(auto_dolly_limit, max(0.0, backward_clearance - clearance_margin))
		print(f"Forward clearance: {forward_clearance:.3f} m" if forward_clearance is not None else "Forward clearance: no hit")
		print(f"Backward clearance: {backward_clearance:.3f} m" if backward_clearance is not None else "Backward clearance: no hit")
		print(f"Max forward dolly: {max_forward_dolly:.3f} m")
		print(f"Max backward dolly: {max_backward_dolly:.3f} m")

		for fov_deg in args.fovs:
			delta_distance = apply_fov(
				camera_obj,
				original_location,
				original_angle,
				base_distance,
				fov_deg,
				max_forward_dolly,
				max_backward_dolly,
			)
			view_layer.update()
			print(
				f"Applied FOV {fov_deg:.2f} deg on '{camera_obj.name}' -> camera at "
				f"({camera_obj.location.x:.3f}, {camera_obj.location.y:.3f}, {camera_obj.location.z:.3f}), "
				f"dolly {delta_distance:+.3f} m"
			)
			if args.render:
				scene.camera = camera_obj
				scene.render.filepath = render_path(args.output_dir, args.prefix, camera_obj.name, fov_deg)
				bpy.ops.render.render(write_still=True)
				print(f"Rendered {scene.render.filepath}")

		if args.render and not args.keep_last_view:
			camera_obj.location = original_location
			camera_obj.data.angle = original_angle
			view_layer.update()

	if args.render:
		scene.render.filepath = original_filepath
		if not args.keep_last_view:
			scene.camera = original_scene_camera
			print("Original camera views restored.")


if __name__ == "__main__":
	extra_args = sys.argv[sys.argv.index("--") + 1 :] if "--" in sys.argv else []
	main(extra_args)
