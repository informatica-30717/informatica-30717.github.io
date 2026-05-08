#!/usr/bin/env python3
from __future__ import annotations

import os
import re
import shutil
import subprocess
import sys
import time
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
BUILD_ROOT = REPO_ROOT / "_texbuild"

DEFAULT_TARGETS = (
    "practicas/practica1.tex",
    "practicas/practica2.tex",
    "practicas/practica3.tex",
    "practicas/practica4.tex",
    "practicas/practica9.tex",
)

AUXILIARY_EXTENSIONS = (
    ".aux",
    ".bbl",
    ".bcf",
    ".blg",
    ".fdb_latexmk",
    ".fls",
    ".lof",
    ".log",
    ".lot",
    ".out",
    ".run.xml",
    ".synctex.gz",
    ".toc",
)


def flag_enabled(name: str) -> bool:
    value = os.environ.get(name, "").strip().lower()
    return value in {"1", "true", "yes", "on"}


def repo_path(path: str | Path) -> Path:
    return (REPO_ROOT / path).resolve()


def repo_relative(path: Path) -> str:
    return path.resolve().relative_to(REPO_ROOT).as_posix()


def target_paths() -> list[Path]:
    if len(sys.argv) > 1:
        targets = sys.argv[1:]
        return normalize_targets(targets)

    configured = os.environ.get("QUARTO_TEX_TARGETS", "").strip()

    if configured.lower() == "all":
        return sorted((REPO_ROOT / "practicas").glob("practica*.tex"))

    if configured:
        targets = [
            item.strip()
            for item in re.split(r"[;,\n]+", configured)
            if item.strip()
        ]
    else:
        targets = list(DEFAULT_TARGETS)

    return normalize_targets(targets)


def normalize_targets(targets: list[str]) -> list[Path]:
    paths = []
    for target in targets:
        path = repo_path(target)
        if path.suffix == ".qmd":
            path = path.with_suffix(".tex")
        paths.append(path)

    return paths


def output_dir_for(target: Path) -> Path:
    relative_parent = target.parent.resolve().relative_to(REPO_ROOT)
    path = REPO_ROOT / "_site" / relative_parent
    path.mkdir(parents=True, exist_ok=True)
    return path


def cache_dir_for(target: Path) -> Path:
    relative_parent = target.parent.resolve().relative_to(REPO_ROOT)
    path = BUILD_ROOT / relative_parent / target.stem
    path.mkdir(parents=True, exist_ok=True)
    return path


def run_latex_pdf(target: Path) -> None:
    output_dir = output_dir_for(target)
    cache_dir = cache_dir_for(target)
    command = [
        "latexmk",
        "-lualatex",
        "-interaction=nonstopmode",
        "-file-line-error",
        "-noemulate-aux-dir",
        f"-auxdir={os.path.relpath(cache_dir, target.parent)}",
        target.name,
    ]

    if flag_enabled("QUARTO_TEX_FORCE"):
        command.insert(1, "-gg")

    subprocess.run(
        command,
        cwd=target.parent,
        check=True,
    )
    sync_existing_pdf(target)


def sync_existing_pdf(target: Path) -> Path | None:
    source_pdf = target.with_suffix(".pdf")
    if not source_pdf.exists():
        return None

    output_pdf = output_dir_for(target) / source_pdf.name
    shutil.copy2(source_pdf, output_pdf)
    return output_pdf


def ensure_html_pdf_link(target: Path) -> None:
    output_dir = output_dir_for(target)
    html_path = output_dir / f"{target.stem}.html"
    pdf_path = output_dir / f"{target.stem}.pdf"

    if not html_path.exists() or not pdf_path.exists():
        return

    text = html_path.read_text(encoding="utf-8")
    pdf_href = f"{target.stem}.pdf"
    if f'href="{pdf_href}"' in text:
        return

    pdf_link = f'<li><a href="{pdf_href}"><i class="bi bi-file-pdf"></i>PDF</a></li>'
    alternate_formats = re.compile(
        r'(<div class="quarto-alternate-formats"><h2>.*?</h2><ul>.*?)(</ul></div>)',
        re.DOTALL,
    )
    updated, replacements = alternate_formats.subn(rf"\1{pdf_link}\2", text, count=1)

    if replacements == 0:
        toc_close = "</nav>"
        custom_block = (
            '<div class="quarto-alternate-formats"><h2>Otros formatos</h2>'
            f"<ul>{pdf_link}</ul></div>"
        )
        updated = text.replace(toc_close, f"{custom_block}{toc_close}", 1)

    if updated != text:
        html_path.write_text(updated, encoding="utf-8")


def clean_auxiliary_files(
    target: Path,
    include_output_dir: bool = True,
    include_cache: bool = False,
) -> None:
    base = target.with_suffix("")
    bases = [base]

    if include_output_dir:
        bases.append(output_dir_for(target) / target.with_suffix("").name)

    if include_cache:
        bases.append(cache_dir_for(target) / target.stem)

    for item in bases:
        for extension in AUXILIARY_EXTENSIONS:
            path = item.with_suffix(extension)
            if path.exists():
                unlink_generated_file(path)


def unlink_generated_file(path: Path) -> None:
    for attempt in range(6):
        try:
            path.unlink()
            return
        except FileNotFoundError:
            return
        except PermissionError:
            if attempt == 5:
                print(
                    f"[practice-tex] Could not remove locked file: {repo_relative(path)}",
                    file=sys.stderr,
                )
                return
            time.sleep(0.35)


def main() -> int:
    if os.environ.get("PRACTICE_TEX_RUNNING") == "1":
        return 0

    build_tex = flag_enabled("QUARTO_BUILD_TEX")
    clean_tex = flag_enabled("QUARTO_TEX_CLEAN")

    targets = target_paths()
    existing_targets = [target for target in targets if target.exists()]
    missing = [target for target in targets if not target.exists()]

    if build_tex and shutil.which("latexmk") is None:
        print("[practice-tex] latexmk was not found on PATH.", file=sys.stderr)
        return 1

    if build_tex and missing:
        for target in missing:
            print(f"[practice-tex] Missing target: {target}", file=sys.stderr)
        return 1

    if build_tex:
        for target in existing_targets:
            print(f"[practice-tex] Compiling {repo_relative(target)}", flush=True)
            run_latex_pdf(target)
    else:
        for target in existing_targets:
            sync_existing_pdf(target)

    for target in existing_targets:
        ensure_html_pdf_link(target)

    for target in existing_targets:
        clean_auxiliary_files(target, include_cache=clean_tex)
        if clean_tex:
            unlink_generated_file(target.with_suffix(".pdf"))

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
