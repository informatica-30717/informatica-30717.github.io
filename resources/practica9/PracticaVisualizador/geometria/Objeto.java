package geometria;
import java.io.*;
import java.util.Scanner;

/**
 * Representa un objeto que se puede cargar a partir de un archivo y pintar.
 * 
 * @author Adolfo
 */
public class Objeto {
	private Punto[] puntos;
	private Normal[] normales;
	private Cara[] _caras;	
	private Punto _centro;
	
	/**
	 * Calcula el centro del objeto como la media de todos los puntos
	 * del objeto
	 */
	private void actualizarCentro()
	{
		Vector4 centro = new Vector4(0.0,0.0,0.0,0.0);
		for (int i=0;i<puntos.length;i++)
		    centro.sumar(puntos[i].aVector4());
		_centro = centro.aPunto();
	}
	
	/**
	 * Devuelve el centro del objeto
	 * @return Centro del objeto
	 */
	public Punto centro()
	{
		return _centro;
	}

	public double tamanoAabb()
	{
		if ((puntos == null) || (puntos.length == 0))
		{
			return 1.0;
		}

		double minX = puntos[0].x();
		double minY = puntos[0].y();
		double minZ = puntos[0].z();
		double maxX = minX;
		double maxY = minY;
		double maxZ = minZ;

		for (int i = 1; i < puntos.length; i++)
		{
			double x = puntos[i].x();
			double y = puntos[i].y();
			double z = puntos[i].z();
			if (x < minX) minX = x;
			if (y < minY) minY = y;
			if (z < minZ) minZ = z;
			if (x > maxX) maxX = x;
			if (y > maxY) maxY = y;
			if (z > maxZ) maxZ = z;
		}

		double ancho = maxX - minX;
		double alto = maxY - minY;
		double profundo = maxZ - minZ;
		return Math.max(1.0e-6, Math.max(ancho, Math.max(alto, profundo)));
	}

	public double radioAabb()
	{
		if ((puntos == null) || (puntos.length == 0))
		{
			return 0.5;
		}

		double minX = puntos[0].x();
		double minY = puntos[0].y();
		double minZ = puntos[0].z();
		double maxX = minX;
		double maxY = minY;
		double maxZ = minZ;

		for (int i = 1; i < puntos.length; i++)
		{
			double x = puntos[i].x();
			double y = puntos[i].y();
			double z = puntos[i].z();
			if (x < minX) minX = x;
			if (y < minY) minY = y;
			if (z < minZ) minZ = z;
			if (x > maxX) maxX = x;
			if (y > maxY) maxY = y;
			if (z > maxZ) maxZ = z;
		}

		double ancho = maxX - minX;
		double alto = maxY - minY;
		double profundo = maxZ - minZ;
		return 0.5 * Math.sqrt(ancho * ancho + alto * alto + profundo * profundo);
	}
	
	/**
	 * Devuelve las caras del objeto
	 * 
	 * @return Caras del objeto
	 */
	public Cara[] caras()
	{
		return _caras;
	}

	public int numeroVertices()
	{
		return puntos == null ? 0 : puntos.length;
	}

	public int numeroCaras()
	{
		return _caras == null ? 0 : _caras.length;
	}
	
	/**
	 * Construye un objeto vacio sin caras ni puntos ni normales
	 */
	public Objeto()
	{
		_centro = new Punto(0.0,0.0,0.0);
	}
	
	/**
	 * Transforma este objeto en un objeto vacio sin caras ni puntos ni normales
	 */
	public void reiniciar()
	{	
		_caras=null;
		normales=null;
		puntos=null;
		_centro = new Punto(0.0,0.0,0.0);
	}
	
	/**
	 * Construye todas las normales que no se han cargado como la media de las normales
	 * que contienen los vertices.
	 */
	private void construirNormales()
	{
		if ((puntos==null)||(_caras==null)) return;
		int p; int f; int v;
		Vector4 tmp_normals[] = new Vector4[puntos.length];
		for (p=0;p<puntos.length;p++) tmp_normals[p] = new Vector4(0, 0, 0, 0);
		
		for (p=0;p<puntos.length;p++)
		{
			//Buscamos el punto p en todos los vertices de todas las caras
			for (f=0;f<_caras.length;f++)
			{
				for (v=0;v<_caras[f].vertices().length;v++)
				{
					//Si lo encontramos, entonces consideramos esta cara para la normal
					if ((_caras[f].vertice(v).punto == puntos[p])
							&&(_caras[f].vertice(v).normal == null))
					{
						tmp_normals[p].sumar(_caras[f].normal().aVector4());
					}
				}
			}
		}
		
		int offset;
		
		if (normales == null)
		{
			offset = 0;
			normales = new Normal[tmp_normals.length];
		}
		else
		{
			offset = normales.length;
			Normal aux[] = normales;
			normales = new Normal[aux.length + tmp_normals.length];
			for (p=0;p<offset;p++)
			{
				normales[p] = aux[p];
			}
		}
		
		for (p=0;p<tmp_normals.length;p++)
		{
			//Para poner esta normal nos aseguramos de que sume mas que 0
			if (tmp_normals[p].productoEscalar(tmp_normals[p])>0)
				normales[p+offset] = tmp_normals[p].aNormal();
		}
		
		//Copiamos las normales temporales en nuestra estructura de objeto
		for (p=0;p<puntos.length;p++)
		{
			for (f=0;f<_caras.length;f++)
			{
				for (v=0;v<_caras[f].vertices().length;v++)
				{
					if ((_caras[f].vertice(v).punto == puntos[p])
							&&(_caras[f].vertice(v).normal == null))
					{
						_caras[f].vertice(v).normal = normales[p+offset];
					}
				}
			}
		}
	}
	
