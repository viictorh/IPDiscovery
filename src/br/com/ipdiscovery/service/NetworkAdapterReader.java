package br.com.ipdiscovery.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;

import br.com.ipdiscovery.bean.NetworkAdapter;
import br.com.ipdiscovery.helper.PromptExecutor;

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

	private void findSubnetMaskByIp(NetworkAdapter networkAdapter) throws SocketException, UnknownHostException {
		InetAddress localHost = Inet4Address.getByName(networkAdapter.getIp());
		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);

		networkAdapter.setConnectionType(networkInterface.getName());
		for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
			InetAddress inetAddr = address.getAddress();

			if (!(inetAddr instanceof Inet4Address)) {
				continue;
			}
			networkAdapter.setMask(generateMaskFromPrefix(address.getNetworkPrefixLength()));
		}
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

	public static void main(String[] args) throws IOException {

		InetAddress localHost = Inet4Address.getByName("172.16.0.199");
		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);

		for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
			System.out.println(address.getNetworkPrefixLength());
		}

		for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
				.hasMoreElements();) {
			NetworkInterface cur = interfaces.nextElement();
			System.out.println("===========================");
			System.out.println(cur.getName());
			System.out.println(cur.getDisplayName());
			System.out.println(cur.getHardwareAddress());
			Enumeration<NetworkInterface> subInterfaces = cur.getSubInterfaces();
			while (subInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface2 = (NetworkInterface) subInterfaces.nextElement();
				System.out.println(networkInterface2.getName());
				System.out.println(networkInterface2.getDisplayName());
			}
			Enumeration<InetAddress> inetAddresses = cur.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
				System.out.println(inetAddress.getHostAddress());
				System.out.println(inetAddress.getCanonicalHostName());
				System.out.println(inetAddress.getHostName());
			}
			System.out.println("===========================");
			if (cur.isLoopback()) {
				continue;
			}

			System.out.println("interface " + cur.getName());

			for (InterfaceAddress addr : cur.getInterfaceAddresses()) {
				InetAddress inet_addr = addr.getAddress();

				if (!(inet_addr instanceof Inet4Address)) {
					continue;
				}

				System.out.println("  address: " + inet_addr.getHostAddress() + "/" + addr.getNetworkPrefixLength());

				System.out.println("  broadcast address: " + addr.getBroadcast().getHostAddress());
			}
		}

		System.out.println("============================================");
		System.out.println("============================================");
		System.out.println("============================================");
		int netPrefix = -1;
		try {
			// Since this is for IPv4, it's 32 bits, so set the sign value of
			// the int to "negative"...
			int shiftby = (1 << 31);
			// For the number of bits of the prefix -1 (we already set the sign
			// bit)
			for (int i = netPrefix - 1; i > 0; i--) {
				// Shift the sign right... Java makes the sign bit sticky on a
				// shift...
				// So no need to "set it back up"...
				shiftby = (shiftby >> 1);
			}
			// Transform the resulting value in xxx.xxx.xxx.xxx format, like if
			/// it was a standard address...
			String maskString = Integer.toString((shiftby >> 24) & 255) + "." + Integer.toString((shiftby >> 16) & 255)
					+ "." + Integer.toString((shiftby >> 8) & 255) + "." + Integer.toString(shiftby & 255);
			// Return the address thus created...
			System.out.println("MASK" + maskString);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
