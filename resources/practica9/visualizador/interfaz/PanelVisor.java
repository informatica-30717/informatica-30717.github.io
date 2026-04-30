package interfaz;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.*;
import java.util.Arrays;

import renderer.PostProcess;
import renderer.RenderBuffers;
import renderer.RenderMode;
import renderer.Shader;

/**
 * Este panel representa la parte de interfaz gráfico
 * que permite visualizar un objeto, ya sea en malla de alambre
 * o raster
 * 
 * @author Adolfo
 */
public class PanelVisor extends JPanel {
	public static interface CameraListener {
		void cameraChanged(escena.Camara camera);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3552228178473578163L;
	private static final double FAR_DEPTH = 1.0e20;
	private static final int DEFAULT_RENDER_SCALE = 1;
	private static final double INTERACTION_RENDER_FACTOR = 0.65;
	private static final double FOV_INICIAL = 28.0;
	private static final double INCLINACION_INICIAL = 12.0;
	private static final double ROTACION_INICIAL = -24.0;
	
	private geometria.Objeto _objeto;
	
	private escena.Camara _camara;
	private geometria.Matriz4x4 _proyeccion;
	
	//Just for rasterizing color
	private escena.Material _material;
	private escena.Luz _luz;
	private final RenderBuffers renderBuffers;
	private final Shader shader;
	private final PostProcess postProcess;
	private RenderMode renderMode;
	private String statusMessage;
	private Point lastMousePoint;
	private CameraListener cameraListener;
	private int renderScale;
	private boolean sceneDirty;
	private boolean cameraInteracting;
	private long lastRenderNanos;
	private int lastRenderedFaces;
	private int projectionWidth;
	private int projectionHeight;
	private int[] scanlineXIni;
	private int[] scanlineXFin;
	private double[] scanlineZIni;
	private double[] scanlineZFin;
	private double[] scanlinePIniX;
	private double[] scanlinePIniY;
	private double[] scanlinePIniZ;
	private double[] scanlinePFinX;
	private double[] scanlinePFinY;
	private double[] scanlinePFinZ;
	private double[] scanlineNIniX;
	private double[] scanlineNIniY;
	private double[] scanlineNIniZ;
	private double[] scanlineNFinX;
	private double[] scanlineNFinY;
	private double[] scanlineNFinZ;
	private int scanlineStartY;
	private int scanlineEndY;
	private double[] projectedX;
	private double[] projectedY;
	private double[] projectedZ;
	private double projectionM00;
	private double projectionM01;
	private double projectionM02;
	private double projectionM03;
	private double projectionM10;
	private double projectionM11;
	private double projectionM12;
	private double projectionM13;
	private double projectionM20;
	private double projectionM21;
	private double projectionM22;
	private double projectionM23;
	private double projectionM30;
	private double projectionM31;
	private double projectionM32;
	private double projectionM33;

	//Different modes
	boolean _backfaceCulling; //Activates / deactivates backface culling
	
	/**
	 * Modifica el material de Phong del objeto para dibujar la escena.
	 * 
	 * Solo es util en modo raster
	 * 
	 * @param m El nuevo material
	 */
	public void modificarMaterial(escena.Material m)
	{
		_material = m;
		marcarEscenaSucia();
		if (renderMode.isRaster()) this.repaint();
	}
	
	/**
	 * Material del objeto
	 * 
	 * @return Material del objeto
	 */
	public escena.Material material()
	{
		return _material;
	}
	
	/**
	 * Modifica la iluminacion de la escena
	 * 
	 * Solo tiene sentido en modo raster, que es cuando
	 * se tiene en cuenta
	 * 
	 * @param l La nueva iluminacion de la escena
	 */
	public void modificarLuz(escena.Luz l)
	{
		_luz = l;
		marcarEscenaSucia();
		if (renderMode.isRaster()) this.repaint();
	}
	
	/**
	 * Iluminacion de la escena
	 * 
	 * @return Iluminacion de la escena
	 */
	public escena.Luz luz()
	{
		return _luz;
	}
	
	/**
	 * Modifica el modo de dibujo para que sea malla de alambre
	 */
	public void modificarMallaAlambre()
	{
		renderMode = RenderMode.WIREFRAME;
		marcarEscenaSucia();
		actualizarTituloVentana();
		this.repaint();
	}
	
	/**
	 * Comprueba el modo de dibujo
	 * 
	 * @return true si es malla de alambre
	 */
	public boolean mallaAlambre()
	{
		return renderMode == RenderMode.WIREFRAME;
	}
	
	/**
	 * Modifica el modo de dibujo para que sea raster
	 */
	public void modificarRaster()
	{
		renderMode = RenderMode.PHONG;
		marcarEscenaSucia();
		actualizarTituloVentana();
		this.repaint();
	}
	
	/**
	 * Comprueba el modo de dibujo
	 * 
	 * @return true si es raster
	 */
	public boolean raster()
	{
		return renderMode.isRaster();
	}

	public void modificarModoRender(RenderMode newMode)
	{
		renderMode = newMode;
		marcarEscenaSucia();
		actualizarTituloVentana();
		repaint();
	}

