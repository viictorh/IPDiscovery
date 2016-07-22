package br.com.ipdiscovery.view;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class StartGUI {
	public static JFrame frame;

	public StartGUI() {
		prepareView();
		prepareCardLayout();
		showView();
	}

	private void prepareView() {
		frame = new JFrame("IPDiscovery - v_1.0");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
	}

	private void prepareCardLayout() {
		 frame.add(new CardLayoutManager(frame.getContentPane()));
	}

	private void showView() {
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
