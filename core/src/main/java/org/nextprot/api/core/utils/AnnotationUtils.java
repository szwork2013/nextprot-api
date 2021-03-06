package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;

public class AnnotationUtils {
	
	/**
	 * Filter annotation by its category
	 * @param annotations
	 * @param annotationCategory
	 * @return
	 */
	public static List<Annotation> filterAnnotationsByCategory(List<Annotation> annotations, AnnotationApiModel annotationCategory){
		List<Annotation> annotationList = new ArrayList<Annotation>(); 
		for(Annotation a : annotations){
			if(a.getAPICategory() != null && (a.getAPICategory().equals(annotationCategory) || a.getAPICategory().isChildOf(annotationCategory))){
				annotationList.add(a);
			}
		}
		return annotationList;
	}
	

	public static Set<Long> getXrefIdsForAnnotations(List<Annotation> annotations){

		Set<Long> xrefIds = new HashSet<Long>(); 
		for(Annotation a : annotations){
			for(AnnotationEvidence e : a.getEvidences()){
				if(e.isResourceAXref()){
					xrefIds.add(e.getResourceId());
				}
			}
		}
		return xrefIds;
	}


	public static Set<Long> getPublicationIdsForAnnotations(List<Annotation> annotations) {

		Set<Long> publicationIds = new HashSet<Long>(); 
		for(Annotation a : annotations){
			for(AnnotationEvidence e : a.getEvidences()){
				if(e.isResourceAPublication()){
					publicationIds.add(e.getResourceId());
				}
			}
		}
		return publicationIds;
	
	}

}
