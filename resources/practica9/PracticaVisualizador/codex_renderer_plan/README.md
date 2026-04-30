# Codex renderer modernization plan

This folder contains files to guide a Codex agent when modernizing the old Java teaching renderer.

Recommended usage:

1. Copy `AGENTS.md` to the root of the Java renderer repository.
2. Open `FIRST_CODEX_PROMPT.txt` and paste it into Codex as the first task.
3. After Codex reports back, run the phases in `CODEX_PLAN.md` one by one.
4. Use `STUDENT_GUIDE_TEMPLATE.md` and `TEACHER_NOTES_TEMPLATE.md` as starting points after the code has been modernized.

Main constraints:

- no OpenGL,
- no LWJGL,
- no JOGL,
- no ImGui,
- no JavaFX,
- no Maven/Gradle unless explicitly decided later,
- plain Java + Swing + BufferedImage,
- CPU software renderer,
- approximate/fake AO is acceptable and recommended.
