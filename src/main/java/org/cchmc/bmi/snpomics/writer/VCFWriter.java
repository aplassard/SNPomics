package org.cchmc.bmi.snpomics.writer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.cchmc.bmi.snpomics.Genotype;
import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.annotation.interactive.InteractiveAnnotation;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;
import org.cchmc.bmi.snpomics.reader.GenotypeIterator;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.reader.VCFReader;
import org.cchmc.bmi.snpomics.util.StringUtils;

/*
 * Four major test cases to consider in the input/output pairing:
 * 
 * Input is:
 * 	1) VCF with genotypes
 *  2) VCF without genotypes
 *  3) Some other GenotypeIterator
 *  4) Some other InputIterator
 */

public class VCFWriter implements VariantWriter {
	
	private interface VCFHelper {
		void setInput (InputIterator input);
		List<String> importMetaInformation();
		Map<String, String> getInfo();
		String getHeaderLine();
		String getGenotypes();
		boolean hasGenotypes();
		String getFilter();
		static final String headerLine = "#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO";
	}
	
	private class VCFtoVCFHelper implements VCFHelper {
		@Override
		public void setInput(InputIterator input) {
			if (!(input instanceof VCFReader))
				throw new SnpomicsException("VCFtoVCFHelper must be paired with a VCFReader");
			vcf = (VCFReader)input;
		}
		@Override
		public List<String> importMetaInformation() {
			return vcf.getMetaInformation();
		}
		@Override
		public Map<String, String> getInfo() {
			return vcf.getInfo();
		}
		@Override
		public String getHeaderLine() {
			if (writeGenotypes && hasGenotypes()) {
				ArrayList<String> f = new ArrayList<String>();
				f.add(headerLine);
				f.add("FORMAT");
				f.addAll(vcf.getSamples());
				return StringUtils.join("\t", f);
			}
			return headerLine;
		}
		@Override
		public String getGenotypes() {
			return vcf.getRawGenotypesAndFormat();
		}
		@Override
		public boolean hasGenotypes() {
			return vcf.hasGenotypes();
		}
		@Override
		public String getFilter() {
			return vcf.getFilter();
		}
		private VCFReader vcf;
	}
	
	private abstract class NewVCFHelper implements VCFHelper {
		@Override
		public List<String> importMetaInformation() {
			List<String> result = new ArrayList<String>();
			result.add("##fileformat=VCFv4.1");
			return result;
		}
		@Override
		public Map<String, String> getInfo() {
			return new HashMap<String,String>();
		}
		@Override
		public String getFilter() {
			return "PASS";
		}
	}
	
	private class GenoToVCFHelper extends NewVCFHelper {
		@Override
		public void setInput(InputIterator input) {
			if (!(input instanceof GenotypeIterator))
				throw new SnpomicsException("GenoToVCFHelper must be paired with a GenotypeIterator");
			geno = (GenotypeIterator)input;
		}
		@Override
		public String getHeaderLine() {
			if (writeGenotypes && hasGenotypes()) {
				ArrayList<String> f = new ArrayList<String>();
				f.add(headerLine);
				f.add("FORMAT");
				f.addAll(geno.getSamples());
				return StringUtils.join("\t", f);
			}
			return headerLine;
		}
		@Override
		public String getGenotypes() {
			if (!hasGenotypes())
				return "";
			ArrayList<String> f = new ArrayList<String>();
			f.add("GT");
			for (Genotype g : geno.getGenotypes()) {
				if (g.isCalled())
					f.add(StringUtils.join("/", g.getAlleles()));
				else
					f.add("./.");
			}
			return StringUtils.join("\t", f);
		}
		@Override
		public boolean hasGenotypes() {
			return geno.hasGenotypes();
		}
		private GenotypeIterator geno; 
	}
	
	private class SiteToVCFHelper extends NewVCFHelper {
		@Override
		public void setInput(InputIterator input) {
		}
		@Override
		public String getHeaderLine() {
			return headerLine;
		}
		@Override
		public String getGenotypes() {
			return "";
		}
		@Override
		public boolean hasGenotypes() {
			return false;
		}
	}
	
	public VCFWriter() {
		writeGenotypes = true;
	}
	
	@Override
	public void setOutput(PrintWriter writer) {
		output = writer;
	}

	@Override
	public void pairWithInput(InputIterator input) {
		if (input instanceof VCFReader)
			helper = new VCFtoVCFHelper();
		else if (input instanceof GenotypeIterator)
			helper = new GenoToVCFHelper();
		else
			helper = new SiteToVCFHelper();
		helper.setInput(input);
	}

