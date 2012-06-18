package org.cchmc.bmi.snpomics.annotation;

import java.util.List;

import org.cchmc.bmi.snpomics.GenomicSpan;

public interface MappedAnnotationLoader<T extends MappedAnnotation> extends
		AnnotationLoader<T> {

	List<T> loadByOverlappingPosition(GenomicSpan position);
	List<T> loadByExactPosition(GenomicSpan position);
}
