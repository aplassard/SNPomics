package org.cchmc.bmi.snpomics.annotation.reference;

import java.util.List;

import org.cchmc.bmi.snpomics.GenomicSpan;

@AnnotationType("genes")
public class TranscriptAnnotation implements MappedAnnotation {

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public GenomicSpan getPosition() {
		return pos;
	}

	@Override
	public boolean isOnForwardStrand() {
		return onForwardStrand;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProtID() {
		return protID;
	}

	public void setProtID(String protID) {
		this.protID = protID;
	}

	public GenomicSpan getPos() {
		return pos;
	}

	public void setPosition(GenomicSpan pos) {
		this.pos = pos.clone();
	}

	public GenomicSpan getCds() {
		return cds;
	}

	public void setCds(GenomicSpan cds) {
		this.cds = cds.clone();
	}

	public List<GenomicSpan> getExons() {
		return exons;
	}

	public void setExons(List<GenomicSpan> exons) {
		this.exons = exons;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOnForwardStrand(boolean onForwardStrand) {
		this.onForwardStrand = onForwardStrand;
	}

	public boolean isProteinCoding() {
		return cds.length() > 0;
	}
	public boolean overlaps(GenomicSpan span) {
		return getPos().overlaps(span);
	}
	public boolean exonOverlaps(GenomicSpan span) {
		for (GenomicSpan x : exons) {
			if (x.overlaps(span))
				return true;
		}
		return false;
	}
	public boolean contains(GenomicSpan span) {
		return getPos().contains(span);
	}
	public boolean exonContains(GenomicSpan span) {
		for (GenomicSpan x : exons)
			if (x.contains(span))
				return true;
		return false;
	}
	public boolean isCoding(GenomicSpan region) {
		return this.cds.overlaps(region) && exonOverlaps(region);
	}

	private String id; //transcript
	private String name; //gene
	private String protID; //protein
	private GenomicSpan pos;
	private GenomicSpan cds;
	private boolean onForwardStrand;
	private List<GenomicSpan> exons;
}
