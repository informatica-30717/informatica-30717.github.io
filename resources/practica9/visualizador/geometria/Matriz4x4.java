package geometria;
/*
 * (C) 2004 - Geotechnical Software Services
 * 
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
 * Implementacion de una matriz cuadrada de cuatro dimensiones
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 * @author Modificaciones de Adolfo
 */   
public class Matriz4x4 
{
	/**
	 * Componentes de la matriz
	 */
  private double[]  m_;  // of 16


  
  /**
   * Construye una matriz identidad
   */
  public Matriz4x4()
  {
    inicializar();
    aIdentidad();
  }

  
  /**
   * Construye una matriz copiando a partir de otra matriz
   * 
   * @param matriz  Matriz a copiar
   */
  public Matriz4x4 (Matriz4x4 matriz)
  {
    inicializar();
    modificar (matriz);
  }

  

  /**
   * Construye una matriz a partir de sus valores
   * 
   * @param m00  Valor del elemento m[0,0].
   * @param m01  Valor del elemento m[0,1].
   * @param m02  Valor del elemento m[0,2].
   * @param m03  Valor del elemento m[0,3].
   * @param m10  Valor del elemento m[1,0].
   * @param m11  Valor del elemento m[1,1].
   * @param m12  Valor del elemento m[1,2].
   * @param m13  Valor del elemento m[1,3].
   * @param m20  Valor del elemento m[2,0].
   * @param m21  Valor del elemento m[2,1].
   * @param m22  Valor del elemento m[2,2].
   * @param m23  Valor del elemento m[2,3].
   * @param m30  Valor del elemento m[3,0].
   * @param m31  Valor del elemento m[3,1].
   * @param m32  Valor del elemento m[3,2].
   * @param m33  Valor del elemento m[3,3].
   */
  public Matriz4x4 (double m00, double m01, double m02, double m03,
                    double m10, double m11, double m12, double m13,
                    double m20, double m21, double m22, double m23,
                    double m30, double m31, double m32, double m33)
  {
    inicializar();
    modificar (m00, m01, m02, m03,
         m10, m11, m12, m13,
         m20, m21, m22, m23,
         m30, m31, m32, m33);
  }
  


  /**
   * Inicializa la matriz
   * 
   * Solo se llamara en constructores.
   */
  private void inicializar()
  {
    m_ = new double[16];
  }

  

  /**
   * Hace que esta matriz se convierta en una matriz identidad.
   */
  public void aIdentidad()
  {
    for (int i=0; i<4; i++)
      for (int j=0; j<4; j++)
        m_[i*4 + j] = i == j ? 1.0 : 0.0;
  }



  /**
   * Modifica los valores de esta matriz como copia de los de otra matriz
   *
   * @param matriz Matriz a copiar
   */
  public void modificar(Matriz4x4 matriz)
  {
    for (int i=0; i<16; i++)
      m_[i] = matriz.m_[i];
  }
  
  
  /**
   * Modifica esta matriz con respecto a nuevos elementos
   * 
   * @param m00  Valor del elemento m[0,0].
   * @param m01  Valor del elemento m[0,1].
   * @param m02  Valor del elemento m[0,2].
   * @param m03  Valor del elemento m[0,3].
   * @param m10  Valor del elemento m[1,0].
   * @param m11  Valor del elemento m[1,1].
   * @param m12  Valor del elemento m[1,2].
   * @param m13  Valor del elemento m[1,3].
   * @param m20  Valor del elemento m[2,0].
   * @param m21  Valor del elemento m[2,1].
   * @param m22  Valor del elemento m[2,2].
   * @param m23  Valor del elemento m[2,3].
   * @param m30  Valor del elemento m[3,0].
   * @param m31  Valor del elemento m[3,1].
   * @param m32  Valor del elemento m[3,2].
   * @param m33  Valor del elemento m[3,3].
   */
  public void modificar (double m00, double m01, double m02, double m03,
                   double m10, double m11, double m12, double m13,
                   double m20, double m21, double m22, double m23,
                   double m30, double m31, double m32, double m33)
  {
    m_[0]  = m00;
    m_[1]  = m01;
    m_[2]  = m02;
    m_[3]  = m03;  
  
    m_[4]  = m10;
    m_[5]  = m11;
    m_[6]  = m12;
    m_[7]  = m13;  

    m_[8]  = m20;
    m_[9]  = m21;
    m_[10] = m22;
    m_[11] = m23;  

    m_[12] = m30;
    m_[13] = m31;
    m_[14] = m32;
    m_[15] = m33;  
  }

