package interfaz;

import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

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
	
	/**
	 * Funcion privada que ayuda a la dsitribucion de las diferentes etiquetas
	 * con respecto a los diferentes sliders
	 * 
	 * @param label Etiqueta
	 * @param slider El correspondiente slider
	 * @return El panel que contiene la etiqueta y el slider
	 */
	private JPanel crearPanelSlider(String label, JSlider slider) 
	{
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(new JLabel(label));
		panel.add(Box.createHorizontalStrut(5));
		panel.add(slider);
		
		Hashtable<Integer,JComponent> labelTable = new Hashtable<Integer,JComponent>();
		labelTable.put( new Integer( 0 ), new JLabel("0") );
		labelTable.put( new Integer( 50 ), new JLabel(new Double(0.5*_max).toString()) );
		labelTable.put( new Integer( 100 ), new JLabel(new Double(_max).toString()) );

        slider.setLabelTable(labelTable);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);

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
		this.setPreferredSize(new Dimension(300,200));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        sliderR = new JSlider(JSlider.HORIZONTAL,0,100,100);
        sliderG = new JSlider(JSlider.HORIZONTAL,0,100,100);
        sliderB = new JSlider(JSlider.HORIZONTAL,0,100,100);
		InterfazTema.estilizarSlider(sliderR, new java.awt.Color(0xD05252));
		InterfazTema.estilizarSlider(sliderG, new java.awt.Color(0x5D9E63));
		InterfazTema.estilizarSlider(sliderB, new java.awt.Color(0x5E7FB7));
		this.add(this.crearPanelSlider("R", sliderR));
		this.add(this.crearPanelSlider("G", sliderG));
		this.add(this.crearPanelSlider("B", sliderB));
		this.add(Box.createVerticalGlue());
	}
	
	
}
