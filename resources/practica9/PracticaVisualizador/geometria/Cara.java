package geometria;

public class Cara {
	private Vertice[] _v;
	private Normal normalCache;
	private Punto centroCache;
	
	/**
	 * Construye una cara a partir de su lista de vértices
	 * 
	 * @param v La lista de vértices
	 */
	public Cara(Vertice[] v)
	{
		_v=v;
	}
	
	/**
	 * Construye una cara con tres vértices.
	 * 
	 * @param v1 Primer vértice
	 * @param v2 Segundo vértice
	 * @param v3 Tercer vértice
	 */
	public Cara(Vertice v1, Vertice v2, Vertice v3)
	{
		_v=new Vertice[3];
		_v[0]=v1;
		_v[1]=v2;
		_v[2]=v3;
	}
	
	/**
	 * Construye una cara con cuatro vertices.
	 * 
	 * @param v1 Primer vertice
	 * @param v2 Segundo vertice
	 * @param v3 Tercer vertice
	 * @param v4 Cuarto vertice
	 */
	public Cara(Vertice v1, Vertice v2, Vertice v3, Vertice v4)
	{
		_v=new Vertice[4];
		_v[0]=v1;
		_v[1]=v2;
		_v[2]=v3;
		_v[3]=v4;
	}

	/**
	 * Obtiene un vector con todos los vertices de esta cara
	 * 
	 * @return El vector con los vertices.
	 */
	public Vertice[] vertices()
	{
		return _v;
	}
	
	/**
	 * Obtiene un vertice de la cara a partir de su indice
	 *
	 * El indice tiene que estar dentro del rango (tiene que ser menor
	 * que el numero de vertices de esta cara).
	 * 
	 * @return El vertice correspondiente al índice
	 */
	public Vertice vertice(int i)
	{
		return _v[i];
	}
	
	/**
	 * Modifica un vertice a partir de su indice.
	 * 
	 * @param i El indice
	 * @param v El nuevo vertice para ese indice.
	 */
	public void modificaVertice(int i, Vertice v)
	{
		_v[i]=v;
		normalCache = null;
		centroCache = null;
	}
	
	/**
	 * Obtiene la normal a esta cara, calculada como el producto vectorial
	 * de las aristas (vectores que unen dos vertices). Asi se asegura que
	 * el vector es perpendicular a ambas aristas y por tanto normal a la cara
	 * 
	 * @return La normal de la cara
	 */
	public Normal normal()
	{
		if (normalCache == null)
		{
			Direccion du = new Direccion(_v[0].punto,_v[1].punto);
			Direccion dv = new Direccion(_v[2].punto,_v[0].punto);
			normalCache = Direccion.productoVectorial(du, dv).aVector4().aNormal();
		}
		return normalCache;
	}
	
	/**
	 * Obtiene el centro de esta cara, entendido como el punto medio de
	 * todos sus vertices
	 * 
	 * @return Centro de la cara
	 */
	public Punto centro()
	{
		if (centroCache == null)
		{
			double x = 0.0;
			double y = 0.0;
			double z = 0.0;
			for (int i = 0; i < _v.length; i++)
			{
				x += _v[i].punto.x();
				y += _v[i].punto.y();
				z += _v[i].punto.z();
			}
			centroCache = new Punto(x / (double) _v.length, y / (double) _v.length, z / (double) _v.length);
		}
		return centroCache;
	}
}
