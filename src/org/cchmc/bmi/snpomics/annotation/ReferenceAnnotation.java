package org.cchmc.bmi.snpomics.annotation;

/**
 * <p>A ReferenceAnnotation is an annotation that exists independent of any Variants - ie,
 * it is (or could be) a track at UCSC.  Most (all?) ReferenceAnnotations should actually
 * be {@link MappedAnnotation MappedAnnotations} in order to capture the genomic locus</p>
 * <p>When creating a new ReferenceAnnotation, you must also create a {@link AnnotationImporter}
 * and {@link AnnotationLoader} for each {@link AnnotationFactory factory} you wish to support, and
 * modify the getImporter/getLoader methods in the factory as appropriate</p>
 * @author dexzb9
 *
 */
public interface ReferenceAnnotation {

	/**
	 * The name of the annotation is a (non-unique) human-readable string
	 */
	String getName();
	
	/**
	 * The ID of an annotation must be unique, might or might not be human-readable
	 */
	String getID();
	

}
