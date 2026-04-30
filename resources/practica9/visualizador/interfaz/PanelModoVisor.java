package interfaz;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import renderer.RenderMode;

/**
 * Panel que define el modo de visualización de un PanelVisor.
 *
 * Esto incluye si es raster o malla de alambre, y si se utiliza
 * backface culling o no.
 *
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class PanelModoVisor extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1241500050560512894L;

	JComboBox<String> modeBox;
	JComboBox<String> qualityBox;
	JCheckBox backfaceCullingBox;
	JCheckBox debugBackfaceCullingBox;
	PanelVisor viewportPanel;

	/**
	 * Construye el panel.
	 *
	 * @param vp El visor que está mostrando el objeto en el modo correspondiente
	 */
	PanelModoVisor(PanelVisor vp)
	{
		viewportPanel = vp;
		setOpaque(false);
		setLayout(new GridBagLayout());
		setAlignmentX(Component.LEFT_ALIGNMENT);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 10, 0);

		JTextArea description = InterfazTema.crearTextoSecundario(
			"Alterna modos de render, resolución y backface culling.",
			true);
		description.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		constraints.gridy = 0;
		add(description, constraints);

		RenderMode[] modes = RenderMode.values();
		String[] modeStrings = new String[modes.length];
		for (int i = 0; i < modes.length; i++)
		{
			modeStrings[i] = modes[i].label();
		}
		modeBox = new JComboBox<String>(modeStrings);
		InterfazTema.estilizarComboBox(modeBox);
		modeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		modeBox.setSelectedIndex(viewportPanel.modoRender().ordinal());
		modeBox.addActionListener(this);
		constraints.gridy = 1;
		add(InterfazTema.crearFilaCampo("Modo", modeBox, true), constraints);

		qualityBox = new JComboBox<String>(new String[] {"Baja (1x)", "Alta (2x)", "Muy alta (3x)"});
		InterfazTema.estilizarComboBox(qualityBox);
		qualityBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		qualityBox.setSelectedIndex(viewportPanel.escalaRender() - 1);
		qualityBox.addActionListener(this);
		constraints.gridy = 2;
		add(InterfazTema.crearFilaCampo("Resolución", qualityBox, true), constraints);

		JTextArea qualityHint = InterfazTema.crearTextoSecundario(
			"1x prioriza fluidez. 3x mejora la nitidez del render.",
			true);
		qualityHint.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		constraints.gridy = 3;
		add(qualityHint, constraints);

		backfaceCullingBox = new JCheckBox("Backface culling");
		InterfazTema.estilizarCheckBox(backfaceCullingBox, true);
		backfaceCullingBox.setForeground(new Color(0xC2CCD6));
		backfaceCullingBox.setSelected(viewportPanel.backfaceCulling());
		backfaceCullingBox.addActionListener(this);

		debugBackfaceCullingBox = new JCheckBox("Ver descartadas");
		InterfazTema.estilizarCheckBox(debugBackfaceCullingBox, true);
		debugBackfaceCullingBox.setForeground(new Color(0xC2CCD6));
		debugBackfaceCullingBox.setSelected(viewportPanel.depuracionBackfaceCulling());
		debugBackfaceCullingBox.setToolTipText("Muestra solo las caras descartadas cuando el culling está activo.");
		debugBackfaceCullingBox.addActionListener(this);

		JPanel toggleRow = new JPanel(new GridLayout(1, 2, 12, 0));
		toggleRow.setOpaque(false);
		toggleRow.add(backfaceCullingBox);
		toggleRow.add(debugBackfaceCullingBox);
		constraints.gridy = 4;
		constraints.insets = new Insets(6, 0, 0, 0);
		add(toggleRow, constraints);
	}

	public Dimension getMaximumSize()
	{
		Dimension preferred = getPreferredSize();
		return new Dimension(Integer.MAX_VALUE, preferred.height);
	}

	/**
	 * Modifica el modo del PanelVisor cada vez que se modifique algún elemento del interfaz.
	 *
	 * @param event Se ignora este parámetro
	 */
	public void actionPerformed(ActionEvent event)
	{
		this.viewportPanel.modificarModoRender(RenderMode.fromIndex(modeBox.getSelectedIndex()));
		viewportPanel.modificarEscalaRender(qualityBox.getSelectedIndex() + 1);
		viewportPanel.modificarBackfaceCulling(backfaceCullingBox.isSelected());
		viewportPanel.modificarDepuracionBackfaceCulling(debugBackfaceCullingBox.isSelected());
		viewportPanel.repaint();
	}
}
