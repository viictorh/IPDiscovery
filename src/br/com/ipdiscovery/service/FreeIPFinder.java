package br.com.ipdiscovery.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import br.com.ipdiscovery.bean.NetworkAdapter;
import br.com.ipdiscovery.bean.SearchConfiguration;
import br.com.ipdiscovery.bean.SearchType;
import br.com.ipdiscovery.helper.PromptExecutor;

public class FreeIPFinder {

	private SearchConfiguration configuration;
	private NetworkAdapter adapter;

	public FreeIPFinder(SearchConfiguration configuration, NetworkAdapter adapter) {
		this.configuration = configuration;
		this.adapter = adapter;
	}

	public void startSearch() throws IOException, InterruptedException {
		for (String range : configuration.getIpRange()) {
			for (int i = configuration.getIpStart(); i <= configuration.getIpFinish(); i++) {
				String ip = range + i;
				changeAdapterSettings(ip);

				switch (configuration.getSearchType()) {
				case BOTH:
					if (isConnectionGranted(true)) {
						ipWithFullAccess(ip, SearchType.PROXY);
					}
					if (isConnectionGranted(false)) {
						ipWithFullAccess(ip, SearchType.NO_PROXY);
					}
					break;
				case PROXY:
					if (isConnectionGranted(true)) {
						ipWithFullAccess(ip, SearchType.PROXY);
					}
					break;
				case NO_PROXY:
					if (isConnectionGranted(false)) {
						ipWithFullAccess(ip, SearchType.NO_PROXY);
					}
					break;
				}

			}
		}
	}

	private void ipWithFullAccess(String ip, SearchType proxy) {
		System.out.println("IP LIBERADO:" + ip + " search type:" + proxy);

	}

	private boolean isConnectionGranted(boolean useProxy) throws MalformedURLException {
		URL website = new URL(configuration.getBlockedWebPage());
		try {
			HttpURLConnection connection = null;
			if (useProxy) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(configuration.getProxyConfiguration().getIp(),
								configuration.getProxyConfiguration().getPort()));
				connection = (HttpURLConnection) website.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) website.openConnection();
			}
			String pageContent = readInputStreamToString(connection);
			if (pageContent == null || pageContent.isEmpty()) {
				return false;
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private String readInputStreamToString(HttpURLConnection connection) {
		String result = null;
		StringBuffer sb = new StringBuffer();
		InputStream is = null;

		try {
			is = new BufferedInputStream(connection.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			result = sb.toString();
		} catch (Exception e) {
			result = null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}

	private void changeAdapterSettings(String ip) throws IOException, InterruptedException {
		String[] startAsAdmin = new String[] { "cmd.exe", "/c", "netsh", "interface", "ip", "set", "address", "name=",
				adapter.getConnectionType(), "source=static", "addr=", ip, "mask=", adapter.getMask(), "gateway=",
				adapter.getGateway() };

		String[] command2 = { "cmd.exe", "/c", "netsh", "interface", "ip", "add", "dnsserver",
				adapter.getConnectionType(), "address=", adapter.getDns1(), "index=", "1" };
		String[] command3 = { "cmd.exe", "/c", "netsh", "interface", "ip", "add", "dnsserver",
				adapter.getConnectionType(), "address=", adapter.getDns2(), "index=", "2" };

		PromptExecutor.executeCommand(startAsAdmin);
		PromptExecutor.executeCommand(command2);
		PromptExecutor.executeCommand(command3);
		TimeUnit.SECONDS.sleep(10);
	}

}