	public RenderMode modoRender()
	{
		return renderMode;
	}
	
	/**
	 * Modifica la utilizacion del backface culling
	 * 
	 * @param activated true para activarlo, false para desactivarlo
	 */
	public void modificarBackfaceCulling(boolean activated)
	{
		_backfaceCulling = activated;
		marcarEscenaSucia();
		repaint();
	}

	private void marcarEscenaSucia()
	{
		sceneDirty = true;
	}
	
	/**
	 * Comprueba si esta activado o no el backface culling
	 * 
	 * @return true si esta activado
	 */
	public boolean backfaceCulling()
	{
		return _backfaceCulling;
	}
	
	/**
	 * Constructor
	 * 
	 * @param o El objeto a visualizar
	 */
	public PanelVisor(geometria.Objeto o) {
	    setPreferredSize(new Dimension(600, 600));
	    _objeto=o;
	    renderBuffers = new RenderBuffers();
	    shader = new Shader();
	    postProcess = new PostProcess();
	    _camara = new escena.Camara(18, FOV_INICIAL, INCLINACION_INICIAL, ROTACION_INICIAL);
	    _luz = new escena.Luz(
	    		   new escena.Color(1.0,1.0,1.0),
	               new geometria.Punto(100.0,100.0,1000.0),
	               new escena.Color(0.1,0.1,0.1));
	    _material = new escena.Material(new escena.Color(0.6,0.1,0.1),1.0,10.0);
	    renderMode = RenderMode.CLAY;
	    renderScale = DEFAULT_RENDER_SCALE;
	    sceneDirty = true;
	    statusMessage = "Arrastra para orbitar. Mayús o botón derecho desplazan. Rueda para zoom.";
	    _backfaceCulling=false;
	    setFocusable(true);
	    configurarAtajos();
	    configurarInteraccionCamara();
	    actualizar();
	  }
	  
	/**
	 * Camara con la que se enfoca al objeto
	 * 
	 * @return Camara con la que se enfoca al objeto
	 */
	public escena.Camara camara()
	{
		return _camara;
	}
	
	/**
	 * Modifica la camara con la que se enfoca al objeto
	 * 
	 * @param camara Nueva camara
	 */
	public void modificarCamara(escena.Camara camara)
	{
		_camara=camara;
		this.actualizar();
		notificarCambioCamara();
		this.repaint();
	}

	public void establecerListenerCamara(CameraListener listener)
	{
		cameraListener = listener;
		notificarCambioCamara();
	}

	private void notificarCambioCamara()
	{
		if (cameraListener != null)
		{
			cameraListener.cameraChanged(_camara);
		}
	}

	private geometria.Punto focoCamara()
	{
		return _camara.foco(_objeto.centro());
	}

	private geometria.Punto posicionCamara()
	{
		return _camara.posicion(_objeto.centro());
	}

	private int anchoRender()
	{
		double factor = cameraInteracting && renderMode.isRaster() ? INTERACTION_RENDER_FACTOR : 1.0;
		return Math.max(1, (int) Math.round(getWidth() * renderScale * factor));
	}

	private int altoRender()
	{
		double factor = cameraInteracting && renderMode.isRaster() ? INTERACTION_RENDER_FACTOR : 1.0;
		return Math.max(1, (int) Math.round(getHeight() * renderScale * factor));
	}

	public int escalaRender()
	{
		return renderScale;
	}

	public void modificarEscalaRender(int nuevaEscala)
	{
		int escalaNormalizada = Math.max(1, Math.min(3, nuevaEscala));
		if (renderScale == escalaNormalizada)
		{
			return;
		}
		renderScale = escalaNormalizada;
		statusMessage = "Resolución interna: " + renderScale + "x";
		actualizar();
		actualizarTituloVentana();
		repaint();
	}

	private double tamanoObjeto()
	{
		return Math.max(1.0, _objeto.tamanoAabb());
	}

	private double radioObjeto()
	{
		return Math.max(0.5, _objeto.radioAabb());
	}

	private double distanciaCamaraPorDefecto()
	{
		double radio = radioObjeto();
		double distancia = (radio * 1.45) / Math.tan(Math.toRadians(FOV_INICIAL * 0.5));
		return Math.max(6.0, distancia * 1.2);
	}

	private double escalaDesplazamientoPorPixel()
	{
		double altoViewport = Math.max(1.0, getHeight());
		double distancia = Math.max(_camara.distancia(), radioObjeto());
		double unidadesPorPixel = (2.0 * Math.tan(Math.toRadians(_camara.fov() * 0.5)) * distancia) / altoViewport;
		return Math.max(unidadesPorPixel, tamanoObjeto() / 900.0);
	}
		
