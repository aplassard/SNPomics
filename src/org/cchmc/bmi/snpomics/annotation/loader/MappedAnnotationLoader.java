package org.cchmc.bmi.snpomics.annotation.loader;

import java.util.List;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.annotation.MappedAnnotation;

/**
 * An augmented {@link AnnotationLoader} that can retrieve MappedAnnotations
 * based on their position
 * @author dexzb9
 */
public interface MappedAnnotationLoader<T extends MappedAnnotation> extends
		AnnotationLoader<T> {

	/**
	 * Get the list of Annotations that overlap the provided position
	 */
	List<T> loadByOverlappingPosition(GenomicSpan position);

	/**
	 * Get the Annotation(s) that occupy this precise location
	 */
	List<T> loadByExactPosition(GenomicSpan position);
}
