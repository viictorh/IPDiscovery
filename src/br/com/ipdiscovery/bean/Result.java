package br.com.ipdiscovery.bean;

import br.com.ipdiscovery.constant.SearchType;
import br.com.ipdiscovery.constant.Status;

public class Result {
	private String ip;
	private Status status;
	private SearchType searchType;

	public Result(String ip, Status status, SearchType searchType) {
		this.ip = ip;
		this.status = status;
		this.searchType = searchType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Result [ip=");
		builder.append(ip);
		builder.append(", status=");
		builder.append(status);
		builder.append(", searchType=");
		builder.append(searchType);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Result other = (Result) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		return true;
	}

}
