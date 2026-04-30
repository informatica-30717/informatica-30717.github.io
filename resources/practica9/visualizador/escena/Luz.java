package escena;

/**
 * Clase que contiene la definicion de la iluminacion de una escena
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class Luz {
	Color _color;
	geometria.Punto _posicion;
	Color _colorAmbiente;
	
	/**
	 * Construye la iluminacion
	 * 
	 * @param color Color de la luz
	 * @param posicion Posicion de la luz
	 * @param colorAmbiente Iluminacion de ambiente
	 */
	public Luz(Color color,geometria.Punto posicion,Color colorAmbiente)
	{
		_color = color;
		_posicion = posicion;
		_colorAmbiente = colorAmbiente;
	}
	
	/**
	 * Color de la luz
	 * 
	 * @return Color de la luz
	 */
	public Color color()
	{
		return _color;
	}
	
	/**
	 * Posicion de la luz
	 * 
	 * @return Posicion de la luz
	 */
	public geometria.Punto posicion()
	{
		return _posicion;
	}
	
	/**
	 * Color de la iluminacion de ambiente
	 * 
	 * @return Color de la iluminacion de ambiente
	 */
	public Color colorAmbiente()
	{
		return _colorAmbiente;
	}
}
