package org.nextprot.api.service;

import java.util.List;

import org.nextprot.rdf.domain.Terminology;


public interface TerminologyService {
	
	/**
	 * Gets terminology by accession
	 * @param id
	 * @return
	 */
	public Terminology findTerminologyByAccession(String accession);
	
	/**
	 * Gets terms by title case insensitive
	 * @param title
	 * @return
	 */
	public List<Terminology> findTerminologByTitle(String title);
	
	/**
	 * Retrieves terms by name
	 * @param name
	 * @return
	 */
	public List<Terminology> findTerminologyByName(String name);	

	/**
	 * Retrieves terms by ontology
	 * @param the name of ontology
	 * @return
	 */
	public List<Terminology> findTerminologyByOntology(String ontology);	
	
	
	/**
	 * Retrieves terms sorted by ontology
	 * @return
	 */
	public List<Terminology> findAllTerminology();	

	
}