	/**
	 * Devuelve la matriz de proyeccion perspectiva para
	 * la escena actual
	 * 
	 * @return La matriz actual
	 */
	private geometria.Matriz4x4 matrizDeProyeccion()
	{
		int width = anchoRender();
		int height = altoRender();
		double tamanoEscena = tamanoObjeto();
		double nearPlane = Math.max(0.25, tamanoEscena * 0.02);
		double farPlane = Math.max(250.0, _camara.distancia() + tamanoEscena * 12.0);
		//Las matrices multiplican a los vectores desde la izquierda.
		//Eso significa que la ultima transformacion que se incluye
		//en la matriz es la primera que se aplica al vector.
		//O sea, que se han de mirar las matrices de abajo a arriba
		//dentro de este codigo
		
	    geometria.Matriz4x4 proyeccion = new geometria.Matriz4x4();
	    //Transformamos las unidades reales a unidades medidas en 
	    // pixeles de pantalla
	    proyeccion.pantalla(width, height);
	    //Aplicamos la transformacion perspectiva
	    proyeccion.perspectiva(camara().fov()*Math.PI/180.0,
	    						(double) width / (double) height,
	    						nearPlane,farPlane);
	    //Alejamos la camara del objeto en la proyeccion
	    proyeccion.traslacion(0, 0, camara().distancia());
	    //Incluimos la inclinacion de la camara
	    proyeccion.rotacionX(camara().inclinacion()*Math.PI/180.0);
	    //Hacemos que la camara tenga en cuenta la rotacion alrededor 
	    //  del eje vertical
	    proyeccion.rotacionY(camara().rotacion()*Math.PI/180.0);
	    //Hacemos que la camara apunte al centro del objeto
	    geometria.Punto foco = focoCamara();
	    proyeccion.traslacion(-foco.x(), -foco.y(), -foco.z());
	    
	    return proyeccion;
	}
	
	/**
	 * Actualiza la matriz de proyeccion
	 */
	private void actualizar()
	{
		_proyeccion=matrizDeProyeccion();
		projectionWidth = anchoRender();
		projectionHeight = altoRender();
		actualizarCacheProyeccion();
		marcarEscenaSucia();
	}

	private boolean proyeccionDesactualizada()
	{
		return _proyeccion == null || projectionWidth != anchoRender() || projectionHeight != altoRender();
	}

	private void actualizarCacheProyeccion()
	{
		projectionM00 = _proyeccion.elemento(0, 0);
		projectionM01 = _proyeccion.elemento(0, 1);
		projectionM02 = _proyeccion.elemento(0, 2);
		projectionM03 = _proyeccion.elemento(0, 3);
		projectionM10 = _proyeccion.elemento(1, 0);
		projectionM11 = _proyeccion.elemento(1, 1);
		projectionM12 = _proyeccion.elemento(1, 2);
		projectionM13 = _proyeccion.elemento(1, 3);
		projectionM20 = _proyeccion.elemento(2, 0);
		projectionM21 = _proyeccion.elemento(2, 1);
		projectionM22 = _proyeccion.elemento(2, 2);
		projectionM23 = _proyeccion.elemento(2, 3);
		projectionM30 = _proyeccion.elemento(3, 0);
		projectionM31 = _proyeccion.elemento(3, 1);
		projectionM32 = _proyeccion.elemento(3, 2);
		projectionM33 = _proyeccion.elemento(3, 3);
	}

	private void transformarPunto(geometria.Punto source, int index)
	{
		double x = source.x();
		double y = source.y();
		double z = source.z();
		double projectedW = projectionM30 * x + projectionM31 * y + projectionM32 * z + projectionM33;
		if (projectedW == 0.0)
		{
			projectedW = 1.0e-12;
		}
		double inverseW = 1.0 / projectedW;
		projectedX[index] = (projectionM00 * x + projectionM01 * y + projectionM02 * z + projectionM03) * inverseW;
		projectedY[index] = (projectionM10 * x + projectionM11 * y + projectionM12 * z + projectionM13) * inverseW;
		projectedZ[index] = (projectionM20 * x + projectionM21 * y + projectionM22 * z + projectionM23) * inverseW;
	}

	private void transformarPunto(geometria.Punto source, double[] destination)
	{
		double x = source.x();
		double y = source.y();
		double z = source.z();
		double projectedW = projectionM30 * x + projectionM31 * y + projectionM32 * z + projectionM33;
		if (projectedW == 0.0)
		{
			projectedW = 1.0e-12;
		}
		double inverseW = 1.0 / projectedW;
		destination[0] = (projectionM00 * x + projectionM01 * y + projectionM02 * z + projectionM03) * inverseW;
		destination[1] = (projectionM10 * x + projectionM11 * y + projectionM12 * z + projectionM13) * inverseW;
		destination[2] = (projectionM20 * x + projectionM21 * y + projectionM22 * z + projectionM23) * inverseW;
	}

	private void asegurarCapacidadScanline(int renderHeight)
	{
		if (scanlineXIni != null && scanlineXIni.length == renderHeight)
		{
			return;
		}
		scanlineXIni = new int[renderHeight];
		scanlineXFin = new int[renderHeight];
		scanlineZIni = new double[renderHeight];
		scanlineZFin = new double[renderHeight];
		scanlinePIniX = new double[renderHeight];
		scanlinePIniY = new double[renderHeight];
		scanlinePIniZ = new double[renderHeight];
		scanlinePFinX = new double[renderHeight];
		scanlinePFinY = new double[renderHeight];
		scanlinePFinZ = new double[renderHeight];
		scanlineNIniX = new double[renderHeight];
		scanlineNIniY = new double[renderHeight];
		scanlineNIniZ = new double[renderHeight];
		scanlineNFinX = new double[renderHeight];
		scanlineNFinY = new double[renderHeight];
		scanlineNFinZ = new double[renderHeight];
	}

