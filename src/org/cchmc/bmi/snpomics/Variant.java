package org.cchmc.bmi.snpomics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cchmc.bmi.snpomics.annotation.interactive.InteractiveAnnotation;

public class Variant {
	/*
	 * The storage of annotations is complex, because there are so many possibilities.  Unfortunately,
	 * it's going to be up to the VariantWriters to sort all this out.
	 * 
	 * Annotations are first separated by type, then by which alternate allele they specify.
	 * Finally, a given alt allele (or SimpleVariant) can have multiple annotations (for instance,
	 * in the case of alternative transcripts), so store those as a List
	 */

	private GenomicSpan position;
	private String ref;
	private List<String> alt;
	private Map<Class<? extends InteractiveAnnotation>, List<List<? extends InteractiveAnnotation>>> annot;
	
	public Variant() {
		annot = new HashMap<Class<? extends InteractiveAnnotation>, List<List<? extends InteractiveAnnotation>>>();
	}
	
	public GenomicSpan getPosition() {
		return position;
	}
	public void setPosition(GenomicSpan position) {
		this.position = position.clone();
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public List<String> getAlt() {
		return alt;
	}
	public void setAlt(List<String> alt) {
		this.alt = alt;
	}
	public List<? extends InteractiveAnnotation> getAnnot(Class<? extends InteractiveAnnotation> cls, int altAllele) {
		if (!annot.containsKey(cls))
			return Collections.emptyList();
		return annot.get(cls).get(altAllele);
	}
	public void addAnnotation(Class<? extends InteractiveAnnotation> cls, List<? extends InteractiveAnnotation> newAnnot, int altAllele) {
		if (!annot.containsKey(cls)) {
			annot.put(cls, new ArrayList<List<? extends InteractiveAnnotation>>());
		}
		annot.get(cls).add(altAllele, newAnnot);
	}
	public void addAnnotation(List<? extends InteractiveAnnotation> newAnnot, int altAllele) {
		if (newAnnot.size() == 0)
			throw new IllegalArgumentException("addAnnotation called with no explicit class and an empty list");
		addAnnotation(newAnnot.get(0).getClass(), newAnnot, altAllele);
	}
	
	public SimpleVariant getSimpleVariant(int altAllele) {
		return new SimpleVariant(getPosition(), getRef(), getAlt().get(altAllele));
	}
}
