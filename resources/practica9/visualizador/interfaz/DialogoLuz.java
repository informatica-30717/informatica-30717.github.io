package interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Esta clase representa a un cuadro de dialogo que gracias
 * a PanelLuz edita la iluminacion de la escena del PanelVisor
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class DialogoLuz extends JDialog
						implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6136689410898218488L;

	PanelLuz lightPanel;
	PanelVisor wp;
	private escena.Luz originalLight;
	private boolean accepted;
	
	/**
	 * Distribuye el interfaz de este cuadro de dialogo
	 */
	private void distribuirInterfaz()
	{
	    this.setTitle("Luz");
	    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    this.getContentPane().setBackground(InterfazTema.WINDOW_BACKGROUND);
	    
	    BorderLayout layout = new BorderLayout();
	    layout.setHgap(10);
	    layout.setVgap(10);
	    this.getContentPane().setLayout(layout);
	    JPanel dataPanel = new JPanel();
	    dataPanel.setOpaque(false);
	    dataPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setOpaque(false);
	    this.add(dataPanel,BorderLayout.CENTER);
	    this.add(buttonPanel,BorderLayout.SOUTH);

	    //ButtonPanel
	    JButton buttonAccept = new JButton("Aceptar");
	    JButton buttonCancel = new JButton("Cancelar");
	    InterfazTema.estilizarBoton(buttonAccept, true);
	    InterfazTema.estilizarBoton(buttonCancel, false);

	   //Lay out the buttons from left to right.
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    buttonPanel.add(Box.createHorizontalGlue());
	    buttonPanel.add(buttonAccept);
	    buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	    buttonPanel.add(buttonCancel);
	    buttonPanel.add(Box.createHorizontalGlue());
	    
	    dataPanel.setLayout(new BoxLayout(dataPanel,BoxLayout.Y_AXIS));
	    dataPanel.add(lightPanel);
	    
	    buttonAccept.setActionCommand("aceptar");
	    buttonAccept.addActionListener(this);
	    buttonCancel.setActionCommand("cancelar");
	    buttonCancel.addActionListener(this);
	}
	
	/**
	 * Construye el dialogo correspondiente, guardandose el PanelVisor
	 * para modificarlo si se pulsa aceptar
	 * 
	 * @param _wp Panel visor a considerar
	 */
	public DialogoLuz(PanelVisor _wp)
	{
		super();

		wp = _wp;
		originalLight = new escena.Luz(
			new escena.Color(wp.luz().color()),
			new geometria.Punto(wp.luz().posicion()),
			new escena.Color(wp.luz().colorAmbiente()));
		
		lightPanel = new PanelLuz();
		lightPanel.modificarLuz(wp.luz());
		distribuirInterfaz();
		registrarVistaPrevia();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event)
			{
				cancelarCambios();
			}

			public void windowClosed(WindowEvent event)
			{
				if (!accepted)
				{
					restaurarLuzOriginal();
				}
			}
		});
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(wp);
		this.setVisible(true);
	}

	private void registrarVistaPrevia()
	{
		lightPanel.spinnerX.addChangeListener(this);
		lightPanel.spinnerY.addChangeListener(this);
		lightPanel.spinnerZ.addChangeListener(this);
		lightPanel.panelColor.sliderR.addChangeListener(this);
		lightPanel.panelColor.sliderG.addChangeListener(this);
		lightPanel.panelColor.sliderB.addChangeListener(this);
		lightPanel.panelColorAmbiente.sliderR.addChangeListener(this);
		lightPanel.panelColorAmbiente.sliderG.addChangeListener(this);
		lightPanel.panelColorAmbiente.sliderB.addChangeListener(this);
	}

	private void restaurarLuzOriginal()
	{
		wp.modificarLuz(new escena.Luz(
			new escena.Color(originalLight.color()),
			new geometria.Punto(originalLight.posicion()),
			new escena.Color(originalLight.colorAmbiente())));
		wp.modificarMensajeEstado("Luz restaurada.");
	}

	private void cancelarCambios()
	{
		accepted = false;
		restaurarLuzOriginal();
		dispose();
	}
	
	/**
	 * Cuando se pulsa aceptar, se modifica la iluminacion del panel visor
	 * Si es cancelar, simplemente se cierra el cuadro de dialogo
	 * 
	 * Se llama automaticamente a partir del interfaz
	 * 
	 * @param event El evento que ha ocurrido para llamar a este metodo
	 */
	public void actionPerformed(ActionEvent event) {
		if ("aceptar".equals(event.getActionCommand()))
		{
			accepted = true;
			wp.modificarLuz(this.lightPanel.luz());
			wp.modificarMensajeEstado("Luz actualizada.");
		}
		else
		{
			cancelarCambios();
			return;
		}
		this.dispose();
	}

	public void stateChanged(ChangeEvent event)
	{
		wp.modificarLuz(this.lightPanel.luz());
		wp.modificarMensajeEstado("Vista previa de la luz.");
	}

}
