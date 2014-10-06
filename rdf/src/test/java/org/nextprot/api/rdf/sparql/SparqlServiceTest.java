package org.nextprot.api.rdf.sparql;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.rdf.domain.SparqlParameters;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("unit")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/rdf-context.xml")
public class SparqlServiceTest {

	String sSuperLite = "?entry :isoform/:expression/:in ?s." + "?s :subPartOf term:TS-1030;rdfs:label ?name.";

	String sLite = "SELECT  ?entry { " + "?entry :isoform/:expression/:in ?s." + "?s :childOf term:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	String s = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX : <http://nextprot.org/rdf#>" + "PREFIX term: <http://nextprot.org/rdf/terminology/>" + "SELECT  ?entry { "
			+ "?entry :isoform/:expression/:in ?s." + "?s rdfs:subClassOf term:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	String c = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX : <http://nextprot.org/rdf#>" + "PREFIX term: <http://nextprot.org/rdf/terminology/>"
			+ "SELECT (count(distinct ?entry) as ?c) { " + "?entry :isoform/:expression/:in ?s." + "?s rdfs:subClassOf term:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	@Autowired
	private SparqlService sparqlService;

	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@Test
	public void testEntries() {

		SparqlParameters sparqlParams = new SparqlParameters();
		sparqlParams.setSparql(sLite);
		sparqlParams.setQueryTitle("title1");

		List<String> entries = sparqlService.findEntries(sparqlParams);
		for (String s : entries) {
			System.out.println(s);
		}

	}

	@Test
	public void testNoCacheEntries() {

		SparqlParameters sparqlParams = new SparqlParameters();
		sparqlParams.setSparql(sLite);
		sparqlParams.setQueryTitle("titleNoCache");
		sparqlParams.setTestId("testId" + System.currentTimeMillis());

		List<String> entries = sparqlService.findEntriesNoCache(sparqlParams);
		for (String s : entries) {
			System.out.println(s);
		}
	}

}
