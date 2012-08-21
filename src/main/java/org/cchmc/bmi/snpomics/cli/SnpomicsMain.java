package org.cchmc.bmi.snpomics.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.cchmc.bmi.snpomics.SnpomicsEngine;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.factory.JdbcFactory;
import org.cchmc.bmi.snpomics.cli.arguments.AnnotateArguments;
import org.cchmc.bmi.snpomics.cli.arguments.ConverterFactory;
import org.cchmc.bmi.snpomics.cli.arguments.CreateArguments;
import org.cchmc.bmi.snpomics.cli.arguments.ImportArguments;
import org.cchmc.bmi.snpomics.cli.arguments.ListArguments;
import org.cchmc.bmi.snpomics.cli.arguments.MainArguments;

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
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		if (arg.configFile != null)
			loadConfigFile(arg.configFile);
		populateSnpomicsProperties();
	
		AnnotationFactory factory = new JdbcFactory();
		if (arg.initializeBackend)
			factory.initializeEmptyBackend();
		//Don't set the genome here - if the command is "create" it won't
		//exist yet
		
		String command = parser.getParsedCommand();
		if (command.equals("create")) {
			createArg.name = arg.genome;
			CreateCommand.run(factory, createArg);
		} else {
			//Now that we know it's not create, set the genome
			if (arg.genome != null)
				factory.setGenome(arg.genome);

			if (command.equals("list")) {
				ListCommand.run(factory, listArg);
			} else if (command.equals("annot")) {
				AnnotateCommand.run(factory, annotArg);
			} else if (command.equals("import")) {
				ImportCommand.run(factory, importArg);
			}
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
			System.err.println("Can't read config file: "+e.getMessage());
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
			e.printStackTrace();
		}
	}
	
	
}
