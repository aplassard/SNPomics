package org.cchmc.bmi.snpomics.annotation;


/**
 * A generic piece of information that describes a genomic entity (ie gene or TFBS)
 * or interaction (ie effect of mutation on protein)
 * 
 * When adding new Annotations, developers must also:
 * <ul>
 *  <li>Create a new {@link Annotator} and add it to the {@link Annotate factory}</li>
 *  <li>Create a new {@link AnnotationLoader} and add it to the appropriate {@link AnnotationFactory}
 *  (once for each desired factory)</li>
 *  <li>Create a new {@link AnnotationImporter} for add it to the appropriate {@link AnnotationFactory}
 *  (once for each desired factory)</li>
 * </ul>
 * @author dexzb9
 *
 */
public interface Annotation {

	/**
	 * The name of the annotation is a (non-unique) human-readable string
	 */
	String getName();
	
	/**
	 * The ID of an annotation must be unique, might or might not be human-readable
	 */
	String getID();
	
}
