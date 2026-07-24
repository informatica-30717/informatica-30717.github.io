<div align="center">
  <img src="resources/unizar.png" alt="Universidad de Zaragoza" width="190">

  <h1>Informática 30717</h1>

  <p>
    <strong>Material de prácticas para el Grado en Estudios en Arquitectura</strong><br>
    Universidad de Zaragoza - Curso 2025-2026
  </p>

  <p>
    <a href="https://informatica-30717.github.io/"><strong>Abrir la web</strong></a>
    ·
    <a href="calendar.qmd">Calendario</a>
    ·
    <a href="practicas">Prácticas</a>
    ·
    <a href="resources">Recursos</a>
  </p>
</div>

<p align="center">
  <a href="https://quarto.org/"><img alt="Quarto" src="https://img.shields.io/badge/Quarto-website-39729E?style=for-the-badge"></a>
  <img alt="Curso" src="https://img.shields.io/badge/curso-2025--2026-1f7a5c?style=for-the-badge">
  <img alt="Licencia" src="https://img.shields.io/badge/licencia-CC%20BY--NC--SA%204.0-6b4e9b?style=for-the-badge">
</p>

<p align="center">
  <img alt="Stars" src="https://img.shields.io/github/stars/informatica-30717/informatica-30717.github.io?style=flat-square&label=stars&color=2f80ed">
  <img alt="Forks" src="https://img.shields.io/github/forks/informatica-30717/informatica-30717.github.io?style=flat-square&label=forks&color=16a34a">
  <img alt="Watchers" src="https://img.shields.io/github/watchers/informatica-30717/informatica-30717.github.io?style=flat-square&label=watchers&color=7c3aed">
  <img alt="Last commit" src="https://img.shields.io/github/last-commit/informatica-30717/informatica-30717.github.io?style=flat-square&label=last%20commit&color=f97316">
  <img alt="Repo size" src="https://img.shields.io/github/repo-size/informatica-30717/informatica-30717.github.io?style=flat-square&label=repo%20size&color=64748b">
</p>

---

## Qué es este repositorio

Este repositorio contiene el material docente de la asignatura de **Informática (Código 30717)** de la Universidad de Zaragoza. Incluye guiones de prácticas, recursos descargables, visualizaciones interactivas y material de apoyo para trabajar programación y herramientas digitales aplicadas a arquitectura.

## Mapa de prácticas

| Sesión | Tema | Guion |
|---:|---|---|
| 1 | Introducción a Processing | [`practicas/practica1.qmd`](practicas/practica1.qmd) |
| 2 | Entorno de programación en Java | [`practicas/practica2.qmd`](practicas/practica2.qmd) |
| 3 | Resolución de problemas en Java I | [`practicas/practica3.qmd`](practicas/practica3.qmd) |
| 4 | Aspectos avanzados de Excel | [`practicas/practica4.qmd`](practicas/practica4.qmd) |
| 5 | Resolución de problemas en Java II | [`practicas/practica5.qmd`](practicas/practica5.qmd) |
| 6 | Resolución de problemas en Java III | [`practicas/practica6.qmd`](practicas/practica6.qmd) |
| 7 | Introducción a Grasshopper | [`practicas/practica7.qmd`](practicas/practica7.qmd) |
| 8 | BIM semántico | [`practicas/practica8.qmd`](practicas/practica8.qmd) |
| 9 | Visualizador 3D | [`practicas/practica9.qmd`](practicas/practica9.qmd) |

## Estructura

```text
.
|-- index.qmd              # Portada del sitio
|-- calendar.qmd           # Calendario de prácticas
|-- practicas/             # Guiones fuente de cada sesión
|-- resources/             # Recursos descargables y assets
|-- styles.css             # Estilos globales
|-- sidebar.yml            # Navegación lateral
`-- _quarto.yml            # Configuración Quarto
```

## Desarrollo local

```bash
quarto preview
```

La configuración del proyecto usa el puerto `3000` para la vista previa local.

Para generar la web completa:

```bash
quarto render
```

Consulta [`BUILD.md`](BUILD.md) para el flujo completo de Quarto, PDFs y scripts personalizados.

## Autores

Alfonso López Ruiz, Fernando Bobillo, Ignacio Huitzil, Yamilka Toca Díaz.

## Licencia

<p>
  <img src="https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by-nc-sa.png" alt="Creative Commons BY-NC-SA 4.0" width="120">
</p>

Material docente protegido bajo licencia **CC BY-NC-SA 4.0**. Se permite su uso docente no comercial con atribución y licencia compartida.
