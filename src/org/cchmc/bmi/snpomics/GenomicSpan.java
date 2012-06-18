package org.cchmc.bmi.snpomics;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a span on a chromosome, in one-based inclusive coordinates
 * ie, the first nucleotide is span 1-1
 * @author dexzb9
 *
 * TODO: Cache bin(s), clear cache when coords change
 */
public class GenomicSpan implements Comparable<GenomicSpan>, Cloneable {

	public GenomicSpan() {}
	public GenomicSpan(String chrom, long start, long end) {
		setChromosome(chrom);
		setStart(start);
		setEnd(end);
	}
	public GenomicSpan(String chrom, long pos) {
		this(chrom, pos, pos);
	}
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public long getStart() {
		return start+1;
	}
	public void setStart(long start) {
		this.start = start-1;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public long length() {
		return end-start;
	}
	
	public boolean overlaps(GenomicSpan other) {
		if (chromosome.equals(other.chromosome)) {
			return (other.end > start) && (other.start < end);
		}
		return false;
	}
	
	public boolean contains(GenomicSpan other) {
		if (chromosome.equals(other.chromosome)) {
			return (other.start >= start) && (other.end <= end);
		}
		return false;
	}

	public int getBin() {
		//Start and end need to be 0-based
		int end = (int) (getEnd()-1);
		int start = (int) (getStart()-1);
		
		if (start >> 14 == end >> 14)
			return 4681 + (start >> 14);
		if (start >> 17 == end >> 17)
			return 585 + (start >> 17);
		if (start >> 20 == end >> 20)
			return 73 + (start >> 20);
		if (start >> 23 == end >> 23)
			return 9 + (start >> 23);
		if (start >> 26 == end >> 26)
			return 1 + (start >> 26);
		return 0;
	}
	
	public List<Integer> getOverlappingBins() {
		int start = (int) (getStart()-1);
		int end = (int) (getEnd()-1);
		ArrayList<Integer> bins = new ArrayList<Integer>();
		bins.add(0);
		for (int i=1+(start>>26); i<=1+(end>>26); i++)
			bins.add(i);		
		for (int i=9+(start>>23); i<=9+(end>>23); i++)
			bins.add(i);
		for (int i=73+(start>>20); i<=73+(end>>20); i++)
			bins.add(i);
		for (int i=585+(start>>17); i<=585+(end>>17); i++)
			bins.add(i);
		for (int i=4681+(start>>14); i<=4681+(end>>14); i++)
			bins.add(i);
		return bins;
	}

	public GenomicSpan intersect(GenomicSpan other) {
		if (!chromosome.equals(other.chromosome))
			return new GenomicSpan();
		GenomicSpan result;
		try {
			result = clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		if (result.start < other.start)
			result.start = other.start;
		if (result.end > other.end)
			result.end = other.end;
		if (result.start > result.end)
			result.start = result.end = 0;
		return result;
	}
	@Override
	public int compareTo(GenomicSpan other) {
		if (chromosome.equals(other.chromosome)) {
			return (int) (start - other.start);
		}
		return chromosome.compareTo(other.chromosome);
	}
	public GenomicSpan clone() throws CloneNotSupportedException {
		return (GenomicSpan)super.clone();
	}
	@Override
	public boolean equals(Object arg) {
		if (arg == null)
			return false;
		if (!(arg instanceof GenomicSpan))
			return false;
		GenomicSpan other = (GenomicSpan)arg;
		return chromosome.equals(other.chromosome) &&
				(start == other.start) &&
				(end == other.end);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(chromosome);
		sb.append(":");
		sb.append(getStart());
		if (end != start+1) {
			sb.append("-");
			sb.append(getEnd());
		}
		return sb.toString();
	}
	public static GenomicSpan parseSpan(String str) {
		GenomicSpan gs = new GenomicSpan();
		String[] fields = str.split("[-:]");
		gs.setChromosome(fields[0]);
		gs.setStart(Integer.parseInt(fields[1]));
		if (fields.length == 2)
			gs.setEnd(gs.getStart());
		else
			gs.setEnd(Integer.parseInt(fields[2]));
		return gs;
	}

	private String chromosome;
	private long start;
	private long end;
}
