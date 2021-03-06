package org.nextprot.api.core.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.nextprot.api.commons.constants.XrefAnnotationMapping;
import org.nextprot.api.core.dao.AnnotationDAO;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.dao.PtmDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Feature;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.IsoformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Lazy
@Service
public class AnnotationServiceImpl implements AnnotationService {

	@Autowired private AnnotationDAO annotationDAO;
	@Autowired private DbXrefDao dbXrefDAO;
	@Autowired private PtmDao ptmDao;
	@Autowired private DbXrefService xrefService;
	@Autowired private IsoformService isoService;
	

	@Override
	@Cacheable("annotations")
	public List<Annotation> findAnnotations(String entryName) {

		Preconditions.checkArgument(entryName != null, "The annotation name should be set wit #withName(...)");
		
		List<Annotation> annotations = annotationDAO.findAnnotationsByEntryName(entryName);
		if (! annotations.isEmpty()) {

			List<Long> annotationIds = Lists.transform(annotations, new AnnotationFunction());
	
			// Evidences
			List<AnnotationEvidence> evidences = annotationDAO.findAnnotationEvidencesByAnnotationIds(annotationIds);
			Multimap<Long, AnnotationEvidence> evidencesByAnnotationId = Multimaps.index(evidences, new AnnotationEvidenceFunction());
			for (Annotation annotation : annotations) {
				annotation.setEvidences(new ArrayList<AnnotationEvidence>(evidencesByAnnotationId.get(annotation.getAnnotationId())));
			}
	
			// Evidences properties
			if(!evidences.isEmpty()){
				List<Long> evidencesIds = Lists.transform(evidences, new AnnotationEvidenceIdFunction());
				List<AnnotationEvidenceProperty> evidenceProperties = annotationDAO.findAnnotationEvidencePropertiesByEvidenceIds(evidencesIds);
				Multimap<Long, AnnotationEvidenceProperty> propertiesByEvidenceId = Multimaps.index(evidenceProperties, new AnnotationEvidencePropertyFunction());
				for (AnnotationEvidence evidence : evidences) {
					evidence.setProperties(new ArrayList<AnnotationEvidenceProperty>(propertiesByEvidenceId.get(evidence.getEvidenceId())));
				}
			}
	
			// Isoforms
			List<AnnotationIsoformSpecificity> isoforms = annotationDAO.findAnnotationIsoformsByAnnotationIds(annotationIds);
			Multimap<Long, AnnotationIsoformSpecificity> isoformsByAnnotationId = Multimaps.index(isoforms, new AnnotationIsoformFunction());
	
			for (Annotation annotation : annotations) {
				annotation.setTargetingIsoforms(new ArrayList<AnnotationIsoformSpecificity>(isoformsByAnnotationId.get(annotation.getAnnotationId())));
			}
	
			// Properties
			List<AnnotationProperty> properties = annotationDAO.findAnnotationPropertiesByAnnotationIds(annotationIds);
			Multimap<Long, AnnotationProperty> propertiesByAnnotationId = Multimaps.index(properties, new AnnotationPropertyFunction());
	
			for (Annotation annotation : annotations) {
				annotation.setProperties(new ArrayList<AnnotationProperty>(propertiesByAnnotationId.get(annotation.getAnnotationId())));
			}
	
	
			//Removes annotations which do not map to any isoform, this may happen in case where the annotation has been seen in a peptide and the annotation was propagated to the master, but we were not able to map to any isoform
			Iterator<Annotation> annotationsIt = annotations.iterator();
			while(annotationsIt.hasNext()){
				Annotation a = annotationsIt.next();
				if(a.getTargetingIsoformsMap().size() == 0){
					annotationsIt.remove();
				}
			}
	
			for (Annotation annot : annotations) {
				refactorDescription(annot);
			}
		}
		
		annotations.addAll(this.getXrefsLikeAnnotations(entryName));

		return annotations;

	}
	
