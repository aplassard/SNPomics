package org.cchmc.bmi.snpomics.annotation;

import org.cchmc.bmi.snpomics.GenomicSpan;

public interface MappedAnnotation extends Annotation {

	public GenomicSpan getPosition();
	public boolean isOnForwardStrand();
	
}
