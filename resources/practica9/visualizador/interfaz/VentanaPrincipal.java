package interfaz;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import renderer.RenderMode;

/**
 * Contiene la ventana principal del sistema
 *
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class VentanaPrincipal extends JFrame implements ActionListener, ComponentListener {
	private static class SidebarPanel extends JPanel implements Scrollable {
		private static final long serialVersionUID = 1L;

		public Dimension getPreferredScrollableViewportSize()
		{
			return getPreferredSize();
		}

		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return 18;
		}

		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return Math.max(36, visibleRect.height - 36);
		}

		public boolean getScrollableTracksViewportWidth()
		{
			return true;
		}

		public boolean getScrollableTracksViewportHeight()
		{
			return false;
		}
	}

	private static final long serialVersionUID = -8360113271848738742L;

	PanelCamara cameraPanel;
	PanelModoVisor visualizationModePanel;
	PanelVisor viewportPanel;
	geometria.Objeto object;

	/**
	 * Crea todos los elementos del interfaz de la ventana principal y los distribuye
	 */
	private void distribuirInterfaz()
	{
		this.setTitle("Visualizador");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addComponentListener(this);
		this.getContentPane().setBackground(InterfazTema.WINDOW_BACKGROUND);

		JPanel root = new JPanel(new BorderLayout(0, 0));
		root.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		root.setBackground(InterfazTema.WINDOW_BACKGROUND);
		this.setContentPane(root);

		cameraPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		cameraPanel.setOpaque(false);
		cameraPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder());

		visualizationModePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		visualizationModePanel.setOpaque(false);
		visualizationModePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder());

		JPanel scenePanel = new JPanel();
		scenePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		scenePanel.setOpaque(false);
		scenePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		scenePanel.setLayout(new BoxLayout(scenePanel, BoxLayout.Y_AXIS));

		JButton materialButton = crearBotonAccion("Material", "material", true);
		JButton lightButton = crearBotonAccion("Luz", "luz", false);
		scenePanel.add(materialButton);
		scenePanel.add(Box.createVerticalStrut(10));
		scenePanel.add(lightButton);
		scenePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, scenePanel.getPreferredSize().height));

		JPanel filePanel = new JPanel();
		filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		filePanel.setOpaque(false);
		filePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));

		JButton openButton = crearBotonAccion("Abrir modelo", "abrir", true);
		JButton closeButton = crearBotonAccion("Cerrar modelo", "cerrar", false);
		JButton screenshotButton = crearBotonAccion("Guardar PNG", "captura", false);
		JButton exitButton = crearBotonAccion("Salir", "salir", false);
		filePanel.add(openButton);
		filePanel.add(Box.createVerticalStrut(10));
		filePanel.add(closeButton);
		filePanel.add(Box.createVerticalStrut(10));
		filePanel.add(screenshotButton);
		filePanel.add(Box.createVerticalStrut(10));
		filePanel.add(exitButton);
		filePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, filePanel.getPreferredSize().height));

		JTextArea visualizationHints = InterfazTema.crearTextoSecundario(
			"Arrastra: órbita\nMayús o botón derecho: desplaza\nRueda: zoom | Ctrl+rueda: FOV\n1-6: modo | O: contorno | V: viñeta | S: captura | R: reset",
			true);
		visualizationHints.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JLabel footer = InterfazTema.crearEtiquetaSecundaria("", true);
		footer.setAlignmentX(Component.LEFT_ALIGNMENT);
		footer.setText("<html><div style='width:244px;'>Arrastra: órbita<br>Mayús o botón derecho: desplaza<br>Rueda: zoom | Ctrl+rueda: FOV<br>1-6: modo | O: contorno | V: viñeta | S: captura | R: reset</div></html>");
		footer.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

		JTabbedPane sidebarTabs = new JTabbedPane();
		InterfazTema.estilizarPestanas(sidebarTabs);
		sidebarTabs.setTabPlacement(JTabbedPane.LEFT);
		sidebarTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		sidebarTabs.setPreferredSize(new Dimension(388, 0));
		sidebarTabs.setMinimumSize(new Dimension(372, 0));
		agregarPestana(sidebarTabs, "Cámara", crearPestanaHerramientas("Cámara", cameraPanel, null));
		agregarPestana(sidebarTabs, "Visualización", crearPestanaHerramientas("Visualización", visualizationModePanel, footer));
		agregarPestana(sidebarTabs, "Escena", crearPestanaHerramientas("Escena", scenePanel, null));
		agregarPestana(sidebarTabs, "Archivo", crearPestanaHerramientas("Archivo", filePanel, null));
		sidebarTabs.setComponentAt(1, crearPestanaHerramientas("Visualización", visualizationModePanel, visualizationHints));
		root.add(sidebarTabs, BorderLayout.EAST);

		JPanel viewportShell = new JPanel(new BorderLayout());
		viewportShell.setOpaque(true);
		viewportShell.setBackground(InterfazTema.VIEWPORT_FRAME);
		viewportShell.setBorder(InterfazTema.crearBordeViewport());
		viewportShell.add(viewportPanel, BorderLayout.CENTER);
		root.add(viewportShell, BorderLayout.CENTER);
		this.setJMenuBar(null);

		this.pack();
		this.setSize(1120, 760);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.viewportPanel.requestFocusInWindow();
	}

	private void agregarPestana(JTabbedPane sidebarTabs, String title, JComponent content)
	{
		sidebarTabs.addTab(title, content);
		int tabIndex = sidebarTabs.getTabCount() - 1;
		sidebarTabs.setTabComponentAt(tabIndex, new RotatedTabLabel(sidebarTabs, title, true));
	}

	private JScrollPane crearPestanaHerramientas(String title, JComponent principal, JComponent secundario)
	{
		SidebarPanel content = new SidebarPanel();
		content.setOpaque(true);
		content.setBackground(InterfazTema.SIDEBAR_BACKGROUND);
		content.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 18, 14, 18));
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		JLabel titleLabel = InterfazTema.crearEtiquetaPanelLateral(title);
		titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		content.add(titleLabel);
		content.add(Box.createVerticalStrut(10));

		content.add(crearBloqueAnchoCompleto(principal));
		if (secundario != null)
		{
			content.add(Box.createVerticalStrut(16));
			content.add(crearBloqueAnchoCompleto(secundario));
		}
		content.add(Box.createVerticalGlue());

		JScrollPane scroll = new JScrollPane(content);
		scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		scroll.setViewportBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.getVerticalScrollBar().setUnitIncrement(18);
		scroll.getViewport().setBackground(InterfazTema.SIDEBAR_BACKGROUND);
		return scroll;
	}

	private JPanel crearBloqueAnchoCompleto(JComponent component)
	{
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(false);
		wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
		wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		component.setAlignmentX(Component.LEFT_ALIGNMENT);
		wrapper.add(component, BorderLayout.NORTH);
		return wrapper;
	}

	private JButton crearBotonAccion(String text, String actionCommand, boolean primary)
	{
		JButton button = new JButton(text);
		button.setActionCommand(actionCommand);
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.addActionListener(this);
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
		InterfazTema.estilizarBoton(button, primary);
		return button;
	}

	public VentanaPrincipal()
	{
		InterfazTema.aplicarTemaGlobal();
		object = new geometria.Objeto();
		viewportPanel = new PanelVisor(object);
		cameraPanel = new PanelCamara(viewportPanel);
		visualizationModePanel = new PanelModoVisor(viewportPanel);
		distribuirInterfaz();
	}

	public void configurarCamaraInicial(double fov, double inclinacion, double rotacion)
	{
		viewportPanel.configurarCamaraInicial(fov, inclinacion, rotacion);
	}

	public void aplicarModoRender(RenderMode mode)
	{
		viewportPanel.modificarModoRender(mode);
	}

	public void aplicarMaterial(escena.Material material)
	{
		viewportPanel.modificarMaterial(material);
	}

	public void aplicarLuz(escena.Luz luz)
	{
		viewportPanel.modificarLuz(luz);
	}

	public boolean cargarModeloInicial(String modelName)
	{
		String trimmedName = modelName == null ? "" : modelName.trim();
		if (trimmedName.length() == 0)
		{
			return false;
		}

		File model = new File(new File("objetos_3d"), trimmedName);
		if (!model.isFile())
		{
			String message = "No se encontro el modelo inicial: " + trimmedName;
			viewportPanel.modificarMensajeEstado(message);
			System.out.println(message + " (" + model.getAbsolutePath() + ")");
			return false;
		}
		return cargarModelo(model);
	}

	public boolean cargarPrimerModeloDisponible()
	{
		File model = buscarPrimerModeloDisponible();
		if (model == null)
		{
			String message = "No se encontro ningun OBJ en la carpeta objetos_3d.";
			viewportPanel.modificarMensajeEstado(message);
			System.out.println(message);
			return false;
		}
		return cargarModelo(model);
	}

	private File buscarPrimerModeloDisponible()
	{
		File objectDirectory = new File("objetos_3d");
		if (!objectDirectory.isDirectory())
		{
			return null;
		}

		File[] objectFiles = objectDirectory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".obj");
			}
		});
		if (objectFiles == null || objectFiles.length == 0)
		{
			return null;
		}

		Arrays.sort(objectFiles);
		return objectFiles[0];
	}

	private boolean cargarModelo(File file)
	{
		try
		{
			object.cargarObj(file.getAbsolutePath());
			viewportPanel.reiniciarCamara();
			viewportPanel.modificarMensajeEstado("Modelo cargado: " + file.getName());
			viewportPanel.repaint();
			System.out.println("Modelo cargado: " + file.getAbsolutePath());
			return true;
		}
		catch (IOException exception)
		{
			String message = "No se pudo cargar el modelo seleccionado.";
			viewportPanel.modificarMensajeEstado(message);
			System.out.println(message + " (" + file.getAbsolutePath() + ")");
			return false;
		}
	}

	/**
	 * Obtiene y procesa todos los clicks que se hacen en el panel lateral.
	 *
	 * @param e La acción
	 */
	public void actionPerformed(ActionEvent e)
	{
		if ("abrir".equals(e.getActionCommand()))
		{
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				cargarModelo(fc.getSelectedFile());
			}
		}
		else if ("cerrar".equals(e.getActionCommand()))
		{
			object.reiniciar();
			viewportPanel.reiniciarCamara();
			viewportPanel.modificarMensajeEstado("No hay ningún modelo cargado.");
			viewportPanel.repaint();
		}
		else if ("salir".equals(e.getActionCommand()))
		{
			System.exit(0);
		}
		else if ("captura".equals(e.getActionCommand()))
		{
			viewportPanel.guardarCaptura();
		}
		else if ("material".equals(e.getActionCommand()))
		{
			@SuppressWarnings("unused")
			DialogoMaterial dm = new DialogoMaterial(this.viewportPanel);
		}
		else if ("luz".equals(e.getActionCommand()))
		{
			@SuppressWarnings("unused")
			DialogoLuz dl = new DialogoLuz(this.viewportPanel);
		}
	}

	public void componentHidden(ComponentEvent e)
	{
	}

	public void componentMoved(ComponentEvent e)
	{
	}

	public void componentResized(ComponentEvent e)
	{
		this.viewportPanel.repaint(100);
	}

	public void componentShown(ComponentEvent e)
	{
		this.viewportPanel.repaint();
	}
}