	private void asegurarCapacidadProyectada(int vertexCount)
	{
		if (projectedX != null && projectedX.length >= vertexCount)
		{
			return;
		}
		projectedX = new double[vertexCount];
		projectedY = new double[vertexCount];
		projectedZ = new double[vertexCount];
	}
	
	/**
	 * Calcula el color de phong para un punto concreto y una normal
	 * @param punto El punto en el que estamos comprobando el color
	 * @param n La normal a la superficie en ese punto
	 * @return El color, ya en formato de java
	 */
	private java.awt.Color colorPhong(geometria.Punto punto, geometria.Normal n)
	{	
		//Direccion de la camara
		geometria.Direccion cameraDirection =
			new geometria.Direccion(punto,this.posicionCamara());
		cameraDirection.normalizar();
		
		//Calculamos iluminacion ambiente
		escena.Color ambientColor = _luz.colorAmbiente().multiplicado(_material.kd());

		//Calculamos iluminacion difusa
		geometria.Direccion d = new geometria.Direccion(punto,this._luz.posicion());
		d.normalizar();
		double diffuseCosine = d.aVector4().productoEscalar(n.aVector4());
		if (diffuseCosine < 0) diffuseCosine = 0;
		escena.Color diffuseColor = _luz.color().multiplicado(_material.kd()).multiplicado(diffuseCosine);
		
		//Calculamos iluminacion especular
		double specularCosine = 0;
		if (diffuseCosine>0)
		{
			geometria.Direccion reflectedDirection = cameraDirection.reflejado(n);
			specularCosine = d.aVector4().productoEscalar(reflectedDirection.aVector4());
			if (specularCosine<0) specularCosine = 0;
		}
		escena.Color specularColor = _luz.color().multiplicado(_material.ks()).multiplicado(Math.pow(specularCosine, _material.es()));
		
		//Devolvemos todo sumado y en formato de java
		return ambientColor.sumado(diffuseColor).sumado(specularColor).aAwtColor();
	}
	
	/**
	 * A partir de las aristas de una cara, calcula mediante interpolacion los valores
	 * iniciales y finales de puntos, zs y normales para después hacer scanline e interpolar
	 * 
	 * Se llama solo en modo raster
	 * 
	 * @param cara La cara en si
	 * @param xIni Pixeles inicial en el eje x
	 * @param xFin Pixeles final en el eje y
	 * @param zIni Profundidades z iniciales, para el z-buffer
	 * @param zFin Profundidades z finales, para el z-buffer
	 * @param nIni Normales iniciales, para interpolar
	 * @param nFin Normales finales, para interpolar
	 * @param pIni Puntos iniciales, para  interpolar
	 * @param pFin Puntos finales, para interpolar
	 */
	private void modificarBordesScanline(geometria.Cara cara,
			int renderWidth, int renderHeight)
	{
		int vertexCount = cara.vertices().length;
		asegurarCapacidadProyectada(vertexCount);
		scanlineStartY = renderHeight;
		scanlineEndY = -1;
		geometria.Normal faceNormal = cara.normal();

		for (int i = 0; i < vertexCount; i++)
		{
			transformarPunto(cara.vertice(i).punto, i);
			int y = (int) projectedY[i];
			if (y < scanlineStartY)
			{
				scanlineStartY = y;
			}
			if (y > scanlineEndY)
			{
				scanlineEndY = y;
			}
		}

		if (scanlineStartY == scanlineEndY)
		{
			scanlineStartY = 0;
			scanlineEndY = -1;
			return;
		}

		scanlineStartY = Math.max(0, scanlineStartY);
		scanlineEndY = Math.min(renderHeight - 1, scanlineEndY);
		Arrays.fill(scanlineXIni, scanlineStartY, scanlineEndY + 1, renderWidth + 1);
		Arrays.fill(scanlineXFin, scanlineStartY, scanlineEndY + 1, -1);

		for (int i = 0; i < vertexCount; i++)
		{
			int next = (i + 1) % vertexCount;
			double v1x = projectedX[i];
			double v1y = projectedY[i];
			double v1z = projectedZ[i];
			double v2x = projectedX[next];
			double v2y = projectedY[next];
			double v2z = projectedZ[next];
			geometria.Punto p1 = cara.vertice(i).punto;
			geometria.Punto p2 = cara.vertice(next).punto;
			geometria.Normal n1 = cara.vertice(i).normal != null ? cara.vertice(i).normal : faceNormal;
			geometria.Normal n2 = cara.vertice(next).normal != null ? cara.vertice(next).normal : faceNormal;

			if (v2y < v1y)
			{
				double tempValue = v1x; v1x = v2x; v2x = tempValue;
				tempValue = v1y; v1y = v2y; v2y = tempValue;
				tempValue = v1z; v1z = v2z; v2z = tempValue;
				geometria.Punto tempPoint = p1; p1 = p2; p2 = tempPoint;
				geometria.Normal tempNormal = n1; n1 = n2; n2 = tempNormal;
			}

			double deltaY = v2y - v1y;
			if (deltaY == 0.0)
			{
				continue;
			}

			double deltaT = 1.0 / deltaY;
			double t = 0.0;
			for (int y = (int) v1y; y < (int) v2y; y++)
			{
				if (y >= 0 && y < renderHeight)
				{
					int xpos = (int) (((v2x - v1x) * t) + v1x);
					double pointX = lerp(p1.x(), p2.x(), t);
					double pointY = lerp(p1.y(), p2.y(), t);
					double pointZ = lerp(p1.z(), p2.z(), t);
					double normalX = lerp(n1.x(), n2.x(), t);
					double normalY = lerp(n1.y(), n2.y(), t);
					double normalZ = lerp(n1.z(), n2.z(), t);
					if (xpos < scanlineXIni[y])
					{
						scanlineXIni[y] = xpos;
						scanlineZIni[y] = lerp(v1z, v2z, t);
						scanlinePIniX[y] = pointX;
						scanlinePIniY[y] = pointY;
						scanlinePIniZ[y] = pointZ;
						scanlineNIniX[y] = normalX;
						scanlineNIniY[y] = normalY;
						scanlineNIniZ[y] = normalZ;
					}
					if (xpos > scanlineXFin[y])
					{
						scanlineXFin[y] = xpos;
						scanlineZFin[y] = lerp(v1z, v2z, t);
						scanlinePFinX[y] = pointX;
						scanlinePFinY[y] = pointY;
						scanlinePFinZ[y] = pointZ;
						scanlineNFinX[y] = normalX;
						scanlineNFinY[y] = normalY;
						scanlineNFinZ[y] = normalZ;
					}
				}
				t += deltaT;
			}
		}
	}
	
