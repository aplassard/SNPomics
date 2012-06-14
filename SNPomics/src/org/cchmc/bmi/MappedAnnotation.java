package org.cchmc.bmi;

public interface MappedAnnotation extends Annotation {

	public GenomicSpan getPosition();
	public boolean isOnForwardStrand();
	
}
