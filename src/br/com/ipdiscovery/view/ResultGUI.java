package br.com.ipdiscovery.view;

import java.awt.CardLayout;
import java.awt.Toolkit;
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
import javax.swing.table.DefaultTableCellRenderer;

import br.com.ipdiscovery.bean.Execution;
import br.com.ipdiscovery.bean.Result;
import br.com.ipdiscovery.service.FreeIPFinder;
import br.com.ipdiscovery.view.table.ResultTableModel;

public class ResultGUI extends JPanel implements CardLayoutSetting {

	private static final long serialVersionUID = -2023334124969527783L;
	private JProgressBar progressBar;
	private JTable table;
	private JButton cancelButton;
	private JButton returnButton;
	private Timer timer;
	private Execution execution;
	private SwingWorker<Object, Object> worker;

	public ResultGUI(final CardLayoutManager cardLayoutManager, Execution execution) {
		this.execution = execution;
		createGUI();
		prepareButtons(cardLayoutManager, this);
		preparePanel();
	}

	private void prepareButtons(CardLayoutManager cardLayoutManager, final ResultGUI resultGUI) {
		cancelButton.addActionListener(l -> {
			worker.cancel(true);
			returnButton.setVisible(true);
			cancelButton.setVisible(false);
		});

		returnButton.addActionListener(l -> {
			cardLayoutManager.removePanel(resultGUI);
			cardLayoutManager.changeVisibleCardLayout(ConfigurationGUI.class.getSimpleName());
		});
	}

	public void start() {
		worker = new SwingWorker<Object, Object>() {
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
		timer = new Timer(1000, l -> {
			progressBar.setValue(execution.getCurrentProgressValue());
			System.out.println(
					"Execução: " + execution.getCurrentProgressValue() + "/" + execution.getMaxProgressValue());
			if (!worker.isCancelled() && execution.getCurrentProgressValue() < execution.getMaxProgressValue()) {
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
			}
		});
		timer.start();

	}

	private void createGUI() {
		this.setLayout(new CardLayout());
		cancelButton = new JButton("Cancelar");
		returnButton = new JButton("Voltar");
		returnButton.setVisible(false);

		progressBar = new JProgressBar(0, execution.getMaxProgressValue());
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		table = new JTable();
		ResultTableModel model = new ResultTableModel(new ArrayList<>(), "IP", "Tipo (Proxy/Sem Proxy)", "Status");
		table.setModel(model);
		table.setAutoCreateColumnsFromModel(false);
		DefaultTableCellRenderer fce = model.new TableStyle();
		table.setDefaultRenderer(Object.class, fce);
	}

	private void preparePanel() {
		JPanel panel = new JPanel();
		JScrollPane jScrollPane = new JScrollPane(table);
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
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(returnButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(progressBar)))
				.addComponent(jScrollPane));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(cancelButton)
						.addComponent(returnButton).addComponent(progressBar))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jScrollPane)));

		this.add(panel);
		StartGUI.frame.repaint();
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
