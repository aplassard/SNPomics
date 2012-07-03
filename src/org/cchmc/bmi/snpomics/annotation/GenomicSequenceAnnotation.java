package org.cchmc.bmi.snpomics.annotation;

import org.cchmc.bmi.snpomics.GenomicSpan;

public class GenomicSequenceAnnotation implements MappedAnnotation {

	public GenomicSequenceAnnotation(GenomicSpan span, String sequence) {
		this.span = span;
		this.sequence = sequence;
	}
	
	@Override
	public String getName() {
		return span.toString();
	}

	@Override
	public String getID() {
		return span.toString();
	}

	@Override
	public GenomicSpan getPosition() {
		return span;
	}

	@Override
	public boolean isOnForwardStrand() {
		return true;
	}
	
	public String getSequence() {
		return sequence;
	}
	
	private final GenomicSpan span;
	private final String sequence;
}
