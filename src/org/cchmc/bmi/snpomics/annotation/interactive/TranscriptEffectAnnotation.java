package org.cchmc.bmi.snpomics.annotation.interactive;

import java.util.List;

import org.cchmc.bmi.snpomics.annotation.reference.GenomicSequenceAnnotation;
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
	
	@MetaAnnotation(name="Gene", description="Name of overlapping gene(s)",
			ref={TranscriptAnnotation.class, GenomicSequenceAnnotation.class})
	public String getGeneName() {
		return tx.getName();
	}
	
	@MetaAnnotation(name="Transcript", description="Name of overlapping transcript(s)",
			ref={TranscriptAnnotation.class, GenomicSequenceAnnotation.class})
	public String getTranscriptName() {
		return tx.getID();
	}

	@MetaAnnotation(name="Protein", description="Name of overlapping protein(s)",
			ref={TranscriptAnnotation.class, GenomicSequenceAnnotation.class})
	public String getProtName() {
		return tx.getProtID();
	}

	@MetaAnnotation(name="CdnaVariation", description="HGVS nomenclature for cDNA-level changes",
			ref={TranscriptAnnotation.class, GenomicSequenceAnnotation.class})
	public String getHgvsCdnaName() {
		return dnaName.getName();
	}

	@MetaAnnotation(name="ProteinVariation", description="HGVS nomenclature for protein-level changes",
			ref={TranscriptAnnotation.class, GenomicSequenceAnnotation.class})
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