	/**
	 * Hace un scanline para una linea concreta pintando los pixeles correspondientes
	 * 
	 * @param y La fila en la que se hace el scanline
	 * @param xIni Columna inicial
	 * @param xFin Columna final
	 * @param zIni Profundidad z inicial
	 * @param zFin Profundidad z final
	 * @param nIni Normal inicial
	 * @param nFin Normal final
	 * @param pIni Punto inicial
	 * @param pFin Punto final
	 */
	private void scanline(
			int y, int xIni, int xFin, double zIni, double zFin,
			double nIniX, double nIniY, double nIniZ,
			double nFinX, double nFinY, double nFinZ,
			double pIniX, double pIniY, double pIniZ,
			double pFinX, double pFinY, double pFinZ,
			double cameraX, double cameraY, double cameraZ)
	{
		int width = renderBuffers.width();
		int[] pixels = renderBuffers.pixels();
		double[] depthBuffer = renderBuffers.depth();
		float[] normalXBuffer = renderBuffers.normalX();
		float[] normalYBuffer = renderBuffers.normalY();
		float[] normalZBuffer = renderBuffers.normalZ();
		double t=0.0;
		double deltat=1.0/((double)xFin-(double)xIni+1);
		for (int x=xIni;x<=xFin;x++)
		{
			if (x < 0 || x >= width)
			{
				t+=deltat;
				continue;
			}
			double z=(1.0-t)*zIni + t*zFin;
			int index = renderBuffers.index(x, y);
			if (z<depthBuffer[index])
			{
				depthBuffer[index] = z;
				double normalX = lerp(nIniX, nFinX, t);
				double normalY = lerp(nIniY, nFinY, t);
				double normalZ = lerp(nIniZ, nFinZ, t);
				double inverseNormalLength = inverseLength(normalX, normalY, normalZ);
				normalX *= inverseNormalLength;
				normalY *= inverseNormalLength;
				normalZ *= inverseNormalLength;
				normalXBuffer[index] = (float) normalX;
				normalYBuffer[index] = (float) normalY;
				normalZBuffer[index] = (float) normalZ;
				pixels[index] = shader.shade(renderMode,
					lerp(pIniX, pFinX, t),
					lerp(pIniY, pFinY, t),
					lerp(pIniZ, pFinZ, t),
					normalX, normalY, normalZ,
					_material, _luz,
					cameraX, cameraY, cameraZ);
			}
			t+=deltat;
		}
	}
	
	/**
	 * Pinta una cara en modo raster, utilizando algoritmo de scanline
	 * 
	 * @param g2 Variable grafica
	 * @param cara La cara a pintar
	 * @param z_buffer El z-buffer, que sirve para tener en cuenta si la cara que queremos
	 * pintar esta por detras o por delante de las caras pintadas anteriormente
	 */
	private void pintarCaraRaster(geometria.Cara cara, double cameraX, double cameraY, double cameraZ)
	{
		int renderHeight = renderBuffers.height();
		int renderWidth = renderBuffers.width();
		asegurarCapacidadScanline(renderHeight);
		modificarBordesScanline(cara, renderWidth, renderHeight);
		for (int y = scanlineStartY; y <= scanlineEndY; y++)
		{
			if (scanlineXIni[y] <= scanlineXFin[y])
			{
				scanline(y, scanlineXIni[y], scanlineXFin[y], scanlineZIni[y], scanlineZFin[y],
					scanlineNIniX[y], scanlineNIniY[y], scanlineNIniZ[y],
					scanlineNFinX[y], scanlineNFinY[y], scanlineNFinZ[y],
					scanlinePIniX[y], scanlinePIniY[y], scanlinePIniZ[y],
					scanlinePFinX[y], scanlinePFinY[y], scanlinePFinZ[y],
					cameraX, cameraY, cameraZ);
			}
		}
	}
	
