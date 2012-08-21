package org.cchmc.bmi.snpomics.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.SnpomicsEngine;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.cli.arguments.AnnotateArguments;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.writer.VariantWriter;

public class AnnotateCommand {

	public static void run(AnnotationFactory factory, AnnotateArguments args) {
		try {
			InputIterator input = constructInput(args.inputFile, args.inputType);
			VariantWriter output = constructOutput(args.outputFile, args.outputType);

			Map<String, OutputField> potentialFields = SnpomicsEngine.getAllowedOutput();
			
			List<OutputField> desiredAnnotations = new ArrayList<OutputField>();
			for (String field : args.fields) {
				if (!potentialFields.containsKey(field))
					throw new RuntimeException(field + " isn't a recognized annotation");
				desiredAnnotations.add(potentialFields.get(field));
			}
			
			long start = System.currentTimeMillis();
			SnpomicsEngine.run(input, output, factory, desiredAnnotations);
			long end = System.currentTimeMillis();
			System.err.printf("Runtime: %.2f s\n", (float)(end-start)/1000.0);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AnnotationNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static InputIterator constructInput(File file, String type) throws FileNotFoundException {
		InputIterator input = null;
		//Construct the InputIterator
		if (type == null) {
			input = SnpomicsEngine.getBestInputIteratorForFile(file);
			if (input == null) {
				System.err.println("Couldn't determine type of input.  Rerun with --input-type");
				System.exit(1);
			}
		} else {
			Map<String, Class<? extends InputIterator>> readers = SnpomicsEngine.getReaders();
			if (!readers.containsKey(type)) {
				System.err.println("Unrecognized input type "+type);
				System.exit(1);
			}
			try {
				input = readers.get(type).newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (file.getName().equals("-"))
			input.setInput(new BufferedReader(new InputStreamReader(System.in)));
		else
			input.setInput(new BufferedReader(new FileReader(file)));
		return input;
	}
	
	private static VariantWriter constructOutput(File file, String type) throws IOException {
		VariantWriter output = null;
		//Construct the VariantWriter
		if (type == null) {
			output = SnpomicsEngine.getBestVariantWriterForFile(file);
			if (output == null) {
				System.err.println("Couldn't determine type of output.  Rerun with --output-type");
				System.exit(1);
			}
		} else {
			Map<String, Class<? extends VariantWriter>> writers = SnpomicsEngine.getWriters();
			if (!writers.containsKey(type)) {
				System.err.println("Unrecognized output type "+type);
				System.exit(1);
			}
			try {
				output = writers.get(type).newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (file.getName().equals("-"))
			output.setOutput(new PrintWriter(System.out));
		else
			output.setOutput(new PrintWriter(new FileWriter(file)));
		return output;
	}

}
