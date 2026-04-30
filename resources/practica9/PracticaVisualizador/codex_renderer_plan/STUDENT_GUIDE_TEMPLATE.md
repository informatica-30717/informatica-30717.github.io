# Guía para estudiantes — Visualizador Java modernizado

> No es necesario entender todo el visualizador. En esta práctica trabajaremos solo con algunas clases concretas.

## 1. Objetivo

En esta práctica vamos a explorar un visualizador 3D escrito en Java. El programa carga un objeto en formato OBJ, lo proyecta en pantalla y lo dibuja usando un z-buffer y distintos modos de sombreado.

El objetivo no es programar un motor gráfico completo, sino identificar algunas piezas importantes de un proyecto orientado a objetos y modificar pequeños fragmentos para ver cambios visuales.

## 2. Cómo ejecutar el proyecto

1. Importa el proyecto en Eclipse o IntelliJ como proyecto Java existente.
2. Ejecuta la clase principal indicada por el profesor.
3. Al abrirse la ventana, debería cargarse automáticamente un modelo 3D.
4. Si no se carga ningún modelo, revisa que exista una carpeta de modelos dentro del proyecto.

## 3. Ficheros importantes

Durante la práctica trabajaremos principalmente con estos ficheros:

- `scene/Material.java`
- `scene/Light.java`
- `renderer/Shader.java`
- `renderer/PostProcess.java`

El resto de clases forman parte de la infraestructura del visualizador. Puedes mirarlas, pero no es necesario entenderlas completamente.

## 4. Controles sugeridos

- `1`: modo malla de alambre
- `2`: modo Phong
- `3`: modo Toon
- `4`: modo Clay / arcilla
- `5`: visualizar normales
- `6`: visualizar profundidad
- `A`: activar/desactivar oclusión ambiental aproximada
- `O`: activar/desactivar contorno
- `V`: activar/desactivar viñeteado
- `S`: guardar captura de pantalla

## 5. Actividades visuales

1. Carga el modelo por defecto y cambia entre los distintos modos de visualización.
2. Compara el modo Phong con el modo Toon.
3. Activa y desactiva la oclusión ambiental aproximada. ¿Qué zonas cambian más?
4. Activa y desactiva el contorno. ¿Ayuda a entender la forma del objeto?
5. Visualiza el z-buffer. ¿Qué representa una zona clara u oscura?
6. Visualiza las normales. ¿Por qué cada zona tiene un color distinto?

## 6. Actividades de código

### Material

Abre `scene/Material.java` y modifica:

- color base,
- componente ambiental,
- componente difusa,
- componente especular,
- brillo especular,
- intensidad del rim lighting si existe.

Prueba a conseguir:

1. Un material amarillo mate.
2. Un material con brillo especular fuerte.
3. Un material casi sin componente especular.

### Luz

Abre `scene/Light.java` y modifica:

- color de la luz,
- intensidad,
- dirección o posición.

Observa cómo cambia el objeto.

### Shader

Abre `renderer/Shader.java`. Localiza los términos:

- ambiental,
- difuso,
- especular.

Prueba a desactivar temporalmente alguno de ellos y explica qué cambia.

### Postprocesado

Abre `renderer/PostProcess.java`. Modifica los valores de:

- fuerza de viñeteado,
- fuerza de contorno,
- fuerza de oclusión ambiental aproximada.

Recuerda: la oclusión ambiental de esta práctica es una aproximación visual usando el z-buffer. No es una simulación física completa.

## 7. Preguntas

1. ¿En qué paquetes está dividido el proyecto?
2. ¿Qué clases contiene cada paquete?
3. ¿Dónde está la clase principal del programa?
4. ¿Qué clase representa el material?
5. ¿Qué clase representa la luz?
6. ¿Qué clase se encarga de calcular el color de un píxel?
7. ¿Qué es el z-buffer y para qué sirve?
8. ¿Qué diferencia visual hay entre Phong y Toon?
9. ¿Qué muestra el modo de normales?
10. ¿Qué muestra el modo de profundidad?
11. ¿Qué ocurre al eliminar el término especular?
12. ¿Qué ocurre al cambiar la dirección de la luz?
13. ¿Por qué la oclusión ambiental aproximada oscurece algunas zonas?
14. ¿Qué ventaja tiene trabajar solo con algunos ficheros concretos dentro de un proyecto grande?
