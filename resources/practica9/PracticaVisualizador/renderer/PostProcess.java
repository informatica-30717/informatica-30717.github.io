package renderer;

public class PostProcess {
	public static final boolean ENABLE_VIGNETTE = true;
	public static final boolean ENABLE_OUTLINE = true;
	public static final double OUTLINE_STRENGTH = 0.32;
	public static final double VIGNETTE_STRENGTH = 0.18;
	private static final int[][] OUTLINE_NEIGHBORS = new int[][] {
		{1, 0}, {-1, 0}, {0, 1}, {0, -1}
	};

	private boolean vignetteEnabled = ENABLE_VIGNETTE;
	private boolean outlineEnabled = ENABLE_OUTLINE;

	public void apply(RenderBuffers buffers, RenderMode mode)
	{
		if (!mode.isRaster() || (!vignetteEnabled && !outlineEnabled))
		{
			return;
		}

		int[] pixels = buffers.pixels();
		int[] source = buffers.scratchPixels();
		System.arraycopy(pixels, 0, source, 0, pixels.length);
		double[] depth = buffers.depth();
		float[] normalX = buffers.normalX();
		float[] normalY = buffers.normalY();
		float[] normalZ = buffers.normalZ();
		int width = buffers.width();
		int height = buffers.height();
		double farDepth = 1.0e20;

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int index = y * width + x;
				if (depth[index] >= farDepth)
				{
					continue;
				}

				double factor = 1.0;
				if (outlineEnabled)
				{
					factor *= 1.0 - computeOutline(depth, normalX, normalY, normalZ, width, height, x, y, index);
				}
				if (vignetteEnabled)
				{
					factor *= 1.0 - computeVignette(width, height, x, y);
				}

				pixels[index] = multiply(source[index], factor);
			}
		}
	}

	private double computeOutline(double[] depth, float[] normalX, float[] normalY, float[] normalZ,
			int width, int height, int x, int y, int index)
	{
		double currentDepth = depth[index];
		double strongestDepthDelta = 0.0;
		double strongestNormalDelta = 0.0;

		for (int[] neighbor : OUTLINE_NEIGHBORS)
		{
			int sampleX = x + neighbor[0];
			int sampleY = y + neighbor[1];
			if (sampleX < 0 || sampleX >= width || sampleY < 0 || sampleY >= height)
			{
				continue;
			}

			int sampleIndex = sampleY * width + sampleX;
			if (depth[sampleIndex] >= 1.0e20)
			{
				strongestDepthDelta = Math.max(strongestDepthDelta, 1.0);
				continue;
			}

			strongestDepthDelta = Math.max(strongestDepthDelta,
				Math.min(1.0, Math.abs(depth[sampleIndex] - currentDepth) * 35.0));
			double dx = normalX[index] - normalX[sampleIndex];
			double dy = normalY[index] - normalY[sampleIndex];
			double dz = normalZ[index] - normalZ[sampleIndex];
			strongestNormalDelta = Math.max(strongestNormalDelta, Math.min(1.0, Math.sqrt(dx * dx + dy * dy + dz * dz)));
		}

		return OUTLINE_STRENGTH * Math.max(strongestDepthDelta * 0.6, strongestNormalDelta * 0.7);
	}

	private double computeVignette(int width, int height, int x, int y)
	{
		double nx = (2.0 * x) / Math.max(1.0, width - 1.0) - 1.0;
		double ny = (2.0 * y) / Math.max(1.0, height - 1.0) - 1.0;
		double distance = Math.sqrt(nx * nx + ny * ny);
		double edge = Math.max(0.0, distance - 0.35) / 0.65;
		return Math.min(VIGNETTE_STRENGTH, edge * VIGNETTE_STRENGTH);
	}

	private int multiply(int rgb, double factor)
	{
		int alpha = (rgb >> 24) & 0xff;
		int red = (int) Math.max(0, Math.min(255, Math.round(((rgb >> 16) & 0xff) * factor)));
		int green = (int) Math.max(0, Math.min(255, Math.round(((rgb >> 8) & 0xff) * factor)));
		int blue = (int) Math.max(0, Math.min(255, Math.round((rgb & 0xff) * factor)));
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	public boolean vignetteEnabled()
	{
		return vignetteEnabled;
	}

	public void toggleVignette()
	{
		vignetteEnabled = !vignetteEnabled;
	}

	public boolean outlineEnabled()
	{
		return outlineEnabled;
	}

	public void toggleOutline()
	{
		outlineEnabled = !outlineEnabled;
	}
}
