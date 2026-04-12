---
name: quarto-reading-time-pills
description: 'Add reading-time pills to Quarto practica pages. Use when updating practicas/practica*.qmd, standardizing estimated reading time labels, or batch-applying content metadata badges in course material.'
argument-hint: 'Scope (all or subset), language for pill text, words-per-minute baseline, and placement (below title or after intro).'
user-invocable: true
---

# Quarto Reading Time Pills

## What This Skill Produces
This skill updates practica Quarto files so each target page includes a visible pill with approximate reading time, using a consistent format and placement.

## When to Use
- Add or refresh estimated reading time in practicas.
- Standardize pill wording and style across practica pages.
- Apply reading-time labels after major content changes.

## Inputs To Confirm
- Target files: all `practicas/practica*.qmd` by default, or a subset if requested.
- Language for pill text (default: Spanish, for example `~8 min de lectura`).
- Reading speed baseline (default: 220 words per minute).
- Placement rule (default): directly below the title.

## Procedure
1. Identify target files under `practicas/` matching `practica*.qmd`.
2. For each file, estimate reading time using:
   - word count from instructional content,
   - formula $\text{minutes} = \max(1, \lceil \text{words} / \text{wpm} \rceil)$.
3. Build a standard pill snippet and insert it in the agreed placement.
4. Avoid duplicates:
   - if a reading-time pill already exists, update the value and keep one pill only.
5. Keep page style consistent:
   - reuse existing classes/components if present,
   - otherwise use a minimal, portable Quarto-compatible HTML badge.
6. Validate render behavior with Quarto preview/build and confirm no warnings introduced by the new snippet.

## Decision Points
- If a file is mostly slides (`revealjs`) and placement conflicts with title slides, place the pill in the first non-title content block.
- If a page has very short content, still show `~1 min` minimum.
- If there are code-heavy sections, include explanatory text/code comments in the estimate only when they are clearly instructional reading content.

## Completion Checks
- Each target file has exactly one reading-time pill.
- Pill text format is consistent across files.
- Estimated minutes use the agreed baseline and rounding rule.
- Quarto preview/build succeeds for edited files.

## Standard Snippet (Default)
Use this when no existing badge style is defined in the page:

```html
<div class="reading-time-pill">
  <span class="badge rounded-pill text-bg-secondary">~8 min de lectura</span>
</div>
```

## Suggested Invocation Prompts
- `/quarto-reading-time-pills Add reading-time pills to all practica qmd files in Spanish using 220 wpm, place below title.`
- `/quarto-reading-time-pills Update only practica5.qmd, practica6.qmd, practica7.qmd after content edits.`
