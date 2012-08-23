package org.cchmc.bmi.snpomics.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cchmc.bmi.snpomics.AnnotatedGenotype;
import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.util.StringUtils;

public class VCFReader implements GenotypeIterator {
	
	public VCFReader() {
		isInitialized = false;
		skipFiltered = false;
	}
	
	@Override
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
					if (f.length >= VCF_FIRST_SAMP_FIELD)
						samples = new ArrayList<String>(Arrays.asList(f).subList(VCF_FIRST_SAMP_FIELD, f.length));
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
			do {
				String line = in.readLine();
				if (line == null) {
					fields = null;
					return false;
				}
				fields = line.trim().split("\t");
			} while (skipFiltered && !getFilter().equals(VCF_PASS));
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
		position.setChromosome(fields[VCF_CHROM_FIELD]);
		position.setStart(Long.parseLong(fields[VCF_POS_FIELD]));
		position.setEnd(position.getStart()+fields[VCF_REF_FIELD].length()-1);
		result.setPosition(position);
		result.setRef(fields[VCF_REF_FIELD]);
		result.setAlt(Arrays.asList(fields[VCF_ALT_FIELD].split(",")));
		if (fields[VCF_ID_FIELD] != ".")
			result.setId(fields[VCF_ID_FIELD]);
		if (fields[VCF_QUAL_FIELD] != ".")
			result.setQualString(fields[VCF_QUAL_FIELD]);
		return result;
	}
	
	@Override
	public List<AnnotatedGenotype> getGenotypes() {
		if (!hasGenotypes())
			return Collections.emptyList();
		ArrayList<AnnotatedGenotype> gt = new ArrayList<AnnotatedGenotype>();
		String[] format = fields[VCF_FORMAT_FIELD].split(":");
		for (int i=VCF_FIRST_SAMP_FIELD;i<fields.length;i++) {
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
			return StringUtils.join("\t", Arrays.asList(fields).subList(VCF_FORMAT_FIELD, fields.length));
		return "";
	}
	
	/**
	 * Parses the INFO column of the current line into a Map&lt;String, String>.  Flag fields
	 * will be present in the Map, but will have null for a value
	 * @return
	 */
	public Map<String, String> getInfo() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (String info : fields[VCF_INFO_FIELD].split(";")) {
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
		return fields[VCF_FILTER_FIELD];
	}

	@Override
	public String name() {
		return "vcf";
	}

	@Override
	public String description() {
		return "Variant Context Format (VCF) files, either with or without genotypes";
	}

	@Override
	public String preferredExtension() {
		return "vcf";
	}

	@Override
	public Set<String> allowedExtensions() {
		return Collections.emptySet();
	}

	@Override
	public void setDynamicParameters(Map<String, String> param) {
		if (param.containsKey(SKIP_FAILED_OPTION)) {
			String value = param.get(SKIP_FAILED_OPTION).toLowerCase();
			if (value.equals("1") || "yes".startsWith(value) || "true".startsWith(value))
				skipFiltered = true;
			else
				skipFiltered = false;
		}
	}

	@Override
	public Map<String, String> getAvailableParameters() {
		return recognizedOptions;
	}

	private boolean isInitialized;
	private BufferedReader in;
	private ArrayList<String> headers;
	private List<String> samples;
	private String[] fields;
	private static Map<String, String> recognizedOptions;
	private boolean skipFiltered;
	
	private static final String VCF_PASS = "PASS";
	private static final String SKIP_FAILED_OPTION = "skipFiltered";
	private static final int VCF_CHROM_FIELD = 0;
	private static final int VCF_POS_FIELD = 1;
	private static final int VCF_ID_FIELD = 2;
	private static final int VCF_REF_FIELD = 3;
	private static final int VCF_ALT_FIELD = 4;
	private static final int VCF_QUAL_FIELD = 5;
	private static final int VCF_FILTER_FIELD = 6;
	private static final int VCF_INFO_FIELD = 7;
	private static final int VCF_FORMAT_FIELD = 8;
	private static final int VCF_FIRST_SAMP_FIELD = 9;

	static {
		recognizedOptions = new HashMap<String, String>();
		recognizedOptions.put(SKIP_FAILED_OPTION, "Skips over variants that have any value but 'PASS' in the FILTER column");
	}
}
