package renderer;

public enum RenderMode {
	WIREFRAME("Malla de alambre"),
	PHONG("Phong"),
	TOON("Toon"),
	CLAY("Clay"),
	NORMALS("Normales"),
	ONDAS("Ondas"),
	DEPTH("Profundidad");

	private final String label;

	RenderMode(String label)
	{
		this.label = label;
	}

	public String label()
	{
		return label;
	}

	public boolean isRaster()
	{
		return this != WIREFRAME;
	}

	public static RenderMode fromIndex(int index)
	{
		RenderMode[] modes = values();
		if (index < 0 || index >= modes.length)
		{
			return CLAY;
		}
		return modes[index];
	}
}