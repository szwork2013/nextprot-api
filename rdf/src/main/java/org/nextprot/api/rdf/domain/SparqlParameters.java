package org.nextprot.api.rdf.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.KeyValueRepresentation;

public class SparqlParameters implements Serializable, KeyValueRepresentation {

	private static final long serialVersionUID = -4731073176544576359L;

	private String sparqlEndpoint;

	public String getSparqlEndpoint() {
		return sparqlEndpoint;
	}

	public void setSparqlEndpoint(String sparqlEndpoint) {
		this.sparqlEndpoint = sparqlEndpoint;
	}

	private String queryTitle;
	private String queryTags;

	// If the sparql originates from a user query it will be filled along with
	// the sparql
	private long userQueryId;

	private String testId;

	private String format;
	private long timeout;
	private String sparql;

	public String getSparql() {
		return sparql;
	}

	public void setSparql(String sparql) {
		this.sparql = sparql;
	}

	public String getQueryTitle() {
		return queryTitle;
	}

	public void setQueryTitle(String queryTitle) {
		this.queryTitle = queryTitle;
	}

	public String getQueryTags() {
		return queryTags;
	}

	public void setQueryTags(String queryTags) {
		this.queryTags = queryTags;
	}

	public long getUserQueryId() {
		return userQueryId;
	}

	public void setUserQueryId(long userQueryId) {
		this.userQueryId = userQueryId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public long getTimeout() {
		return timeout;
	}

	void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toKeyValueString() {

		StringBuilder sb = new StringBuilder();

		if (sparql != null) {
			sb.append("sparql=");
			sb.append(sparql);
			sb.append(";");
		}

		long sparqlHash = this.sparql.trim().replaceAll("\n ", "").hashCode();
		sb.append("sparqlHash=");
		sb.append(sparqlHash);
		sb.append(";");

		if (queryTitle != null) {
			sb.append("queryTitle=");
			sb.append(queryTitle);
			sb.append(";");
		}

		return sb.toString();

	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getEquivalentHttpQueryRequest() {
		StringBuilder sb = new StringBuilder();
		sb.append("query=");
		sb.append(sparql);
		sb.append(";");
		
		return URLEncoder.encode(sb.toString());
		//return sb.toString().replaceAll(" ", "%3Fa").replaceAll("\n", "%0D%0A");
	}

}
