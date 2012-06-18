package org.cchmc.bmi.snpomics;

import java.util.List;

import org.cchmc.bmi.snpomics.annotation.Annotation;

public class Variant {

	private GenomicSpan position;
	private String ref;
	private List<String> alt;
	private List<Annotation> annot;
}