	@Override
	public void writeHeaders(List<OutputField> fields) {
		annotationList = fields;
		
		//Import headers from the input
		for (String s : helper.importMetaInformation())
			output.println(s);
		
		//Write headers for the annotations
		for (OutputField f : annotationList) {
			StringBuilder sb = new StringBuilder();
			sb.append("##INFO=<ID=");
			sb.append(f.getName());
			sb.append(",Number=.,Type=String,Description=\"");
			sb.append(f.getDescription());
			sb.append("\">");
			output.println(sb.toString());
		}
		
		//And write the VCF header
		output.println(helper.getHeaderLine());
	}

	@Override
	public void writeVariant(Variant annotatedVariant) {
		Map<String, String> info = helper.getInfo();

		/*
		 * This is a little complicated.
		 * Each annotation type is a field in INFO, so iterate through those first
		 * For each annotation, iterate through the alt alleles and comma-separate
		 * For each alt allele, get the (possibly several) annotations and pipe-separate
		 */
		try {
			for (OutputField field : annotationList) {
				List<String> annot = new ArrayList<String>();
				if (annotatedVariant.isInvariant()) {
					List<String> allele = new ArrayList<String>();
					for (InteractiveAnnotation ann : annotatedVariant.getAnnot(field.getDeclaringClass(), 0))
						allele.add(escapeString(field.getOutput(ann)));
					annot.add(StringUtils.join("|", allele));
		
				} else for (int i=0;i<annotatedVariant.getAlt().size(); i++) {
					List<String> allele = new ArrayList<String>();
					for (InteractiveAnnotation ann : annotatedVariant.getAnnot(field.getDeclaringClass(), i))
						allele.add(escapeString(field.getOutput(ann)));
					annot.add(StringUtils.join("|", allele));
				}
				String value = StringUtils.join(",", annot);
				if (nonEmptyAnnotation.matcher(value).find())
					info.put(field.getName(), value);
			}
		} catch (Exception e) {
			throw new SnpomicsException("Can't get annotation", e);
		}
		
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(annotatedVariant.getPosition().getChromosome());
		fields.add(Long.toString(annotatedVariant.getPosition().getStart()));
		fields.add(annotatedVariant.getId() == null ? "." : annotatedVariant.getId());
		fields.add(annotatedVariant.getRef());
		fields.add(StringUtils.join(",", annotatedVariant.getAlt()));
		fields.add(annotatedVariant.getQualString() == null ? "." : annotatedVariant.getQualString());
		fields.add(helper.getFilter());
		
		ArrayList<String> infoStr = new ArrayList<String>();
		for (String key : info.keySet()) {
			if (info.get(key) == null)
				infoStr.add(key);
			else
				infoStr.add(key + "=" + info.get(key));
		}
		fields.add(infoStr.isEmpty() ? "." : StringUtils.join(";", infoStr));
		
		if (writeGenotypes && helper.hasGenotypes())
			fields.add(helper.getGenotypes());
		
		output.println(StringUtils.join("\t", fields));
	}

	@Override
	public void close() {
		output.close();
	}

	@Override
	public String name() {
		return "vcf";
	}

	@Override
	public String description() {
		return "Variant Context Format (VCF), with genotypes if available";
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
		if (param.containsKey(GENOTYPE_OPTION)) {
			String value = param.get(GENOTYPE_OPTION).toLowerCase();
			if (value.equals("1") || "yes".startsWith(value) || "true".startsWith(value))
				writeGenotypes = true;
			else
				writeGenotypes = false;
		}
	}

	@Override
	public Map<String, String> getAvailableParameters() {
		return recognizedOptions;
	}
	
	/**
	 * Sanitizes strings destined for the INFO fields to meet VCF spec (no whitespace, 
	 * semicolon, equals sign, or comma).  An equals sign is replaced by the HTML
	 * entity "&amp;eq;", all others are replaced by underscores
	 * @param raw
	 * @return
	 */
	public String escapeString(String raw) {
		return raw.replaceAll("[ ,;]", "_").replaceAll("=", "&eq;");
	}

	private PrintWriter output;
	private List<OutputField> annotationList;
	private VCFHelper helper;
	private static final Pattern nonEmptyAnnotation = Pattern.compile("[^,|]");
	private boolean writeGenotypes;
	private static Map<String, String> recognizedOptions;
	private static final String GENOTYPE_OPTION = "genotype";
	
	static {
		recognizedOptions = new HashMap<String, String>();
		recognizedOptions.put(GENOTYPE_OPTION, "Write sample-level genotypes, or a 'sites-only' VCF?");
	}
}
