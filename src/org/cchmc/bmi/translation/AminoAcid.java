package org.cchmc.bmi.translation;

import java.util.HashMap;

public enum AminoAcid {
	ALA("Alanine",        "Ala", "A"),
	ARG("Arginine",       "Arg", "R"),
	ASN("Asparagine",     "Asn", "N"),
	ASP("Aspartic acid",  "Asp", "D"),
	CYS("Cysteine",       "Cys", "C"),
	GLU("Glutamic acid",  "Glu", "E"),
	GLN("Glutamine",      "Gln", "Q"),
	GLY("Glycine",        "Gly", "G"),
	HIS("Histidine",      "His", "H"),
	ILE("Isoleucine",     "Ile", "I"),
	LEU("Leucine",        "Leu", "L"),
	LYS("Lysine",         "Lys", "K"),
	MET("Methionine",     "Met", "M"),
	PHE("Phenylalanine",  "Phe", "F"),
	PRO("Proline",        "Pro", "P"),
	SER("Serine",         "Ser", "S"),
	THR("Threonine",      "Thr", "T"),
	TRP("Tryptophan",     "Trp", "W"),
	TYR("Tyrosine",       "Tyr", "Y"),
	VAL("Valine",         "Val", "V"),
	SEC("Selenocysteine", "Sec", "U"),
	PYL("Pyrrolysine",    "Pyl", "O"),
	STOP("Stop codon",    "*",   "*"),
	UNK("Unknown",        "Xaa", "X");

	public String fullName() {
		return fullName;
	}
	public String abbrev() {
		return threeLetterCode;
	}
	public String code() {
		return code;
	}
	@Override
	public String toString() {
		return fullName();
	}
	
	public static AminoAcid lookup(String name) {
		if (name.length() == 1)
			return lookupOne.get(name);
		if (name.length() == 3)
			return lookupThree.get(name);
		return lookupFull.get(name);
	}
	
	private AminoAcid(String fullName, String threeLetterCode, String code) {
		this.fullName = fullName;
		this.threeLetterCode = threeLetterCode;
		this.code = code;		
	}
	private String fullName;
	private String threeLetterCode;
	private String code;
	
	private static HashMap<String, AminoAcid> lookupFull;
	private static HashMap<String, AminoAcid> lookupThree;
	private static HashMap<String, AminoAcid> lookupOne;
	static {
		lookupFull = new HashMap<String, AminoAcid>();
		lookupThree = new HashMap<String, AminoAcid>();
		lookupOne = new HashMap<String, AminoAcid>();
		
		for (AminoAcid aa : AminoAcid.values()) {
			lookupFull.put(aa.fullName(), aa);
			lookupThree.put(aa.abbrev(), aa);
			lookupOne.put(aa.code(), aa);
		}
	}
}
