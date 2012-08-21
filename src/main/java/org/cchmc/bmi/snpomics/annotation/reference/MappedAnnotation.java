package org.cchmc.bmi.snpomics.annotation.reference;

import org.cchmc.bmi.snpomics.GenomicSpan;

/**
 * A MappedAnnotation is any {@link Annotation} that has a unique genomic position
 * (chromosome, start, stop, strand)
 * @author dexzb9
 *
 */
public interface MappedAnnotation extends ReferenceAnnotation {

	/**
	 * Where is it?
	 * @return the GenomicSpan describing the position of this Annotation
	 */
	GenomicSpan getPosition();
	
	/**
	 * Which strand is it on?
	 * @return true if this annotation lies on the forward strand (or is not strand-specific)
	 */
	boolean isOnForwardStrand();
	
}
