package escena;

/**
 * Clase que contiene la definicion de una camara a traves de la que se
 * observa una escena
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class Camara {
	private static final double DISTANCIA_MINIMA = 5.0;
	private static final double DISTANCIA_MAXIMA = 10000.0;
	private static final double FOV_MINIMO = 10.0;
	private static final double FOV_MAXIMO = 120.0;
	private static final double INCLINACION_MINIMA = -89.0;
	private static final double INCLINACION_MAXIMA = 89.0;

	private double _distancia;
	private double _fov;
	private double _inclinacion;
	private double _rotacion;
	private double _desplazamientoX;
	private double _desplazamientoY;
	private double _desplazamientoZ;
	
	geometria.Punto _posicion;
	
	/**
	 * Constructor de la camara
	 * 
	 * @param distancia Distancia al objecto
	 * @param fov Field of view, angulo de vision horizontal (en grados)
	 * @param inclinacion Inclinacion de la camara sobre el plano horizontal (en grados)
	 * @param rotacion Rotacion de la camara alrededor del objeto (en grados)
	 */
	public Camara(double distancia, double fov, double inclinacion, double rotacion)
	{
		this(distancia, fov, inclinacion, rotacion, 0.0, 0.0, 0.0);
	}

	public Camara(double distancia, double fov, double inclinacion, double rotacion,
			double desplazamientoX, double desplazamientoY, double desplazamientoZ)
	{
		_posicion = new geometria.Punto();
		_desplazamientoX = desplazamientoX;
		_desplazamientoY = desplazamientoY;
		_desplazamientoZ = desplazamientoZ;
		modificarParametros(distancia, fov, inclinacion, rotacion);
	}

	private double limitar(double value, double minimo, double maximo)
	{
		if (value < minimo)
		{
			return minimo;
		}
		if (value > maximo)
		{
			return maximo;
		}
		return value;
	}

	private void actualizarPosicion()
	{
		double rotacionRad = Math.toRadians(_rotacion);
		double inclinacionRad = Math.toRadians(_inclinacion);
		_posicion.modificar(
			-_distancia * Math.sin(-rotacionRad) * Math.cos(-inclinacionRad),
			_distancia * Math.sin(-inclinacionRad),
			_distancia * Math.cos(-rotacionRad) * Math.cos(-inclinacionRad));
	}

	public void modificarParametros(double distancia, double fov, double inclinacion, double rotacion)
	{
		_distancia = limitar(distancia, DISTANCIA_MINIMA, DISTANCIA_MAXIMA);
		_fov = limitar(fov, FOV_MINIMO, FOV_MAXIMO);
		_inclinacion = limitar(inclinacion, INCLINACION_MINIMA, INCLINACION_MAXIMA);
		_rotacion = rotacion;
		actualizarPosicion();
	}

	public void modificarDistancia(double distancia)
	{
		_distancia = limitar(distancia, DISTANCIA_MINIMA, DISTANCIA_MAXIMA);
		actualizarPosicion();
	}

	public void modificarFov(double fov)
	{
		_fov = limitar(fov, FOV_MINIMO, FOV_MAXIMO);
	}

	public void modificarInclinacion(double inclinacion)
	{
		_inclinacion = limitar(inclinacion, INCLINACION_MINIMA, INCLINACION_MAXIMA);
		actualizarPosicion();
	}

	public void modificarRotacion(double rotacion)
	{
		_rotacion = rotacion;
		actualizarPosicion();
	}

	public void orbitar(double deltaRotacion, double deltaInclinacion)
	{
		_rotacion += deltaRotacion;
		_inclinacion = limitar(_inclinacion + deltaInclinacion, INCLINACION_MINIMA, INCLINACION_MAXIMA);
		actualizarPosicion();
	}

	public void zoom(double deltaDistancia)
	{
		modificarDistancia(_distancia + deltaDistancia);
	}

	public void modificarDesplazamiento(double desplazamientoX, double desplazamientoY, double desplazamientoZ)
	{
		_desplazamientoX = desplazamientoX;
		_desplazamientoY = desplazamientoY;
		_desplazamientoZ = desplazamientoZ;
	}

	public void desplazar(double deltaX, double deltaY)
	{
		desplazar(deltaX, deltaY, Math.max(1.0, _distancia) * 0.0035);
	}

	public void desplazar(double deltaX, double deltaY, double unidadesPorPixel)
	{
		double rotacionRad = Math.toRadians(_rotacion);
		double inclinacionRad = Math.toRadians(_inclinacion);
		double escala = Math.max(1.0e-4, unidadesPorPixel);

		double rightX = Math.cos(rotacionRad);
		double rightY = 0.0;
		double rightZ = -Math.sin(rotacionRad);

		double upX = Math.sin(rotacionRad) * Math.sin(inclinacionRad);
		double upY = Math.cos(inclinacionRad);
		double upZ = Math.cos(rotacionRad) * Math.sin(inclinacionRad);

		_desplazamientoX += (deltaX * rightX + deltaY * upX) * escala;
		_desplazamientoY += (deltaX * rightY + deltaY * upY) * escala;
		_desplazamientoZ += (deltaX * rightZ + deltaY * upZ) * escala;
	}

	public void reiniciar(double distancia, double fov, double inclinacion, double rotacion)
	{
		_desplazamientoX = 0.0;
		_desplazamientoY = 0.0;
		_desplazamientoZ = 0.0;
		modificarParametros(distancia, fov, inclinacion, rotacion);
	}

	public void reiniciar()
	{
		reiniciar(100.0, 10.0, 0.0, 0.0);
	}
	
	/**
	 * Distancia al objeto
	 * @return Distancia al objeto
	 */
	public double distancia()
	{
		return _distancia;
	}
	
	/**
	 * Angulo de vision horizontal (en grados)
	 * @return Angulo de vision horizontal (en grados)
	 */
	public double fov()
	{
		return _fov;
	}
	
	/**
	 * Inclinacion sobre el plano horiztonal (en grados)
	 * @return Inclinacion sobre el plano horiztonal (en grados)
	 */
	public double inclinacion()
	{
		return _inclinacion;
	}
	
	/**
	 * Rotacion de la camara alrededor del objeto (en grados)
	 * @return Rotacion de la camara alrededor del objeto (en grados)
	 */
	public double rotacion()
	{
		return _rotacion;
	}
	
	/**
	 * Devuelve la posicion de la camara
	 * 
	 * @return Posicion de la camara
	 */
	public geometria.Punto posicion()
	{
		return _posicion;
	}

	public geometria.Punto foco(geometria.Punto centroObjeto)
	{
		return new geometria.Punto(
			centroObjeto.x() + _desplazamientoX,
			centroObjeto.y() + _desplazamientoY,
			centroObjeto.z() + _desplazamientoZ);
	}

	public geometria.Punto posicion(geometria.Punto centroObjeto)
	{
		geometria.Punto foco = foco(centroObjeto);
		return new geometria.Punto(foco.x() + _posicion.x(), foco.y() + _posicion.y(), foco.z() + _posicion.z());
	}

	public double desplazamientoX()
	{
		return _desplazamientoX;
	}

	public double desplazamientoY()
	{
		return _desplazamientoY;
	}

	public double desplazamientoZ()
	{
		return _desplazamientoZ;
	}
}
