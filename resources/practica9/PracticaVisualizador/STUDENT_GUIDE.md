# Guia breve del visualizador

## Como ejecutar el proyecto

1. Abre el proyecto como un proyecto Java normal en Eclipse o IntelliJ.
2. Ejecuta la clase `app.Main`.
3. La ventana del visualizador se abre automaticamente.
4. Si existe un modelo `.obj` en `objetos_3d`, se carga por defecto.

No es necesario entender todo el visualizador. En esta practica trabajaremos solo con algunas clases concretas.

## Clases que si debes tocar

- `escena/Material.java`
- `escena/Luz.java`
- `renderer/Shader.java`
- `renderer/PostProcess.java`

## Clases que normalmente no debes tocar

- `geometria/*`
- `interfaz/PanelVisor.java`
- `interfaz/VentanaPrincipal.java`
- `renderer/RenderBuffers.java`
- `renderer/RenderMode.java`

## Modos de visualizacion

- `1`: malla de alambre
- `2`: Phong
- `3`: Toon
- `4`: Clay
- `5`: normales
- `6`: profundidad

## Efectos y acciones

- `A`: activa o desactiva la oclusion ambiental aproximada
- `O`: activa o desactiva el contorno
- `V`: activa o desactiva la vigneta
- `S`: exporta una captura PNG en `screenshots/`

## Ideas de practica

1. Cambia el color del material y compara `Phong` y `Clay`.
2. Baja el coeficiente especular y observa como cambia el brillo.
3. Sube el exponente especular y compara superficies mas mates y mas brillantes.
4. Prueba el modo `Normales` para entender la orientacion de la superficie.
5. Prueba el modo `Profundidad` para relacionarlo con el z-buffer.
6. Activa y desactiva la oclusion ambiental aproximada para ver el sombreado de contacto aproximado usando el z-buffer.
7. Exporta dos capturas y compara el resultado visual.

## Conceptos a repasar

- paquetes y clases
- atributos y metodos
- parametros y tipos de retorno
- proyeccion en perspectiva
- z-buffer
- material y luz
- terminos ambiente, difuso y especular
- fake AO como efecto aproximado basado en profundidad