	/**
	 * Decide si una cara de pintar o no, teniendo en cuenta el backface culling
	 * @param cara
	 * @return true si hay que pintar la cara
	 */
	private boolean considerarCara(geometria.Cara cara)
	{
		if (!this._backfaceCulling) return true;
		else
		{
			geometria.Direccion d = new geometria.Direccion(cara.centro(),this.posicionCamara());
			return d.aVector4().productoEscalar(cara.normal().aVector4()) > 0;
		}
	}
	
	/**
	 * Pinta todo el objeto en modo raster
	 * 
	 * @param g2 Variable de entorno grafico 2D de java
	 */
	private void pintarRaster()
	{
		  renderBuffers.clearGradient(0xfff7f1e7, 0xffdfe9f3, FAR_DEPTH);
		  geometria.Punto cameraPosition = posicionCamara();
		  double cameraX = cameraPosition.x();
		  double cameraY = cameraPosition.y();
		  double cameraZ = cameraPosition.z();

		  geometria.Cara[] facets=_objeto.caras();	
		  lastRenderedFaces = 0;
		                 
		  if (facets!=null)
		  {
			  for(int i=0;i<facets.length;i++)
			  {
				  if (considerarCara(facets[i]))
				  {
					  pintarCaraRaster(facets[i], cameraX, cameraY, cameraZ);
					  lastRenderedFaces++;
				  }
			  }
		  }

		  if (renderMode == RenderMode.DEPTH)
		  {
			  colorearProfundidad();
		  }
		  postProcess.apply(renderBuffers, renderMode);
	}
	
	/**
	 * Pinta los ejes en diferentes colores para la malla de alambre
	 * 
	 * @param g2 Variable de entorno grafico 2D de java
	 */
	private void pintarEjesMallaAlambre(Graphics2D g2)
	{
		  g2.setStroke(new BasicStroke(2.0f));
		  double[] o = new double[3];
		  double[] x = new double[3];
		  double[] y = new double[3];
		  double[] z = new double[3];
		  transformarPunto(new geometria.Punto(0.0,0.0,0.0), o);
		  transformarPunto(new geometria.Punto(1.0,0.0,0.0), x);
		  transformarPunto(new geometria.Punto(0.0,1.0,0.0), y);
		  transformarPunto(new geometria.Punto(0.0,0.0,1.0), z);
		  
		  g2.setColor(Color.red);
		  g2.drawLine((int) o[0], (int) o[1], (int) x[0], (int) x[1]);
		  g2.setColor(Color.green);
		  g2.drawLine((int) o[0], (int) o[1], (int) y[0], (int) y[1]);
		  g2.setColor(Color.blue);
		  g2.drawLine((int) o[0], (int) o[1], (int) z[0], (int) z[1]); 
	}
	
	/**
	 * Pinta una cara en malla de alambre 
	 * 
	 * @param g2 Variable de entorno grafico 2D de java
	 * @param cara La cara a pintar
	 */
	private void pintarCaraMallaAlambre(Graphics2D g2,
			geometria.Cara cara)
	{
		  int vertexCount = cara.vertices().length;
		  if (vertexCount <= 0)
		  {
			  return;
		  }

		  asegurarCapacidadProyectada(vertexCount);
		  for (int i = 0; i < vertexCount; i++)
		  {
			  transformarPunto(cara.vertice(i).punto, i);
		  }

		  for (int i = 1; i < vertexCount; i++)
		  {
			  g2.drawLine((int) projectedX[i - 1], (int) projectedY[i - 1], (int) projectedX[i], (int) projectedY[i]);
		  }
		  g2.drawLine((int) projectedX[vertexCount - 1], (int) projectedY[vertexCount - 1],
		  		(int) projectedX[0], (int) projectedY[0]);
	}
	
	/**
	 * Pinta todo el objeto en malla de alambre
	 * 
	 * @param g2 Variable de entorno grafico 2D de java
	 */
	private void pintarMallaAlambre()
	{
		  renderBuffers.clearGradient(0xfffaf5ef, 0xffdfe7ef, FAR_DEPTH);
		  Graphics2D g2 = renderBuffers.colorBuffer().createGraphics();
		  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		  g2.setColor(new Color(35, 45, 55));
		  lastRenderedFaces = 0;

		  pintarEjesMallaAlambre(g2);
		  
		  geometria.Cara[] facets=_objeto.caras();

		  if (facets!=null)
		  {
			  int i;
			  g2.setColor(new Color(35, 45, 55));
			  for(i=0;i<facets.length;i++)
			  {
				  if (considerarCara(facets[i]))
				  {
					  pintarCaraMallaAlambre(g2,facets[i]);
					  lastRenderedFaces++;
				  }
			  }
		  }
		  g2.dispose();
	 }

