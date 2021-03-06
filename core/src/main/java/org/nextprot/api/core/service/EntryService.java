package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface EntryService {

	/**
	 * Retrieve the Entry by name
	 * @param entryName
	 * @return
	 */
	Entry findEntry(@ValidEntry String entryName);
	
	/**
	 * 
	 * @param entryNames
	 * @return
	 */
	List<Entry> findEntries(List<String> entryNames);
	
	/**
	 * Retrieve the list of entries in a specific chromossome
	 * @param chromossome
	 * @return
	 */
	List<Entry> findEntriesByChromossome(String chromossome);
	
	List<String> findEntryNamesByChromossome(String chromossome);
	
}
