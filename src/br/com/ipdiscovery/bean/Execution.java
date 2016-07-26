package br.com.ipdiscovery.bean;

import java.util.ArrayList;
import java.util.List;

public class Execution {
	private SearchConfiguration configuration;
	private NetworkAdapter adapter;
	private List<Result> results;

	public Execution(SearchConfiguration configuration, NetworkAdapter adapter) {
		this.configuration = configuration;
		this.adapter = adapter;
		this.results = new ArrayList<>();
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

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Execution [configuration=");
		builder.append(configuration);
		builder.append(", adapter=");
		builder.append(adapter);
		builder.append(", results=");
		builder.append(results);
		builder.append("]");
		return builder.toString();
	}

}
