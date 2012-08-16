package org.cchmc.bmi.snpomics.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cchmc.bmi.snpomics.AnnotatedGenotype;
import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.util.StringUtils;

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
		samples = null;
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
					String[] f = line.trim().split("\t");
					if (f.length > 8)
						samples = new ArrayList<String>(Arrays.asList(f).subList(9, f.length));
					else
						samples = Collections.emptyList();
					return;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}
	
	@Override
	public List<String> getSamples() {
		return samples;
	}
	
	@Override
	public boolean hasGenotypes() {
		return samples.size() > 0;
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
		if (fields[2] != ".")
			result.setId(fields[2]);
		if (fields[5] != ".")
			result.setQualString(fields[5]);
		return result;
	}
	
	@Override
	public List<AnnotatedGenotype> getGenotypes() {
		if (!hasGenotypes())
			return Collections.emptyList();
		ArrayList<AnnotatedGenotype> gt = new ArrayList<AnnotatedGenotype>();
		String[] format = fields[8].split(":");
		for (int i=9;i<fields.length;i++) {
			AnnotatedGenotype geno = new AnnotatedGenotype();
			String[] val = fields[i].split(":");
			if (!val[0].startsWith(".")) {
				ArrayList<Integer> alleles = new ArrayList<Integer>();
				for (String code : val[0].split("/|\\|"))
					alleles.add(Integer.parseInt(code));
				geno.setAlleles(alleles);
				for (int j=1;j<val.length;j++)
					geno.setValue(format[j], val[j]);
			}
			gt.add(geno);
		}
		return gt;
	}
	
	/**
	 * Returns all of the Format/Genotype columns from the VCF as a single string.
	 * This method performs ZERO parsing/validation, and is really only useful for passing
	 * information untouched to a VCFWriter
	 * @return
	 */
	public String getRawGenotypesAndFormat() {
		if (hasGenotypes())
			return StringUtils.join("\t", Arrays.asList(fields).subList(8, fields.length));
		return "";
	}
	
	/**
	 * Parses the INFO column of the current line into a Map&lt;String, String>.  Flag fields
	 * will be present in the Map, but will have null for a value
	 * @return
	 */
	public Map<String, String> getInfo() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (String info : fields[7].split(";")) {
			String[] f = info.split("=");
			result.put(f[0], f.length > 1 ? f[1] : null);
		}
		return result;
	}
	
	/**
	 * Returns the "meta-information lines" from the VCF.  Does not include the header line,
	 * ie the one that starts with "#CHROM"
	 * @return
	 */
	public List<String> getMetaInformation() {
		return headers;
	}
	
	public String getFilter() {
		return fields[6];
	}

	private boolean isInitialized;
	private BufferedReader in;
	private ArrayList<String> headers;
	private List<String> samples;
	private String[] fields;
}
