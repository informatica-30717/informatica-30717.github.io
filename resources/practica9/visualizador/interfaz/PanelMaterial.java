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
 * que contiene y permite editar el material de un objeto
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class PanelMaterial extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1791589782531450932L;

	PanelColor kdPanel;
	JSpinner ksSpinner;
	JSpinner esSpinner;
	
	/**
	 * Modifica el interfaz grafico para que represente el material que se
	 * le pasa como parametro
	 * 
	 * @param material El material que representara el interfaz grafico
	 */
	public void modificarMaterial(escena.Material material)
	{
		kdPanel.modificarColor(material.kd());
		((SpinnerNumberModel)ksSpinner.getModel()).setValue(material.ks());
		((SpinnerNumberModel)esSpinner.getModel()).setValue(material.es());
	}
	
	/**
	 * Devuelve el material represetando por el interfaz grafico
	 * 
	 * @return Material representado por el interfaz grafico
	 */
	public escena.Material material()
	{
		return new escena.Material(
				kdPanel.color(),
				((SpinnerNumberModel)ksSpinner.getModel()).getNumber().doubleValue(),
				((SpinnerNumberModel)esSpinner.getModel()).getNumber().doubleValue());
	}
	
	/**
	 * Constructor del panel
	 */
	public PanelMaterial()
	{
		this.setOpaque(false);
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		kdPanel = new PanelColor(1.0);
		JPanel diffuseSection = InterfazTema.crearSeccionDialogo("Coeficiente difuso", kdPanel);
		diffuseSection.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, diffuseSection.getPreferredSize().height));
		this.add(diffuseSection);
		this.add(Box.createVerticalStrut(12));
		
		JPanel specularPanel = new JPanel();
		specularPanel.setOpaque(false);
		specularPanel.setLayout(new BoxLayout(specularPanel,BoxLayout.Y_AXIS));
		
		JPanel ksPanel = new JPanel();
		ksPanel.setOpaque(false);
		specularPanel.add(ksPanel);
		ksPanel.setLayout(new BoxLayout(ksPanel,BoxLayout.X_AXIS));
		ksPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));

		JPanel esPanel = new JPanel();
		esPanel.setOpaque(false);
		specularPanel.add(esPanel);
		esPanel.setLayout(new BoxLayout(esPanel,BoxLayout.X_AXIS));
		esPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
		
        SpinnerModel modelKs = 
        	new SpinnerNumberModel(0.5, //initial value
                0.0, //min
                1.0, //max
                0.1); //step
		ksSpinner = new JSpinner(modelKs);
		InterfazTema.estilizarSpinner(ksSpinner);
				
        SpinnerModel modelEs = 
        	new SpinnerNumberModel(10.0, //initial value
                1.0, //min
                100.0, //max
                1.0); //step
		esSpinner = new JSpinner(modelEs);
		InterfazTema.estilizarSpinner(esSpinner);
		
		ksPanel.add(Box.createHorizontalGlue());
		ksPanel.add(new JLabel("Coeficiente"));
		ksPanel.add(Box.createHorizontalStrut(20));
		ksPanel.add(ksSpinner);
		ksPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, ksPanel.getPreferredSize().height));
		esPanel.add(Box.createHorizontalGlue());
		esPanel.add(new JLabel("Exponente"));
		esPanel.add(Box.createHorizontalStrut(20));
		esPanel.add(esSpinner);
		esPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, esPanel.getPreferredSize().height));
		JPanel specularSection = InterfazTema.crearSeccionDialogo("Parte especular", specularPanel);
		specularSection.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, specularSection.getPreferredSize().height));
		
		this.add(specularSection);
		this.add(Box.createVerticalGlue());
	}
}
