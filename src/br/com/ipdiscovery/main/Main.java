package br.com.ipdiscovery.main;

import java.io.IOException;
import java.util.Arrays;

import javax.swing.JOptionPane;

import br.com.ipdiscovery.bean.NetworkAdapter;
import br.com.ipdiscovery.bean.ProxyConfiguration;
import br.com.ipdiscovery.bean.SearchConfiguration;
import br.com.ipdiscovery.constant.SearchType;
import br.com.ipdiscovery.service.FreeIPFinder;
import br.com.ipdiscovery.service.NetworkAdapterReader;
import br.com.ipdiscovery.view.StartGUI;

public class Main {
	public static void main(String[] args) {
		try {
			executeWithoutUI();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startUI() {
		StartGUI.main(null);
		
		
		try {
			NetworkAdapter networkAdapter = new NetworkAdapterReader().loadNetworkAdapterConfiguration();
			System.out.println(networkAdapter);
			if (networkAdapter.getMask() == null) {
				networkAdapter.setMask(
						JOptionPane.showInputDialog("Insira o valor da mascara de rede (formato: 000.000.000.000): "));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void executeWithoutUI() throws IOException {
		NetworkAdapter networkAdapter = new NetworkAdapterReader().loadNetworkAdapterConfiguration();
		System.out.println(networkAdapter);
		if (networkAdapter.getMask() == null) {
			// networkAdapter.setMask(JOptionPane.showInputDialog(null,
			// "Insira o valor da mascara de rede (formato: 000.000.000.000):
			// "));
			networkAdapter.setMask("255.255.254.0");
		}

		SearchConfiguration config = new SearchConfiguration();
		config.setBlockedWebPage("https://www.facebook.com");
		config.setSearchType(SearchType.BOTH);
		config.setIpRange(Arrays.asList("172.16.0.", "172.16.1."));
		ProxyConfiguration proxyConfig = new ProxyConfiguration();
		proxyConfig.setIp("172.16.1.253");
		proxyConfig.setPort(8080);
		config.setProxyConfiguration(proxyConfig);
//		FreeIPFinder ipfinder = new FreeIPFinder(config, networkAdapter);
//		try {
//			ipfinder.startSearch();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

	}

}
