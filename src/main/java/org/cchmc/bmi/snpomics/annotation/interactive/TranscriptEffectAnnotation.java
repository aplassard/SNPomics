package org.cchmc.bmi.snpomics.annotation.interactive;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.translation.AminoAcid;

/**
 * Encapsulates the various effects that variants have on particular transcripts
 * @author dexzb9
 *
 */
public class TranscriptEffectAnnotation implements InteractiveAnnotation {
	
	/**
	 * An enumeration of the various functional consequences that are possible for
	 * a variant.  Enum values are all lower-case, in violation of Java convention,
	 * because they are actually the SequenceOntology terms.
	 * @see <a href="http://sequenceontology.org/index.html">http://sequenceontology.org/index.html</a>
	 * @author dexzb9
	 *
	 */
	public enum VariantFunction {
		unknown,                 // Not in SO
		
		//INVARIANT:
		region,                  // SO:0000001
		
		//MODIFIER:
		nc_transcript_variant,   // SO:0001619
		NMD_transcript_variant,  // SO:0001621
		five_prime_utr_variant,  // SO:0001623
		three_prime_utr_variant, // SO:0001624
		intron_variant,          // SO:0001627
		intergenic_variant,      // SO:0001628

		//LOW:
		stop_retained_variant,   // SO:0001567
		synonymous_variant,      // SO:0001819
		
		//MODERATE:
		missense_variant,        // SO:0001583
		inframe_insertion,       // SO:0001821
		inframe_deletion,        // SO:0001822

		//HIGH:
		splice_acceptor_variant, // SO:0001574
		splice_donor_variant,    // SO:0001575
		stop_lost,               // SO:0001578
		initiator_codon_variant, // SO:0001582
		stop_gained,             // SO:0001587
		frameshift_variant,      // SO:0001589
	};

	public TranscriptEffectAnnotation(TranscriptAnnotation transcript) {
		tx = transcript;
		dnaName = new HgvsDnaName(tx);
		protName = new HgvsProtName(tx);
		function = null;
	}
	
	@MetaAnnotation(name="Gene", description="Name of overlapping gene(s)",
			ref={TranscriptAnnotation.class})
	public String getGeneName() {
		return (tx == null) ? "" : tx.getName();
	}
	
	@MetaAnnotation(name="Transcript", description="Name of overlapping transcript(s)",
			ref={TranscriptAnnotation.class})
	public String getTranscriptName() {
		return (tx == null) ? "" : tx.getID();
	}

	@MetaAnnotation(name="Protein", description="Name of overlapping protein(s)",
			ref={TranscriptAnnotation.class})
	public String getProtName() {
		return (tx == null) ? "" : tx.getProtID();
	}

	@MetaAnnotation(name="CdnaVariation", description="HGVS nomenclature for cDNA-level changes",
			ref={TranscriptAnnotation.class})
	public String getHgvsCdnaName() {
		return dnaName.getName();
	}

	@MetaAnnotation(name="ProteinVariation", description="HGVS nomenclature for protein-level changes",
			ref={TranscriptAnnotation.class})
	public String getHgvsProteinName() {
		return protName.getName();
	}
	
	@MetaAnnotation(name="VariantFunction", description="SequenceOntology description of variant function (missense, synonymous, etc)",
			ref=TranscriptAnnotation.class)
	public String getVariantFunctionAsString() {
		if (function == null)
			deduceFunction();
		return String.valueOf(function);
	}
	
	public HgvsDnaName getHgvsCdnaObject() {
		return dnaName;
	}
	
	public HgvsProtName getHgvsProteinObject() {
		return protName;
	}
	
	public VariantFunction getVariantFunction() {
		return function;
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
	
	public void setProtUnknownEffect() {
		protName.setUnknown();
	}
	
	public void setVariantFunction(VariantFunction func) {
		function = func;
	}
	
	/**
	 * Uses the cdna and protein names to figure out the functional consequence of the variant
	 */
	private void deduceFunction() {
		String cdna = getHgvsCdnaName();
		String prot = getHgvsProteinName();
		if (cdna.isEmpty()) {
			function = VariantFunction.intergenic_variant;
		} else if (prot.isEmpty()) {
			//Figure out the location in the transcript
			if (Pattern.compile("\\d+[+-]\\d+").matcher(cdna).find())
				function = VariantFunction.intron_variant;
			else if (cdna.contains("n."))
				function = VariantFunction.nc_transcript_variant;
			else if (Pattern.compile("\\*\\d+").matcher(cdna).find())
				function = VariantFunction.three_prime_utr_variant;
			else if (Pattern.compile("-\\d+").matcher(cdna).find())
				function = VariantFunction.five_prime_utr_variant;
			else
				//Should never happen!
				function = VariantFunction.unknown;
		} else {
			//Figure out the effect
			if (prot.contains("p.(=)"))
				function = VariantFunction.synonymous_variant;
			else if (prot.contains("fs*"))
				function = VariantFunction.frameshift_variant;
			else if (prot.contains("ext*"))
				function = VariantFunction.stop_lost;
			else if (prot.contains("Met1?"))
				function = VariantFunction.initiator_codon_variant;
			else {
				Pattern snp = Pattern.compile("p\\.\\(([A-Za-z]{3})\\d+([A-Za-z*]+)\\)");
				Matcher snphit = snp.matcher(prot);
				//If it's a single AA substitution...
				if (snphit.find()) {
					if (snphit.group(2).equals("*"))
						function = VariantFunction.stop_gained;
					else
						function = VariantFunction.missense_variant;
				} else {
					if (prot.contains("p.?")) {
						//It's either a splice or a deletion off the end (unknown)
						if (dnaName.damagesSpliceAcceptor())
							function = VariantFunction.splice_acceptor_variant;
						else if (dnaName.damagesSpliceDonor())
							function = VariantFunction.splice_donor_variant;
						else
							function = VariantFunction.unknown;
					} //This set of conditions won't work for a complex indel - it will be called an
					//insertion...
					else if (cdna.contains("ins"))
						function = VariantFunction.inframe_insertion;
					else if (cdna.contains("del"))
						function = VariantFunction.inframe_deletion;
					else
						function = VariantFunction.unknown;
				}
			}
		}
	}

	private TranscriptAnnotation tx;
	private HgvsDnaName dnaName;
	private HgvsProtName protName;
	private VariantFunction function;
}
