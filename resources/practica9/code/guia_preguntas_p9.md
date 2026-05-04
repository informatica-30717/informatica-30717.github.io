# Guia rapida para resolver P9

| Pregunta | Donde mirar |
| --- | --- |
| 1 | Package Explorer o carpetas de `visualizador`; lista los `.java` de cada paquete. |
| 2 | Outline de `VentanaPrincipal`, `Matriz4x4` y `Objeto`; `static` = de clase, no `static` = de objeto. |
| 3 | `Objeto.construirNormales` y `Objeto.cargarObj`; enumera las declaraciones locales dentro de cada metodo. |
| 4 | Firmas de `Punto.modificar` y `Objeto.interpretarComoVertice`; el tipo esta antes del nombre. |
| 5 | Firmas de las dos sobrecargas de `Punto.modificar` y de `Objeto.cargarObj`; mira lo que hay entre parentesis. |
| 6 | Breakpoint en `Punto.modificar(double x, double y, double z)`; en Debug, mira la vista Variables en los dos primeros parones. |
