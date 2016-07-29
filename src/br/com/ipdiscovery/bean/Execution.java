package br.com.ipdiscovery.bean;

import java.util.LinkedHashSet;
import java.util.Set;

import br.com.ipdiscovery.constant.Status;

/**
 * 
 * @author victor.bello
 *
 *         Classe responsável por centralizar a execução da busca por IPs
 *         livres. Nela estão os resultados e quais configurações foram
 *         escolhidas, bem como os dados da placa de rede que está sendo
 *         utilizada
 */
public class Execution {
	private SearchConfiguration configuration;
	private NetworkAdapter adapter;
	private Set<Result> changedResults;
	private int maxProgressValue = 1;
	private int currentProgressValue;

	private boolean upToDate;

	public Execution(SearchConfiguration configuration, NetworkAdapter adapter) {
		this.configuration = configuration;
		this.adapter = adapter;
		this.changedResults = new LinkedHashSet<>();
		countProgressMax();
	}

	private void countProgressMax() {
		for (String range : configuration.getIpRange()) {
			maxProgressValue += configuration.getIpFinish()
					- Integer.parseInt(range.substring(range.lastIndexOf(".") + 1));
		}

	}

	public SearchConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SearchConfiguration configuration) {
		this.configuration = configuration;
	}

	public NetworkAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(NetworkAdapter adapter) {
		this.adapter = adapter;
	}

	public int getMaxProgressValue() {
		return maxProgressValue;
	}

	public void setMaxProgressValue(int maxProgressValue) {
		this.maxProgressValue = maxProgressValue;
	}

	public int getCurrentProgressValue() {
		return currentProgressValue;
	}

	public void setCurrentProgressValue(int currentProgressValue) {
		this.currentProgressValue = currentProgressValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Execution [configuration=");
		builder.append(configuration);
		builder.append(", adapter=");
		builder.append(adapter);
		builder.append(", results=");
		builder.append("]");
		return builder.toString();
	}

	public boolean isUpToDate() {
		return upToDate;
	}

	public synchronized void upToDate(boolean upToDate, Result result) {
		this.upToDate = upToDate;
		if (upToDate) {
			changedResults.remove(result);
			if (!result.getStatus().equals(Status.TESTING)) {
				currentProgressValue++;
				System.out.println(" ====-----==== ");
				System.out.println("Current value: " + currentProgressValue + " :" + result);
				System.out.println(" ====-----==== ");
			}
		} else {
			changedResults.add(result);
		}

	}

	public Set<Result> getChangedResults() {
		return new LinkedHashSet<>(changedResults);
	}
}
