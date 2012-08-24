package org.cchmc.bmi.snpomics.annotation.interactive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;

/**
 * A description of the variant relative to a gene, following HGVS nomenclature (ie c.76A>C or c.76_77insT).
 * The description is built up from the component parts, which must be separately supplied.  
 * @author dexzb9
 *
 */
public class HgvsDnaName {

	public HgvsDnaName(TranscriptAnnotation tx) {
		this.tx = tx;
		name = null;
		prefix = tx.isProteinCoding() ? "c" : "n";
		endCoord = null;
		startCoord = null;
		ref = null;
		alt = null;
	}
	
	public String getName() {
		if (name == null)
			buildName();
		return name;
	}
	
	public boolean isCoding() {
		return tx.isProteinCoding() && 
			(isCoordinateCoding(startCoord) || (endCoord != null && isCoordinateCoding(endCoord)));
	}
	
	public int getNearestCodingNtToStart() {
		return getNearestCodingNt(startCoord);
	}
	
	public int getNearestCodingNtToEnd() {
		if (endCoord == null)
			return getNearestCodingNtToStart();
		else
			return getNearestCodingNt(endCoord);
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
		sb.append(tx.getID()+":");
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
	
	private boolean isCoordinateCoding(String coord) {
		return codingPattern.matcher(coord).matches();
	}
	
	private int getNearestCodingNt(String coord) {
		Matcher m = coordinatePattern.matcher(coord);
		if (!m.matches())
			throw new SnpomicsException("Coordinate \""+coord+"\" does not match expected pattern");
		String utrSpec = m.group(1);
		//If not in a UTR, then the nearest nt is already spec'd in the coordinate
		if (utrSpec.isEmpty())
			return Integer.parseInt(m.group(2));
		else if (utrSpec.equals("-"))
			return 1;
		else
			return tx.getCdsLength();
	}
	
	static {
		codingPattern = Pattern.compile("^[0-9]+$");
		coordinatePattern = Pattern.compile("^([-*]?)([0-9]+)([-+][0-9]+)?$");
	}
	
	private TranscriptAnnotation tx;
	private String name;
	private String prefix;
	private String startCoord;
	private String endCoord;
	private String ref;
	private String alt;
	private static Pattern codingPattern;
	private static Pattern coordinatePattern;
}
