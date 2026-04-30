package geometria;

/**
 * Representa un vertice de un objeto, con su posicion en el espacio y
 * su direccion normal
 * 
 * @author Adolfo
 */
public class Vertice {
	/**
	 * El punto de este vertice
	 */
	public Punto punto;
	
	/**
	 * La normal de este vertice
	 */
	public Normal normal;
	
	/**
	 * Constructor
	 * 
	 * @param _punto El punto del vertice
	 * @param _normal La normal del vertice
	 */
	public Vertice(Punto _punto, Normal _normal)
	{
		punto=_punto; normal=_normal;
	}
};
