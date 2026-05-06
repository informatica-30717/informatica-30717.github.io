package app;

import escena.Color;
import escena.Luz;
import escena.Material;
import geometria.Punto;
import interfaz.VentanaPrincipal;
import renderer.RenderMode;

/**
 * Configuracion de arranque del visualizador.
 *
 * Usa este archivo para cambios pequenos sin tocar el renderizador.
 */
public final class Configuracion {
	// Modelo que se cargara al arrancar. Si no existe, se abrira el primer OBJ disponible.
	public static final String MODELO_INICIAL = "house.obj";

	// Modo de visualizacion inicial: WIREFRAME, PHONG, TOON, CLAY, NORMALS o DEPTH.
	public static final RenderMode MODO_INICIAL = RenderMode.CLAY;

	// Camara inicial.
	public static final double CAMARA_FOV = 28.0;
	public static final double CAMARA_INCLINACION = 12.0;
	public static final double CAMARA_ROTACION = -24.0;

	// Material del objeto (valores entre 0 y 1 para el color).
	public static final double MATERIAL_R = 0.60;
	public static final double MATERIAL_G = 0.10;
	public static final double MATERIAL_B = 0.10;
	public static final double MATERIAL_KS = 1.0;
	public static final double MATERIAL_EXPONENTE = 10.0;

	// Luz principal.
	// Los valores pueden subir hasta 5.0, igual que en el panel de luz.
	public static final double LUZ_R = 2.2;
	public static final double LUZ_G = 2.2;
	public static final double LUZ_B = 1.8;
	public static final double LUZ_X = 100.0;
	public static final double LUZ_Y = 100.0;
	public static final double LUZ_Z = 300.0;

	// Luz ambiente.
	public static final double AMBIENTE_R = 0.10;
	public static final double AMBIENTE_G = 0.10;
	public static final double AMBIENTE_B = 0.10;

	private Configuracion()
	{
	}

	public static void aplicar(VentanaPrincipal ventana)
	{
		ventana.configurarCamaraInicial(CAMARA_FOV, CAMARA_INCLINACION, CAMARA_ROTACION);
		ventana.aplicarModoRender(MODO_INICIAL);
		ventana.aplicarMaterial(new Material(
			color01(MATERIAL_R, MATERIAL_G, MATERIAL_B),
			Math.max(0.0, Math.min(1.0, MATERIAL_KS)),
			Math.max(1.0, MATERIAL_EXPONENTE)));
		ventana.aplicarLuz(new Luz(
			colorLuz(LUZ_R, LUZ_G, LUZ_B),
			new Punto(LUZ_X, LUZ_Y, LUZ_Z),
			color01(AMBIENTE_R, AMBIENTE_G, AMBIENTE_B)));

		if (!ventana.cargarModeloInicial(MODELO_INICIAL))
		{
			ventana.cargarPrimerModeloDisponible();
		}
	}

	private static Color color01(double r, double g, double b)
	{
		return new Color(clamp01(r), clamp01(g), clamp01(b));
	}

	private static Color colorLuz(double r, double g, double b)
	{
		return new Color(clamp(r, 0.0, 5.0), clamp(g, 0.0, 5.0), clamp(b, 0.0, 5.0));
	}

	private static double clamp01(double value)
	{
		return clamp(value, 0.0, 1.0);
	}

	private static double clamp(double value, double minimum, double maximum)
	{
		if (value < minimum)
		{
			return minimum;
		}
		if (value > maximum)
		{
			return maximum;
		}
		return value;
	}
}
