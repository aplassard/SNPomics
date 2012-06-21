package org.cchmc.bmi.snpomics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.annotation.Annotation;
import org.cchmc.bmi.snpomics.annotation.DummyAnnotation;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.factory.DummyFactory;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.reader.VCFReader;
import org.cchmc.bmi.snpomics.writer.TsvWriter;
import org.cchmc.bmi.snpomics.writer.VariantWriter;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AnnotationFactory factory = new DummyFactory();
			factory.setGenome("dummy");
			InputIterator input = new VCFReader(new BufferedReader(new FileReader(args[0])));
			VariantWriter output = new TsvWriter(new PrintWriter(new FileWriter(args[1])));
			//VariantWriter output = new TsvWriter(new PrintWriter(System.out));
			List<Class<? extends Annotation>> desiredAnnotations = new ArrayList<Class<? extends Annotation>>();
			desiredAnnotations.add(DummyAnnotation.class);
			SnpomicsEngine.run(input, output, factory, desiredAnnotations);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
