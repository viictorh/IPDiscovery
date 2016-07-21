package br.com.ipdiscovery.view;

import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.com.ipdiscovery.bean.NetworkAdapter;
import br.com.ipdiscovery.service.NetworkAdapterReader;

public class ConfigurationGUI extends JPanel implements CardLayoutSetting {

	private static final long serialVersionUID = -5879975686506844823L;
	private JLabel ipRangeLabel = new JLabel("Range de ip: ");
	private JTextArea ipRangeInput = new JTextArea(2, 20);
	private JLabel blockedSiteLabel = new JLabel("Site bloqueado: ");
	private JTextField blockedSiteInput = new JTextField(20);
	private JLabel maskLabel = new JLabel("Mascara de rede: ");
	private JTextField maskInput = new JTextField(20);
	private JLabel gatewayLabel = new JLabel("Gateway: ");
	private JTextField gatewayInput = new JTextField(20);
	private JLabel dns1Label = new JLabel("DNS 1: ");
	private JTextField dns1Input = new JTextField(20);
	private JLabel dns2Label = new JLabel("DNS 2: ");
	private JTextField dns2Input = new JTextField(20);
	private JLabel proxyLabel = new JLabel("Verificar Proxy: ");

	public ConfigurationGUI(CardLayoutManager cardLayoutManager) {
		loadConfig();
		prepareFields();
		preparePanel();
	}

	private void loadConfig() {
		NetworkAdapter networkAdapter;
		try {
			networkAdapter = new NetworkAdapterReader().loadNetworkAdapterConfiguration();
			System.out.println(networkAdapter);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void prepareFields() {
		ipRangeInput.setToolTipText("Separe os ranges com ponto-e-virgula (;) Ex: 192.168.0.2; 192.168.1.65");
		ipRangeInput.setLineWrap(true);
	}

	private void preparePanel() {
		JScrollPane areaScroll = new JScrollPane(ipRangeInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setBorder(BorderFactory.createTitledBorder("Verifique se as configurações estão corretas "));
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(ipRangeLabel)
						.addComponent(blockedSiteLabel).addComponent(maskLabel).addComponent(gatewayLabel)
						.addComponent(dns1Label).addComponent(dns2Label))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(areaScroll)
						.addComponent(blockedSiteInput).addComponent(maskInput).addComponent(gatewayInput)
						.addComponent(dns1Input).addComponent(dns2Input)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(ipRangeLabel)
						.addComponent(areaScroll))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(blockedSiteLabel)
						.addComponent(blockedSiteInput))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(maskLabel)
						.addComponent(maskInput))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(gatewayLabel)
						.addComponent(gatewayInput))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(dns1Label)
						.addComponent(dns1Input))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(dns2Label)
						.addComponent(dns2Input)));
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
