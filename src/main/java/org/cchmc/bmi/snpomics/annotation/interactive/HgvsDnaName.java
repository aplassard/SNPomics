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
		prefix = (tx == null) ? "" : (tx.isProteinCoding() ? "c" : "n");
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
	
	public boolean affectsSplicing() {
		return damagesSpliceDonor() || damagesSpliceAcceptor();
	}

	public boolean damagesSpliceDonor() {
		Matcher startM = splicingPattern.matcher(startCoord);
		if (endCoord == null || endCoord.equals(startCoord)) {
			//SNV - must hit one of +[12]
			return startM.matches() && isDonorSite(startM.group(3));
		} else if (ref.length() > alt.length()) {
			//deletion - must span the donor site
			//however, it should only span one exon/intron junction.  For now,
			//we'll pretend that deleting an entire intron (or exon) has predictable results
			
			Matcher endM = splicingPattern.matcher(endCoord);
			//In order for a deletion to affect the donor site, the end must lie in an intron
			if (endM.matches())
				//There are now three ways to affect the donor site:
				// 1) The deletion starts in an exon and thus spans the splice site
				// 2) The deletion starts in the donor site
				// 3) The deletion ends in the donor site
				return !startM.matches() || 
						isDonorSite(startM.group(3)) || 
						isDonorSite(endM.group(3));
		} else {
			//insertion - must lie between +1 and +2
			return startM.matches() && startM.group(3).equals("+1");
		}
		return false;
	}
	
	public boolean damagesSpliceAcceptor() {
		Matcher startM = splicingPattern.matcher(startCoord);
		//In order to hit an acceptor, the start of the variation must be in an intron
		if (startM.matches()) {
			if (endCoord == null || endCoord.equals(startCoord)) {
				//SNV - must hit one of -[12]
				return isAcceptorSite(startM.group(3));
			} else if (ref.length() > alt.length()) {
				//deletion - must span the acceptor sites
				//however, it should only span one exon/intron junction.  For now,
				//we'll pretend that deleting an entire intron (or exon) has predictable results
				
				Matcher endM = splicingPattern.matcher(endCoord);
				//There are three ways to affect the acceptor site:
				// 1) The deletion ends in an exon and thus spans the splice site
				// 2) The deletion starts in the acceptor site
				// 3) The deletion ends in the acceptor site
				return !endM.matches() || 
						isAcceptorSite(startM.group(3)) || 
						isAcceptorSite(endM.group(3));
			} else {
				//insertion - must lie between -2 and -1
				return startM.group(3).equals("-2");
			}
		}
		return false;
	}

	private boolean isDonorSite(String intronOffset) {
		return intronOffset.equals("+1") || intronOffset.equals("+2");
	}

	private boolean isAcceptorSite(String intronOffset) {
		return intronOffset.equals("-1") || intronOffset.equals("-2");
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
	
	public String getStartCoord() {
		return startCoord;
	}

	public String getEndCoord() {
		return endCoord;
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
		if (tx == null) {
			name = "";
			return;
		}
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
		coordinatePattern = Pattern.compile("^([-*]?)([0-9]+)([-+][ud]?[0-9]+)?$");
		splicingPattern = Pattern.compile("^([-*]?)([0-9]+)([-+][0-9]+)$");
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
	private static Pattern splicingPattern;
}
