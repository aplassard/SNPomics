package org.cchmc.bmi.snpomics.writer;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.annotation.interactive.InteractiveAnnotation;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.util.StringUtils;

public class TsvWriter implements VariantWriter {

	public TsvWriter() {
		delim = "\t";
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
		//No-op - the only data we care about is (are?) in the Variant
	}

	@Override
	public void writeHeaders(List<OutputField> fields) {
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("Chromosome");
		columns.add("Position");
		columns.add("Reference");
		columns.add("Alternate");
		
		annotationList = fields;
		for (OutputField f : fields)
			columns.add(f.getShortName());
		
		output.println(StringUtils.join(delim, columns));
	}

	@Override
	public void writeVariant(Variant annotatedVariant) {
		ArrayList<String> columns = new ArrayList<String>();
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
				for (int i=0;i<annotatedVariant.getAlt().size(); i++) {
					List<String> allele = new ArrayList<String>();
					for (InteractiveAnnotation ann : annotatedVariant.getAnnot(field.getDeclaringClass(), i))
						allele.add(field.getOutput(ann));
					annot.add(StringUtils.join("|", allele));
				}
				columns.add(StringUtils.join(",", annot));
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		output.println(StringUtils.join(delim, columns));
	}
	
	private PrintWriter output;
	private String delim;
	private List<OutputField> annotationList;
	
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
		return "Writes variants only to a tab-separated file";
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
}
