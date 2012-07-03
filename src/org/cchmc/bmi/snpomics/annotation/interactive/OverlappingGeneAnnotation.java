package org.cchmc.bmi.snpomics.annotation.interactive;

import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;

public class OverlappingGeneAnnotation implements InteractiveAnnotation {

	public OverlappingGeneAnnotation(TranscriptAnnotation tx) {
		this.tx = tx;
	}
	
	@Abbreviation("GENE")
	@ShortName("Gene Name")
	@Description("Name of overlapping gene(s)")
	public String getGeneName() {
		return tx.getName();
	}
	
	final private TranscriptAnnotation tx;
}