  /**
   * Devuelve el elemento [i,j] de la matriz
   * 
   * @param i  Fila del elemento (la primera es 0).
   * @param j  Columna del elemento (la primera es 0).
   * @return   El elemento en esa posicion
   */
  public double elemento (int i, int j)
  {
    return m_[i*4 + j];  
  }

  

  /**
   * Modifica el elemento [i,j] de la matriz
   * 
   * @param i  Fila del elemento (la primera es 0).
   * @param j  Columna del elemento (la primera es 0).
   * @param v  Nuevo valor
   */
  public void modificarElemento (int i, int j, double v)
  {
    m_[i*4 + j] = v;
  }
  
  /**
   * Transposes the matrix
   */
  public void transponer()
  {
	  this.modificar(this.elemento(0,0),this.elemento(1,0),this.elemento(2,0),this.elemento(3,0),
			  this.elemento(0,1),this.elemento(1,1),this.elemento(2,1),this.elemento(3,1),
			  this.elemento(0,2),this.elemento(1,2),this.elemento(2,2),this.elemento(3,2),
			  this.elemento(0,3),this.elemento(1,3),this.elemento(2,3),this.elemento(3,3));
  }
  
  /**
   * Multiplica esta matriz por otra matriz
   * 
   * @param matriz  Matriz por la que multiplicar
   */
  public void multiplicar (Matriz4x4 matriz)
  {
    Matriz4x4 producto = new Matriz4x4();
    
    for (int i = 0; i < 16; i += 4) {
      for (int j = 0; j < 4; j++) {
        producto.m_[i + j] = 0.0;
        for (int k = 0; k < 4; k++)
          producto.m_[i + j] += m_[i + k] * matriz.m_[k*4 + j];
      }
    }

    modificar (producto);
  }


  
  /**
   * Multiplica dos matrices
   * 
   * @param m1  Primera matriz a multiplicar
   * @param m2  Segunda matriz a multiplicar
   * @return    Producto matricial
   */
  public static Matriz4x4 multiplicar (Matriz4x4 m1, Matriz4x4 m2)
  {
    Matriz4x4 m = new Matriz4x4 (m1);
    m.multiplicar (m2);
    return m;
  }

  

  /**
   * Multiplicar esta matriz por un vector
   * 
   * @param vector4  Vector a multiplicar
   * @return         Vector multiplicado
   */
  public Vector4 multiplicar (Vector4 vector4)
  {
    Vector4  producto = new Vector4();

    for (int i = 0; i < 4; i++) {
      double v = 0.0;
      for (int j = 0; j < 4; j++)
        v += elemento(i, j) * vector4.elemento (j);
      producto.modificarElemento (i, v);
    }

    return producto;
  }
  
 

  /**
   * Aplica una matriz de traslacion
   * 
   * @param dx  Traslacion en x
   * @param dy  Traslacion en y
   * @param dz  Traslacion en z
   */
  public void traslacion (double dx, double dy, double dz)
  {
    Matriz4x4  matriz = new Matriz4x4();

    matriz.modificarElemento (0, 3, dx);
    matriz.modificarElemento (1, 3, dy);
    matriz.modificarElemento (2, 3, dz);
    
    multiplicar (matriz);
  }
  
  /**
   * Aplica una transformacion perspectiva
   * 
   * @param fov  Campo de vision horizontal
   * @param aspect_ratio ratio entre anchura y altura para la transformacion
   */
  public void perspectiva(double fov, double aspect_ratio)
  {
    Matriz4x4  matriz = new Matriz4x4();

    double tan = Math.tan(fov/2.0);
    matriz.modificarElemento (0, 0, 1.0/(aspect_ratio*tan));
    matriz.modificarElemento (1, 1, 1.0/tan);
    matriz.modificarElemento (2, 3, 1);
    matriz.modificarElemento (3, 3, 0);
    
    multiplicar (matriz);
  }
  
  /**
   * Aplica una transformacion perspectiva
   * 
   * @param fov  Campo de vision horizontal
   * @param aspect_ratio ratio entre anchura y altura para la transformacion
   * @param near Distancia minima
   * @param far Distancia maxima
   */
  public void perspectiva(double fov, double aspect_ratio, double near, double far)
  {
    Matriz4x4  matriz = new Matriz4x4();

    double tan = Math.tan(fov/2.0);
    matriz.modificarElemento (0, 0, 1.0/(aspect_ratio*tan));
    matriz.modificarElemento (1, 1, 1.0/tan);
    matriz.modificarElemento (2, 2, far/(far-near));
    matriz.modificarElemento (2, 3, 1);
    matriz.modificarElemento (3, 2, -near*far/(far-near));
    matriz.modificarElemento (3, 3, 0);
    
    multiplicar (matriz);
  }
  
