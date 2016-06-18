package br.com.ipdiscovery.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

public class MachineFinder {
	public static void main(String[] args) throws MalformedURLException, IOException {
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.16.1.253", 8080));
//		Authenticator authenticator = new Authenticator() {
//
//			public PasswordAuthentication getPasswordAuthentication() {
//				return (new PasswordAuthentication("victor.bello@voxage.com.br", "victor123".toCharArray()));
//			}
//		};
//		Authenticator.setDefault(authenticator);
		
		HttpURLConnection connection = (HttpURLConnection) new URL("http://www.google.com.br").openConnection(proxy);
		System.out.println(connection.getResponseCode());
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder(); // or StringBuffer if not
														// Java 5+
		String line;
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		System.out.println(response.toString());
	}
}
