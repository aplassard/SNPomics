package org.cchmc.bmi.snpomics.writer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cchmc.bmi.snpomics.AnnotatedGenotype;
import org.cchmc.bmi.snpomics.Genotype;
import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.annotation.interactive.InteractiveAnnotation;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;
import org.cchmc.bmi.snpomics.reader.GenotypeIterator;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.util.StringUtils;

public class TsvWriter implements VariantWriter {

	public TsvWriter() {
		delim = "\t";
		geno = null;
		writeGenotypes = true;
	}
	
	@Override
	public void setOutput(PrintWriter writer) {
		output = writer;
	}
	
	public void setDelimiter(String delimiter) {
		delim = delimiter;
	}
	
	@Override
	public void pairWithInput(InputIterator input) {
		if (input instanceof GenotypeIterator)
			geno = (GenotypeIterator)input;
	}

	@Override
	public void writeHeaders(List<OutputField> fields) {
		//We can only write genotypes if they're in the input
		if (writeGenotypes)
			writeGenotypes = (geno == null) ? false : geno.hasGenotypes();
		
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("ID");
		columns.add("Chromosome");
		columns.add("Position");
		columns.add("Reference");
		columns.add("Alternate");
		
		annotationList = fields;
		for (OutputField f : fields)
			columns.add(f.getName());
		
		if (writeGenotypes) {
			List<String> depths = new ArrayList<String>();
			List<String> quals = new ArrayList<String>();
			for (String s : geno.getSamples()) {
				depths.add(s+" Depth");
				quals.add(s+" Qual");
			}
			columns.addAll(geno.getSamples());
			columns.addAll(depths);
			columns.addAll(quals);
		}
		
		output.println(StringUtils.join(delim, columns));
	}

	@Override
	public void writeVariant(Variant annotatedVariant) {
		ArrayList<String> columns = new ArrayList<String>();
		columns.add(annotatedVariant.getId());
		columns.add(annotatedVariant.getPosition().getChromosome());
		columns.add(Long.toString(annotatedVariant.getPosition().getStart()));
		columns.add(annotatedVariant.getRef());
		columns.add(StringUtils.join(",", annotatedVariant.getAlt()));
		
		/*
		 * This is a little complicated.
		 * Each annotation type is a column, so iterate through those first
		 * For each annotation, iterate through the alt alleles and comma-separate
		 * For each alt allele, get the (possible several) annotations and pipe-separate
		 */
		try {
			for (OutputField field : annotationList) {
				List<String> annot = new ArrayList<String>();
				if (annotatedVariant.isInvariant()) {
					List<String> allele = new ArrayList<String>();
					for (InteractiveAnnotation ann : annotatedVariant.getAnnot(field.getDeclaringClass(), 0))
						allele.add(field.getOutput(ann));
					annot.add(StringUtils.join("|", allele));
				} else for (int i=0;i<annotatedVariant.getAlt().size(); i++) {
					List<String> allele = new ArrayList<String>();
					for (InteractiveAnnotation ann : annotatedVariant.getAnnot(field.getDeclaringClass(), i))
						allele.add(field.getOutput(ann));
					annot.add(StringUtils.join("|", allele));
				}
				columns.add(StringUtils.join(",", annot));
			}
		} catch (Exception e) {
			throw new SnpomicsException("Can't get annotation", e);
		}
		
		if (writeGenotypes) {
			List<String> gt = new ArrayList<String>();
			List<String> dp = new ArrayList<String>();
			List<String> gq = new ArrayList<String>();
			for (Genotype g : geno.getGenotypes()) {
				List<String> alleles = g.getAlleles(annotatedVariant);
				if (alleles == null)
					gt.add("No Call");
				else
					gt.add(StringUtils.join("/", alleles));
				if (g instanceof AnnotatedGenotype) {
					AnnotatedGenotype ag = (AnnotatedGenotype)g;
					String depth = ag.getValue("DP");
					dp.add(depth == null ? "-" : depth);
					String qual = ag.getValue("GQ");
					gq.add(qual == null ? "-" : qual);
				} else {
					dp.add("-");
					gq.add("-");
				}
			}
			
			columns.addAll(gt);
			columns.addAll(dp);
			columns.addAll(gq);
		}

		output.println(StringUtils.join(delim, columns));
	}
	
	private PrintWriter output;
	private String delim;
	private List<OutputField> annotationList;
	private boolean writeGenotypes;
	private GenotypeIterator geno;
	
	@Override
	public void close() {
		output.close();
	}

	@Override
	public String name() {
		return "tsv";
	}

	@Override
	public String description() {
		return "Writes a tab-separated file";
	}

	@Override
	public String preferredExtension() {
		return "txt";
	}

	@Override
	public Set<String> allowedExtensions() {
		Set<String> result = new HashSet<String>();
		result.add("tsv");
		return result;
	}

	@Override
	public void setDynamicParameters(Map<String, String> param) {
		//No-op - no parameters!
	}

	@Override
	public Map<String, String> getAvailableParameters() {
		return Collections.emptyMap();
	}
}
