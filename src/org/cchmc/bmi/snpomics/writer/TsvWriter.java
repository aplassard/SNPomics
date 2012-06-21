package org.cchmc.bmi.snpomics.writer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.annotation.Annotation;
import org.cchmc.bmi.snpomics.annotation.ShortName;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.util.StringUtils;

/*
 * TODO: Fix the Annotations in a Variant - right now, the order (and size) of output for this
 * class is all kinds of wrong
 */

public class TsvWriter implements VariantWriter {

	public TsvWriter(PrintWriter writer) {
		output = writer;
		delim = "\t";
	}
	
	public TsvWriter(PrintWriter writer, String delimiter) {
		output = writer;
		delim = delimiter;
	}
	
	@Override
	public void pairWithInput(InputIterator input) {
		//No-op - the only data we care about is (are?) in the Variant
	}

	@Override
	public void writeHeaders(List<Class<? extends Annotation>> annotInfo) {
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("Chromosome");
		columns.add("Position");
		columns.add("Reference");
		columns.add("Alternate");
		
		annotationList = annotInfo;
		for (Class<?> cls : annotInfo) {
			ShortName header = cls.getAnnotation(ShortName.class);
			if (header == null)
				columns.add("[No ShortName]");
			else
				columns.add(header.value());
		}
		
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
		for (Class<? extends Annotation> cls : annotationList) {
			List<String> annot = new ArrayList<String>();
			for (int i=0;i<annotatedVariant.getAlt().size(); i++) {
				List<String> allele = new ArrayList<String>();
				for (Annotation ann : annotatedVariant.getAnnot(cls, i))
					allele.add(ann.toString());
				annot.add(StringUtils.join("|", allele));
			}
			columns.add(StringUtils.join(",", annot));
		}

		output.println(StringUtils.join(delim, columns));
	}
	
	private PrintWriter output;
	private String delim;
	private List<Class<? extends Annotation>> annotationList;
	
	@Override
	public void close() {
		output.close();
	}
}
