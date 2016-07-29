import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author victor.bello
 *
 *         Classe de teste
 */
public class MachineNameDiscovery {
	public static void main(String[] args) throws IOException, InterruptedException {
		String ipAddress = "172.16.1.";
		String ipAddresses[] = { "172.16.0.18 ", "172.16.0.122", "172.16.0.133", "172.16.0.136", "172.16.0.137",
				"172.16.0.138", "172.16.0.154", "172.16.0.173", "172.16.0.190", "172.16.0.199", "172.16.0.252",
				"172.16.1.5  ", "172.16.1.18 ", "172.16.1.43 ", "172.16.1.59 ", "172.16.1.92 ", "172.16.1.118",
				"172.16.1.119", "172.16.1.127", "172.16.1.131", "172.16.1.141", "172.16.1.169", "172.16.1.177",
				"172.16.1.197", "172.16.1.201", "172.16.1.249" };
		for (String ip : ipAddresses) {
			// for (int i = 0; i < 255; i++) {
			// String ip = ipAddress + i;
			InetAddress inetAddress = InetAddress.getByName(ip);
			System.out.println("------------------------------------------");
			System.out.println("IP: " + ip);
			System.out.println("Host Name: " + inetAddress.getHostName());
			String retrieveMacAddress = retrieveMacAddress(false, ip);
			System.out.println("MacAddress: " + retrieveMacAddress);
		}
	}

	private static String retrieveMacAddress(boolean local, String ipAddress) throws IOException, InterruptedException {
		if (local) {
			NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			byte[] hardwareAddress = network.getHardwareAddress();

			if (hardwareAddress == null) {
				return null;
			}

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < hardwareAddress.length; i++) {
				sb.append(String.format("%02X%s", hardwareAddress[i], (i < hardwareAddress.length - 1) ? "-" : ""));
			}

			return sb.toString();

		} else {
			Pattern macpt = null;
			// Find OS and set command according to OS
			String OS = System.getProperty("os.name").toLowerCase();

			String[] cmd;
			if (OS.contains("win")) {
				// Windows
				macpt = Pattern.compile("[0-9a-f]+-[0-9a-f]+-[0-9a-f]+-[0-9a-f]+-[0-9a-f]+-[0-9a-f]+");
				String[] a = { "arp", "-a", ipAddress };
				cmd = a;
			} else {
				// Mac OS X, Linux
				macpt = Pattern.compile("[0-9a-f]+:[0-9a-f]+:[0-9a-f]+:[0-9a-f]+:[0-9a-f]+:[0-9a-f]+");
				String[] a = { "arp", ipAddress };
				cmd = a;
			}

			// Run command
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			// read output with BufferedReader
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();

			// Loop trough lines
			while (line != null) {
				Matcher m = macpt.matcher(line);

				// when Matcher finds a Line then return it as result
				if (m.find()) {
					return m.group(0);
				}

				line = reader.readLine();
			}

		}
		// Return empty string if no MAC is found
		return "";
	}

}
