package org.cchmc.bmi.snpomics.translation;

import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.exception.UserException;


public abstract class GeneticCode {

	public abstract AminoAcid translateCodon(String codon);
		
	public List<AminoAcid> translate(String cds) {
		//Trim any incomplete codons from the end
		if (cds.length() % 3 != 0)
			cds = cds.substring(0, cds.length()-(cds.length()%3));
		
		@SuppressWarnings("serial")
		ArrayList<AminoAcid> peptide = new ArrayList<AminoAcid>() {
			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				for (AminoAcid aa : this)
					sb.append(aa.abbrev());
				return sb.toString();				
			}
		};
		for (int i=0;i<cds.length();i+=3)
			peptide.add(translateCodon(cds.substring(i,i+3)));
		return peptide;
	}
	
	
	public boolean isValidStop(String codon) {
		return translateCodon(codon) == AminoAcid.STOP;
	}
	
	public boolean isValidStart(String codon) {
		return translateCodon(codon) == AminoAcid.MET;
	}
	
	public static GeneticCode getTable(int tableID) {
		switch (tableID) {
		case 1: return new StandardGeneticCode();
		case 2: return new VertebrateMitoGeneticCode();
		}
		throw new UserException.UnknownTranslationTable(tableID);
	}
}
