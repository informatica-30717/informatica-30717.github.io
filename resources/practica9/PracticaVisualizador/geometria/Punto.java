package geometria;

/**
 * Esta clase representa un punto tridimensional en el espacio
 * 
 * @author Adolfo
 */
public class Punto {

	private double[] v_;
	
	/**
	 * Inicializa la memoria. Este metodo es privado y
	 * se llama desde todos los constructores
	 */
	private void inicializar()
	{
	    v_ = new double[3];
	}

	/**
	 * Constructor por defecto
	 */
	public Punto() {
 		inicializar();
		modificar(0,0,0);
	}

	/**
	 * Construye un punto a partir de sus tres coordenadas
	 * 
	 * @param v1 Primera coordenada
	 * @param v2 Segunda coordenada
	 * @param v3 Tercera coordenada
	 */
	public Punto(double v1, double v2, double v3) {
 		inicializar();
		modificar(v1,v2,v3);
	}

	/**
	 * Constructor por copia: construye un punto a partir de otro
	 * 
	 * @param p El otro punto
	 */
	public Punto(Punto p) {
		inicializar();
		modificar(p.elemento(0),p.elemento(1),p.elemento(2));
	}

	/**
	 * Construye un punto a partir de un vector de cuatro dimensiones.
	 * 
	 * Util para utilizar los resultados de multiplicar por una matriz en dentro
	 * de una direccion
	 * 
	 * @param v El vector de cuatro dimensiones (con coordenada homogenea)
	 */
	public Punto(Vector4 v) {
 		inicializar();
		modificar(v);
	}

	/**
	 * Modifica los elementos de este punto
	 * 
	 * @param x  Nueva x
	 * @param y  Nueva y
	 * @param z  Nueva z
	 */
	public void modificar(double x, double y, double z)
	{
	    v_[0]=x; v_[1]=y; v_[2]=z;
	}
	  
	/**
	 * Modifica las componentes de este punto con respecto a un vector con 
	 * coordenada homogenea.
	 * 
	 * Divide por la coordenada homogenea como corresponde
	 * 
	 * @param v  El vector
	 */
	public void modificar(Vector4 v)
	{
		modificar(v.elemento(0)/v.elemento(3),v.elemento(1)/v.elemento(3),v.elemento(2)/v.elemento(3));
	}
	  
	/**
	 * Transforma esta direccion en un vector de 4 dimensiones poniendo su componente
	 * homogenea a 1 (como corresponde a un punto en el espacio)
	 * 
	 * @return El vector con coordenada homogenea resultante
	 */
	public Vector4 aVector4()
	{
		return new Vector4(this);
	}
	
	  /**
	   * Obtiene el elemento iesimo de este punto
	   * 
	   * 0 es 'x', 1 es 'y' y 2 es 'z'.
	   * 
	   * @param i  Indice entre 0 y 2
	   * @return   Elemento iesimo
	   */
	  public double elemento (int i)
	  {
	    return v_[i];
	  }

	  /**
	   * Modifica el elemento iesimo de este punto
	   * 
	   * @param i      Indice del elemento.
	   * @param value  Nuevo valor
	   */
	  public void modificarElemento(int i, double value)
	  {
	    v_[i] = value;
	  }
	  
	  /**
	   * Transforma este punto con respecto a una matriz de transformacion
	   * 
	   * @param matriz La matriz de transformacion
	   */
	  public void transformar(Matriz4x4 matriz)
	  {
		  this.modificar(matriz.multiplicar(this.aVector4()));
	  }
	  
	  /**
	   * Transforma el punto de acuerdo a la matriz, sin modificarlo.
	   * 
	   * @param matriz La matriz de transformacion
	   * @return El punto transformada.
	   */
	  public Punto transformado(Matriz4x4 matriz)
	  {
		  Punto sol = new Punto(this);
		  sol.transformar(matriz);
		  return sol;
	  }
	  
	  /**
	   * Obtiene la coordenada x de este punto
	   * 
	   * @return La coordenada x de este punto
	   */
	  public double x()
	  {
		  return this.elemento(0);
	  }
	  
	  /**
	   * Obtiene la coordenada y de este punto
	   * 
	   * @return La coordenada y de este punto
	   */
	  public double y()
	  {
		  return this.elemento(1);
	  }
	  
	  /**
	   * Obtiene la coordenada z de este punto
	   * 
	   * @return La coordenada z de este punto
	   */
	  public double z()
	  {
		  return this.elemento(2);
	  }
}
