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

	public void setID(String id) {
		this.id = id;
	}

	public String getProtID() {
		return protID;
	}

	public void setProtID(String protID) {
		this.protID = protID;
	}
	
	public String getSplicedSequence() {
		return txSequence;
	}
	
	public void setSplicedSequence(String txSequence) {
		this.txSequence = txSequence;
	}
	
	public String getCodingSequence() {
		if (isProteinCoding() && !txSequence.isEmpty())
			return txSequence.substring(get5UtrLength(), get5UtrLength()+getCdsLength());
		return "";
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
		return getPosition().overlaps(span);
	}
	public boolean exonOverlaps(GenomicSpan span) {
		for (GenomicSpan x : exons) {
			if (x.overlaps(span))
				return true;
		}
		return false;
	}
	public boolean contains(GenomicSpan span) {
		return getPosition().contains(span);
	}
	public boolean exonContains(GenomicSpan span) {
		for (GenomicSpan x : exons)
			if (x.contains(span))
				return true;
		return false;
	}
	public boolean isCoding(GenomicSpan region) {
		return cds.overlaps(region) && exonOverlaps(region);
	}
	public int length() {
		if (length > 0) return length;
		length = 0;
		for (GenomicSpan x : exons)
			length += x.length();
		return length;
	}
	public int get5UtrLength() {
		if (!areUTRsCalculated) calculateUTRs();
		return utr5Length;
	}
	public int get3UtrLength() {
		if (!areUTRsCalculated) calculateUTRs();
		return utr3Length;
	}
	public int getCdsLength() {
		if (!areUTRsCalculated) calculateUTRs();
		return length() - utr5Length - utr3Length;
	}
	private void calculateUTRs() {
		if (areUTRsCalculated) return;
		utr5Length = utr3Length = 0;
		areUTRsCalculated = true;
		//No UTR if the whole thing is untranslated
		if (!isProteinCoding()) return;
		for (GenomicSpan x : exons) {
			//For each UTR, either add the entire exon or just the untranslated portion
			if (x.getEnd() < cds.getStart())
				utr5Length += x.length();
			else if (x.getStart() < cds.getStart())
				utr5Length += cds.getStart() - x.getStart();
			
			if (x.getStart() > cds.getEnd())
				utr3Length += x.length();
			else if (x.getEnd() > cds.getEnd())
				utr3Length += x.getEnd() - cds.getEnd();
		}
		if (!isOnForwardStrand()) {
			int temp = utr5Length;
			utr5Length = utr3Length;
			utr3Length = temp;
		}
	}

	private String id; //transcript
	private String name; //gene
	private String protID; //protein
	private String txSequence = "";
	private GenomicSpan pos;
	private GenomicSpan cds;
	private boolean onForwardStrand;
	private List<GenomicSpan> exons;
	private int utr5Length, utr3Length;
	private int length = 0;
	private boolean areUTRsCalculated = false;
}
