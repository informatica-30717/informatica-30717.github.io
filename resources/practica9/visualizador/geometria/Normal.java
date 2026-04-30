package geometria;

/**
 * Representa una direccion normal, es decir, una direccion cuya longitud es
 * siempre 1
 * 
 * @author Adolfo
 */
public class Normal {
	private Direccion d_;
	
	/**
	 * Inicializa la memoria. Este metodo es privado y
	 * se llama desde todos los constructores
	 */
	private void inicializar()
	{
	    d_ = new Direccion();
	}

	/**
	 * Modifica los elementos de esta normal
	 * 
	 * @param v1  Nuevo primer elemento
	 * @param v2  Nuevo segundo elemento
	 * @param v3  Nuevo tercer elemento
	 */
	void modificar(double v1, double v2, double v3)
	{
		d_.modificar(v1,v2,v3); d_.normalizar();
	}
	
	/**
	 * Modifica las componentes de esta normal con respecto a un vector
	 * 
	 * @param v  El vector
	 */
	void modificar(Vector4 v)
	{
		d_.modificar(v); d_.normalizar();
	}
	
	/**
	 * Constructor por defecto
	 */
	public Normal() {
		inicializar();
		modificar(0,0,1);
	}

	/**
	 * Construye una normal a partir de sus tres coordenadas
	 * 
	 * @param v1 Primera coordenada
	 * @param v2 Segunda coordenada
	 * @param v3 Tercera coordenada
	 */
	public Normal(double v1, double v2, double v3) {
		inicializar();
		modificar(v1, v2, v3);
	}

	/**
	 * Constructor por copia: construye una normal a partir de otra
	 * 
	 * @param n La otra normal
	 */
	public Normal(Normal n) {
		inicializar();
		modificar(n.elemento(0),n.elemento(1),n.elemento(2));
	}
	
	/**
	 * Construye una normal a partir de un vector de cuatro dimensiones.
	 * 
	 * Util para utilizar los resultados de multiplicar por una matriz en dentro
	 * de una normal
	 * 
	 * @param vector4 El vector de cuatro dimensiones (con coordenada homogenea)
	 */
	public Normal(Vector4 vector4) {
		inicializar();
		modificar(vector4);
	}
	
	/**
	 * Transforma esta normal en un vector de 4 dimensiones poniendo su componente
	 * homogenea a 0 (como corresponde a una direccion)
	 * 
	 * @return El vector con coordenada homogenea resultante
	 */
	public Vector4 aVector4()
	{
		return new Vector4(this);
	}
	
	  /**
	   * Obtiene el elemento iesimo de esta direccion.
	   * 
	   * 0 es 'x', 1 es 'y' y 2 es 'z'.
	   * 
	   * @param i  Indice entre 0 y 2
	   * @return   Elemento ieasimo
	   */
	  public double elemento (int i)
	  {
	    return d_.elemento(i);
	  }

	  /**
	   * Modifica el elemento iesimo de esta direccion.
	   * 
	   * @param i      Indice del elemento.
	   * @param value  Nuevo valor
	   */
	  public void modificarElemento(int i, double value)
	  {
	    d_.modificarElemento(i, value); d_.normalizar();
	  }

	  /**
	   * Transforma esta direccion con respecto a una matriz de transformacion
	   * 
	   * @param matriz La matriz de transformacion
	   */
	  public void transformar(Matriz4x4 matriz)
	  {
		  Matriz4x4 normal_matrix=new Matriz4x4(matriz);
		  normal_matrix.invertir();
		  normal_matrix.transponer();
		  this.modificar(normal_matrix.multiplicar(this.aVector4()));
	  }
	  
	  /**
	   * Transforma la normal de acuerdo a la matriz, sin modificarla.
	   * 
	   * @param matriz La matriz de transformacion
	   * @return La normal transformada.
	   */
	  public Normal transformada(Matriz4x4 matriz)
	  {
		  Normal sol = new Normal(this);
		  sol.transformar(matriz);
		  return sol;
	  }
	  
	  /**
	   * Obtiene el producto vectorial entre dos normales
	   * 
	   * La nueva normal es perpendicular a las anteriores
	   * 
	   * @param d1 Primera normal
	   * @param d2 Segunda normal
	   * @return Producto vectorial de ambas normales
	   */
	  static Normal productoVectorial(Normal d1, Normal d2)
	  {
		  return new Normal(Direccion.productoVectorial(d1.d_,d2.d_).aVector4());
	  }

	  /**
	   * Obtiene la coordenada x de esta normal
	   * 
	   * @return La coordenada x de esta normal
	   */
	  public double x()
	  {
		  return this.elemento(0);
	  }
	  
	  /**
	   * Obtiene la coordenada x de esta normal
	   * 
	   * @return La coordenada x de esta normal
	   */
	  public double y()
	  {
		  return this.elemento(1);
	  }
	  
	  /**
	   * Obtiene la coordenada x de esta normal
	   * 
	   * @return La coordenada x de esta normal
	   */
	  public double z()
	  {
		  return this.elemento(2);
	  }
}
