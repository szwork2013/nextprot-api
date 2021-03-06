package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "peptide", description = "The peptide mapping")
public class PeptideMapping implements Serializable{

	private static final long serialVersionUID = 7304469815021872304L;

	@ApiObjectField(description = "The peptide unique name")
	private String peptideUniqueName;

	@ApiObjectField(description = "The peptide evidences")
	private List<PeptideEvidence> evidences;
	
	@ApiObjectField(description = "The peptide isoform specificity")
	private Map<String, IsoformSpecificity> isoformSpecificity;
	
	public PeptideMapping() {
		this.isoformSpecificity = new HashMap<String, IsoformSpecificity>();
	}
	
	public String getPeptideUniqueName() {
		return peptideUniqueName;
	}

	public void setPeptideUniqueName(String peptideUniqueName) {
		this.peptideUniqueName = peptideUniqueName;
	}
	
	public void addEvidence(PeptideEvidence evidence) {
		if(this.evidences == null)
			this.evidences = new ArrayList<PeptideMapping.PeptideEvidence>();
		this.evidences.add(evidence);
	}
	
	public List<PeptideEvidence> getEvidences() {
		return this.evidences;
	}
	
	public Map<String, IsoformSpecificity> getIsoformSpecificity() {
		return this.isoformSpecificity;
	}

	public void setIsoformSpecificity(
			Map<String, IsoformSpecificity> isoformSpecificity) {
		this.isoformSpecificity = isoformSpecificity;
	}

	
	public void addIsoformSpecificityOld(IsoformSpecificity newIsoformSpecificity) {
		IsoformSpecificity iso = null;
		
		if(this.isoformSpecificity.containsKey(newIsoformSpecificity.getIsoformName())) { // add position
			iso = this.isoformSpecificity.get(newIsoformSpecificity.getIsoformName());
			iso.addPosition(newIsoformSpecificity.getPositions().get(0));
			this.isoformSpecificity.put(newIsoformSpecificity.getIsoformName(), iso);
		} else this.isoformSpecificity.put(newIsoformSpecificity.getIsoformName(), newIsoformSpecificity);
	}
	
	
	public void addIsoformSpecificity(IsoformSpecificity newIsoformSpecificity) {
		String isoName = newIsoformSpecificity.getIsoformName();
		if(this.isoformSpecificity.containsKey(isoName)) { // add position
			IsoformSpecificity isospec = this.isoformSpecificity.get(isoName);
			isospec.addPosition(newIsoformSpecificity.getPositions().get(0));
		} else {
			this.isoformSpecificity.put(isoName, newIsoformSpecificity);
		}
	}
	
	
	/**
	 * 
	 * @param isoformName a nextprot isoform unique name (starting with NX_)
	 * @return true if the mapping applies to the isoform otherwise false
	 */
	public boolean isSpecificForIsoform(String isoformName) {
		return this.isoformSpecificity.containsKey(isoformName);
	}
	
	

	public static class PeptideEvidence implements Serializable{

		private static final long serialVersionUID = -6416415250105609274L;
		private String peptideName;
		private String accession;
		private String databaseName;
		private String assignedBy;
		
		public String getPeptideName() {
			return peptideName;
		}
		public void setPeptideName(String peptideName) {
			this.peptideName = peptideName;
		}
		public String getAccession() {
			return accession;
		}
		public void setAccession(String accession) {
			this.accession = accession;
		}
		public String getDatabaseName() {
			return databaseName;
		}
		public void setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
		}
		public String getAssignedBy() {
			return assignedBy;
		}
		public void setAssignedBy(String assignedBy) {
			this.assignedBy = assignedBy;
		}
	}
}
