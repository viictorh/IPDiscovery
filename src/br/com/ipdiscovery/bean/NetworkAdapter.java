package br.com.ipdiscovery.bean;

/**
 * 
 * @author victor.bello
 *
 *         Classe com os valores da placa de rede identificada
 */
public class NetworkAdapter {
	private String ip;
	private String mask;
	private String gateway;
	private String dns1;
	private String dns2;
	private String connectionType;

	public NetworkAdapter() {
		dns1 = "8.8.8.8";
		dns2 = "8.8.4.4";
		connectionType = "Local Area Connection";
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getDns1() {
		return dns1;
	}

	public void setDns1(String dns1) {
		this.dns1 = dns1;
	}

	public String getDns2() {
		return dns2;
	}

	public void setDns2(String dns2) {
		this.dns2 = dns2;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public String getConnectionType() {
		return connectionType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NetworkAdapter [ip=");
		builder.append(ip);
		builder.append(", mask=");
		builder.append(mask);
		builder.append(", gateway=");
		builder.append(gateway);
		builder.append(", dns1=");
		builder.append(dns1);
		builder.append(", dns2=");
		builder.append(dns2);
		builder.append(", connectionType=");
		builder.append(connectionType);
		builder.append("]");
		return builder.toString();
	}

}
