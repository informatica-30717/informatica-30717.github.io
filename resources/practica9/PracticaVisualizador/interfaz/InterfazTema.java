package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public final class InterfazTema {
	public static final Color WINDOW_BACKGROUND = new Color(0xEEE6DA);
	public static final Color SURFACE_BACKGROUND = new Color(0xFFF9F1);
	public static final Color VIEWPORT_FRAME = new Color(0xF4ECE0);
	public static final Color SIDEBAR_BACKGROUND = new Color(0x22313F);
	public static final Color SIDEBAR_CARD = new Color(0xF9F3EA);
	public static final Color SIDEBAR_PANEL = new Color(0x2A3B4B);
	public static final Color INK = new Color(0x243342);
	public static final Color MUTED = new Color(0x66758A);
	public static final Color ACCENT = new Color(0xC66B3D);
	public static final Color ACCENT_SOFT = new Color(0xE6B28D);
	public static final Color BORDER = new Color(0xDCCCB7);
	public static final Color BORDER_DARK = new Color(0x42576A);
	public static final Color WHITE = new Color(0xFFFDF9);

	private static final String FONT_FAMILY = seleccionarFuente(
		"Aptos",
		"Bahnschrift",
		"Segoe UI Variable Text",
		"Segoe UI");
	private static final Font FONT_BASE = new Font(FONT_FAMILY, Font.PLAIN, 14);
	private static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 16);
	private static final Font FONT_HERO = new Font(FONT_FAMILY, Font.BOLD, 28);

	private InterfazTema()
	{
	}

	private static String seleccionarFuente(String... familias)
	{
		String[] disponibles = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i = 0; i < familias.length; i++)
		{
			for (int j = 0; j < disponibles.length; j++)
			{
				if (familias[i].equals(disponibles[j]))
				{
					return familias[i];
				}
			}
		}
		return Font.SANS_SERIF;
	}

	public static void aplicarTemaGlobal()
	{
		UIManager.put("Panel.background", WINDOW_BACKGROUND);
		UIManager.put("Label.background", WINDOW_BACKGROUND);
		UIManager.put("Label.foreground", INK);
		UIManager.put("Button.font", FONT_BASE);
		UIManager.put("Button.background", WHITE);
		UIManager.put("Button.foreground", INK);
		UIManager.put("Button.select", ACCENT_SOFT);
		UIManager.put("ComboBox.font", FONT_BASE);
		UIManager.put("ComboBox.background", WHITE);
		UIManager.put("ComboBox.foreground", INK);
		UIManager.put("CheckBox.font", FONT_BASE);
		UIManager.put("CheckBox.background", SIDEBAR_BACKGROUND);
		UIManager.put("CheckBox.foreground", INK);
		UIManager.put("Spinner.font", FONT_BASE);
		UIManager.put("Spinner.background", WHITE);
		UIManager.put("Spinner.foreground", INK);
		UIManager.put("TextField.font", FONT_BASE);
		UIManager.put("TextField.background", WHITE);
		UIManager.put("TextField.foreground", INK);
		UIManager.put("MenuBar.background", SIDEBAR_BACKGROUND);
		UIManager.put("MenuBar.foreground", WHITE);
		UIManager.put("Menu.background", SIDEBAR_BACKGROUND);
		UIManager.put("Menu.foreground", WHITE);
		UIManager.put("Menu.selectionBackground", BORDER_DARK);
		UIManager.put("MenuItem.background", SIDEBAR_BACKGROUND);
		UIManager.put("MenuItem.foreground", WHITE);
		UIManager.put("MenuItem.selectionBackground", BORDER_DARK);
		UIManager.put("TabbedPane.font", FONT_BASE);
		UIManager.put("TabbedPane.background", SIDEBAR_BACKGROUND);
		UIManager.put("TabbedPane.foreground", WHITE);
		UIManager.put("TabbedPane.selected", SIDEBAR_PANEL);
		UIManager.put("TabbedPane.focus", SIDEBAR_BACKGROUND);
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		UIManager.put("TabbedPane.tabAreaInsets", new Insets(2, 0, 2, 0));
		UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(0, 0, 0, 0));
		UIManager.put("Slider.background", SIDEBAR_CARD);
		UIManager.put("Slider.foreground", ACCENT);
		UIManager.put("TitledBorder.font", FONT_TITLE.deriveFont(13.0f));
		UIManager.put("TitledBorder.titleColor", INK);
	}

	public static Border crearBordeTarjeta(String title)
	{
		Border outer = new LineBorder(BORDER, 1, true);
		Border inner = new EmptyBorder(14, 14, 14, 14);
		TitledBorder titledBorder = BorderFactory.createTitledBorder(new CompoundBorder(outer, inner), title);
		titledBorder.setTitleColor(INK);
		titledBorder.setTitleFont(FONT_TITLE.deriveFont(13.0f));
		return titledBorder;
	}

	public static Border crearBordeTarjetaOscura()
	{
		return new CompoundBorder(new LineBorder(BORDER_DARK, 1, true), new EmptyBorder(18, 18, 18, 18));
	}

	public static Border crearBordeViewport()
	{
		return new LineBorder(new Color(0xCDBAA0), 1, false);
	}

	public static JLabel crearEtiquetaCabecera(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(WHITE);
		label.setFont(FONT_HERO);
		return label;
	}

	public static JLabel crearEtiquetaSeccion(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(WHITE);
		label.setFont(FONT_TITLE.deriveFont(14.0f));
		return label;
	}

	public static JLabel crearEtiquetaPanelLateral(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(new Color(0xC2CCD6));
		label.setFont(FONT_TITLE.deriveFont(16.0f));
		return label;
	}

	public static Font fuenteMicroAcento()
	{
		return FONT_BASE.deriveFont(Font.BOLD, 11.0f);
	}

	public static JLabel crearEtiquetaSecundaria(String text, boolean darkSurface)
	{
		JLabel label = new JLabel(text);
		label.setForeground(darkSurface ? new Color(0xD6DFE8) : MUTED);
		label.setFont(FONT_BASE);
		return label;
	}

	public static JTextArea crearTextoSecundario(String text, boolean darkSurface)
	{
		JTextArea area = new JTextArea(text);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setFocusable(false);
		area.setOpaque(false);
		area.setFont(FONT_BASE);
		area.setForeground(darkSurface ? new Color(0xAEBCCB) : MUTED);
		area.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		area.setBorder(BorderFactory.createEmptyBorder());
		return area;
	}

	public static JPanel crearFilaCampo(String labelText, JComponent control)
	{
		return crearFilaCampo(labelText, control, false);
	}

	public static JPanel crearFilaCampo(String labelText, JComponent control, boolean darkSurface)
	{
		JPanel row = new JPanel(new BorderLayout(18, 0));
		row.setOpaque(false);
		row.setBorder(new EmptyBorder(6, 0, 6, 0));

		JLabel label = new JLabel(labelText);
		label.setFont(FONT_BASE);
		label.setForeground(darkSurface ? WHITE : INK);
		row.add(label, BorderLayout.WEST);
		row.add(control, BorderLayout.EAST);
		return row;
	}

	public static void estilizarSpinner(JSpinner spinner)
	{
		Dimension spinnerSize = new Dimension(112, 26);
		spinner.setPreferredSize(spinnerSize);
		spinner.setMinimumSize(spinnerSize);
		spinner.setMaximumSize(spinnerSize);
		spinner.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(1, 6, 1, 6)));
		JComponent editor = spinner.getEditor();
		editor.setBackground(WHITE);
		editor.setForeground(INK);
		editor.setPreferredSize(spinnerSize);
		editor.setMinimumSize(spinnerSize);
		editor.setMaximumSize(spinnerSize);
		if (editor instanceof JSpinner.DefaultEditor)
		{
			JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
			defaultEditor.getTextField().setBorder(BorderFactory.createEmptyBorder());
			defaultEditor.getTextField().setColumns(6);
			defaultEditor.getTextField().setFont(FONT_BASE);
			defaultEditor.getTextField().setBackground(WHITE);
			defaultEditor.getTextField().setForeground(INK);
		}
	}

	public static void estilizarComboBox(JComboBox<?> comboBox)
	{
		comboBox.setFont(FONT_BASE);
		comboBox.setBackground(WHITE);
		comboBox.setForeground(INK);
		comboBox.setBorder(new EmptyBorder(4, 4, 4, 4));
		comboBox.setPreferredSize(new Dimension(168, 32));
		comboBox.setMinimumSize(new Dimension(168, 32));
	}

	public static void estilizarCheckBox(JCheckBox checkBox)
	{
		estilizarCheckBox(checkBox, false);
	}

	public static void estilizarCheckBox(JCheckBox checkBox, boolean darkSurface)
	{
		checkBox.setOpaque(false);
		checkBox.setForeground(darkSurface ? WHITE : INK);
		checkBox.setFont(FONT_BASE);
	}

	public static void estilizarSlider(JSlider slider, Color accent)
	{
		slider.setOpaque(false);
		slider.setForeground(accent);
	}

	public static void estilizarBoton(JButton button, boolean primary)
	{
		button.setFocusPainted(false);
		button.setOpaque(true);
		button.setBorder(new CompoundBorder(
			new LineBorder(primary ? ACCENT : new Color(0x50677C), 1, true),
			new EmptyBorder(9, 14, 9, 14)));
		button.setBackground(primary ? ACCENT : SIDEBAR_PANEL);
		button.setForeground(WHITE);
		button.setFont(FONT_BASE.deriveFont(Font.BOLD));
		button.setHorizontalAlignment(SwingConstants.CENTER);
	}

	public static void estilizarBotonPlano(AbstractButton button)
	{
		button.setFocusPainted(false);
		button.setOpaque(false);
		button.setForeground(INK);
		button.setFont(FONT_BASE);
	}

	public static void estilizarPestanas(JTabbedPane tabs)
	{
		tabs.setFont(FONT_BASE);
		tabs.setBackground(SIDEBAR_BACKGROUND);
		tabs.setForeground(WHITE);
		tabs.setOpaque(true);
		tabs.setBorder(BorderFactory.createEmptyBorder());
		tabs.setUI(new BasicTabbedPaneUI() {
			protected void paintContentBorder(java.awt.Graphics g, int tabPlacement, int selectedIndex)
			{
			}

			protected Insets getContentBorderInsets(int tabPlacement)
			{
				return new Insets(0, 0, 0, 0);
			}
		});
	}
}