	/**
	 * Solo se utiliza al cargar un archivo obj
	 * @param s La cadena a interpretar
	 * @return El nuevo vertice
	 */
	private Vertice interpretarComoVertice(String s)
	{
		Scanner sc = new Scanner(s);
		sc.useDelimiter("/");
		@SuppressWarnings("unused")
		String s1=new String(""), s2=s1, s3=s1;
		if (sc.hasNext()) s1=sc.next();
		if (sc.hasNext()) s2=sc.next();
		if (sc.hasNext()) s3=sc.next();
		Punto p = null;
		Normal n = null;
		if (!(s1.isEmpty())) p=puntos[Integer.parseInt(s1)-1];
		if (!(s3.isEmpty())) n=normales[Integer.parseInt(s3)-1];
		return new Vertice(p,n);
	}
	
	/**
	 * Carga en este objeto un archivo obj
	 * 
	 * @param filename El nombre del archivo obj a cargar
	 * @throws IOException Si el archivo no se encuentra
	 */
	public void cargarObj(String filename) throws IOException
	{
		reiniciar();
	    int nv=0, nt=0, nn=0, nf=0;

	    BufferedReader in = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = in.readLine()) != null) {
	    	if (!line.isEmpty())
	    	{
	        if (line.charAt(0) == 'v')
	        {
	            if (line.charAt(1) == 'n')
	                nn++;
	            else if (line.charAt(1) == 't')
	                nt++;
	            else
	                nv++;
	        }
	        else if (line.charAt(0) == 'f')
	        {
	            nf++;
	        }
	    	}
	 	}
	    in.close();	
	    in = new BufferedReader(new FileReader(filename));
	    puntos = new Punto[nv];
	    normales = new Normal[nn];
	    _caras = new Cara[nf];

	    int nvertices = 0;
	    int nnormals = 0;
	    int ntextures = 0;
	    int nfacets = 0;

	    while ((line = in.readLine()) != null)
	    {
	    if (!line.isEmpty())
	    {
	        if (line.charAt(0) == 'v')
	        {
	            if (line.charAt(1) == 'n')
	            {	            	
	            	Scanner sc = new Scanner(line.substring(2));
	                double x, y, z;
	    	    	x=Double.parseDouble(sc.next());
	    	    	y=Double.parseDouble(sc.next());
	    	    	z=Double.parseDouble(sc.next());
	                normales[nnormals] = new Normal(x,y,z);
	                nnormals++;
	            }
	            else if (line.charAt(1) == 't')
	            {
//	            	Scanner sc = new Scanner(line.substring(2));
//	                double u, v;
//	                u=sc.nextDouble();
//	                v=sc.nextDouble();
//			texture_coordinates[ntextures] = add_texture_coordinates(SurfaceCoordinates(u,v));
	                ntextures++;
	            }
	            else
	            {
	            	Scanner sc = new Scanner(line.substring(1));
	                double x, y, z;
	    	    	x=Double.parseDouble(sc.next());
	    	    	y=Double.parseDouble(sc.next());
	    	    	z=Double.parseDouble(sc.next());
	                puntos[nvertices] = new Punto(x,y,z);
	                nvertices++;
	            }
	        }
	        else if (line.charAt(0) == 'f')
	        {
            	Scanner sc = new Scanner(line.substring(1));
            	int nfacetvertices=0;
            	while (sc.hasNext()) { sc.next(); nfacetvertices++; }
            	Vertice[] facetvertices = new Vertice[nfacetvertices];
            	int i=0;
            	sc=new Scanner(line.substring(1));
            	while (sc.hasNext())
            	{
            		facetvertices[i]=interpretarComoVertice(sc.next());
            		i++;
            	}
            	
            	_caras[nfacets] = new Cara(facetvertices);
            	
            	nfacets++;
	        }
	    }
	    }
	    in.close();
	    this.construirNormales();
	    this.actualizarCentro();
	}
}
