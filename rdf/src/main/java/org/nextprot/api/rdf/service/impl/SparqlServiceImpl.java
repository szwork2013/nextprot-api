package org.nextprot.api.rdf.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.domain.SparqlParameters;
import org.nextprot.api.rdf.service.SparqlService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

@Service
public class SparqlServiceImpl implements SparqlService {

	private static final String ENTRY_SUFFIX_URI = "http://nextprot.org/rdf/entry/";

	private String timeout;
	private String sparqlEndpointUrl;

	@Override
	@Cacheable("sparql-proxy")
	public ResponseEntity<String> sparqlProxy(String body, String httpRequestQuery, SparqlParameters sparqlParams) {
		return sparqlProxyNoCache(body, httpRequestQuery, sparqlParams);
	}

	@Override
	public ResponseEntity<String> sparqlProxyNoCache(String body, String httpRequestQuery, SparqlParameters sparqlParams) {

		ResponseEntity<String> responseEntity = null;

		String url = this.sparqlEndpointUrl + ((sparqlParams.getSparql() != null) ? ("?" + sparqlParams.getSparql()) : "");

		RestTemplate template = new RestTemplate();
		try {

			responseEntity = template.exchange(new URI(url), HttpMethod.GET, new HttpEntity<String>(body), String.class);
			return responseEntity;

		} catch (RestClientException e) {
			throw new NextProtException(e);
		} catch (URISyntaxException e) {
			throw new NextProtException(e);
		}
	}

	@Override
	@Cacheable("sparql")
	public List<String> findEntries(SparqlParameters sparqlParams) {
		return findEntriesNoCache(sparqlParams);
	}

	@Override
	public List<String> findEntriesNoCache(SparqlParameters sparqlParams) {

		List<String> results = new ArrayList<String>();
		QueryExecution qExec = null;

		try {
			qExec = QueryExecutionFactory.sparqlService(sparqlEndpointUrl, sparqlParams.getSparql());
		} catch (QueryParseException qe) {
			throw new NextProtException("Malformed SPARQL: " + qe.getLocalizedMessage());
		}

		ResultSet rs = qExec.execSelect();

		/**
		 * This give an empty graph.... Model m = rs.getResourceModel(); Graph g
		 * = m.getGraph(); System.err.println("The graph is" + g);
		 */

		Var x = Var.alloc("entry");
		while (rs.hasNext()) {
			Binding b = rs.nextBinding();
			Node entryNode = b.get(x);
			if (entryNode == null) {
				qExec.close();
				throw new NextProtException("Bind your protein result to a variable called ?entry. Example: \"?entry :classifiedWith term:KW-0813.\"");
			} else if (entryNode.toString().indexOf(ENTRY_SUFFIX_URI) == -1) {
				qExec.close();
				throw new NextProtException("Any entry found in the output, however was found: " + entryNode.toString());
			}

			String entry = entryNode.toString().replace(ENTRY_SUFFIX_URI, "").trim();
			results.add(entry);
		}
		qExec.close();
		return results;

	}

	@Value("${sparql.timeout}")
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getTimeout() {
		return timeout;
	}

	public String getUrl() {
		return sparqlEndpointUrl;
	}

	@Value("${sparql.url}")
	public void setUrl(String url) {
		this.sparqlEndpointUrl = url;
	}

}
