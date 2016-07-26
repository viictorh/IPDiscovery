package br.com.ipdiscovery.view;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;

import br.com.ipdiscovery.bean.Execution;
import br.com.ipdiscovery.bean.NetworkAdapter;
import br.com.ipdiscovery.bean.ProxyConfiguration;
import br.com.ipdiscovery.bean.SearchConfiguration;
import br.com.ipdiscovery.constant.SearchType;
import br.com.ipdiscovery.service.NetworkAdapterReader;

public class ConfigurationGUI extends JPanel implements CardLayoutSetting {

	private static final String IPV4_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static final long serialVersionUID = -5879975686506844823L;
	private JLabel ipRangeLabel = new JLabel("Range de ip: ");
	private JTextArea ipRangeInput = new JTextArea(2, 20);
	private JLabel ipRangeLabelError = new JLabel();
	private JLabel blockedSiteLabel = new JLabel("Site bloqueado: ");
	private JTextField blockedSiteInput = new JTextField(20);
	private JLabel blockedSiteLabelError = new JLabel();
	private JLabel maskLabel = new JLabel("Mascara de rede: ");
	private JTextField maskInput = new JTextField(20);
	private JLabel maskLabelError = new JLabel();
	private JLabel gatewayLabel = new JLabel("Gateway: ");
	private JTextField gatewayInput = new JTextField(20);
	private JLabel gatewayLabelError = new JLabel();
	private JLabel dns1Label = new JLabel("DNS 1: ");
	private JTextField dns1Input = new JTextField(20);
	private JLabel dns1LabelError = new JLabel();
	private JLabel dns2Label = new JLabel("DNS 2: ");
	private JTextField dns2Input = new JTextField(20);
	private JLabel dns2LabelError = new JLabel();
	private JLabel proxyLabel = new JLabel("Verificar Proxy: ");
	private JCheckBox proxyBox = new JCheckBox();
	private JLabel proxyIpLabel = new JLabel("IP do proxy: ");
	private JTextField proxyIpInput = new JTextField(20);
	private JLabel proxyIpLabelError = new JLabel();
	private JLabel proxyPortLabel = new JLabel("Porta do proxy: ");
	private JTextField proxyPortInput = new JTextField(20);
	private JLabel proxyPortLabelError = new JLabel();
	private JButton startSearch = new JButton("Iniciar");
	private SearchConfiguration config = new SearchConfiguration();
	private NetworkAdapter networkAdapter;

	public ConfigurationGUI(final CardLayoutManager cardLayoutManager) {
		loadConfig();
		prepareFields(cardLayoutManager);
		preparePanel();
	}

	private void loadConfig() {
		try {
			networkAdapter = new NetworkAdapterReader().loadNetworkAdapterConfiguration();
			System.out.println(networkAdapter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void prepareFields(final CardLayoutManager cardLayoutManager) {
		ipRangeInput.setToolTipText("Separe os ranges com ponto-e-virgula (;) Ex: 192.168.0.2; 192.168.1.65");
		ipRangeInput.setLineWrap(true);
		ipRangeInput.setText(removeLastIpBlock(networkAdapter.getIp()) + "1" + ";");
		blockedSiteInput.setText(config.getBlockedWebPage());
		maskInput.setText(networkAdapter.getMask());
		gatewayInput.setText(networkAdapter.getGateway());
		dns1Input.setText(networkAdapter.getDns1());
		dns2Input.setText(networkAdapter.getDns2());
		changeProxyVisibility(false);
		proxyBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				changeProxyVisibility(true);
			} else {
				changeProxyVisibility(false);
			}
		});
		startSearch.addActionListener(l -> {
			if (validConfiguration()) {
				Execution execution = new Execution(config, networkAdapter);
				ResultGUI resultGUI = new ResultGUI(cardLayoutManager, execution);
				cardLayoutManager.addPanelToCardLayout(resultGUI);
				cardLayoutManager.changeVisibleCardLayout(ResultGUI.class.getSimpleName());
			}
		});
	}

	private String removeLastIpBlock(String ip) {
		return ip.substring(0, ip.lastIndexOf(".") + 1);
	}