	private void asegurarBuffers()
	{
		int renderWidth = anchoRender();
		int renderHeight = altoRender();
		if (renderBuffers.width() != renderWidth || renderBuffers.height() != renderHeight)
		{
			renderBuffers.ensureSize(renderWidth, renderHeight);
			marcarEscenaSucia();
		}
		asegurarCapacidadScanline(renderHeight);
	}

	private void renderizarEscena()
	{
		asegurarBuffers();
		if (!sceneDirty)
		{
			return;
		}
		long renderStart = System.nanoTime();
		if (mallaAlambre())
		{
			pintarMallaAlambre();
		}
		else
		{
			pintarRaster();
		}
		lastRenderNanos = System.nanoTime() - renderStart;
		sceneDirty = false;
	}

	private void colorearProfundidad()
	{
		double minDepth = Double.POSITIVE_INFINITY;
		double maxDepth = Double.NEGATIVE_INFINITY;
		double[] depth = renderBuffers.depth();
		for (int i = 0; i < depth.length; i++)
		{
			if (depth[i] >= 1.0e20)
			{
				continue;
			}
			if (depth[i] < minDepth)
			{
				minDepth = depth[i];
			}
			if (depth[i] > maxDepth)
			{
				maxDepth = depth[i];
			}
		}

		if (!Double.isFinite(minDepth) || !Double.isFinite(maxDepth))
		{
			return;
		}

		double range = Math.max(1.0e-9, maxDepth - minDepth);
		for (int i = 0; i < depth.length; i++)
		{
			if (depth[i] >= 1.0e20)
			{
				continue;
			}
			double normalized = 1.0 - ((depth[i] - minDepth) / range);
			int channel = (int) Math.max(0, Math.min(255, Math.round(normalized * 255.0)));
			renderBuffers.pixels()[i] = 0xff000000 | (channel << 16) | (channel << 8) | channel;
		}
	}

