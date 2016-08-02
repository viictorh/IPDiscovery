package br.com.ipdiscovery.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Scanner;

import br.com.ipdiscovery.bean.NetworkAdapter;
import br.com.ipdiscovery.helper.PromptExecutor;

/**
 * 
 * @author victor.bello
 *
 *         Classe responsável pela leitura dos valores da placa de rede que o
 *         usuário está utilizando
 */
public class NetworkAdapterReader {

	private int metric;
	private static final byte NETWORK = 0;
	private static final byte GATEWAY = 2;
	private static final byte IP = 3;
	private static final byte METRIC = 4;

	public NetworkAdapter loadNetworkAdapterConfiguration() throws IOException {
		NetworkAdapter networkAdapter = new NetworkAdapter();
		findMainAdapterConfig(networkAdapter);
		findSubnetMaskByIp(networkAdapter);

		return networkAdapter;
	}

	private void findSubnetMaskByIp(NetworkAdapter networkAdapter) throws IOException {
		InetAddress localHost = Inet4Address.getByName(networkAdapter.getIp());
		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);

		networkAdapter.setConnectionType(retrieveConnectionName(networkInterface.getIndex()));
		for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
			InetAddress inetAddr = address.getAddress();

			if (!(inetAddr instanceof Inet4Address)) {
				continue;
			}
			networkAdapter.setMask(generateMaskFromPrefix(address.getNetworkPrefixLength()));
		}
	}

	private String retrieveConnectionName(int index) throws IOException {
		final int interfaceIndex = 0;
		final int interfaceName = 4;
		String[] command = new String[] { "cmd.exe", "/c", "netsh", "interface", "ipv4", "show", "interfaces" };
		String result = PromptExecutor.executeCommandAndReadResult(command);
		try (Scanner scanner = new Scanner(result)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] split = line.split("\\s{2,}");
				if (split != null && split[interfaceIndex].trim().equals(String.valueOf(index))) {
					return split[interfaceName];
				}
			}
		}
		return null;
	}

	private String generateMaskFromPrefix(short s) {
		String maskBit = "";
		String mask = null;
		for (int i = 0; i < 32; i++) {
			if (i < s) {
				maskBit += "1";
			} else {
				maskBit += "0";
			}
		}
		if (maskBit.contains("1")) {
			short index = 0;
			mask = "";
			String dot = "";
			while (index < maskBit.length()) {
				mask += dot + Integer.parseInt(maskBit.substring(index, Math.min(index + 8, maskBit.length())), 2);
				index += 8;
				dot = ".";
			}
		}

		return mask;
	}

	public void findMainAdapterConfig(NetworkAdapter networkAdapter) throws IOException {
		String[] command = new String[] { "cmd.exe", "/c", "netstat", "-r", "-n" };
		String result = PromptExecutor.executeCommandAndReadResult(command);
		try (Scanner scanner = new Scanner(result)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.toUpperCase().startsWith("DEFAULT") || line.trim().startsWith("0.0.0.0")) {
					String[] columns = line.trim().split("\\s+");
					if (columns != null && columns.length == 5 && columns[NETWORK].equals("0.0.0.0")) {
						String metric = columns[METRIC];
						try (Scanner checkInt = new Scanner(metric)) {
							if (checkInt.hasNextInt()) {
								int number = Integer.parseInt(metric);
								if (this.metric == 0 || number < this.metric) {
									this.metric = number;
									networkAdapter.setGateway(columns[GATEWAY]);
									networkAdapter.setIp(columns[IP]);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void main(String args[]) throws SocketException {
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets))
			displayInterfaceInformation(netint);
	}

	static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
		if (netint.getIndex() > 1 && netint.getMTU() > 0) {
			System.out.printf("Display name: %s\n", netint.getDisplayName());
			System.out.printf("Name: %s\n", netint.getName());
			System.out.println(netint.getIndex());
			System.out.println(netint.getMTU());
			Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
			for (InetAddress inetAddress : Collections.list(inetAddresses)) {
				System.out.printf("InetAddress: %s\n", inetAddress);
			}
			System.out.printf("\n");
		}
	}
}
