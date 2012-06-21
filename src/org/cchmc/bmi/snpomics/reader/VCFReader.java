package org.cchmc.bmi.snpomics.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.Variant;

public class VCFReader implements GenotypeIterator {
	
	public VCFReader() {
		isInitialized = false;
	}
	
	public VCFReader(BufferedReader input) {
		setInput(input);
	}
	
	public void setInput(BufferedReader input) {
		isInitialized = false;
		in = input;
		initialize();
	}
	
	private void initialize() {
		if (isInitialized)
			return;
		headers = new ArrayList<String>();
		isInitialized = true;
		try {
			String line;
			while (in.ready()) {
				line = in.readLine();
				if (line.startsWith("##")) {
					headers.add(line);
					continue;
				}
				if (line.startsWith("#")) {
					headers.add(line);
					return;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	@Override
	public boolean next() {
		try {
			String line = in.readLine();
			if (line == null) {
				fields = null;
				return false;
			}
			fields = line.trim().split("\t");
		} catch (IOException e) {
			fields = null;
			return false;
		}
		return true;
	}

	@Override
	public Variant getVariant() {
		Variant result = new Variant();
		GenomicSpan position = new GenomicSpan();
		position.setChromosome(fields[0]);
		position.setStart(Long.parseLong(fields[1]));
		position.setEnd(position.getStart()+fields[3].length()-1);
		result.setPosition(position);
		result.setRef(fields[3]);
		result.setAlt(Arrays.asList(fields[4].split(",")));
		return result;
	}

	private boolean isInitialized;
	private BufferedReader in;
	private ArrayList<String> headers;
	private String[] fields;
}
