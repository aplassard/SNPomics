package org.cchmc.bmi.snpomics.annotation.interactive;

import java.util.List;

import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.translation.AminoAcid;

/**
 * Encapsulates the various effects that variants have on particular transcripts
 * @author dexzb9
 *
 */
public class TranscriptEffectAnnotation implements InteractiveAnnotation {

	public TranscriptEffectAnnotation(TranscriptAnnotation transcript) {
		tx = transcript;
		dnaName = new HgvsDnaName(tx);
		protName = new HgvsProtName(tx);
	}
	
	@Abbreviation("GENE")
	@ShortName("Gene Name")
	@Description("Name of overlapping gene(s)")
	public String getGeneName() {
		return tx.getName();
	}
	
	@Abbreviation("TX")
	@ShortName("Transcript Name")
	@Description("Name of overlapping transcript(s)")
	public String getTranscriptName() {
		return tx.getID();
	}

	@Abbreviation("PROT")
	@ShortName("Protein Name")
	@Description("Name of overlapping protein(s)")
	public String getProtName() {
		return tx.getProtID();
	}

	@Abbreviation("cdna")
	@ShortName("cDNA variation")
	@Description("HGVS nomenclature for cDNA-level changes")
	public String getHgvsCdnaName() {
		return dnaName.getName();
	}

	@Abbreviation("ProtVar")
	@ShortName("Protein change")
	@Description("HGVS nomenclature for protein-level changes")
	public String getHgvsProteinName() {
		return protName.getName();
	}
	
	public HgvsDnaName getHgvsCdnaObject() {
		return dnaName;
	}
	
	public HgvsProtName getHgvsProteinObject() {
		return protName;
	}

	public void setCdnaStartCoord(String coord) {
		dnaName.setStartCoordinate(coord);
	}
	
	public void setCdnaEndCoord(String coord) {
		dnaName.setEndCoordinate(coord);
	}
	
	public void setCdnaRefAllele(String allele) {
		dnaName.setRefAllele(allele);
	}
	
	public void setCdnaAltAllele(String allele) {
		dnaName.setAltAllele(allele);
	}
	
	public void setProtStartPos(int pos) {
		protName.setStartCoord(pos);
	}
	
	public void setProtRefAllele(List<AminoAcid> allele) {
		protName.setRef(allele);
	}
	
	public void setProtAltAllele(List<AminoAcid> allele) {
		protName.setAlt(allele);
	}
	
	public void setProtExtension(List<AminoAcid> allele) {
		protName.setExtension(allele);
	}
	
	public void setProtFrameshift(boolean isFrameshift) {
		protName.setFrameshift(isFrameshift);
	}

	private TranscriptAnnotation tx;
	private HgvsDnaName dnaName;
	private HgvsProtName protName;
}