  /**
   * Proyecta en una pantalla de ciertas dimensiones y centra en el origen
   * 
   * @param width anchura de la pantalla en pixeles
   * @param height altura de la pantalla en pixeles
   */
  public void pantalla(int width, int height)
  {
    Matriz4x4  matriz = new Matriz4x4();

    double half_width=0.5*new Double(width);
    double half_height=0.5*new Double(height);
    matriz.modificarElemento (0, 0, half_width);
    matriz.modificarElemento (1, 1, half_height);
    matriz.modificarElemento (0, 3, half_width);
    matriz.modificarElemento (1, 3, half_height);
    
    multiplicar(matriz);
  }

  

  /**
   * Rotacion alrededor del eje X
   * 
   * @param angle  Angulo a rotar en radianes
   */
  public void rotacionX (double angle)
  {
    Matriz4x4 matriz = new Matriz4x4();

    double cosAngle = Math.cos (angle);
    double sinAngle = Math.sin (angle);  

    matriz.modificarElemento (1, 1,  cosAngle);
    matriz.modificarElemento (1, 2,  sinAngle);
    matriz.modificarElemento (2, 1, -sinAngle);
    matriz.modificarElemento (2, 2,  cosAngle);

    multiplicar (matriz);
  }



  /**
   * Rotacion alrededor del eje Y
   * 
   * @param angle  Angulo a rotar en radianes
   */
  public void rotacionY (double angle)
  {
    Matriz4x4 matriz = new Matriz4x4();

    double cosAngle = Math.cos (angle);
    double sinAngle = Math.sin (angle);  

    matriz.modificarElemento (0, 0,  cosAngle);
    matriz.modificarElemento (0, 2, -sinAngle);
    matriz.modificarElemento (2, 0,  sinAngle);
    matriz.modificarElemento (2, 2,  cosAngle);

    multiplicar (matriz);
  }



  /**
   * Rotacion alrededor del eje Z
   * 
   * @param angle  Angulo a rotar en radianes
   */
  public void rotacionZ (double angle)
  {
    Matriz4x4 matriz = new Matriz4x4();

    double cosAngle = Math.cos (angle);
    double sinAngle = Math.sin (angle);  

    matriz.modificarElemento (0, 0,  cosAngle);
    matriz.modificarElemento (0, 1,  sinAngle);
    matriz.modificarElemento (1, 0, -sinAngle);
    matriz.modificarElemento (1, 1,  cosAngle);

    multiplicar (matriz);
  }
 
  /**
   * Aplica una matriz de escalado a esta matriz.
   * 
   * @param x Escalado en x 
   * @param y Escalado en y
   * @param z Escalado en z
   */
  public void escalado (double x, double y, double z)
  {
    Matriz4x4  matriz = new Matriz4x4();

    matriz.modificarElemento (0, 0, x);
    matriz.modificarElemento (1, 1, y);
    matriz.modificarElemento (2, 2, z);  
    
    multiplicar (matriz);
  }


