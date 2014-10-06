package org.nextprot.api.rdf.service;

import java.util.List;

import org.nextprot.api.rdf.domain.SparqlParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

public interface SparqlService {

	ResponseEntity<String> sparqlProxy(@Value("body") String body, String httpRequestQuery, SparqlParameters sparqlQuery);

	ResponseEntity<String> sparqlProxyNoCache(@Value("body") String body, String httpRequestQuery, SparqlParameters sparqlQuery);

	List<String> findEntries(SparqlParameters sparqlParams);

	List<String> findEntriesNoCache(SparqlParameters sparqlParams);

}
