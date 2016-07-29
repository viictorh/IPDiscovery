package br.com.ipdiscovery.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @author victor.bello
 *
 *	Classe responsável pela execução de comandos no Prompt de comando (CMD - DOS)
 */
public class PromptExecutor {

	public static String executeCommandAndReadResult(String... command) throws IOException {
		Process p = executeCommand(command);
		StringBuilder builder = new StringBuilder();

		try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
			String line;
			while ((line = r.readLine()) != null) {
				builder.append(line).append("\n");
			}
		}
		return builder.toString();
	}

	public static Process executeCommand(String... command) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		return builder.start();

	}

}
