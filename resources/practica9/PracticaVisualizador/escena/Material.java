package escena;

/**
 * Define las propiedades de reflexion del material del objeto
 * 
 * Esta informacion no sirve en modo jaula de alambre
 * 
 * @author Adolfo
 */
public class Material {
	private Color _kd;
	private double _ks; 
	private double _es;
	
	/**
	 * Construye el material
	 * 
	 * @param kd Coeficiente difuso, con todas las componentes entre 0 y 1
	 * @param ks Coeficiente especular entre 0 y 1
	 * @param es Exponente especular, define la rugosidad del material, minimo 1
	 */
	public Material(Color kd, double ks, double es)
	{
		_kd = kd; _ks = ks; _es = es;
	}
	
	/**
	 * Coeficiente difuso
	 * 
	 * @return Coeficiente difuso
	 */
	public Color kd()
	{
		return _kd;
	}
	
	/**
	 * Coeficiente especular
	 * 
	 * @return Coeficiente especular
	 */
	public double ks()
	{
		return _ks;
	}
	
	/**
	 * Exponente especular, el material es mas rugoso cuanto menor es este valor
	 * 
	 * @return Exponente especular
	 */
	public double es()
	{
		return _es;
	}
	
}
