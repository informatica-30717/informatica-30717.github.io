package interfaz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class RotatedTabLabel extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final int HORIZONTAL_PADDING = 6;
	private static final int VERTICAL_PADDING = 3;

	private final JTabbedPane tabbedPane;
	private final String title;
	private final boolean clockwise;

	public RotatedTabLabel(JTabbedPane tabbedPane, String title, boolean clockwise)
	{
		this.tabbedPane = tabbedPane;
		this.title = title;
		this.clockwise = clockwise;
		Font baseFont = UIManager.getFont("TabbedPane.font");
		if (baseFont == null)
		{
			baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
		}
		setFont(baseFont.deriveFont(Font.PLAIN, 11.0f));
		setOpaque(false);
	}

	public Dimension getPreferredSize()
	{
		FontMetrics metrics = getFontMetrics(getFont());
		int textWidth = metrics.stringWidth(title) + (HORIZONTAL_PADDING * 2);
		int textHeight = metrics.getHeight() + (VERTICAL_PADDING * 2);
		return new Dimension(textHeight, textWidth);
	}

	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g2 = (Graphics2D) graphics.create();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(getFont());
		g2.setColor(resolveForeground());
		g2.translate(getWidth() / 2.0, getHeight() / 2.0);
		g2.rotate(clockwise ? Math.PI / 2.0 : -Math.PI / 2.0);

		FontMetrics metrics = g2.getFontMetrics();
		int textX = -metrics.stringWidth(title) / 2;
		int textY = (metrics.getAscent() - metrics.getDescent()) / 2;
		g2.drawString(title, textX, textY);
		g2.dispose();
	}

	private Color resolveForeground()
	{
		int tabIndex = tabbedPane.indexOfTabComponent(this);
		if (tabIndex == tabbedPane.getSelectedIndex())
		{
			return InterfazTema.WHITE;
		}
		return new Color(0xD6DFE8);
	}
}