  /**
   * Invierte esta matriz
   */
  public void invertir()
  {
    double[] tmp = new double[12];
    double[] src = new double[16];
    double[] dst = new double[16];  

    // Transpose matrix
    for (int i = 0; i < 4; i++) {
      src[i +  0] = m_[i*4 + 0];
      src[i +  4] = m_[i*4 + 1];
      src[i +  8] = m_[i*4 + 2];
      src[i + 12] = m_[i*4 + 3];
    }

    // Calculate pairs for first 8 elements (cofactors) 
    tmp[0] = src[10] * src[15];
    tmp[1] = src[11] * src[14];
    tmp[2] = src[9]  * src[15];
    tmp[3] = src[11] * src[13];
    tmp[4] = src[9]  * src[14];
    tmp[5] = src[10] * src[13];
    tmp[6] = src[8]  * src[15];
    tmp[7] = src[11] * src[12];
    tmp[8] = src[8]  * src[14];
    tmp[9] = src[10] * src[12];
    tmp[10] = src[8] * src[13];
    tmp[11] = src[9] * src[12];
    
    // Calculate first 8 elements (cofactors)
    dst[0]  = tmp[0]*src[5] + tmp[3]*src[6] + tmp[4]*src[7];
    dst[0] -= tmp[1]*src[5] + tmp[2]*src[6] + tmp[5]*src[7];
    dst[1]  = tmp[1]*src[4] + tmp[6]*src[6] + tmp[9]*src[7];
    dst[1] -= tmp[0]*src[4] + tmp[7]*src[6] + tmp[8]*src[7];
    dst[2]  = tmp[2]*src[4] + tmp[7]*src[5] + tmp[10]*src[7];
    dst[2] -= tmp[3]*src[4] + tmp[6]*src[5] + tmp[11]*src[7];
    dst[3]  = tmp[5]*src[4] + tmp[8]*src[5] + tmp[11]*src[6];
    dst[3] -= tmp[4]*src[4] + tmp[9]*src[5] + tmp[10]*src[6];
    dst[4]  = tmp[1]*src[1] + tmp[2]*src[2] + tmp[5]*src[3];
    dst[4] -= tmp[0]*src[1] + tmp[3]*src[2] + tmp[4]*src[3];
    dst[5]  = tmp[0]*src[0] + tmp[7]*src[2] + tmp[8]*src[3];
    dst[5] -= tmp[1]*src[0] + tmp[6]*src[2] + tmp[9]*src[3];
    dst[6]  = tmp[3]*src[0] + tmp[6]*src[1] + tmp[11]*src[3];
    dst[6] -= tmp[2]*src[0] + tmp[7]*src[1] + tmp[10]*src[3];
    dst[7]  = tmp[4]*src[0] + tmp[9]*src[1] + tmp[10]*src[2];
    dst[7] -= tmp[5]*src[0] + tmp[8]*src[1] + tmp[11]*src[2];
    
    // Calculate pairs for second 8 elements (cofactors)
    tmp[0]  = src[2]*src[7];
    tmp[1]  = src[3]*src[6];
    tmp[2]  = src[1]*src[7];
    tmp[3]  = src[3]*src[5];
    tmp[4]  = src[1]*src[6];
    tmp[5]  = src[2]*src[5];
    tmp[6]  = src[0]*src[7];
    tmp[7]  = src[3]*src[4];
    tmp[8]  = src[0]*src[6];
    tmp[9]  = src[2]*src[4];
    tmp[10] = src[0]*src[5];
    tmp[11] = src[1]*src[4];

    // Calculate second 8 elements (cofactors)
    dst[8]   = tmp[0] * src[13]  + tmp[3] * src[14]  + tmp[4] * src[15];
    dst[8]  -= tmp[1] * src[13]  + tmp[2] * src[14]  + tmp[5] * src[15];
    dst[9]   = tmp[1] * src[12]  + tmp[6] * src[14]  + tmp[9] * src[15];
    dst[9]  -= tmp[0] * src[12]  + tmp[7] * src[14]  + tmp[8] * src[15];
    dst[10]  = tmp[2] * src[12]  + tmp[7] * src[13]  + tmp[10]* src[15];
    dst[10] -= tmp[3] * src[12]  + tmp[6] * src[13]  + tmp[11]* src[15];
    dst[11]  = tmp[5] * src[12]  + tmp[8] * src[13]  + tmp[11]* src[14];
    dst[11] -= tmp[4] * src[12]  + tmp[9] * src[13]  + tmp[10]* src[14];
    dst[12]  = tmp[2] * src[10]  + tmp[5] * src[11]  + tmp[1] * src[9];
    dst[12] -= tmp[4] * src[11]  + tmp[0] * src[9]   + tmp[3] * src[10];
    dst[13]  = tmp[8] * src[11]  + tmp[0] * src[8]   + tmp[7] * src[10];
    dst[13] -= tmp[6] * src[10]  + tmp[9] * src[11]  + tmp[1] * src[8];
    dst[14]  = tmp[6] * src[9]   + tmp[11]* src[11]  + tmp[3] * src[8];
    dst[14] -= tmp[10]* src[11 ] + tmp[2] * src[8]   + tmp[7] * src[9];
    dst[15]  = tmp[10]* src[10]  + tmp[4] * src[8]   + tmp[9] * src[9];
    dst[15] -= tmp[8] * src[9]   + tmp[11]* src[10]  + tmp[5] * src[8];

    // Calculate determinant
    double det = src[0]*dst[0] + src[1]*dst[1] + src[2]*dst[2] + src[3]*dst[3];
    
    // Calculate matrix inverse
    det = 1.0 / det;
    for (int i = 0; i < 16; i++)
      m_[i] = dst[i] * det;
  }


  /**
   * Devuelve la matriz traspuesta
   * 
   * @param matriz  La matriz original
   * @return        La matriz traspuesta a la original
   */
  public static Matriz4x4 traspuesta(Matriz4x4 matriz)
  {
    Matriz4x4 m = new Matriz4x4 (matriz);
    m.transponer();
    return m;
  }
 
  /**
   * Devuelve la matriz inversa
   * 
   * @param matriz  La matriz original
   * @return        La matriz inversa a la original
   */
  public static Matriz4x4 inversa (Matriz4x4 matriz)
  {
    Matriz4x4 m = new Matriz4x4 (matriz);
    m.invertir();
    return m;
  }
}
