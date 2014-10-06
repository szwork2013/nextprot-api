package org.nextprot.api.rdf.controller;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsondoc.core.annotation.Api;
import org.nextprot.api.rdf.domain.SparqlParameters;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller used many to log and tune queries. Check the log times. No cache
 * is used on purpose
 * 
 * @author dteixeira
 */
@Lazy
@Controller
@PreAuthorize("hasRole('USER')")
@Api(name = "Sparql", description = "Sparql endpoint where SPARQL queries are available", role = "ROLE_USER")
public class SparqlController {

	@Autowired
	private SparqlService sparqlService;

	@Autowired
	private SparqlDictionary sparqlDictionary;

	@Autowired
	private UserQueryService userQueryService;

	@RequestMapping("/sparql")
	@ResponseBody
	public ResponseEntity<String> sparql(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {

		SparqlParameters sqp = new SparqlParameters();

		// If it contains the sparql element, it corresponds to the query
		if (request.getParameterValues("sparql") != null) {
			sqp.setSparql(request.getParameterValues("sparql")[0]);
		}

		if (request.getParameterValues("sparqlTitle") != null) {
			sqp.setQueryTitle(request.getParameterValues("sparqlTitle")[0]);
		}

		if (request.getParameterValues("sparqlEndpoint") != null) {
			sqp.setSparqlEndpoint(request.getParameterValues("sparqlEndpoint")[0]);
		}

		if (request.getParameterValues("testId") != null) {
			sqp.setQueryTitle(request.getParameterValues("testId")[0]);
		}

		if (request.getParameterValues("user_query_id") != null) {
			UserQuery uq = this.userQueryService.getUserQueryById(Long.valueOf(request.getParameterValues("user_query_id")[0]));
			sqp.setSparql(uq.getSparql());
			sqp.setUserQueryId(uq.getUserQueryId());
			sqp.setQueryTitle(uq.getTitle());
		}

		if (request.getParameterValues("include_prefixes") != null) {
			sqp.setSparql(sparqlDictionary.getSparqlPrefixes() + sqp.getSparql());
		}

		if (request.getParameterValues("cache_enabled") != null) {
			if (request.getParameterValues("cache_enabled")[0].equalsIgnoreCase(("false"))) {
				return this.sparqlService.sparqlProxy(body, request.getQueryString(), sqp);
			}
		}

		return this.sparqlService.sparqlProxy(body, request.getQueryString(), sqp);

	}

}
