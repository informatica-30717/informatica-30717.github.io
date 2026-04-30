package renderer;

import escena.Color;
import escena.Luz;
import escena.Material;
import geometria.Normal;
import geometria.Punto;

public class Shader {
	public int shade(RenderMode mode, Punto point, Normal normal, Material material,
			Luz light, Punto cameraPosition)
	{
		return shade(mode,
			point.x(), point.y(), point.z(),
			normal.x(), normal.y(), normal.z(),
			material, light,
			cameraPosition.x(), cameraPosition.y(), cameraPosition.z());
	}

	public int shade(RenderMode mode,
			double pointX, double pointY, double pointZ,
			double normalX, double normalY, double normalZ,
			Material material, Luz light,
			double cameraX, double cameraY, double cameraZ)
	{
		double inverseNormalLength = inverseLength(normalX, normalY, normalZ);
		double nx = normalX * inverseNormalLength;
		double ny = normalY * inverseNormalLength;
		double nz = normalZ * inverseNormalLength;

		if (mode == RenderMode.NORMALS)
		{
			return shadeNormals(nx, ny, nz);
		}

		double lightX = light.posicion().x() - pointX;
		double lightY = light.posicion().y() - pointY;
		double lightZ = light.posicion().z() - pointZ;
		double lightInverseLength = inverseLength(lightX, lightY, lightZ);
		double lx = lightX * lightInverseLength;
		double ly = lightY * lightInverseLength;
		double lz = lightZ * lightInverseLength;

		double viewX = cameraX - pointX;
		double viewY = cameraY - pointY;
		double viewZ = cameraZ - pointZ;
		double viewInverseLength = inverseLength(viewX, viewY, viewZ);
		double vx = viewX * viewInverseLength;
		double vy = viewY * viewInverseLength;
		double vz = viewZ * viewInverseLength;

		double diffuse = clamp(nx * lx + ny * ly + nz * lz);
		double halfX = lx + vx;
		double halfY = ly + vy;
		double halfZ = lz + vz;
		double halfInverseLength = inverseLength(halfX, halfY, halfZ);
		double hx = halfX * halfInverseLength;
		double hy = halfY * halfInverseLength;
		double hz = halfZ * halfInverseLength;
		double specular = diffuse > 0.0
			? Math.pow(clamp(nx * hx + ny * hy + nz * hz), material.es())
			: 0.0;
		double rim = Math.pow(1.0 - clamp(nx * vx + ny * vy + nz * vz), 2.0);

		if (mode == RenderMode.TOON)
		{
			return shadeToon(material, light, diffuse, specular, rim);
		}
		else if (mode == RenderMode.CLAY)
		{
			return shadeClay(material, light, diffuse, specular, rim);
		}

		Color ambient = light.colorAmbiente().multiplicado(material.kd()).multiplicado(0.9);
		Color diffuseColor = light.color().multiplicado(material.kd()).multiplicado(diffuse);
		Color specularColor = light.color().multiplicado(material.ks() * specular);
		Color rimColor = light.color().multiplicado(material.kd()).multiplicado(rim * 0.18);
		return toRgb(ambient.sumado(diffuseColor).sumado(specularColor).sumado(rimColor));
	}

	private int shadeNormals(double nx, double ny, double nz)
	{
		double r = nx * 0.5 + 0.5;
		double g = ny * 0.5 + 0.5;
		double b = nz * 0.5 + 0.5;
		return pack(r, g, b);
	}

	private int shadeClay(Material material, Luz light, double diffuse, double specular, double rim)
	{
		double baseR = 0.50 + material.kd().r() * 0.38;
		double baseG = 0.48 + material.kd().g() * 0.34;
		double baseB = 0.45 + material.kd().b() * 0.30;
		double warmLight = 0.24 + diffuse * 0.76;
		double sheen = specular * (0.10 + material.ks() * 0.18) + rim * 0.16;
		double ambient = 0.10 + average(light.colorAmbiente()) * 0.55;
		double tintR = 0.84 + light.color().r() * 0.16;
		double tintG = 0.84 + light.color().g() * 0.16;
		double tintB = 0.84 + light.color().b() * 0.16;
		return pack(
			clamp(baseR * (ambient + warmLight) * tintR + sheen),
			clamp(baseG * (ambient + warmLight) * tintG + sheen * 0.95),
			clamp(baseB * (ambient + warmLight) * tintB + sheen * 0.85));
	}

	private int shadeToon(Material material, Luz light, double diffuse, double specular, double rim)
	{
		double band = quantize(diffuse);
		double shadowLift = 0.16 + average(light.colorAmbiente()) * 0.25;
		double highlight = specular > 0.72 ? 0.16 : 0.0;
		double rimBand = rim > 0.55 ? 0.10 : (rim > 0.28 ? 0.04 : 0.0);
		double lit = shadowLift + band * 0.82;
		double tintR = 0.94 + light.color().r() * 0.06;
		double tintG = 0.92 + light.color().g() * 0.08;
		double tintB = 0.90 + light.color().b() * 0.10;
		return pack(
			clamp(material.kd().r() * lit * tintR + highlight + rimBand),
			clamp(material.kd().g() * lit * tintG + highlight + rimBand * 0.9),
			clamp(material.kd().b() * lit * tintB + highlight + rimBand * 0.8));
	}

	private double average(Color color)
	{
		return (color.r() + color.g() + color.b()) / 3.0;
	}

	private int toRgb(Color color)
	{
		return pack(color.r(), color.g(), color.b());
	}

	private int pack(double r, double g, double b)
	{
		int red = toChannel(r);
		int green = toChannel(g);
		int blue = toChannel(b);
		return 0xff000000 | (red << 16) | (green << 8) | blue;
	}

	private int toChannel(double value)
	{
		int channel = (int) Math.round(clamp(value) * 255.0);
		if (channel < 0)
		{
			return 0;
		}
		if (channel > 255)
		{
			return 255;
		}
		return channel;
	}

	private double clamp(double value)
	{
		if (value < 0.0)
		{
			return 0.0;
		}
		if (value > 1.0)
		{
			return 1.0;
		}
		return value;
	}

	private double quantize(double diffuse)
	{
		if (diffuse < 0.16)
		{
			return 0.18;
		}
		if (diffuse < 0.38)
		{
			return 0.42;
		}
		if (diffuse < 0.68)
		{
			return 0.68;
		}
		return 0.94;
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
}
