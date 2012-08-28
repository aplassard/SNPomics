package org.cchmc.bmi.snpomics.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.exception.UserException;

public class BedReader implements InputIterator {

	@Override
	public void setDynamicParameters(Map<String, String> param) {
		//No-op
	}

	@Override
	public Map<String, String> getAvailableParameters() {
		return Collections.emptyMap();
	}

	@Override
	public void setInput(BufferedReader input) {
		this.input = input;
	}

	@Override
	public boolean next() {
		current = null;
		String line;
		try {
			do {
				line = input.readLine();
				if (line == null)
					return false;
			} while (line.startsWith("#"));
		} catch (IOException e) {
			throw new UserException.IOError(e);
		}
		String[] fields = line.split("\t");
		if (fields.length < 3)
			throw new UserException.IOError(new RuntimeException("BED lines must have at least 3 columns"));
		current = new Variant();
		GenomicSpan pos = new GenomicSpan();
		pos.setChromosome(fields[0]);
		pos.setStart(Long.parseLong(fields[1]));
		pos.setEnd(Long.parseLong(fields[2]));
		current.setPosition(pos);
		if (fields.length >= 4)
			current.setId(fields[3]);
		return true;
	}

	@Override
	public Variant getVariant() {
		return current;
	}

	@Override
	public String name() {
		return "bed";
	}

	@Override
	public String description() {
		return "BED format for genomic intervals, no actual base changes";
	}

	@Override
	public String preferredExtension() {
		return "bed";
	}

	@Override
	public Set<String> allowedExtensions() {
		return Collections.emptySet();
	}

	private BufferedReader input;
	private Variant current;
}
