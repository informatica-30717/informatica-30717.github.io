package interfaz;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Este panel contiene toda la informacion de la camara a traves de la cual
 * se ve la escena del PanelVisor
 * 
 * @author Adolfo Muñoz Orbañanos
 * @author Alfonso López Ruiz
 */
public class PanelCamara extends JPanel
		implements ChangeListener, ActionListener, PanelVisor.CameraListener {

	private static final long serialVersionUID = 5899890224710793957L;

	private JSpinner spinnerFov;
	private JSpinner spinnerDistancia;
	private JSpinner spinnerRotacion;
	private JSpinner spinnerInclinacion;
	private PanelVisor panelVisor;
	private boolean sincronizando;
	private JButton botonReset;

	private double fov()
	{
		return ((SpinnerNumberModel) spinnerFov.getModel()).getNumber().doubleValue();
	}

	private double distancia()
	{
		return ((SpinnerNumberModel) spinnerDistancia.getModel()).getNumber().doubleValue();
	}

	private double rotacion()
	{
		return ((SpinnerNumberModel) spinnerRotacion.getModel()).getNumber().doubleValue();
	}

	private double inclinacion()
	{
		return ((SpinnerNumberModel) spinnerInclinacion.getModel()).getNumber().doubleValue();
	}

	public PanelCamara(PanelVisor vp)
	{
		panelVisor = vp;
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		SpinnerModel modelFov = new SpinnerNumberModel(panelVisor.camara().fov(), 0.1, 180.0, 1.0);
		spinnerFov = new JSpinner(modelFov);
		InterfazTema.estilizarSpinner(spinnerFov);
		add(InterfazTema.crearFilaCampo("FOV", spinnerFov, true));

		SpinnerModel modelDistance = new SpinnerNumberModel(panelVisor.camara().distancia(), 0.1, 10000.0, 1.0);
		spinnerDistancia = new JSpinner(modelDistance);
		InterfazTema.estilizarSpinner(spinnerDistancia);
		add(InterfazTema.crearFilaCampo("Distancia", spinnerDistancia, true));

		SpinnerModel modelRotation = new SpinnerNumberModel(panelVisor.camara().rotacion(), -180.0, 180.0, 5.0);
		spinnerRotacion = new JSpinner(modelRotation);
		InterfazTema.estilizarSpinner(spinnerRotacion);
		add(InterfazTema.crearFilaCampo("Rotacion", spinnerRotacion, true));

		SpinnerModel modelInclination = new SpinnerNumberModel(panelVisor.camara().inclinacion(), -90.0, 90.0, 5.0);
		spinnerInclinacion = new JSpinner(modelInclination);
		InterfazTema.estilizarSpinner(spinnerInclinacion);
		add(InterfazTema.crearFilaCampo("Inclinacion", spinnerInclinacion, true));

		botonReset = new JButton("Recentrar cámara");
		InterfazTema.estilizarBoton(botonReset, false);
		botonReset.setActionCommand("reset");
		botonReset.addActionListener(this);
		botonReset.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
		JPanel resetRow = new JPanel(new BorderLayout());
		resetRow.setOpaque(false);
		resetRow.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 0, 0, 0));
		resetRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
		resetRow.add(botonReset, BorderLayout.CENTER);
		add(resetRow);

		spinnerFov.addChangeListener(this);
		spinnerDistancia.addChangeListener(this);
		spinnerRotacion.addChangeListener(this);
		spinnerInclinacion.addChangeListener(this);
		panelVisor.establecerListenerCamara(this);
		cameraChanged(panelVisor.camara());
	}

	public void stateChanged(ChangeEvent event)
	{
		if (sincronizando)
		{
			return;
		}
		escena.Camara camara = panelVisor.camara();
		camara.modificarParametros(this.distancia(), this.fov(), this.inclinacion(), this.rotacion());
		panelVisor.modificarCamara(camara);
	}

	public void actionPerformed(ActionEvent event)
	{
		if ("reset".equals(event.getActionCommand()))
		{
			panelVisor.reiniciarCamara();
		}
	}

	public void cameraChanged(escena.Camara camera)
	{
		sincronizando = true;
		((SpinnerNumberModel) spinnerFov.getModel()).setValue(camera.fov());
		((SpinnerNumberModel) spinnerDistancia.getModel()).setValue(camera.distancia());
		((SpinnerNumberModel) spinnerRotacion.getModel()).setValue(camera.rotacion());
		((SpinnerNumberModel) spinnerInclinacion.getModel()).setValue(camera.inclinacion());
		sincronizando = false;
	}
}
