package geometria;
import java.io.*;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Scanner;

/**
 * Representa un objeto que se puede cargar a partir de un archivo y pintar.
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class Objeto {
	private static final String CACHE_MAGIC = "VISUALIZADOR_OBJ_CACHE";
	private static final int CACHE_VERSION = 1;
	private static final String CACHE_EXTENSION = ".vbin";

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
		if ((puntos == null) || (puntos.length == 0))
		{
			_centro = new Punto(0.0,0.0,0.0);
			return;
		}

		Vector4 centro = new Vector4(0.0,0.0,0.0,0.0);
		boolean foundPoint = false;
		for (int i=0;i<puntos.length;i++)
		{
			if (puntos[i] == null)
			{
				continue;
			}
			centro.sumar(puntos[i].aVector4());
			foundPoint = true;
		}
		_centro = foundPoint ? centro.aPunto() : new Punto(0.0,0.0,0.0);
	}

	private int primerPuntoValido()
	{
		if (puntos == null)
		{
			return -1;
		}

		for (int i = 0; i < puntos.length; i++)
		{
			if (puntos[i] != null)
			{
				return i;
			}
		}

		return -1;
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
		int firstPointIndex = primerPuntoValido();
		if (firstPointIndex < 0)
		{
			return 1.0;
		}

		double minX = puntos[firstPointIndex].x();
		double minY = puntos[firstPointIndex].y();
		double minZ = puntos[firstPointIndex].z();
		double maxX = minX;
		double maxY = minY;
		double maxZ = minZ;

		for (int i = firstPointIndex + 1; i < puntos.length; i++)
		{
			if (puntos[i] == null)
			{
				continue;
			}
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
		int firstPointIndex = primerPuntoValido();
		if (firstPointIndex < 0)
		{
			return 0.5;
		}

		double minX = puntos[firstPointIndex].x();
		double minY = puntos[firstPointIndex].y();
		double minZ = puntos[firstPointIndex].z();
		double maxX = minX;
		double maxY = minY;
		double maxZ = minZ;

		for (int i = firstPointIndex + 1; i < puntos.length; i++)
		{
			if (puntos[i] == null)
			{
				continue;
			}
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
		if (puntos == null)
		{
			return 0;
		}

		int validPoints = 0;
		for (int i = 0; i < puntos.length; i++)
		{
			if (puntos[i] != null)
			{
				validPoints++;
			}
		}
		return validPoints;
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

	private File archivoCache(File source)
	{
		File parent = source.getParentFile();
		if (parent == null)
		{
			parent = new File(".");
		}
		return new File(parent, source.getName() + CACHE_EXTENSION);
	}

	private IdentityHashMap<Punto, Integer> indicesPuntos()
	{
		IdentityHashMap<Punto, Integer> indices = new IdentityHashMap<Punto, Integer>();
		if (puntos == null)
		{
			return indices;
		}

		for (int i = 0; i < puntos.length; i++)
		{
			if (puntos[i] != null)
			{
				indices.put(puntos[i], Integer.valueOf(i));
			}
		}
		return indices;
	}

	private IdentityHashMap<Normal, Integer> indicesNormales()
	{
		IdentityHashMap<Normal, Integer> indices = new IdentityHashMap<Normal, Integer>();
		if (normales == null)
		{
			return indices;
		}

		for (int i = 0; i < normales.length; i++)
		{
			if (normales[i] != null)
			{
				indices.put(normales[i], Integer.valueOf(i));
			}
		}
		return indices;
	}

	private void escribirPunto(DataOutputStream out, Punto punto) throws IOException
	{
		out.writeBoolean(punto != null);
		if (punto != null)
		{
			out.writeDouble(punto.x());
			out.writeDouble(punto.y());
			out.writeDouble(punto.z());
		}
	}

	private Punto leerPunto(DataInputStream in) throws IOException
	{
		if (!in.readBoolean())
		{
			return null;
		}
		return new Punto(in.readDouble(), in.readDouble(), in.readDouble());
	}

	private void escribirNormal(DataOutputStream out, Normal normal) throws IOException
	{
		out.writeBoolean(normal != null);
		if (normal != null)
		{
			out.writeDouble(normal.x());
			out.writeDouble(normal.y());
			out.writeDouble(normal.z());
		}
	}

	private Normal leerNormal(DataInputStream in) throws IOException
	{
		if (!in.readBoolean())
		{
			return null;
		}
		return new Normal(in.readDouble(), in.readDouble(), in.readDouble());
	}

	private void guardarCacheBinaria(File source) throws IOException
	{
		File cache = archivoCache(source);
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(cache)));
		try
		{
			out.writeUTF(CACHE_MAGIC);
			out.writeInt(CACHE_VERSION);
			out.writeLong(source.length());
			out.writeLong(source.lastModified());

			out.writeInt(puntos == null ? 0 : puntos.length);
			if (puntos != null)
			{
				for (int i = 0; i < puntos.length; i++)
				{
					escribirPunto(out, puntos[i]);
				}
			}

			out.writeInt(normales == null ? 0 : normales.length);
			if (normales != null)
			{
				for (int i = 0; i < normales.length; i++)
				{
					escribirNormal(out, normales[i]);
				}
			}

			IdentityHashMap<Punto, Integer> pointIndices = indicesPuntos();
			IdentityHashMap<Normal, Integer> normalIndices = indicesNormales();
			out.writeInt(_caras == null ? 0 : _caras.length);
			if (_caras != null)
			{
				for (int i = 0; i < _caras.length; i++)
				{
					Vertice[] vertices = _caras[i].vertices();
					out.writeInt(vertices.length);
					for (int j = 0; j < vertices.length; j++)
					{
						Integer pointIndex = pointIndices.get(vertices[j].punto);
						Integer normalIndex = normalIndices.get(vertices[j].normal);
						out.writeInt(pointIndex == null ? -1 : pointIndex.intValue());
						out.writeInt(normalIndex == null ? -1 : normalIndex.intValue());
					}
				}
			}
		}
		finally
		{
			out.close();
		}
	}

	private boolean cargarCacheBinaria(File source) throws IOException
	{
		if (!source.isFile())
		{
			return false;
		}

		File cache = archivoCache(source);
		if (!cache.isFile())
		{
			return false;
		}

		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(cache)));
		try
		{
			if (!CACHE_MAGIC.equals(in.readUTF()))
			{
				return false;
			}
			if (in.readInt() != CACHE_VERSION)
			{
				return false;
			}
			if ((in.readLong() != source.length()) || (in.readLong() != source.lastModified()))
			{
				return false;
			}

			Punto[] cachedPoints = new Punto[in.readInt()];
			for (int i = 0; i < cachedPoints.length; i++)
			{
				cachedPoints[i] = leerPunto(in);
			}

			Normal[] cachedNormals = new Normal[in.readInt()];
			for (int i = 0; i < cachedNormals.length; i++)
			{
				cachedNormals[i] = leerNormal(in);
			}

			Cara[] cachedFaces = new Cara[in.readInt()];
			for (int i = 0; i < cachedFaces.length; i++)
			{
				int vertexCount = in.readInt();
				Vertice[] vertices = new Vertice[vertexCount];
				for (int j = 0; j < vertexCount; j++)
				{
					int pointIndex = in.readInt();
					int normalIndex = in.readInt();
					if ((pointIndex < 0) || (pointIndex >= cachedPoints.length))
					{
						throw new IOException("Cache binaria con indice de punto invalido.");
					}
					Normal normal = null;
					if (normalIndex >= 0)
					{
						if (normalIndex >= cachedNormals.length)
						{
							throw new IOException("Cache binaria con indice de normal invalido.");
						}
						normal = cachedNormals[normalIndex];
					}
					vertices[j] = new Vertice(cachedPoints[pointIndex], normal);
				}
				cachedFaces[i] = new Cara(vertices);
			}

			puntos = cachedPoints;
			normales = cachedNormals;
			_caras = cachedFaces;
			actualizarCentro();
			return true;
		}
		finally
		{
			in.close();
		}
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
			if (puntos[p] == null)
			{
				continue;
			}
			//Buscamos el punto p en todos los vertices de todas las caras
			for (f=0;f<_caras.length;f++)
			{
				if (_caras[f] == null)
				{
					continue;
				}
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
			if (puntos[p] == null)
			{
				continue;
			}
			for (f=0;f<_caras.length;f++)
			{
				if (_caras[f] == null)
				{
					continue;
				}
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

	private int resolverIndiceObj(String indexToken, int total)
	{
		if ((indexToken == null) || indexToken.isEmpty() || (total <= 0))
		{
			return -1;
		}

		int index = Integer.parseInt(indexToken);
		if (index > 0)
		{
			index--;
		}
		else if (index < 0)
		{
			index = total + index;
		}
		else
		{
			return -1;
		}

		if ((index < 0) || (index >= total))
		{
			return -1;
		}

		return index;
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
		int pointIndex = resolverIndiceObj(s1, puntos == null ? 0 : puntos.length);
		int normalIndex = resolverIndiceObj(s3, normales == null ? 0 : normales.length);
		if (pointIndex >= 0) p=puntos[pointIndex];
		if (normalIndex >= 0) n=normales[normalIndex];
		return new Vertice(p,n);
	}

	private boolean caraValida(Vertice[] facetvertices)
	{
		if ((facetvertices == null) || (facetvertices.length < 3))
		{
			return false;
		}

		for (int i = 0; i < facetvertices.length; i++)
		{
			if ((facetvertices[i] == null) || (facetvertices[i].punto == null))
			{
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Carga en este objeto un archivo obj
	 * 
	 * @param filename El nombre del archivo obj a cargar
	 * @throws IOException Si el archivo no se encuentra
	 */
	public void cargarObj(String filename) throws IOException
	{
		File source = new File(filename);
		try
		{
			if (cargarCacheBinaria(source))
			{
				System.out.println("Modelo cargado desde cache binaria: " + archivoCache(source).getAbsolutePath());
				return;
			}
		}
		catch (IOException exception)
		{
			System.out.println("Cache binaria ignorada: " + exception.getMessage());
		}
		catch (RuntimeException exception)
		{
			System.out.println("Cache binaria ignorada: " + exception.getMessage());
		}

		cargarObjTexto(filename);
		try
		{
			guardarCacheBinaria(source);
		}
		catch (IOException exception)
		{
			System.out.println("No se pudo guardar la cache binaria: " + exception.getMessage());
		}
	}

	private void cargarObjTexto(String filename) throws IOException
	{
		reiniciar();
	    int nv=0, nt=0, nn=0, nf=0;

	    BufferedReader in = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = in.readLine()) != null) {
	    	line = line.trim();
	    	if (line.isEmpty() || line.startsWith("#"))
	    	{
	    		continue;
	    	}
	        if (line.startsWith("vn ") || line.startsWith("vn\t"))
	        {
	            nn++;
	        }
	        else if (line.startsWith("vt ") || line.startsWith("vt\t"))
	        {
	            nt++;
	        }
	        else if (line.startsWith("v ") || line.startsWith("v\t"))
	        {
	            nv++;
	        }
	        else if (line.startsWith("f ") || line.startsWith("f\t"))
	        {
	            nf++;
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
	    	line = line.trim();
	    if (!line.isEmpty() && !line.startsWith("#"))
	    {
	        if (line.startsWith("vn ") || line.startsWith("vn\t"))
	        {
	            	Scanner sc = new Scanner(line.substring(2));
	                double x, y, z;
	    	    	x=Double.parseDouble(sc.next());
	    	    	y=Double.parseDouble(sc.next());
	    	    	z=Double.parseDouble(sc.next());
	                normales[nnormals] = new Normal(x,y,z);
	                nnormals++;
	        }
	        else if (line.startsWith("vt ") || line.startsWith("vt\t"))
	        {
//	        	Scanner sc = new Scanner(line.substring(2));
//	            double u, v;
//	            u=sc.nextDouble();
//	            v=sc.nextDouble();
//		texture_coordinates[ntextures] = add_texture_coordinates(SurfaceCoordinates(u,v));
	            ntextures++;
	        }
	        else if (line.startsWith("v ") || line.startsWith("v\t"))
	        {
	            	Scanner sc = new Scanner(line.substring(1));
	                double x, y, z;
	    	    	x=Double.parseDouble(sc.next());
	    	    	y=Double.parseDouble(sc.next());
	    	    	z=Double.parseDouble(sc.next());
	                puntos[nvertices] = new Punto(x,y,z);
	                nvertices++;
	        }
	        else if (line.startsWith("f ") || line.startsWith("f\t"))
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
            	
	            	if (caraValida(facetvertices))
	            	{
	            		_caras[nfacets] = new Cara(facetvertices);
	            		nfacets++;
	            	}
	        }
	    }
	    }
	    in.close();
	    if (nvertices < puntos.length)
	    {
	    	puntos = Arrays.copyOf(puntos, nvertices);
	    }
	    if (nnormals < normales.length)
	    {
	    	normales = Arrays.copyOf(normales, nnormals);
	    }
	    if (nfacets < _caras.length)
	    {
	    	_caras = Arrays.copyOf(_caras, nfacets);
	    }
	    this.construirNormales();
	    this.actualizarCentro();
	}
}
