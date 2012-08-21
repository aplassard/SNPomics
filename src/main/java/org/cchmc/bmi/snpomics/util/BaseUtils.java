package org.cchmc.bmi.snpomics.util;

public class BaseUtils {
	public static char complement(char base) {
		switch (base) {
		case 'A' : return 'T';
		case 'C' : return 'G';
		case 'G' : return 'C';
		case 'T' : return 'A';
		default  : return base;
		}
	}

	private static StringBuilder complement_builder(char[] bases) {
		StringBuilder sb = new StringBuilder(bases.length);
		for (char base : bases) {
			sb.append(complement(base));
		}
		return sb;
	}
	
	public static String complement(char[] bases) {
		return complement_builder(bases).toString();
	}

	public static String complement(String bases) {
		return complement(bases.toCharArray());
	}

	public static String reverse(String str) {
		return new StringBuilder(str).reverse().toString();
	}
	
	public static String reverseComplement(String str) {
		return complement_builder(str.toCharArray()).reverse().toString();
	}

}
