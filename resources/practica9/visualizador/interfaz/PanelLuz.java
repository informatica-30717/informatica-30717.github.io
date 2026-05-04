package interfaz;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * Esta clase define un panel que se puede poner como interfaz grafico
 * que contiene y permite editar toda la iluminacion de la escena
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class PanelLuz extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5704123346739927000L;

	PanelColor panelColor;
	JSpinner spinnerX;
	JSpinner spinnerY;
	JSpinner spinnerZ;
	PanelColor panelColorAmbiente;

	/**
	 * Modifica el interfaz grafico para que represente la iluminacion que se le 
	 * pasa como parametro
	 * 
	 * @param luz La luz para definir este interfaz grafico
	 */
	public void modificarLuz(escena.Luz luz)
	{
		panelColor.modificarColor(luz.color());
		((SpinnerNumberModel)spinnerX.getModel()).setValue(luz.posicion().x());
		((SpinnerNumberModel)spinnerY.getModel()).setValue(luz.posicion().y());
		((SpinnerNumberModel)spinnerZ.getModel()).setValue(luz.posicion().z());
		panelColorAmbiente.modificarColor(luz.colorAmbiente());
	}
	
	/**
	 * Devuelve la luz que hay ahora mismo puesta en el panel, que ha sido editada por el
	 * usuario
	 * 
	 * @return La luz resultante
	 */
	public escena.Luz luz()
	{
		return new escena.Luz(
				panelColor.color(),
				new geometria.Punto(
						((SpinnerNumberModel)spinnerX.getModel()).getNumber().doubleValue(),
						((SpinnerNumberModel)spinnerY.getModel()).getNumber().doubleValue(),
						((SpinnerNumberModel)spinnerZ.getModel()).getNumber().doubleValue()),
				panelColorAmbiente.color());	
	}
	
	/**
	 * Construye el panel y distribuye los elementos
	 */
	public PanelLuz()
	{
		this.setOpaque(false);
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		panelColor = new PanelColor(5.0);
		JPanel mainColorSection = InterfazTema.crearSeccionDialogo("Color principal", panelColor);
		mainColorSection.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, mainColorSection.getPreferredSize().height));
		this.add(mainColorSection);
		this.add(Box.createVerticalStrut(12));

		JPanel positionPanel = new JPanel();
		positionPanel.setOpaque(false);
		positionPanel.setLayout(new BoxLayout(positionPanel,BoxLayout.Y_AXIS));
		
		JPanel xPanel = new JPanel();
		xPanel.setOpaque(false);
		positionPanel.add(xPanel);
		xPanel.setLayout(new BoxLayout(xPanel,BoxLayout.X_AXIS));
		xPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));

		JPanel yPanel = new JPanel();
		yPanel.setOpaque(false);
		positionPanel.add(yPanel);
		yPanel.setLayout(new BoxLayout(yPanel,BoxLayout.X_AXIS));
		yPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
		
		JPanel zPanel = new JPanel();
		zPanel.setOpaque(false);
		positionPanel.add(zPanel);
		zPanel.setLayout(new BoxLayout(zPanel,BoxLayout.X_AXIS));
		zPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
 
		SpinnerModel modelX = 
        	new SpinnerNumberModel(0.0, //initial value
                -2000.0, //min
                2000.0, //max
                100); //step
		spinnerX = new JSpinner(modelX);
		InterfazTema.estilizarSpinner(spinnerX);
		
		SpinnerModel modelY = 
        	new SpinnerNumberModel(0.0, //initial value
                -2000.0, //min
                2000.0, //max
                100); //step
		spinnerY = new JSpinner(modelY);
		InterfazTema.estilizarSpinner(spinnerY);

		SpinnerModel modelZ = 
        	new SpinnerNumberModel(0.0, //initial value
                -2000.0, //min
                2000.0, //max
                100); //step
		spinnerZ = new JSpinner(modelZ);
		InterfazTema.estilizarSpinner(spinnerZ);

		xPanel.add(Box.createHorizontalGlue());
		xPanel.add(new JLabel("X"));
		xPanel.add(Box.createHorizontalStrut(20));
		xPanel.add(spinnerX);
		xPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, xPanel.getPreferredSize().height));
		
		yPanel.add(Box.createHorizontalGlue());
		yPanel.add(new JLabel("Y"));
		yPanel.add(Box.createHorizontalStrut(20));
		yPanel.add(spinnerY);
		yPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, yPanel.getPreferredSize().height));
		
		zPanel.add(Box.createHorizontalGlue());
		zPanel.add(new JLabel("Z"));
		zPanel.add(Box.createHorizontalStrut(20));
		zPanel.add(spinnerZ);
		zPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, zPanel.getPreferredSize().height));
		
		JPanel positionSection = InterfazTema.crearSeccionDialogo("Posicion", positionPanel);
		positionSection.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, positionSection.getPreferredSize().height));
		this.add(positionSection);
		this.add(Box.createVerticalStrut(12));
		
		panelColorAmbiente = new PanelColor(1.0);
		JPanel ambientColorSection = InterfazTema.crearSeccionDialogo("Color ambiente", panelColorAmbiente);
		ambientColorSection.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, ambientColorSection.getPreferredSize().height));
		this.add(ambientColorSection);

		this.add(Box.createVerticalGlue());
	}
}
