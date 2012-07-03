package org.cchmc.bmi.snpomics.annotation.interactive;

import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;

/**
 * A description of the variant relative to a gene, following HGVS nomenclature (ie c.76A>C or c.76_77insT).
 * The description is built up from the component parts, which must be separately supplied.  
 * @author dexzb9
 *
 */
public class HgvsDnaName implements InteractiveAnnotation {

	public HgvsDnaName(TranscriptAnnotation tx) {
		this.tx = tx;
		name = null;
		prefix = "c";
		endCoord = null;
		reference = null;
		startCoord = null;
		ref = null;
		alt = null;
	}
	
	@Abbreviation("cdna")
	@ShortName("cDNA variation")
	@Description("HGVS nomenclature for cDNA-level changes")
	public String getName() {
		if (name == null)
			buildName();
		return name;
	}
	
	@Abbreviation("GENE")
	@ShortName("Gene Name")
	@Description("Name of overlapping gene(s)")
	public String getGeneName() {
		return tx.getName();
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public void setProteinCoding(boolean isCoding) {
		prefix = isCoding ? "c" : "n";
	}
	
	public void setStartCoordinate(String coord) {
		startCoord = coord;
	}
	
	public void setEndCoordinate(String coord) {
		endCoord = coord;
	}
	
	public void setRefAllele(String allele) {
		ref = allele;
	}
	
	public void setAltAllele(String allele) {
		alt = allele;
	}

	private void buildName() {
		StringBuilder sb = new StringBuilder();
		if (reference != null)
			sb.append(reference+":");
		sb.append(prefix+".");
		sb.append(startCoord);
		if (endCoord != null && !endCoord.equals(startCoord))
			sb.append("_"+endCoord);
		if (ref == null) ref = "";
		if (alt == null) alt = "";
		if (ref.length() == 1 && alt.length() == 1) {
			sb.append(ref);
			sb.append(">");
			sb.append(alt);
		} else {
			//Can't have an elseif here - complex indels are both
			//eg: c.112_117delAGGTCAinsTG
			if (ref.length() > 0) {
				sb.append("del");
				if (ref.length() < 10)
					sb.append(ref);
				
			}
			if (alt.length() > 0) {
				sb.append("ins");
				if (alt.length() > 20)
					sb.append(alt.length());
				else
					sb.append(alt);
			}
		}
		name = sb.toString();
	}
	
	private TranscriptAnnotation tx;
	private String name;
	private String reference;
	private String prefix;
	private String startCoord;
	private String endCoord;
	private String ref;
	private String alt;
}