	private boolean validConfiguration() {
		boolean valid = true;
		String rangeInput = ipRangeInput.getText();
		if (StringUtils.isBlank(rangeInput)) {
			valid = false;
			fieldValidError(ipRangeInput, ipRangeLabelError, "Preencha ao menos um IP");
		} else {
			ipRangeInput.setText(rangeInput.endsWith(";") ? rangeInput : rangeInput + ";");
			String[] ranges = rangeInput.split(";");
			List<String> ipRange = new ArrayList<>();
			String invalidIp = "";
			String comma = "";
			for (String ip : ranges) {
				if (!validIP(ip)) {
					valid = false;
					invalidIp += comma + ip;
					comma = ", ";
				} else {
					ipRange.add(ip);
				}
			}
			if (StringUtils.isBlank(invalidIp)) {
				fieldValidSuccess(ipRangeInput, ipRangeLabelError);
				config.setIpRange(ipRange);
			} else {
				fieldValidError(ipRangeInput, ipRangeLabelError, "IP(s) " + invalidIp + " inválido(s)");
			}
		}

		if (StringUtils.isBlank(blockedSiteInput.getText())) {
			valid = false;
			fieldValidError(blockedSiteInput, blockedSiteLabelError,
					"Preencha um site que esteja bloqueado atualmente");
		} else {
			String url = blockedSiteInput.getText().toLowerCase();
			if (!url.startsWith("http://") || !url.startsWith("https://")) {
				url = "http://" + url;
			}
			fieldValidSuccess(blockedSiteInput, blockedSiteLabelError);
			config.setBlockedWebPage(url);
		}
		if (StringUtils.isBlank(maskInput.getText()) || !validIP(maskInput.getText())) {
			valid = false;
			fieldValidError(maskInput, maskLabelError, "Preencha com uma máscara de rede válida");
		} else {
			networkAdapter.setMask(maskInput.getText());
			fieldValidSuccess(maskInput, maskLabelError);
		}
		if (StringUtils.isBlank(gatewayInput.getText()) || !validIP(gatewayInput.getText())) {
			valid = false;
			fieldValidError(gatewayInput, gatewayLabelError, "Preencha com um gateway válido");
		} else {
			networkAdapter.setGateway(gatewayInput.getText());
			fieldValidSuccess(gatewayInput, gatewayLabelError);
		}
		if (StringUtils.isBlank(dns1Input.getText()) || !validIP(dns1Input.getText())) {
			valid = false;
			fieldValidError(dns1Input, dns1LabelError, "Preencha com um dns válido");
		} else {
			networkAdapter.setDns1(dns1Input.getText());
			fieldValidSuccess(dns1Input, dns1LabelError);
		}
		if (StringUtils.isBlank(dns2Input.getText()) || !validIP(dns2Input.getText())) {
			valid = false;
			fieldValidError(dns2Input, dns2LabelError, "Preencha com um dns válido");
		} else {
			networkAdapter.setDns2(dns2Input.getText());
			fieldValidSuccess(dns2Input, dns2LabelError);
		}
		if (proxyBox.isSelected()) {
			config.setSearchType(SearchType.BOTH);
			ProxyConfiguration proxyConfig = new ProxyConfiguration();
			if (StringUtils.isBlank(proxyIpInput.getText()) || !validIP(proxyIpInput.getText())) {
				valid = false;
				fieldValidError(proxyIpInput, proxyIpLabelError, "Preencha com o IP do proxy");
			} else {
				proxyConfig.setIp(proxyIpInput.getText());
				fieldValidSuccess(proxyIpInput, proxyIpLabelError);
			}
			if (StringUtils.isBlank(proxyPortInput.getText())) {
				valid = false;
				fieldValidError(proxyPortInput, proxyPortLabelError, "Preencha a porta de acesso para o proxy");
			} else {
				proxyConfig.setPort(Integer.parseInt(proxyPortInput.getText()));
				fieldValidSuccess(proxyPortInput, proxyPortLabelError);
			}
			config.setProxyConfiguration(proxyConfig);
		} else {
			fieldValidSuccess(proxyIpInput, proxyIpLabelError);
			fieldValidSuccess(proxyPortInput, proxyPortLabelError);
		}

		StartGUI.frame.pack();
		return valid;
	}

	private boolean validIP(String ip) {
		Pattern pattern = Pattern.compile(IPV4_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	private void changeProxyVisibility(boolean visible) {
		proxyIpLabel.setVisible(visible);
		proxyIpInput.setVisible(visible);
		proxyPortLabel.setVisible(visible);
		proxyPortInput.setVisible(visible);
		StartGUI.frame.pack(); // TODO verificar como fazer para a tela
								// redimensionar ao adicionar novos componentes
	}

	private void fieldValidError(JComponent field, JLabel label, String message) {
		field.setBorder(BorderFactory.createLineBorder(Color.RED));
		label.setVisible(true);
		label.setText(message);
	}

	private void fieldValidSuccess(JComponent field, JLabel label) {
		field.setBorder(UIManager.getBorder("TextField.border"));
		label.setVisible(false);
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
						.addComponent(dns1Label).addComponent(dns2Label).addComponent(proxyLabel)
						.addComponent(proxyIpLabel).addComponent(proxyPortLabel).addComponent(startSearch))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(areaScroll)
						.addComponent(blockedSiteInput).addComponent(maskInput).addComponent(gatewayInput)
						.addComponent(dns1Input).addComponent(dns2Input).addComponent(proxyBox)
						.addComponent(proxyIpInput).addComponent(proxyPortInput))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(ipRangeLabelError)
						.addComponent(blockedSiteLabelError).addComponent(maskLabelError)
						.addComponent(gatewayLabelError).addComponent(dns1LabelError).addComponent(dns2LabelError)
						.addComponent(proxyIpLabelError).addComponent(proxyPortLabelError)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(ipRangeLabel)
						.addComponent(areaScroll).addComponent(ipRangeLabelError))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(blockedSiteLabel)
						.addComponent(blockedSiteInput).addComponent(blockedSiteLabelError))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(maskLabel)
						.addComponent(maskInput).addComponent(maskLabelError))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(gatewayLabel)
						.addComponent(gatewayInput).addComponent(gatewayLabelError))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(dns1Label)
						.addComponent(dns1Input).addComponent(dns1LabelError))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(dns2Label)
						.addComponent(dns2Input).addComponent(dns2LabelError))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(proxyLabel)
						.addComponent(proxyBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(proxyIpLabel)
						.addComponent(proxyIpInput).addComponent(proxyIpLabelError))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(proxyPortLabel)
						.addComponent(proxyPortInput).addComponent(proxyPortLabelError))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(startSearch)));
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
