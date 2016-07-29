package br.com.ipdiscovery.bean;

/**
 * 
 * @author victor.bello
 *
 *	Configuração de proxy, caso necessário, que será utilizado 
 */
public class ProxyConfiguration {

	private String ip;
	private int port;
	private String user;
	private String pass;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProxyConfiguration [ip=");
		builder.append(ip);
		builder.append(", port=");
		builder.append(port);
		builder.append(", user=");
		builder.append(user);
		builder.append(", pass=");
		builder.append(pass);
		builder.append("]");
		return builder.toString();
	}

}
