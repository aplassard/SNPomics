package org.cchmc.bmi.snpomics;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a span on a chromosome, in one-based inclusive coordinates ie, the
 * first nucleotide is span 1-1
 * 
 * @author dexzb9
 */
public final class GenomicSpan implements Comparable<GenomicSpan>, Cloneable {

	/*
	 * Note that internally, start and end are represented in the UCSC
	 * zero-based half open style. That is, the first nucleotide is the span 1-1
	 * externally, but it's 0-1 internally
	 */

	public GenomicSpan() {
		clearBinCache();
	}

	public GenomicSpan(String chrom, long start, long end) {
		setChromosome(chrom);
		setStart(start);
		setEnd(end);
	}

	public GenomicSpan(String chrom, long pos) {
		this(chrom, pos, pos);
	}
	
	public static GenomicSpan fromBin(String chromosome, int bin) {
		long start, end;
		//Don't allow it to run off the end forever
		if (bin > 65266)
			bin = 0;
		if (bin >= 4681) {
			start = (bin-4681) << 14;
			end = start + (1 << 14) - 1;
		} else if (bin >= 585) {
			start = (bin-585) << 17;
			end = start + (1 << 17) - 1;
		} else if (bin >= 73) {
			start = (bin-73) << 20;
			end = start + (1 << 20) - 1;
		} else if (bin >= 9) {
			start = (bin-9) << 23;
			end = start + (1 << 23) - 1;
		} else if (bin >= 1) {
			start = (bin-1) << 26;
			end = start + (1 << 26) - 1;
		} else {
			start = 0;
			end = (1 << 29) - 1;
		}
		return new GenomicSpan(chromosome, start+1, end+1);
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public long getStart() {
		return start + 1;
	}

	public void setStart(long start) {
		this.start = start - 1;
		clearBinCache();
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
		clearBinCache();
	}

	public long length() {
		return end - start;
	}

	public boolean overlaps(GenomicSpan other) {
		if (chromosome == null)
			return false;
		if (chromosome.equals(other.chromosome))
			return (other.end > start) && (other.start < end);
		return false;
	}

	public boolean contains(GenomicSpan other) {
		if (chromosome == null)
			return false;
		if (chromosome.equals(other.chromosome))
			return (other.start >= start) && (other.end <= end);
		return false;
	}

	private int calculateBin() {
		// Start and end need to be 0-based
		final int end = (int) (getEnd() - 1);
		final int start = (int) (getStart() - 1);

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

	public int getBin() {
		if (bin < 0)
			bin = calculateBin();
		return bin;
	}

	public List<Integer> getOverlappingBins() {
		if (overlappingBins.size() == 0) {
			final int start = (int) (getStart() - 1);
			final int end = (int) (getEnd() - 1);
			overlappingBins.add(0);
			for (int i = 1 + (start >> 26); i <= 1 + (end >> 26); i++)
				overlappingBins.add(i);
			for (int i = 9 + (start >> 23); i <= 9 + (end >> 23); i++)
				overlappingBins.add(i);
			for (int i = 73 + (start >> 20); i <= 73 + (end >> 20); i++)
				overlappingBins.add(i);
			for (int i = 585 + (start >> 17); i <= 585 + (end >> 17); i++)
				overlappingBins.add(i);
			for (int i = 4681 + (start >> 14); i <= 4681 + (end >> 14); i++)
				overlappingBins.add(i);
		}
		return overlappingBins;
	}

	public GenomicSpan intersect(GenomicSpan other) {
		if (!chromosome.equals(other.chromosome))
			return new GenomicSpan();
		GenomicSpan result;
		result = clone();
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
		if (chromosome.equals(other.chromosome))
			return (int) (start - other.start);
		return chromosome.compareTo(other.chromosome);
	}

	@Override
	public GenomicSpan clone() {
		try {
			return (GenomicSpan) super.clone();
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public boolean equals(Object arg) {
		if (arg == null)
			return false;
		if (!(arg instanceof GenomicSpan))
			return false;
		final GenomicSpan other = (GenomicSpan) arg;
		return chromosome.equals(other.chromosome) && (start == other.start)
				&& (end == other.end);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(chromosome);
		sb.append(":");
		sb.append(getStart());
		if (end != start + 1) {
			sb.append("-");
			sb.append(getEnd());
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return (int) (41*chromosome.hashCode()+13*start+end);
	}

	public static GenomicSpan parseSpan(String str) {
		final GenomicSpan gs = new GenomicSpan();
		final String[] fields = str.split("[-:]");
		gs.setChromosome(fields[0]);
		gs.setStart(Integer.parseInt(fields[1]));
		if (fields.length == 2)
			gs.setEnd(gs.getStart());
		else
			gs.setEnd(Integer.parseInt(fields[2]));
		return gs;
	}

	private void clearBinCache() {
		bin = -1;
		if (overlappingBins == null)
			overlappingBins = new ArrayList<Integer>();
		else
			overlappingBins.clear();
	}

	private String chromosome;
	private long start;
	private long end;
	private int bin;
	private ArrayList<Integer> overlappingBins;
}
