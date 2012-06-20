package org.cchmc.bmi.snpomics;

import java.util.List;

import org.cchmc.bmi.snpomics.annotation.Annotation;

public class Variant {

	private GenomicSpan position;
	private String ref;
	private List<String> alt;
	private List<Annotation> annot;
	
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
	public List<Annotation> getAnnot() {
		return annot;
	}
	public void setAnnot(List<Annotation> annot) {
		this.annot = annot;
	}
	
	public SimpleVariant getSimpleVariant(int altAllele) {
		return new SimpleVariant(getPosition(), getRef(), getAlt().get(altAllele));
	}
}
