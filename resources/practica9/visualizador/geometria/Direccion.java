package geometria;

/**
 * Esta clase representa a una dirección en el espacio
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class Direccion {

	private double[] v_;

	/**
	 * Inicializa la memoria de esta direccion. Este metodo es privado y
	 * se llama desde todos los constructores
	 */
	private void inicializar()
	{
	    v_ = new double[3];
	}

	/**
	 * Constructor por defecto
	 */
	public Direccion() {
		inicializar();
		modificar(0,0,1);
	}

	/**
	 * Construye una direccion a partir de sus tres coordenadas
	 * 
	 * @param v1 Primera coordenada
	 * @param v2 Segunda coordenada
	 * @param v3 Tercera coordenada
	 */
	public Direccion(double v1, double v2, double v3) {
		inicializar();
		modificar(v1,v2,v3);
	}

	/**
	 * Constructor por copia: construye una direccion a partir de otra
	 * 
	 * @param d La otra direccion
	 */
	public Direccion(Direccion d) {
		inicializar();
		modificar(d.elemento(0),d.elemento(1),d.elemento(2));
	}
	
	/**
	 * Construye una direccion representando el vector que va desde un punto
	 * hasta otro
	 * 
	 * @param desde El punto de origen de esta direccion
	 * @param hasta El punto de destino de esta direccion
	 */
	public Direccion(Punto desde, Punto hasta)
	{
		inicializar();
		modificar(hasta.elemento(0)-desde.elemento(0),
				hasta.elemento(1)-desde.elemento(1),
				hasta.elemento(2)-desde.elemento(2));
	}

	/**
	 * Construye una direccion a partir de un vector de cuatro dimensiones.
	 * 
	 * Util para utilizar los resultados de multiplicar por una matriz en dentro
	 * de una direccion
	 * 
	 * @param v El vector de cuatro dimensiones (con coordenada homogenea)
	 */
	public Direccion(Vector4 v) {
		inicializar();
		modificar(v);
	}
	
	/**
	 * Calcula el la direccion que se veria si se reflejara la direccion actual
	 * en una superficie.
	 * 
	 * @param normal La normal a la superficie sobre la que se refleja.
	 * @return El vector reflejado
	 */
	public Direccion reflejado(Normal normal)
	{
		Direccion d=this; d.normalizar();
		double ci=d.aVector4().productoEscalar(normal.aVector4()); 
		d.modificar(2.0*ci*normal.elemento(0)-d.elemento(0),
				2.0*ci*normal.elemento(1)-d.elemento(1),
				2.0*ci*normal.elemento(2)-d.elemento(2));
		d.normalizar();
		double l = this.longitud();
		d.modificar(l*d.elemento(0),l*d.elemento(1),l*d.elemento(2));
		return d;
	}
	
	/**
	 * Transforma esta direccion en un vector de 4 dimensiones poniendo su componente
	 * homogenea a 0 (como corresponde a una direccion)
	 * 
	 * @return El vector con coordenada homogenea resultante
	 */
	public Vector4 aVector4()
	{
		return new Vector4(this);
	}
	
	/**
	 * Calcula la longitud de esta direccion
	 * 
	 * @return El modulo de esta direccion
	 */
	public double longitud()
	{
		return (Math.sqrt(v_[0]*v_[0]+v_[1]*v_[1]+v_[2]*v_[2]));
	}
	
	/**
	 * Modifica los elementos de esta direccion
	 * 
	 * @param v1  Nuevo primer elemento
	 * @param v2  Nuevo segundo elemento
	 * @param v3  Nuevo tercer elemento
	 */
	public void modificar (double v1, double v2, double v3)
	{
	    v_ = new double[3];
	    v_[0]=v1; v_[1]=v2; v_[2]=v3;
	}
	
	/**
	 * Modifica las componentes de esta direccion con respecto a un vector
	 * 
	 * @param v  El vector
	 */
	public void modificar(Vector4 v)
	{
		modificar(v.elemento(0),v.elemento(1),v.elemento(2));
	}

	/**
	 * Normaliza la direccion, asegurando que su longitud es 1
	 */
	public void normalizar()
	{
		double l=this.longitud();
		modificar(elemento(0)/l,elemento(1)/l,elemento(2)/l);
	}

	  /**
	   * Obtiene el elemento iesimo de esta direccion.
	   * 
	   * 0 es 'x', 1 es 'y' y 2 es 'z'.
	   * 
	   * @param i  Indice entre 0 y 2
	   * @return   Elemento iesimo
	   */
	  public double elemento(int i)
	  {
	    return v_[i];
	  }

	  /**
	   * Modifica el elemento iesimo de esta direccion.
	   * 
	   * @param i      Indice del elemento.
	   * @param value  Nuevo valor
	   */
	  public void modificarElemento(int i, double value)
	  {
	    v_[i] = value;
	  }
	  
	  /**
	   * Transforma esta direccion con respecto a una matriz de transformacion
	   * 
	   * @param matriz La matriz de transformacion
	   */
	  public void transformar(Matriz4x4 matriz)
	  {
		  this.modificar(matriz.multiplicar(this.aVector4()));
	  }
	  
	  /**
	   * Transforma la direccion de acuerdo a la matriz, sin modificarla.
	   * 
	   * @param matriz La matriz de transformacion
	   * @return La direccion transformada.
	   */
	  public Direccion transformada(Matriz4x4 matriz)
	  {
		  Direccion sol = new Direccion(this);
		  sol.transformar(matriz);
		  return sol;
	  }
	  
	  /**
	   * Obtiene el producto vectorial entre dos direcciones.
	   * 
	   * La nueva direccion es perpendicular a las anteriores
	   * 
	   * @param d1 Primera direccion
	   * @param d2 Segunda direccion
	   * @return Producto vectorial de ambas direcciones
	   */
	  public static Direccion productoVectorial(Direccion d1, Direccion d2)
	  {
		  return new Direccion(d1.y()*d2.z()-d1.z()*d2.y(),
				  d1.z()*d2.x() - d1.x()*d2.z(),
				  d1.x()*d2.y() - d1.y()*d2.x());
	  }
	  
	  /**
	   * Obtiene la coordenada x de esta direccion
	   * 
	   * @return La coordenada x de esta direccion
	   */
	  public double x()
	  {
		  return this.elemento(0);
	  }
	  
	  /**
	   * Obtiene la coordenada y de esta direccion
	   * 
	   * @return La coordenada y de esta direccion
	   */
	  public double y()
	  {
		  return this.elemento(1);
	  }
	  
	  /**
	   * Obtiene la coordenada z de esta direccion
	   * 
	   * @return La coordenada z de esta direccion
	   */
	  public double z()
	  {
		  return this.elemento(2);
	  }
}
