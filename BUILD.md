# Build Guide

This project uses Quarto for the web site and slides, plus a small custom LaTeX workflow for the polished PDF versions of selected practices.

`BUILD.md` is a normal place for this kind of documentation: `README.md` can stay focused on what the project is, while this file explains how to build, preview, publish, and troubleshoot it.

## Requirements

- Quarto
- Python 3
- A LaTeX distribution with `latexmk` and `lualatex`, such as MiKTeX or TeX Live
- PowerShell on Windows, for the convenience PDF launcher

## Source Files

The repository has two kinds of source files:

- `practicas/practica*.qmd` are the Quarto sources for the web pages and slides.
- `practicas/practica*.tex` are hand-owned LaTeX sources for PDF versions.

Do not edit generated files in `_site/`. They will be replaced by Quarto.

The generated PDFs are copied to `_site/practicas/`. Local files such as `practicas/practica1.pdf` and the `_texbuild/` directory are build outputs and cache files.

## Quarto Web And Slides

Preview the site locally:

```powershell
quarto preview
```

Render the full site:

```powershell
quarto render
```

Render a single practice page:

```powershell
quarto render practicas/practica1.qmd --to html
```

Render a single slide deck:

```powershell
quarto render practicas/practica1.qmd --to revealjs
```

By default, Quarto does not compile the custom LaTeX PDFs. This keeps `quarto preview` fast and avoids overwriting hand-edited `.tex` files.

If a matching PDF already exists beside a `.tex` file, the post-render script copies it into `_site/practicas/` and adds the PDF link to the Quarto "Otros formatos" panel.

## Custom PDF Builds

Use the launcher script for PDFs:

```powershell
scripts\pdf
```

That builds the default PDF practices, currently practice 1 through practice 9.

Build only practice 1:

```powershell
scripts\pdf p1
```

Build only practice 8:

```powershell
scripts\pdf p8
```

Build several practices:

```powershell
scripts\pdf p1 p8 p9
```

Build every available `practicas/practica*.tex` file:

```powershell
scripts\pdf all
```

Force a full LaTeX rebuild:

```powershell
scripts\pdf p1 -Force
```

Clean LaTeX cache files for the selected target:

```powershell
scripts\pdf p1 -Clean
```

If PowerShell blocks `.ps1` execution, use the `.cmd` shim:

```powershell
scripts\pdf.cmd p1
```

## What the scripts do

`scripts/pdf.ps1` is the user-facing launcher. It accepts short names like `p1`, `p8`, and `all`, sets the needed environment variables, and calls the Python build script.

`scripts/pdf.cmd` is a Windows-friendly wrapper around `scripts/pdf.ps1`.

`scripts/build-practice-tex.py` is the internal build script. It is also registered in `_quarto.yml` as a `post-render` script, but it only compiles PDFs when `QUARTO_BUILD_TEX=1` is set.

The LaTeX style shared by the practice PDFs lives in:

```text
resources/latex/practice-pdf-style.tex
```

That file contains the common cover layout, colors, boxes, captions, code style, and image helpers.

## Quarto + PDF Workflow

The recommended workflow while editing is:

```powershell
quarto preview
scripts\pdf p1
```

Use Quarto for the live web preview, and run `scripts\pdf` whenever you want to update the PDF.

If you really want Quarto to compile PDFs during render or preview, set `QUARTO_BUILD_TEX=1` first:

```powershell
$env:QUARTO_BUILD_TEX = "1"
quarto render
```

For normal editing, the launcher is easier and more explicit.

## Publishing

Before publishing, rebuild the PDFs that changed and then render or publish the site:

```powershell
scripts\pdf
quarto render
```

Then publish using the usual Quarto command for the project:

```powershell
quarto publish gh-pages
```

If the publish command performs its own render and you want it to build PDFs too, set:

```powershell
$env:QUARTO_BUILD_TEX = "1"
quarto publish gh-pages
```

Use that option on a clean machine or CI runner, where the local `practicas/practicaN.pdf` files may not already exist.

## Adding a new practice PDF

1. Create a new hand-owned file such as `practicas/practica10.tex`.
2. Use the shared style file from `resources/latex/practice-pdf-style.tex`.
3. Build it with:

```powershell
scripts\pdf practicas\practica10.tex
```

If the new practice should be part of the default `scripts\pdf` build, add it to `DEFAULT_TARGETS` in `scripts/build-practice-tex.py`.

## Troubleshooting

If the PDF looks stale, run:

```powershell
scripts\pdf p1 -Force
```

If LaTeX cache files seem confused, run:

```powershell
scripts\pdf p1 -Clean
scripts\pdf p1
```

The first LaTeX build can be slow because packages, fonts, and auxiliary files have to be prepared. Later builds should be faster because `_texbuild/` keeps the cache.

If a `.tex` edit disappears, check that you are editing `practicas/practicaN.tex`, not a generated file in `_site/` or a Quarto temporary output.

If an image is not centered in a PDF, prefer the shared helper in the `.tex` source:

```latex
\PracticeCenteredImage[width=0.6\textwidth]{../resources/practica1/images/example.png}
```

That keeps the source easier to read than raw Pandoc-style figure blocks.

For longer code snippets, put the source in the practice resources folder and include it from the `.tex` file:

```latex
\PracticeCode{../resources/practica2/snippets/java/hola-mundo-errors.java}
```

This keeps the PDF source readable while still allowing the code examples to be edited directly.
