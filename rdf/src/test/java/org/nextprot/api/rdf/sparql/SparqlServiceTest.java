package org.nextprot.api.rdf.sparql;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.rdf.domain.SparqlParameters;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("unit")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/rdf-context.xml")
public class SparqlServiceTest {

	@Autowired
	private SparqlDictionary sparqlDictionary;

	@Autowired
	private SparqlService sparqlService;

	@Test
	public void testSparqlService() {

		SparqlParameters sparqlParams = new SparqlParameters();
		sparqlParams.setSparql(sparqlDictionary.getSparqlOnly("abc-test-query"));
		sparqlParams.setQueryTitle("title1");

		String httpRequest = sparqlParams.getEquivalentHttpQueryRequest();
		System.out.println(httpRequest);
		//"query=select+%3Fa+%3Fb+%3Fc+%0D%0Awhere+%7B%3Fa+%3Fb+%3Fc%7D+%0D%0ALIMIT+10"
		ResponseEntity<String> entity = sparqlService.sparqlProxy("", sparqlParams.getEquivalentHttpQueryRequest(), sparqlParams);
		System.out.println(entity.getBody());
		
	}

	@Test
	public void testEntries() {

		SparqlParameters sparqlParams = new SparqlParameters();
		sparqlParams.setSparql(sparqlDictionary.getSparqlWithPrefixes("get-10-entries"));

		List<String> entries = sparqlService.findEntries(sparqlParams);
		assertTrue(entries.size() == 10);

	}

}
