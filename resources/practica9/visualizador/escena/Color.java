package escena;

/**
 * Esta clase representa un color en coordenadas RGB
 * 
 * R - Red - Rojo
 * G - Green - Verde
 * B - Blue - Azul
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class Color {
	private double c_[];
	
	/**
	 * Inicializa la memoria. Este metodo es privado y
	 * se llama desde todos los constructores
	 */
	private void inicializar()
	{
	    c_ = new double[3];
	}
	
	/**
	 * Modifica las componentes de este color
	 * 
	 * @param r  Nueva r - rojo
	 * @param g  Nueva g - verde
	 * @param b  Nueva b - azul
	 */
	public void modificar(double r, double g, double b)
	{
		c_[0]=r; c_[1]=g; c_[2]=b;
	}
	
	/**
	 * Modifica las componentes de este color sacandolas de otro color
	 * 
	 * @param c El otro color
	 */
	public void modificar(Color c)
	{
		modificar(c.r(),c.g(),c.b());
	}

	/**
	 * Constructor por defecto, todas las componentes a 0 (negro)
	 */
	public Color()
	{
		inicializar(); modificar(0.0,0.0,0.0);
	}
	
	/**
	 * Constructor a partir de las componentes r g b 
	 * 
	 * @param r Rojo
	 * @param g Verde
	 * @param b Azul
	 */
	public Color(double r, double g, double b)
	{
		inicializar(); modificar(r,g,b);
	}
	
	/**
	 * Canal rojo
	 * 
	 * @return Canal rojo
	 */
	public double r() { return c_[0]; }
	
	/**
	 * Canal verde
	 * 
	 * @return Canal verde
	 */
	public double g() { return c_[1]; }
	
	/**
	 * Canal azul
	 * 
	 * @return Canal azul
	 */
	public double b() { return c_[2]; }
	
	/**
	 * Constructor por copia: construye un color a partir de otro
	 * 
	 * @param c El otro color
	 */
	public Color(Color c)
	{
		inicializar(); modificar(c.r(),c.g(),c.b());
	}
	
	/**
	 * Suma a este color, otro color, componente a componente
	 * 
	 * @param c Color a sumar
	 */
	public void sumar(Color c)
	{
		modificar(this.r()+c.r(),this.g()+c.g(),this.b()+c.b());
	}

	/**
	 * Multiplica a este color, otro color, componente a componente
	 * 
	 * @param c Color a multiplicar
	 */
	public void multiplicar(Color c)
	{
		modificar(this.r()*c.r(),this.g()*c.g(),this.b()*c.b());
	}
	
	/**
	 * Multiplica este color por un numero real, componente a componente
	 * 
	 * @param f El factor
	 */
	public void multiplicar(double f)
	{
		modificar(this.r()*f,this.g()*f,this.b()*f);		
	}
	
	/**
	 * Devuelve la suma de este color con otro
	 * 
	 * @param c El otro color
	 * @return La suma entre este color y c
	 */
	public Color sumado(Color c)
	{
		return new Color(this.r()+c.r(),this.g()+c.g(),this.b()+c.b());
	}

	/**
	 * Devuelve la multiplicacion de este color con otro
	 * 
	 * @param c El otro color
	 * @return La multiplicacion entre este color y c
	 */
	public Color multiplicado(Color c)
	{
		return new Color(this.r()*c.r(),this.g()*c.g(),this.b()*c.b());
	}
	
	/**
	 * Devuelve la multiplicacion de este color por un factor
	 * 
	 * @param f El factor
	 * @return La multiplicacion entre este color y f
	 */
	public Color multiplicado(double f)
	{
		return new Color(this.r()*f,this.g()*f,this.b()*f);
	}
	
	/**
	 * Devuelve este color transformado al formato de color que entiende Java,
	 * para pintar por pantalla
	 * 
	 * @return El color en formato java
	 */
	public java.awt.Color aAwtColor()
	{
		int r = (int)(this.r()*255.0); if (r<0) r=0; if (r>255) r=255;
		int g = (int)(this.g()*255.0); if (g<0) g=0; if (g>255) g=255;
		int b = (int)(this.b()*255.0); if (b<0) b=0; if (b>255) b=255;
		return new java.awt.Color(r,g,b);
	}
}
