package br.com.ipdiscovery.view;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import br.com.ipdiscovery.bean.Execution;
import br.com.ipdiscovery.service.FreeIPFinder;
import br.com.ipdiscovery.view.table.ResultTableModel;

public class ResultGUI extends JPanel implements CardLayoutSetting {

	private JProgressBar progressBar;
	private JTable table;
	private JButton cancelButton;

	public ResultGUI(final CardLayoutManager cardLayoutManager, Execution execution) {
		createGUI(execution);
		preparePanel();
//		start(execution);
	}

	private void start(Execution execution) {
		FreeIPFinder f = new FreeIPFinder(execution);
		try {
			f.startSearch();
		} catch (IOException | InterruptedException e) {

			e.printStackTrace();
		}

	}

	private void createGUI(Execution execution) {
		// Create the demo's UI.
		this.setLayout(new CardLayout());

		cancelButton = new JButton("Cancelar");
		cancelButton.setActionCommand("cancel");

		progressBar = new JProgressBar(0, 1000);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		table = new JTable();
		ResultTableModel model = new ResultTableModel(execution.getResults(), "Aplica��o", "Status", "Mensagem");
		table.setModel(model);
		table.setAutoCreateColumnsFromModel(false);
		// DefaultTableCellRenderer fce = model.new TableStyle();
		// table.setDefaultRenderer(Object.class, fce);
	}

	private void preparePanel() {
		JPanel panel = new JPanel();
		JScrollPane jScrollPane = new JScrollPane(table);
		jScrollPane.setPreferredSize(new Dimension(400, 500));
		table.setFillsViewportHeight(true);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setBorder(BorderFactory.createTitledBorder("Acompanhamento "));
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
