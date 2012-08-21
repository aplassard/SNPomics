package org.cchmc.bmi.snpomics;

import java.util.ArrayList;
import java.util.List;

public class Genotype {

	/**
	 * <p>Get a list of the called alleles, encoded as indices into the alleles described in a
	 * Variant (0=ref, 1=alt 1, 2=alt 2, etc).  Note that the length of this list reflects the number
	 * of copies of the locus.</p>
	 * <p>In the simplest case, this will be a list of length 2 over the alphabet {0,1}, but more
	 * sophisticated callers will make haploid (or generally aneuploid) calls with potentially more
	 * alternate alleles</p>
	 * @return an ArrayList of Integer offsets, or null if no call was made
	 */
	public List<Integer> getAlleles() { return alleles; }
	
	/**
	 * <p>Get a list of the called alleles, represented as Strings (ie, "A" or "ACGGT")</p>
	 * <p>The same caveats and conditions as in {@link Genotype#getAlleles()} exists</p>
	 * @param var
	 * @return
	 */
	public List<String> getAlleles(Variant var) {
		if (alleles == null)
			return null;
		List<String> result = new ArrayList<String>();
		for (Integer i : alleles) {
			result.add((i == 0) ? var.getRef() : var.getAlt().get(i-1));
		}
		return result;
	}
	
	public void setAlleles(List<Integer> _alleles) {
		alleles = new ArrayList<Integer>(_alleles);
	}
	
	public boolean isCalled() {
		return alleles != null;
	}
	
	private List<Integer> alleles = null;
}