	private void pintarOverlay(Graphics2D g2)
	{
		if (renderBuffers != null)
		{
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(new Color(24, 32, 40, 170));
			g2.fillRoundRect(18, 18, 252, 84, 22, 22);
			g2.setColor(new Color(255, 247, 236));
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14.0f));
			g2.drawString("Modo: " + renderMode.label(), 34, 43);
			g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12.0f));
			g2.drawString("Render: " + renderBuffers.width() + "x" + renderBuffers.height() + " px", 34, 64);
			g2.drawString(
				"Frame: " + String.format(java.util.Locale.US, "%.1f ms", lastRenderNanos / 1_000_000.0),
				34,
				85);
			return;
		}

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(new Color(24, 32, 40, 170));
		g2.fillRoundRect(18, 18, 470, 104, 22, 22);
		g2.setColor(new Color(255, 247, 236));
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14.0f));
		g2.drawString("Modo: " + renderMode.label(), 34, 43);
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12.0f));
		g2.drawString("1-6 modos   O contorno   V viñeta   S PNG   R reset", 34, 63);
		g2.drawString("Arrastra órbita, rueda zoom, Mayús o botón derecho desplaza", 34, 82);
		String stats = _objeto.numeroVertices() + " vertices   " + _objeto.numeroCaras() + " caras   "
			+ renderBuffers.width() + "x" + renderBuffers.height() + " px   "
			+ String.format(java.util.Locale.US, "%.1f ms", lastRenderNanos / 1_000_000.0);
		g2.drawString(stats, 34, 101);
		if (statusMessage != null && statusMessage.length() > 0)
		{
			int bubbleWidth = Math.max(320, g2.getFontMetrics().stringWidth(statusMessage) + 32);
			g2.setColor(new Color(24, 32, 40, 180));
			g2.fillRoundRect(18, getHeight() - 54, bubbleWidth, 34, 18, 18);
			g2.setColor(new Color(255, 247, 236));
			g2.drawString(statusMessage, 34, getHeight() - 32);
		}
	}

	private void configurarAtajos()
	{
		configurarAtajo("modo1", KeyStroke.getKeyStroke('1'), RenderMode.WIREFRAME);
		configurarAtajo("modo2", KeyStroke.getKeyStroke('2'), RenderMode.PHONG);
		configurarAtajo("modo3", KeyStroke.getKeyStroke('3'), RenderMode.TOON);
		configurarAtajo("modo4", KeyStroke.getKeyStroke('4'), RenderMode.CLAY);
		configurarAtajo("modo5", KeyStroke.getKeyStroke('5'), RenderMode.NORMALS);
		configurarAtajo("modo6", KeyStroke.getKeyStroke('6'), RenderMode.DEPTH);

		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();

		inputMap.put(KeyStroke.getKeyStroke('O'), "toggleOutline");
		actionMap.put("toggleOutline", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e)
			{
				postProcess.toggleOutline();
				marcarEscenaSucia();
				actualizarTituloVentana();
				repaint();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke('V'), "toggleVignette");
		actionMap.put("toggleVignette", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e)
			{
				postProcess.toggleVignette();
				marcarEscenaSucia();
				actualizarTituloVentana();
				repaint();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke('S'), "screenshot");
		actionMap.put("screenshot", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e)
			{
				guardarCaptura();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke('R'), "resetCamera");
		actionMap.put("resetCamera", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e)
			{
				reiniciarCamara();
			}
		});
	}

	private void configurarInteraccionCamara()
	{
		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{
				requestFocusInWindow();
				lastMousePoint = e.getPoint();
				cameraInteracting = true;
			}

			public void mouseDragged(MouseEvent e)
			{
				if (lastMousePoint == null)
				{
					lastMousePoint = e.getPoint();
					return;
				}

				int deltaX = e.getX() - lastMousePoint.x;
				int deltaY = e.getY() - lastMousePoint.y;
				boolean panDrag = (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0 || e.isShiftDown();
				if (panDrag)
				{
					_camara.desplazar(deltaX, deltaY, escalaDesplazamientoPorPixel());
					statusMessage = "Cámara desplazada.";
				}
				else
				{
					_camara.orbitar(-deltaX * 0.45, deltaY * 0.35);
					statusMessage = "Cámara orbitando.";
				}
				lastMousePoint = e.getPoint();
				actualizar();
				notificarCambioCamara();
				repaint();
			}

			public void mouseReleased(MouseEvent e)
			{
				lastMousePoint = null;
				if (cameraInteracting)
				{
					cameraInteracting = false;
					actualizar();
					repaint();
				}
			}

			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					reiniciarCamara();
				}
			}

			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.isControlDown())
				{
					_camara.modificarFov(_camara.fov() + e.getPreciseWheelRotation() * 2.0);
					statusMessage = "FOV ajustado.";
				}
				else
				{
					double delta = e.getPreciseWheelRotation() * Math.max(2.0, _camara.distancia() * 0.08);
					_camara.zoom(delta);
					statusMessage = "Zoom de cámara.";
				}
				actualizar();
				notificarCambioCamara();
				repaint();
			}
		};

		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
	}

	private void configurarAtajo(String actionName, KeyStroke keyStroke, final RenderMode mode)
	{
		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();
		inputMap.put(keyStroke, actionName);
		actionMap.put(actionName, new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e)
			{
				modificarModoRender(mode);
			}
		});
	}

	public void modificarMensajeEstado(String message)
	{
		statusMessage = message;
		repaint();
	}

	private double lerp(double start, double end, double t)
	{
		return (1.0 - t) * start + t * end;
	}

	private double inverseLength(double x, double y, double z)
	{
		double length = Math.sqrt(x * x + y * y + z * z);
		if (length == 0.0)
		{
			return 1.0;
		}
		return 1.0 / length;
	}

	public void reiniciarCamara()
	{
		_camara.reiniciar(distanciaCamaraPorDefecto(), FOV_INICIAL, INCLINACION_INICIAL, ROTACION_INICIAL);
		statusMessage = "Cámara reiniciada.";
		actualizar();
		notificarCambioCamara();
		repaint();
	}

	private void actualizarTituloVentana()
	{
		Window window = SwingUtilities.getWindowAncestor(this);
		if (window instanceof JFrame)
		{
			StringBuilder title = new StringBuilder("Visualizador");
			title.append(" - ").append(renderMode.label());
			if (renderMode.isRaster())
			{
				title.append(" | ").append(renderScale).append("x");
				title.append(" | Outline ").append(postProcess.outlineEnabled() ? "on" : "off");
				title.append(" | Vignette ").append(postProcess.vignetteEnabled() ? "on" : "off");
			}
			((JFrame) window).setTitle(title.toString());
		}
	}

	public void guardarCaptura()
	{
		if (proyeccionDesactualizada())
		{
			actualizar();
		}
		renderizarEscena();
		File directory = new File("screenshots");
		if (!directory.exists())
		{
			directory.mkdirs();
		}

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		File output = new File(directory, "render_" + timestamp + ".png");
		try
		{
			ImageIO.write(renderBuffers.colorBuffer(), "png", output);
			statusMessage = "Captura guardada en " + output.getPath();
			System.out.println("Captura guardada: " + output.getAbsolutePath());
		}
		catch (IOException exception)
		{
			statusMessage = "No se pudo guardar la captura.";
			System.out.println("No se pudo guardar la captura: " + exception.getMessage());
		}
		repaint();
	}

	 /**
	  * Este metodo se llama automaticamente para redibujar el objeto cuando es necesario
	  */
	 public void paintComponent(Graphics g) {
		  super.paintComponent(g);
		  if (getWidth() <= 0 || getHeight() <= 0)
		  {
			  return;
		  }
		  if (proyeccionDesactualizada())
		  {
			  this.actualizar();
		  }
		  renderizarEscena();
		  Graphics2D g2 = (Graphics2D)g;
		  if (renderBuffers.colorBuffer() == null)
		  {
			  return;
		  }
		  g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		  g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		  g2.drawImage(renderBuffers.colorBuffer(), 0, 0, getWidth(), getHeight(), null);
		  pintarOverlay(g2);
	   }
}



