package org.nextprot.api.solr;


public class AutocompleteConfiguration extends IndexConfiguration {
	
	private final char STAR = '*';
	private final String RANDOM_STRING = "dfjhgdfjh";
	
	public AutocompleteConfiguration(String name) {
		super(name);
	}
	
	public AutocompleteConfiguration(String name, IndexConfiguration parentConfiguration) {
		super(name, parentConfiguration);
	}
	
	/**
	 * It is different from the simple build query since
	 * it adds a star to the end of the last incomplete token
	 * if that's the case
	 * 
	 * ex. kin*, kinase
	 * 
	 * @param query
	 * @return
	 */
	@Override
	public String buildQuery(Query query) {
		String initialQuery = query.getQueryString();
		String queryString = super.buildQuery(query);
		
		// finishes with space
		if(initialQuery.charAt(initialQuery.length()-1) == WHITESPACE.charAt(0)) {
			this.otherParameters.put("facet.prefix", RANDOM_STRING);
		} else {
			String[] tokens = query.getQueryString().split(WHITESPACE);
			String lastToken = tokens[tokens.length-1].toLowerCase();
			char lastCharOfLastToken = lastToken.charAt(lastToken.length()-1);
			String prefix = "";
			
			if(lastCharOfLastToken != STAR) {
				queryString += STAR;
				prefix = lastToken;
			} else {
				prefix = lastToken.substring(0, lastToken.length()-1);
				
			}
				
			this.otherParameters.put("facet.prefix", prefix);
			
		}
		return queryString;
	}
	
}
