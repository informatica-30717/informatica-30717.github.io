package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

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
import javax.swing.plaf.basic.BasicSliderUI;
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
		"Segoe UI Variable Text",
		"Segoe UI",
		"Aptos",
		"Bahnschrift");
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
		UIManager.put("TabbedPane.tabAreaInsets", new Insets(8, 8, 0, 8));
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

	public static JPanel crearSeccionDialogo(String title, JComponent content)
	{
		JPanel section = new JPanel(new BorderLayout(0, 7));
		section.setOpaque(false);
		section.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		JLabel label = new JLabel(title);
		label.setForeground(MUTED);
		label.setFont(FONT_TITLE.deriveFont(12.5f));
		label.setBorder(new EmptyBorder(0, 2, 0, 0));
		section.add(label, BorderLayout.NORTH);

		JPanel card = new JPanel(new BorderLayout());
		card.setOpaque(true);
		card.setBackground(SIDEBAR_CARD);
		card.setBorder(new CompoundBorder(
			new LineBorder(new Color(0xE5D7C5), 1, true),
			new EmptyBorder(10, 12, 10, 12)));
		card.add(content, BorderLayout.CENTER);
		section.add(card, BorderLayout.CENTER);

		return section;
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
		slider.setOpaque(true);
		slider.setForeground(accent);
		slider.setBackground(SIDEBAR_CARD);
		slider.setFocusable(false);
		slider.setPaintTicks(false);
		slider.setPaintLabels(false);
		slider.setPreferredSize(new Dimension(178, 36));
		slider.setMinimumSize(new Dimension(132, 36));
		slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		slider.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		slider.setUI(new BasicSliderUI(slider) {
			public void setThumbLocation(int x, int y)
			{
				super.setThumbLocation(x, y);
				slider.repaint();
				Container parent = slider.getParent();
				if (parent != null)
				{
					parent.repaint();
				}
			}

			public void paint(Graphics g, JComponent component)
			{
				g.setColor(component.getBackground());
				g.fillRect(0, 0, component.getWidth(), component.getHeight());
				super.paint(g, component);
			}

			protected Dimension getThumbSize()
			{
				return new Dimension(18, 18);
			}

			public void paintTrack(Graphics g)
			{
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				int y = trackRect.y + (trackRect.height / 2) - 3;
				int start = trackRect.x;
				int width = trackRect.width;
				int fill = thumbRect.x + (thumbRect.width / 2) - start;
				if (fill < 0)
				{
					fill = 0;
				}
				if (fill > width)
				{
					fill = width;
				}

				g2.setColor(new Color(0xE2D4C3));
				g2.fillRoundRect(start, y, width, 6, 6, 6);
				g2.setColor(accent);
				g2.fillRoundRect(start, y, fill, 6, 6, 6);
				g2.dispose();
			}

			public void paintThumb(Graphics g)
			{
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				int x = thumbRect.x + 2;
				int y = thumbRect.y + 2;
				int size = thumbRect.width - 4;

				g2.setColor(WHITE);
				g2.fillOval(x, y, size, size);
				g2.setColor(accent);
				g2.drawOval(x, y, size - 1, size - 1);
				g2.dispose();
			}

			public void paintFocus(Graphics g)
			{
			}
		});
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
		tabs.setFont(FONT_BASE.deriveFont(Font.BOLD, 12.0f));
		tabs.setBackground(SIDEBAR_BACKGROUND);
		tabs.setForeground(new Color(0xD8E1EA));
		tabs.setOpaque(true);
		tabs.setBorder(BorderFactory.createEmptyBorder());
		tabs.setFocusable(false);
		tabs.setUI(new BasicTabbedPaneUI() {
			protected void installDefaults()
			{
				super.installDefaults();
				tabInsets = new Insets(8, 12, 8, 12);
				selectedTabPadInsets = new Insets(0, 0, 0, 0);
				tabAreaInsets = new Insets(8, 8, 0, 8);
				contentBorderInsets = new Insets(0, 0, 0, 0);
			}

			protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight)
			{
				return 34;
			}

			protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
					int x, int y, int w, int h, boolean isSelected)
			{
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(isSelected ? SIDEBAR_PANEL : new Color(0x243747));
				g2.fillRoundRect(x + 2, y + 3, w - 4, h - 6, 8, 8);
				if (isSelected)
				{
					g2.setColor(ACCENT);
					g2.fillRoundRect(x + 12, y + h - 6, w - 24, 2, 2, 2);
				}
				g2.dispose();
			}

			protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
					int x, int y, int w, int h, boolean isSelected)
			{
			}

			protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
					int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected)
			{
			}

			protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
					int tabIndex, String title, Rectangle textRect, boolean isSelected)
			{
				g.setFont(font);
				g.setColor(isSelected ? WHITE : new Color(0xB8C7D5));
				g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
			}

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
