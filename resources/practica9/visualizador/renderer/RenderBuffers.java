package renderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class RenderBuffers {
	private BufferedImage colorBuffer;
	private int[] pixels;
	private double[] depth;
	private float[] normalX;
	private float[] normalY;
	private float[] normalZ;
	private int[] scratchPixels;
	private int width;
	private int height;

	public void ensureSize(int newWidth, int newHeight)
	{
		if (newWidth <= 0 || newHeight <= 0)
		{
			return;
		}
		if (newWidth == width && newHeight == height && colorBuffer != null)
		{
			return;
		}

		width = newWidth;
		height = newHeight;
		colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) colorBuffer.getRaster().getDataBuffer()).getData();
		depth = new double[width * height];
		normalX = new float[width * height];
		normalY = new float[width * height];
		normalZ = new float[width * height];
		scratchPixels = new int[width * height];
	}

	public void clearGradient(int topRgb, int bottomRgb, double farDepth)
	{
		if (pixels == null)
		{
			return;
		}

		for (int y = 0; y < height; y++)
		{
			double t = height <= 1 ? 0.0 : (double) y / (double) (height - 1);
			int rowRgb = mixRgb(topRgb, bottomRgb, t);
			int offset = y * width;
			Arrays.fill(pixels, offset, offset + width, rowRgb);
		}
		Arrays.fill(depth, farDepth);
		Arrays.fill(normalX, 0.0f);
		Arrays.fill(normalY, 0.0f);
		Arrays.fill(normalZ, 0.0f);
	}

	public void clearTransparent(double farDepth)
	{
		if (pixels == null)
		{
			return;
		}

		Arrays.fill(pixels, 0x00000000);
		Arrays.fill(depth, farDepth);
		Arrays.fill(normalX, 0.0f);
		Arrays.fill(normalY, 0.0f);
		Arrays.fill(normalZ, 0.0f);
	}

	private int mixRgb(int topRgb, int bottomRgb, double t)
	{
		int topR = (topRgb >> 16) & 0xff;
		int topG = (topRgb >> 8) & 0xff;
		int topB = topRgb & 0xff;
		int bottomR = (bottomRgb >> 16) & 0xff;
		int bottomG = (bottomRgb >> 8) & 0xff;
		int bottomB = bottomRgb & 0xff;

		int r = (int) Math.round(topR * (1.0 - t) + bottomR * t);
		int g = (int) Math.round(topG * (1.0 - t) + bottomG * t);
		int b = (int) Math.round(topB * (1.0 - t) + bottomB * t);
		return 0xff000000 | (r << 16) | (g << 8) | b;
	}

	public int index(int x, int y)
	{
		return y * width + x;
	}

	public BufferedImage colorBuffer()
	{
		return colorBuffer;
	}

	public int[] pixels()
	{
		return pixels;
	}

	public double[] depth()
	{
		return depth;
	}

	public float[] normalX()
	{
		return normalX;
	}

	public float[] normalY()
	{
		return normalY;
	}

	public float[] normalZ()
	{
		return normalZ;
	}

	public int[] scratchPixels()
	{
		return scratchPixels;
	}

	public int width()
	{
		return width;
	}

	public int height()
	{
		return height;
	}
}
