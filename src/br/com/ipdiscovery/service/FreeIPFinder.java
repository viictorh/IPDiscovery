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
import java.net.UnknownHostException;

import br.com.ipdiscovery.bean.Execution;
import br.com.ipdiscovery.bean.NetworkAdapter;
import br.com.ipdiscovery.bean.Result;
import br.com.ipdiscovery.constant.SearchType;
import br.com.ipdiscovery.constant.Status;
import br.com.ipdiscovery.helper.PromptExecutor;

/**
 * 
 * @author victor.bello
 *
 *         Classe responsável pela execução dos comandos para trocar os valores
 *         do adaptador de rede, os IPs que serão testados e enviar os
 *         resultados das operações
 */
public class FreeIPFinder {

	private Execution execution;

	public FreeIPFinder(Execution execution) {
		this.execution = execution;
	}

	public void startSearch() throws IOException, InterruptedException {
		for (String range : execution.getConfiguration().getIpRange()) {
			for (int i = retrieveLastIpBlock(range); i <= execution.getConfiguration().getIpFinish(); i++) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				String ip = removeLastIpBlock(range) + i;
				Status status = Status.TESTING;
				SearchType proxy = execution.getConfiguration().getSearchType();
				ipTest(ip, status, proxy);
				if (changedAdapterSettings(ip)) {

					switch (execution.getConfiguration().getSearchType()) {
					case BOTH: {
						boolean proxyConnectionGranted = isConnectionGranted(true);
						boolean noProxyConnectionGranted = isConnectionGranted(false);

						status = proxyConnectionGranted || noProxyConnectionGranted ? Status.FREE : Status.BLOCKED;
						if (proxyConnectionGranted && !noProxyConnectionGranted) {
							proxy = SearchType.PROXY;
						} else if (!proxyConnectionGranted && noProxyConnectionGranted) {
							proxy = SearchType.NO_PROXY;
						}
						break;
					}
					case PROXY:
						status = isConnectionGranted(true) ? Status.FREE : Status.BLOCKED;
						break;
					case NO_PROXY:
						status = isConnectionGranted(false) ? Status.FREE : Status.BLOCKED;
						break;
					}
				} else {
					status = Status.TIMEOUT;
				}
				ipTest(ip, status, proxy);

			}
		}
	}

	private void ipTest(String ip, Status status, SearchType proxy) {
		Result r2 = new Result(ip, status, proxy);
		execution.upToDate(false, r2);
	}

	private int retrieveLastIpBlock(String ip) {
		return Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1));
	}

	private String removeLastIpBlock(String ip) {
		return ip.substring(0, ip.lastIndexOf(".") + 1);
	}

	private boolean isConnectionGranted(boolean useProxy) throws MalformedURLException {
		URL website = new URL(execution.getConfiguration().getBlockedWebPage());
		try {
			HttpURLConnection connection = null;
			if (useProxy) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(execution.getConfiguration().getProxyConfiguration().getIp(),
								execution.getConfiguration().getProxyConfiguration().getPort()));
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

	private boolean changedAdapterSettings(String ip) throws IOException, InterruptedException {
		String[] startAsAdmin = new String[] { "cmd.exe", "/c", "netsh", "interface", "ip", "set", "address", "name=",
				execution.getAdapter().getConnectionType(), "source=static", "addr=", ip, "mask=",
				execution.getAdapter().getMask(), "gateway=", execution.getAdapter().getGateway() };

		String[] command2 = { "cmd.exe", "/c", "netsh", "interface", "ip", "add", "dnsserver",
				execution.getAdapter().getConnectionType(), "address=", execution.getAdapter().getDns1(), "index=",
				"1" };
		String[] command3 = { "cmd.exe", "/c", "netsh", "interface", "ip", "add", "dnsserver",
				execution.getAdapter().getConnectionType(), "address=", execution.getAdapter().getDns2(), "index=",
				"2" };

		PromptExecutor.executeCommand(startAsAdmin);
		PromptExecutor.executeCommand(command2);
		PromptExecutor.executeCommand(command3);
		return checkIPChanged(ip);
	}

	private boolean checkIPChanged(String ip) throws UnknownHostException, IOException {
		NetworkAdapter adapter = null;
		long init = System.currentTimeMillis();
		do {
			NetworkAdapterReader reader = new NetworkAdapterReader();
			adapter = new NetworkAdapter();
			reader.findMainAdapterConfig(adapter);
			if (ip.equals(adapter.getIp())) {
				return true;
			}
		} while (((System.currentTimeMillis() - init) / 1000) < 20);
		return false;
	}

}
