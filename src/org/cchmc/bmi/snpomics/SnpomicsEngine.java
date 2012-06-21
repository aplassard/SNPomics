package org.cchmc.bmi.snpomics;

import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.annotation.Annotate;
import org.cchmc.bmi.snpomics.annotation.Annotation;
import org.cchmc.bmi.snpomics.annotation.annotator.Annotator;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.writer.VariantWriter;

public class SnpomicsEngine {
	public static void run(InputIterator input, VariantWriter output, AnnotationFactory factory, 
			List<Class<? extends Annotation>> annotations) throws AnnotationNotFoundException {
		
		//Initialize the output
		output.pairWithInput(input);
		output.writeHeaders(annotations);
		
		//Create and store the Annotators
		List<Annotator<? extends Annotation>> annotators = new ArrayList<Annotator<?>>();
		for (Class<? extends Annotation> ann : annotations)
			annotators.add(Annotate.getAnnotator(ann, factory));
		
		//Cycle through the input
		while (input.next()) {
			Variant annotated = input.getVariant();
			//For each variant, annotate each allele with each annotation
			for (Annotator<? extends Annotation> ann : annotators) {
				for (int i=0; i<annotated.getAlt().size(); i++) {
					SimpleVariant sv = annotated.getSimpleVariant(i);
					annotated.addAnnotation(ann.annotate(sv, factory), i);
				}
			}
			//And save the results
			output.writeVariant(annotated);
		}
		output.close();
	}
}