	public List<Annotation> getXrefsLikeAnnotations(String entryName) {

	    // build annotations from xrefs
		List<Isoform> isoforms = this.isoService.findIsoformsByEntryName(entryName);
		List<DbXref> xrefs = this.xrefService.findDbXrefsAsAnnotByEntry(entryName);
		List<Annotation> annotations = this.annotationDAO.createAdditionalAnnotationsFromXrefs(xrefs, entryName);
		// build evidences annotations and link them to annotations
		for (Annotation annotation : annotations) {
			List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
			AnnotationEvidence evidence = new AnnotationEvidence();
			evidence.setAnnotationId(annotation.getAnnotationId());
			DbXref pxref = annotation.getParentXref();
			XrefAnnotationMapping xam = XrefAnnotationMapping.getByDatabaseName(pxref.getDatabaseName());
			evidence.setEvidenceId(annotation.getAnnotationId() + 20000000000L);
			evidence.setAssignedBy(xam.getSrcName());
			evidence.setResourceId(pxref.getDbXrefId());
			evidence.setResourceAccession(pxref.getAccession());
			evidence.setResourceDb(pxref.getDatabaseName());
			evidence.setResourceAssociationType("evidence");
			evidence.setResourceType("database");
			evidence.setNegativeEvidence(false);
			evidence.setExperimentalContextId(null);
			evidence.setResourceDescription(null);
			evidence.setPublicationMD5(null);
			evidence.setProperties(new ArrayList<AnnotationEvidenceProperty>());
			evidence.setQualifierType(xam.getQualifierType());    
			evidence.setQualityQualifier(xam.getQualityQualifier());   
			evidence.setAssignmentMethod(xam.getAssignmentMethod()); 
			evidences.add(evidence);
			annotation.setEvidences(evidences);
		}
		
		// build isoform specificity from isoforms and annotations and link them to annotations
		for (Annotation annotation : annotations) {
			List<AnnotationIsoformSpecificity> isospecs = new ArrayList<AnnotationIsoformSpecificity>();
			for (Isoform iso: isoforms) {
				AnnotationIsoformSpecificity isospec = new AnnotationIsoformSpecificity();
				isospec.setAnnotationId(annotation.getAnnotationId());
				isospec.setFirstPosition(0);
				isospec.setLastPosition(0);
				isospec.setIsoformName(iso.getUniqueName());
				isospec.setSpecificity("UNKNOWN");
				isospecs.add(isospec);
			}
			annotation.setTargetingIsoforms(isospecs);
			
		}

		return annotations;
	}

	
	
	@Override
	public List<Feature> findPtmsByMaster(String uniqueName) {
		return this.ptmDao.findPtmsByEntry(uniqueName);
	}

	@Override
	public List<Feature> findPtmsByIsoform(String isoformUniqueName) {
		String masterUniqueName = extractMasterUniqueName(isoformUniqueName);
		List<Feature> ptms = this.ptmDao.findPtmsByEntry(masterUniqueName);
		return filterByIsoform(isoformUniqueName, ptms);
	}
	
	
	// Function class to filter using guava ///////////////////////////////////////////////////////////////////////////////

	private class AnnotationPropertyFunction implements Function<AnnotationProperty, Long> {
		public Long apply(AnnotationProperty annotation) {
			return annotation.getAnnotationId();
		}
	}

	private class AnnotationIsoformFunction implements Function<AnnotationIsoformSpecificity, Long> {
		public Long apply(AnnotationIsoformSpecificity annotation) {
			return annotation.getAnnotationId();
		}
	}

	private class AnnotationEvidenceFunction implements Function<AnnotationEvidence, Long> {
		public Long apply(AnnotationEvidence annotation) {
			return annotation.getAnnotationId();
		}
	}

	private class AnnotationEvidencePropertyFunction implements Function<AnnotationEvidenceProperty, Long> {
		public Long apply(AnnotationEvidenceProperty property) {
			return property.getEvidenceId();
		}
	}

	private class AnnotationFunction implements Function<Annotation, Long> {
		public Long apply(Annotation annotation) {
			return annotation.getAnnotationId();
		}
	}

	private class AnnotationEvidenceIdFunction implements Function<AnnotationEvidence, Long> {
		public Long apply(AnnotationEvidence evidence) {
			return evidence.getEvidenceId();
		}
	}
	
	// Refactor the descriptions  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static void refactorDescription(Annotation annotation) {

		String category = annotation.getCategory();

		setDefaultDescription(annotation);

