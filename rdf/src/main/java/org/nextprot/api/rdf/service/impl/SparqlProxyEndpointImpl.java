package org.nextprot.api.rdf.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlProxyEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SparqlProxyEndpointImpl implements SparqlProxyEndpoint {

	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@Override
	public ResponseEntity<String> sparql(String body, String queryString) {

		ResponseEntity<String> responseEntity = null;

		String url = sparqlEndpoint.getUrl() + ((queryString != null) ? ("?" + queryString) : "");
		// uri = new URI("http", null, "kant", 8890, request.getRequestURI(), (qs == null) ? qs : URLDecoder.decode(qs, "UTF-8"), null);

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

}
