package org.cchmc.bmi.snpomics.translation;

import java.util.HashMap;


public class VertebrateMitoGeneticCode extends GeneticCode {

	@Override
	public AminoAcid translateCodon(String codon) {
		if (table.containsKey(codon))
			return table.get(codon);
		return AminoAcid.UNK;
	}
	
	/**
	 * Both AGA and AGG are listed as "valid", though that's not precisely true.  In reality, the
	 * mitoribosome slips backward one nucleotide, and the actual codon that is translated is UAG,
	 * an actual Stop.  However, for purposes of validating gene CDSs, we'll call it valid.  These
	 * only happen in CO1 and ND6 (human), but I'm going to call that a common case
	 */
	@Override
	public boolean isValidStop(String codon) {
		return super.isValidStop(codon) || codon.equals("AGA") || codon.equals("AGG");
	}
	
	/**
	 * Start codons vary somewhat among species.  Humans can also start at AUU, and mice at
	 * AUU or AUC
	 */
	@Override
	public boolean isValidStart(String codon) {
		//ATG and ATA are Met, ATT is also valid in humans
		return super.isValidStart(codon) || codon.equals("ATT") || codon.equals("ATC");
	}
	
	private static HashMap<String, AminoAcid> table;
	static {
		table = new HashMap<String, AminoAcid>();
		
		table.put("TTT", AminoAcid.PHE);
		table.put("TTC", AminoAcid.PHE);
		table.put("TTA", AminoAcid.LEU);
		table.put("TTG", AminoAcid.LEU);
		
		table.put("TCT", AminoAcid.SER);
		table.put("TCC", AminoAcid.SER);
		table.put("TCA", AminoAcid.SER);
		table.put("TCG", AminoAcid.SER);
		
		table.put("TAT", AminoAcid.TYR);
		table.put("TAC", AminoAcid.TYR);
		table.put("TAA", AminoAcid.STOP);
		table.put("TAG", AminoAcid.STOP);
		
		table.put("TGT", AminoAcid.CYS);
		table.put("TGC", AminoAcid.CYS);
		table.put("TGA", AminoAcid.TRP);
		table.put("TGG", AminoAcid.TRP);

		
		table.put("CTT", AminoAcid.LEU);
		table.put("CTC", AminoAcid.LEU);
		table.put("CTA", AminoAcid.LEU);
		table.put("CTG", AminoAcid.LEU);
		
		table.put("CCT", AminoAcid.PRO);
		table.put("CCC", AminoAcid.PRO);
		table.put("CCA", AminoAcid.PRO);
		table.put("CCG", AminoAcid.PRO);
		
		table.put("CAT", AminoAcid.HIS);
		table.put("CAC", AminoAcid.HIS);
		table.put("CAA", AminoAcid.GLN);
		table.put("CAG", AminoAcid.GLN);
		
		table.put("CGT", AminoAcid.ARG);
		table.put("CGC", AminoAcid.ARG);
		table.put("CGA", AminoAcid.ARG);
		table.put("CGG", AminoAcid.ARG);

		
		table.put("ATT", AminoAcid.ILE);
		table.put("ATC", AminoAcid.ILE);
		table.put("ATA", AminoAcid.MET);
		table.put("ATG", AminoAcid.MET);
		
		table.put("ACT", AminoAcid.THR);
		table.put("ACC", AminoAcid.THR);
		table.put("ACA", AminoAcid.THR);
		table.put("ACG", AminoAcid.THR);
		
		table.put("AAT", AminoAcid.ASN);
		table.put("AAC", AminoAcid.ASN);
		table.put("AAA", AminoAcid.LYS);
		table.put("AAG", AminoAcid.LYS);
		
		table.put("AGT", AminoAcid.SER);
		table.put("AGC", AminoAcid.SER);
		table.put("AGA", AminoAcid.ARG);
		table.put("AGG", AminoAcid.ARG);

		
		table.put("GTT", AminoAcid.VAL);
		table.put("GTC", AminoAcid.VAL);
		table.put("GTA", AminoAcid.VAL);
		table.put("GTG", AminoAcid.VAL);
		
		table.put("GCT", AminoAcid.ALA);
		table.put("GCC", AminoAcid.ALA);
		table.put("GCA", AminoAcid.ALA);
		table.put("GCG", AminoAcid.ALA);
		
		table.put("GAT", AminoAcid.ASP);
		table.put("GAC", AminoAcid.ASP);
		table.put("GAA", AminoAcid.GLU);
		table.put("GAG", AminoAcid.GLU);
		
		table.put("GGT", AminoAcid.GLY);
		table.put("GGC", AminoAcid.GLY);
		table.put("GGA", AminoAcid.GLY);
		table.put("GGG", AminoAcid.GLY);
	}

}
