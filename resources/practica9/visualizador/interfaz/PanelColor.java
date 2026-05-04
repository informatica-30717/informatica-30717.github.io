package interfaz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel de interfaz grafico que define un color en coordenadas RGB
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class PanelColor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582279387075471806L;

	/**
	 * Modifica el color representado por este panel
	 * 
	 * @param v Nuevo color
	 */
	public void modificarColor(escena.Color v)
	{
		int r = (int)(100.0*v.r()/this._max); if (r<0) r=0; if (r>100) r=100;
		sliderR.setValue(r);
		int g = (int)(100.0*v.g()/this._max); if (g<0) g=0; if (g>100) g=100;
		sliderG.setValue(g);
		int b = (int)(100.0*v.b()/this._max); if (b<0) b=0; if (b>100) b=100;
		sliderB.setValue(b);
	}
	
	/**
	 * Obtiene el color representado por este panel
	 * 
	 * @return Color representado por este panel
	 */
	public escena.Color color()
	{
		return new escena.Color(
				(double)(sliderR.getValue())*this._max/100.0,
				(double)(sliderG.getValue())*this._max/100.0,
				(double)(sliderB.getValue())*this._max/100.0);		
	}
	
	private double _max;
	JSlider sliderR;
	JSlider sliderG;
	JSlider sliderB;

	private String textoValor(JSlider slider)
	{
		double value = (double)(slider.getValue()) * this._max / 100.0;
		return String.format(Locale.US, "%.2f", value);
	}

	private JLabel crearEtiquetaCanal(String text, Color accent)
	{
		JLabel label = new JLabel(text);
		label.setOpaque(true);
		label.setBackground(accent);
		label.setForeground(InterfazTema.WHITE);
		label.setFont(label.getFont().deriveFont(Font.BOLD, 12.0f));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(24, 24));
		label.setMinimumSize(new Dimension(24, 24));
		label.setMaximumSize(new Dimension(24, 24));
		return label;
	}
	
	/**
	 * Funcion privada que ayuda a la dsitribucion de las diferentes etiquetas
	 * con respecto a los diferentes sliders
	 * 
	 * @param label Etiqueta
	 * @param slider El correspondiente slider
	 * @return El panel que contiene la etiqueta y el slider
	 */
	private JPanel crearPanelSlider(String label, JSlider slider, Color accent) 
	{
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(InterfazTema.SIDEBAR_CARD);
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(new EmptyBorder(7, 2, 7, 2));

		JLabel valueLabel = new JLabel(textoValor(slider));
		valueLabel.setForeground(InterfazTema.MUTED);
		valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 12.0f));
		valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		valueLabel.setPreferredSize(new Dimension(48, 24));
		valueLabel.setMinimumSize(new Dimension(48, 24));
		valueLabel.setMaximumSize(new Dimension(48, 24));

		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event)
			{
				valueLabel.setText(textoValor(slider));
				slider.repaint();
				panel.repaint();
			}
		});

		panel.add(crearEtiquetaCanal(label, accent));
		panel.add(Box.createHorizontalStrut(10));
		panel.add(slider);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(valueLabel);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        return panel;
	}
	
	/**
	 * Construye el PanelColor
	 * 
	 * @param max Valor maximo que podra tener el color, que sera representado en los
	 * sliders
	 */
	public PanelColor(double max)
	{
		_max=max;
		this.setOpaque(false);
		this.setPreferredSize(new Dimension(320,178));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 178));
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		Color red = new Color(0xD05252);
		Color green = new Color(0x5D9E63);
		Color blue = new Color(0x5E7FB7);
        sliderR = new JSlider(JSlider.HORIZONTAL,0,100,100);
        sliderG = new JSlider(JSlider.HORIZONTAL,0,100,100);
        sliderB = new JSlider(JSlider.HORIZONTAL,0,100,100);
		InterfazTema.estilizarSlider(sliderR, red);
		InterfazTema.estilizarSlider(sliderG, green);
		InterfazTema.estilizarSlider(sliderB, blue);
		this.add(this.crearPanelSlider("R", sliderR, red));
		this.add(this.crearPanelSlider("G", sliderG, green));
		this.add(this.crearPanelSlider("B", sliderB, blue));
		this.add(Box.createVerticalGlue());
	}
	
	
}
