package geometria;
/*
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA  02111-1307, USA.
 */


/**
 * Vector de 4 elementos para trabajar con Matriz4x4
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 * @author Adolfo
 */   
public class Vector4
{
  private double[] v_;
 
	/**
	 * Inicializa la memoria. Este metodo es privado y
	 * se llama desde todos los constructores
	 */
  private void inicializar()
  {
    v_ = new double[4];
    for (int i = 0; i < 4; i++)
      v_[i] = 0.0;
  }


  /**
   * Constructor por defecto, todos los elementos a cero
   */
  public Vector4()
  {
    inicializar();
  }

	/**
	 * Construye un punto a partir de sus cuatro coordenadas
	 * 
	 * @param v1 Primera coordenada
	 * @param v2 Segunda coordenada
	 * @param v3 Tercera coordenada
	 * @param v4 Cuarta coordenada
	 */
  public Vector4 (double v1, double v2, double v3, double v4)
  {
    inicializar();
    modificar (v1, v2, v3, v4);
  }

	/**
	 * Constructor por copia: construye un vector a partir de otro
	 * 
	 * @param vector4 El otro vector
	 */
  public Vector4(Vector4 vector4)
  {
    inicializar();
    modificar (vector4);
  }

	/**
	 * Construye un vector a partir de un punto, con coordenada homogenea a 1
	 * 
	 * @param punto El punto
	 */
  public Vector4(Punto punto)
  {
    inicializar();
    modificar(punto);
  }

	/**
	 * Construye un vector a partir de una direccion, con coordenada homogenea a 0
	 * 
	 * @param direccion La direccion
	 */
  public Vector4(Direccion direccion)
  {
    inicializar();
    modificar(direccion);
  }

	/**
	 * Construye un vector a partir de una normal, con coordenada homogenea a 0
	 * 
     * @param normal La normal
     */
  public Vector4(Normal normal)
  {
    inicializar();
    modificar(normal);
  }

	/**
	 * Modifica los elementos de este vector
	 * 
	 * @param v1  Nuevo primer elemento
	 * @param v2  Nuevo segundo elemento
	 * @param v3  Nuevo tercer elemento
	 * @param v4  Nuevo cuarto elemento
	 */
  public void modificar (double v1, double v2, double v3, double v4)
  {
    v_[0] = v1;
    v_[1] = v2;
    v_[2] = v3;
    v_[3] = v4;
  }

	/**
	 * Modifica las componentes de este vector copiandolas de otro vector
	 * 
     * @param vector  Vector a copiar
     */
  public void modificar(Vector4 vector)
  {
    for (int i = 0; i < 4; i++)
      v_[i] = vector.v_[i];
  }

	/**
	 * Modifica las componentes de este vector a partir de un punto
	 * 
     * @param punto  Punto a copiar
     */
  public void modificar(Punto punto)
  {
	  modificar(punto.elemento(0),punto.elemento(1),punto.elemento(2),1);
  }

	/**
	 * Modifica las componentes de este vector a partir de una direccion
	 * 
     * @param direccion  Direccion a copiar
     */
  public void modificar(Direccion direccion)
  {
	  modificar(direccion.elemento(0),direccion.elemento(1),direccion.elemento(2),0);
  }

	/**
	 * Modifica las componentes de este vector a partir de una normal
	 * 
     * @param normal Normal a copiar
     */
  public void modificar(Normal normal)
  {
	  modificar(normal.elemento(0),normal.elemento(1),normal.elemento(2),0);
  }

  /**
   * Obtiene el elemento iesimo de este vector
   * 
   * @param i  Indice entre 0 y 3
   * @return   Elemento iesimo
   */
  public double elemento (int i)
  {
    return v_[i];
  }

  /**
   * Modifica el elemento iesimo de este vector
   * 
   * @param i      Indice del elemento.
   * @param value  Nuevo valor
   */
  public void modificarElemento(int i, double value)
  {
    v_[i] = value;
  }
  
  
  /**
   * Calcula el producto escalar de este vector con otro
   *
   * @param v El otro vector
   * @return El producto escalar
   */
  public double productoEscalar(Vector4 v)
  {
	  double value=0;
	  for (int i = 0; i < 4; i++) {
		   value+=this.elemento(i)*v.elemento(i);
	  }
	  return value;
  }

  /**
   * Devuelve un punto construido a partir de este vector
   * 
   * @return El nuevo punto que corresponde a este vector
   */
  public Punto aPunto()
  {
	  return new Punto(this);
  }
  
  /**
   * Devuelve un punto construido a partir de esta direccion
   * 
   * @return El nuevo punto que corresponde a esta direccion
   */
  public Direccion aDireccion()
  {
	  return new Direccion(this);
  }
  
  /**
   * Devuelve un punto construido a partir de esta normal
   * 
   * @return El nuevo punto que corresponde a esta normal
   */
  public Normal aNormal()
  {
	  return new Normal(this);
  }
  
  /**
   * Interpola dos vector
   * 
   * @param t Entre 0 y 1, define el porcentaje de interpolacion (con 0 el resultado
   * es v0 y con 1 el resultado es v1).
   * @param v0 El vector para t=0
   * @param v1 El vector para t=1
   */
  public static Vector4 interpolar(double t, Vector4 v0, Vector4 v1)
  {
	  return new Vector4(
			  v0.elemento(0)*(1.0-t) + v1.elemento(0)*t,
			  v0.elemento(1)*(1.0-t) + v1.elemento(1)*t,
			  v0.elemento(2)*(1.0-t) + v1.elemento(2)*t,
			  v0.elemento(3)*(1.0-t) + v1.elemento(3)*t);
  }
  
  
  /**
   * Suma un vector a este, componente por componente
   *
   * @param v El otro vector a sumar
   */
  public void sumar(Vector4 v)
  {
	  for (int i=0;i<4;i++)
	  {
		  this.v_[i]+=v.elemento(i);
	  }
  }
  
  /**
   * Suma dos vectores componente a componente
   *
   * @param v0 Primer vector
   * @param v1 Segundo vector
   * @return La suma componente a componente
   */
  public static Vector4 sumar(Vector4 v0, Vector4 v1)
  {
	  return new Vector4(
			  v0.elemento(0)+v1.elemento(0),
			  v0.elemento(1)+v1.elemento(1),
			  v0.elemento(2)+v1.elemento(2),
			  v0.elemento(3)+v1.elemento(3));		  
  }
}
