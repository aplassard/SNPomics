package org.cchmc.bmi.snpomics.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.cchmc.bmi.snpomics.Genome;
import org.cchmc.bmi.snpomics.SnpomicsEngine;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.factory.JdbcFactory;
import org.cchmc.bmi.snpomics.cli.arguments.AnnotateArguments;
import org.cchmc.bmi.snpomics.cli.arguments.ConverterFactory;
import org.cchmc.bmi.snpomics.cli.arguments.CreateArguments;
import org.cchmc.bmi.snpomics.cli.arguments.ImportArguments;
import org.cchmc.bmi.snpomics.cli.arguments.ListArguments;
import org.cchmc.bmi.snpomics.cli.arguments.MainArguments;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;
import org.cchmc.bmi.snpomics.exception.UserException;
import org.cchmc.bmi.snpomics.util.FastaReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class SnpomicsMain {

	public static void main(String[] args) {
		MainArguments arg = new MainArguments();
		ListArguments listArg = new ListArguments();
		AnnotateArguments annotArg = new AnnotateArguments();
		CreateArguments createArg = new CreateArguments();
		ImportArguments importArg = new ImportArguments();

		JCommander parser = new JCommander(arg);
		parser.addConverterFactory(new ConverterFactory());
		parser.addCommand(annotArg);
		parser.addCommand(listArg);
		parser.addCommand(createArg);
		parser.addCommand(importArg);

		try {
			parser.parse(args);

			if (arg.configFile != null)
				loadConfigFile(arg.configFile);
			populateSnpomicsProperties();
		
			AnnotationFactory factory = new JdbcFactory();
			if (arg.initializeBackend)
				factory.initializeEmptyBackend();
			if (arg.fasta != null)
				factory.setFasta(new FastaReader(arg.fasta));
			//Don't set the genome here - if the command is "create" it won't
			//exist yet
			
			String command = parser.getParsedCommand();
			if (command == null)
				throw new ParameterException("Must specify a command (annot, list, create, import)");
			if (command.equals("create")) {
				createArg.name = arg.genome;
				CreateCommand.run(factory, createArg);
			} else {
				//Now that we know it's not create, set the genome
				if (arg.genome != null)
					factory.setGenome(arg.genome);
				else
					setGenomeIfOnlyOnePossibility(factory);
	
				if (command.equals("list")) {
					ListCommand.run(factory, listArg);
				} else if (command.equals("annot")) {
					AnnotateCommand.run(factory, annotArg);
				} else if (command.equals("import")) {
					ImportCommand.run(factory, importArg);
				}
			}
		} catch (OutOfMemoryError e) {
			exitWithUserException(new UserException.OutOfMemory());
		} catch (ParameterException e) {
			exitWithBadParameters(e);
		} catch (UserException e) {
			exitWithUserException(e);
		} catch (SnpomicsException e) {
			exitWithException(e);
		} catch (Exception e) {
			exitWithException(new SnpomicsException(e));
		}
	}
	
	private static void loadConfigFile(File config) {
		Properties prop = new Properties();
		InputStream input;
		try {
			if (config.getName().equals("-"))
				input = System.in;
			else
				input = new FileInputStream(config);
			prop.load(input);
		} catch (IOException e) {
			throw new UserException.IOError(e);
		}
		
		Preferences node = Preferences.userNodeForPackage(SnpomicsEngine.class);
		for (String key : prop.stringPropertyNames()) {
			node.put(key, prop.getProperty(key));
		}
	}
	
	private static void populateSnpomicsProperties() {
		Preferences node = Preferences.userNodeForPackage(SnpomicsEngine.class);
		try {
			for (String key : node.keys())
				SnpomicsEngine.setProperty(key, node.get(key, ""));
		} catch (BackingStoreException e) {
			throw new SnpomicsException("Error storing Preferences", e);
		}
	}
	
	private static void setGenomeIfOnlyOnePossibility(AnnotationFactory factory) {
		Set<Genome> genomes = factory.getAvailableGenomes();
		if (genomes.size() == 1)
			factory.setGenome(genomes.iterator().next().getName());
	}
	
	private static void exitWithBadParameters(ParameterException e) {
		System.err.println(e.getMessage());
		System.exit(1);
	}
	
	private static void exitWithUserException(UserException e) {
		System.err.println("You goofed: "+e.getMessage());
		if (e.getCause() != null)
			System.err.println("Underlying exception: "+e.getCause().getMessage());
		System.exit(1);
	}
	
	private static void exitWithException(SnpomicsException e) {
		e.printStackTrace();
		System.err.println();
		StringBuilder sb = new StringBuilder();
		sb.append("I goofed: ");
		sb.append(e.getMessage());
		if (e.getCause() != null) {
			sb.append(" - caused by: ");
			sb.append(e.getCause().getMessage());
		}
		System.err.println(sb.toString());
		System.exit(1);
	}
	
}