		if (category.equals("sequence caution")) {
			setSequenceCautionDescription(annotation);
		} else if (category.equals("go molecular function") || category.equals("go cellular component") || category.equals("go biological process")) {
			setGODescription(annotation);
		} else if (category.equals("sequence conflict") || category.equals("sequence variant") || category.equals("mutagenesis site")) {
			setVariantDescription(annotation);
		} 
		
	}
	
	private static void setDefaultDescription(Annotation annotation) {

		if (annotation.getDescription() == null || annotation.getDescription().indexOf(":") == 1) {
			annotation.setDescription(annotation.getCvTermName());
		}
	}

	private static void setSequenceCautionDescription(Annotation annotation) {

		SortedSet<String> acs = new TreeSet<String>();

		// GET all evidences from that annotation
		// If the resource type of the evidence is from DATABASE then add its xref emblAcs.add
		for (AnnotationEvidence evidence : annotation.getEvidences()) {
			if (evidence.getResourceAssociationType().equals("evidence") && evidence.getResourceType().equals("database")) {
				acs.add(evidence.getResourceAccession());
			}
		}

		StringBuilder sb = new StringBuilder("The sequence").append((acs.size() > 1 ? "s" : ""));
		for (String emblAc : acs) {
			sb.append(" ").append(emblAc);
		}
		sb.append(" differ").append((acs.size() == 1 ? "s" : "")).append(" from that shown.");

		// Beginning of the sentence finish, then:
		List<AnnotationProperty> conflictTypeProps = new ArrayList<AnnotationProperty>();
		for (AnnotationProperty ap : annotation.getProperties()) {
			if (ap.getName().equals("conflict type"))
				conflictTypeProps.add(ap);
		}

		SortedSet<AnnotationProperty> sortedPositions = getSortedPositions(annotation);
		if (conflictTypeProps != null && !conflictTypeProps.isEmpty()) {
			sb.append(" Reason:");
			for (AnnotationProperty prop : conflictTypeProps) {
				sb.append(" ").append(prop.getValue());
			}
			if (!sortedPositions.isEmpty()) {
				sb.append(" at position").append((sortedPositions.size() > 1 ? "s" : ""));
				for (AnnotationProperty p : sortedPositions) {
					sb.append(" ").append(p.getValue());
				}
			}
			sb.append(".");
		}
		if (StringUtils.isNotEmpty(annotation.getDescription())) {
			sb.append(" ").append(annotation.getDescription());
		}

		annotation.setDescription(sb.toString());
	}

	private static SortedSet<AnnotationProperty> getSortedPositions(Annotation annotation) {
		SortedSet<AnnotationProperty> sortedPositions = new TreeSet<AnnotationProperty>(new Comparator<AnnotationProperty>() {
			public int compare(AnnotationProperty p1, AnnotationProperty p2) {
				return Integer.valueOf(p1.getValue()).compareTo(Integer.valueOf(p2.getValue()));
			}
		});

		List<AnnotationProperty> conflictPositions = new ArrayList<AnnotationProperty>();
		for (AnnotationProperty ap : annotation.getProperties()) {
			if (ap.getName().equals("position"))
				conflictPositions.add(ap);
		}

		if (conflictPositions != null && !conflictPositions.isEmpty()) {
			sortedPositions.addAll(conflictPositions);
		}
		return sortedPositions;
	}



	private static void setVariantDescription(Annotation annotation) {

		if (annotation.getVariant() != null) {

			if (annotation.getVariant().getVariant().equals("")) {
				String description = annotation.getDescription();
				annotation.setDescription("Missing " + description);
			}
		}

	}

	private static void setGODescription(Annotation annotation) {
		
		//Example if the cv term is : nuclear proteasome complex and there is a GO term of go_qualifier, the description changes to:
		//Then the description Colocalizes with nuclear proteasome complex

		for (AnnotationEvidence evidence : annotation.getEvidences()) {
			if (evidence.getResourceAssociationType().equals("evidence")) {
				String goqualifier=evidence.getGoQualifier();
				if (goqualifier != null) if (!goqualifier.isEmpty()) {
					String description = StringUtils.capitalize(goqualifier.replaceAll("_", " ") + " ") + annotation.getDescription();
					annotation.setDescription(description);
					break;
				}


			}
		}

	}

	
	private List<Feature> filterByIsoform(String isoformUniqueName, List<Feature> annotations) {
		List<Feature> filteredFeatures = new ArrayList<Feature>();
		for (Feature f : annotations) {
			if (f.getIsoformAccession().equalsIgnoreCase(isoformUniqueName)) {
				filteredFeatures.add(f);
			}
		}
		return filteredFeatures;
	}
	
	private String extractMasterUniqueName(String isoformUniqueName) {
		if (isoformUniqueName.indexOf("-") < 1) {
			throw new InvalidParameterException(String.format(
					"Invalid isoform accession [%s]", isoformUniqueName));
		}
		return isoformUniqueName.substring(0, isoformUniqueName.indexOf("-"));
	}
}
