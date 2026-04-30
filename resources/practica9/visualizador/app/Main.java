package app;

import javax.swing.SwingUtilities;

import interfaz.VentanaPrincipal;

public class Main {
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				new VentanaPrincipal();
			}
		});
	}
}