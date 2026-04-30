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
 * a PanelMaterial edita el material del objeto del PanelVisor
 * 
 * @author Adolfo
 */
public class DialogoMaterial extends JDialog 
							implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2614321116693126835L;

	PanelMaterial materialPanel;
	PanelVisor wp;
	private escena.Material originalMaterial;
	private boolean accepted;
	
	/**
	 * Distribuye el interfaz de este cuadro de dialogo
	 */
	private void distribuirInterfaz()
	{
	    this.setTitle("Material");
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
	    dataPanel.add(materialPanel);
	    
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
	public DialogoMaterial(PanelVisor _wp)
	{
		super();

		wp = _wp;
		originalMaterial = new escena.Material(
			new escena.Color(wp.material().kd()),
			wp.material().ks(),
			wp.material().es());
		
		materialPanel = new PanelMaterial();
		materialPanel.modificarMaterial(wp.material());
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
					restaurarMaterialOriginal();
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
		materialPanel.ksSpinner.addChangeListener(this);
		materialPanel.esSpinner.addChangeListener(this);
		materialPanel.kdPanel.sliderR.addChangeListener(this);
		materialPanel.kdPanel.sliderG.addChangeListener(this);
		materialPanel.kdPanel.sliderB.addChangeListener(this);
	}

	private void restaurarMaterialOriginal()
	{
		wp.modificarMaterial(new escena.Material(
			new escena.Color(originalMaterial.kd()),
			originalMaterial.ks(),
			originalMaterial.es()));
		wp.modificarMensajeEstado("Material restaurado.");
	}

	private void cancelarCambios()
	{
		accepted = false;
		restaurarMaterialOriginal();
		dispose();
	}
	
	/**
	 * Cuando se pulsa aceptar, se modifica el material del panel visor
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
			wp.modificarMaterial(this.materialPanel.material());
			wp.modificarMensajeEstado("Material actualizado.");
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
		wp.modificarMaterial(this.materialPanel.material());
		wp.modificarMensajeEstado("Vista previa del material.");
	}
}
