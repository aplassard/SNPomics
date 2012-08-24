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
import java.util.Set;

import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.SnpomicsEngine;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.cli.arguments.AnnotateArguments;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;
import org.cchmc.bmi.snpomics.exception.UserException;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.writer.VariantWriter;

public class AnnotateCommand {

	public static void run(AnnotationFactory factory, AnnotateArguments args) {
		try {
			InputIterator input = constructInput(args.inputFile, args.inputType);
			input.setDynamicParameters(args.inputOptions);
			
			//Validate args.inputOptions
			Set<String> validKeys = input.getAvailableParameters().keySet();
			for (String key : args.inputOptions.keySet()) {
				if (!validKeys.contains(key))
					System.err.println("WARN: Unknown input option '"+key+"', ignoring");
			}
			
			VariantWriter output = constructOutput(args.outputFile, args.outputType);
			output.setDynamicParameters(args.outputOptions);

			//Validate args.outputOptions
			validKeys = output.getAvailableParameters().keySet();
			for (String key : args.outputOptions.keySet()) {
				if (!validKeys.contains(key))
					System.err.println("WARN: Unknown output option '"+key+"', ignoring");
			}

			Map<String, OutputField> potentialFields = SnpomicsEngine.getAllowedOutput();
			
			List<OutputField> desiredAnnotations = new ArrayList<OutputField>();
			for (String field : args.fields) {
				if (!potentialFields.containsKey(field))
					throw new SnpomicsException(field + " isn't a recognized annotation");
				desiredAnnotations.add(potentialFields.get(field));
			}
			
			long start = System.currentTimeMillis();
			SnpomicsEngine.run(input, output, factory, desiredAnnotations);
			long end = System.currentTimeMillis();
			System.err.printf("Runtime: %.2f s\n", (float)(end-start)/1000.0);

		} catch (FileNotFoundException e) {
			throw new UserException.FileNotFound(e);
		} catch (IOException e) {
			throw new UserException.IOError(e);
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
			} catch (Exception e) {
				throw new SnpomicsException("Can't instantiate InputIterator", e);
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
			} catch (Exception e) {
				throw new SnpomicsException("Can't instantiate VariantWriter", e);
			}
		}
		if (file.getName().equals("-"))
			output.setOutput(new PrintWriter(System.out));
		else
			output.setOutput(new PrintWriter(new FileWriter(file)));
		return output;
	}

}
