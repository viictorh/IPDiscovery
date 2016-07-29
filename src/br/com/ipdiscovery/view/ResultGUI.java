package br.com.ipdiscovery.view;

import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import br.com.ipdiscovery.bean.Execution;
import br.com.ipdiscovery.bean.Result;
import br.com.ipdiscovery.service.FreeIPFinder;
import br.com.ipdiscovery.view.table.ResultTableModel;

public class ResultGUI extends JPanel implements CardLayoutSetting {

	private static final long serialVersionUID = -2023334124969527783L;
	private JProgressBar progressBar;
	private JTable table;
	private JButton cancelButton;
	private Timer timer;
	private Execution execution;

	public ResultGUI(final CardLayoutManager cardLayoutManager, Execution execution) {
		this.execution = execution;
		createGUI();
		preparePanel();
		start(execution);
	}

	private void start(Execution execution) {
		final SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				FreeIPFinder f = new FreeIPFinder(execution);
				try {
					f.startSearch();
				} catch (IOException | InterruptedException e) {

					e.printStackTrace();
				}
				return null;
			}

		};
		createTimer();
		worker.execute();

	}

	private void createTimer() {
		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				progressBar.setValue(execution.getCurrentProgressValue());
				System.out.println(
						"Execução: " + execution.getCurrentProgressValue() + "/" + execution.getMaxProgressValue());
				if (true) {
					if (!execution.isUpToDate()) {
						ResultTableModel model = (ResultTableModel) table.getModel();
						for (Result r : execution.getChangedResults()) {
							model.addRowOrEdit(r);
							execution.upToDate(true, r);
						}
					}
				} else {
					Toolkit.getDefaultToolkit().beep();
					timer.stop();
					// progressBar.setValue(progressBar.getMinimum());
					// TODO remover botao cancelar. Criar botao "voltar" ou
					// "fechar"

				}

			}
		});
		timer.start();

	}

	private void createGUI() {
		// Create the demo's UI.
		this.setLayout(new CardLayout());

		cancelButton = new JButton("Cancelar");
		cancelButton.setActionCommand("cancel");

		progressBar = new JProgressBar(0, 1000);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		table = new JTable();
		ResultTableModel model = new ResultTableModel(new ArrayList<>(), "IP", "Tipo (Proxy/Sem Proxy)", "Status");
		table.setModel(model);
		table.setAutoCreateColumnsFromModel(false);
		// DefaultTableCellRenderer fce = model.new TableStyle();
		// table.setDefaultRenderer(Object.class, fce);
	}

	private void preparePanel() {
		JPanel panel = new JPanel();
		JScrollPane jScrollPane = new JScrollPane(table);
		// jScrollPane.setPreferredSize(new Dimension(1600, 1700));
		table.setFillsViewportHeight(true);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setBorder(BorderFactory.createTitledBorder("Verificando IPs "));
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(cancelButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(progressBar)))
				.addComponent(jScrollPane));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(cancelButton)
						.addComponent(progressBar))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jScrollPane)));

		this.add(panel);
		StartGUI.frame.pack();

	}

	@Override
	public CardLayoutSetting retrievePanel() {
		return this;
	}

	@Override
	public String retrivePanelName() {
		return this.getClass().getSimpleName();
	}

}
