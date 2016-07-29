package br.com.ipdiscovery.view;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Manifest;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author victor.bello
 * 
 * Criação do frame principal do programa
 */

public class StartGUI {
	public static JFrame frame;

	public StartGUI() {
		prepareView();
		prepareCardLayout();
		showView();
	}

	private void prepareView() {
		frame = new JFrame("IPDiscovery - Version: " + readVersion());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
	}

	private String readVersion() {
		URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
		try {
			URL url = cl.findResource("META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(url.openStream());
			return manifest.getMainAttributes().getValue("Manifest-Version");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void prepareCardLayout() {
		frame.add(new CardLayoutManager(frame.getContentPane()));
	}

	private void showView() {
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new StartGUI();
			}
		});
	}

}
