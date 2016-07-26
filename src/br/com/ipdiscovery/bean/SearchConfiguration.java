package br.com.ipdiscovery.bean;

import java.util.List;

import br.com.ipdiscovery.constant.SearchType;

public class SearchConfiguration {
	private int ipStart;
	private int ipFinish;
	private String blockedWebPage;
	private SearchType searchType;
	private ProxyConfiguration proxyConfiguration;
	private List<String> ipRange;

	public SearchConfiguration() {
		ipStart = 1;
		ipFinish = 255;
		blockedWebPage = "http://facebook.com";
		searchType = SearchType.NO_PROXY;
	}

	public int getIpStart() {
		return ipStart;
	}

	public void setIpStart(int ipStart) {
		this.ipStart = ipStart;
	}

	public int getIpFinish() {
		return ipFinish;
	}

	public void setIpFinish(int ipFinish) {
		this.ipFinish = ipFinish;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public ProxyConfiguration getProxyConfiguration() {
		return proxyConfiguration;
	}

	public void setProxyConfiguration(ProxyConfiguration proxyConfiguration) {
		this.proxyConfiguration = proxyConfiguration;
	}

	public List<String> getIpRange() {
		return ipRange;
	}

	public void setIpRange(List<String> ipRange) {
		this.ipRange = ipRange;
	}

	public String getBlockedWebPage() {
		return blockedWebPage;
	}

	public void setBlockedWebPage(String blockedWebPage) {
		if (blockedWebPage != null && !blockedWebPage.startsWith("http")) {
			blockedWebPage = "http://" + blockedWebPage;
		}
		this.blockedWebPage = blockedWebPage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchConfiguration [ipStart=");
		builder.append(ipStart);
		builder.append(", ipFinish=");
		builder.append(ipFinish);
		builder.append(", blockedWebPage=");
		builder.append(blockedWebPage);
		builder.append(", searchType=");
		builder.append(searchType);
		builder.append(", proxyConfiguration=");
		builder.append(proxyConfiguration);
		builder.append(", ipRange=");
		builder.append(ipRange);
		builder.append("]");
		return builder.toString();
	}

}
