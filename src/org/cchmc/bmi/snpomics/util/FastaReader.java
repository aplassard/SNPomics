package org.cchmc.bmi.snpomics.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.exception.UncheckedSnpomicsException;

public class FastaReader {

	public FastaReader(File fastaFile) {
		initialize(fastaFile);
	}
	
	@SuppressWarnings("serial")
	public void initialize(File fastaFile) {
		//Initialize the cache
		cache = new LinkedHashMap<GenomicSpan, String>(64, (float) 0.75, true) {
			@Override
			protected boolean removeEldestEntry(Entry<GenomicSpan, String> arg0) {
				return size() > 60;
			}
		};
		
		//Now initialize the FAI structure and the Reader
		index = new HashMap<String, FaiEntry>();
		try {
			reader = new RandomAccessFile(fastaFile, "r");
				
			BufferedReader faiReader = new BufferedReader(new FileReader(fastaFile.getAbsolutePath()+".fai"));
			String line;
			while ((line = faiReader.readLine()) != null) {
				FaiEntry fai = new FaiEntry(line);
				index.put(fai.chrom, fai);
			}
			faiReader.close();
		} catch (FileNotFoundException e) {
			throw new UncheckedSnpomicsException("Can't find fasta or index", e);
		} catch (IOException e) {
			throw new UncheckedSnpomicsException("IO error in fasta index", e);
		}

	}

	public String getSequence(GenomicSpan position) {
		String result = null;
		if (cache.containsKey(position))
			result = cache.get(position);
		else {
			try {
				//TODO: Verify that position is entirely inside chromosome
				//and in index
				StringBuilder sb = new StringBuilder();
				FaiEntry fai = index.get(position.getChromosome());
				long start = position.getStart() - 1;
				long linesToSkip = start / fai.basePerLine;
				long bytesToSkip = start % fai.basePerLine;
				reader.seek(fai.fileOffset + (linesToSkip*fai.bytePerLine) + bytesToSkip);
				while (sb.length() < position.length())
					sb.append(reader.readLine());
				result = sb.substring(0, (int) position.length());
			} catch (IOException e) {
				throw new UncheckedSnpomicsException("Error reading fasta", e);
			}
			if (result != null)
				cache.put(position, result);
		}
		return result;
	}
	
	class FaiEntry {
		public FaiEntry(String line) {
			String[] fields = line.split("\t");
			chrom = fields[0];
			size = Integer.parseInt(fields[1]);
			fileOffset = Long.parseLong(fields[2]);
			basePerLine = Integer.parseInt(fields[3]);
			bytePerLine = Integer.parseInt(fields[4]);
		}
		public String chrom;
		public int size;
		public long fileOffset;
		public int basePerLine;
		public int bytePerLine;
	}

	private Map<GenomicSpan, String> cache;
	private RandomAccessFile reader;
	private Map<String, FaiEntry> index;
}
