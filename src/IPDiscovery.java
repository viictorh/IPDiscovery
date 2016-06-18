import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPDiscovery {
	private static String ipRange;
	private static String mask;
	private static String gateway;
	private static String dns1;
	private static String dns2;

	public static void main(String[] args) throws IOException, InterruptedException {
		List<String> ipsliberados = new ArrayList<>();
		loadConfiguration();
		List<String> ranges = new ArrayList<>();
		ranges.add(ipRange);
		if (args.length > 0) {
			for (String string : args) {
				if (!string.equals(ipRange)) {
					ranges.add(string);
				}
			}
		}
		System.out.println("IP RANGE - " + ranges);
		System.out.println("Mascara - " + mask);
		System.out.println("Gateway - " + gateway);
		System.out.println("dns1 - " + dns1);
		System.out.println("dns2 - " + dns2);

		for (String ip : ranges) {
			for (int i = 1; i <= 255; i++) {
				String ip2 = ip + i;
				String[] startAsAdmin = new String[] { "cmd.exe", "/c", "netsh", "interface", "ip", "set", "address",
						"name=", "Local Area Connection", "source=static", "addr=", ip2, "mask=", mask, "gateway=",
						gateway };

				String[] command2 = { "cmd.exe", "/c", "netsh", "interface", "ip", "add", "dnsserver",
						"Local Area Connection", "address=", dns1, "index=", "1" };
				String[] command3 = { "cmd.exe", "/c", "netsh", "interface", "ip", "add", "dnsserver",
						"Local Area Connection", "address=", dns2, "index=", "2" };

				createProcess(startAsAdmin);
				createProcess(command2);
				createProcess(command3);

				TimeUnit.SECONDS.sleep(10);
				String readResult = readResult(createProcess("cmd.exe", "/c", "ping www.facebook.com"));
				if (readResult.contains("Reply from 31.13.73.36")) {
					System.out.println("ip liberado " + ip2);
					ipsliberados.add(ip2);
				} else {
					System.out.println("ip bloqueado " + ip2);
				}
			}
		}
		for (String ipliberado : ipsliberados) {
			System.out.println(ipliberado);
		}
	}

	private static void loadConfiguration() throws IOException {
		String ipConfigResult = readResult(createProcess("cmd.exe", "/c", "ipconfig /all"));
		Scanner scanner = new Scanner(ipConfigResult);
		Pattern ip = Pattern.compile("([0-9]{1,3}[\\.]){3}[0-9]{1,3}");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.toUpperCase().contains("IPV4")) {
				Matcher m = ip.matcher(line);
				if (m.find()) {
					ipRange = m.group().substring(0, m.group().lastIndexOf(".") + 1);
				}
			} else if (line.toUpperCase().contains("SUBNET MASK")) {
				Matcher m = ip.matcher(line);
				if (m.find()) {
					mask = m.group();
				}

			} else if (line.toUpperCase().contains("GATEWAY")) {
				Matcher m = ip.matcher(line);
				if (m.find()) {
					gateway = m.group();
				}

			} else if (line.toUpperCase().contains("DNS SERVERS")) {
				Matcher m = ip.matcher(line);
				if (m.find()) {
					dns1 = m.group();
				}
				if (scanner.hasNextLine()) {
					line = scanner.nextLine();
					m = ip.matcher(line);
					if (m.find()) {
						dns2 = m.group();
					}
				}
			}
		}
		scanner.close();
	}

	private static Process createProcess(String... command) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		Process p = builder.start();
		return p;
	}

	private static String readResult(Process p) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = r.readLine()) != null) {
				builder.append(line).append("\n");
			}
		} finally {
			if (r != null) {
				r.close();
			}
		}
		// System.out.println(builder);
		return builder.toString();

	}

}
