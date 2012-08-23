package org.cchmc.bmi.snpomics.cli.arguments;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames="list") 
public class ListArguments {

	public static enum ListableItem { 
		GENOMES, REFERENCES, DEFAULT_REFERENCES, ANNOTATIONS, PARAMETERS, READERS, WRITERS, READER_OPTIONS, WRITER_OPTIONS };
	
	@Parameter(description="Values to list - one of (genomes, annotations, references, " +
			"default_references, parameters, readers, writers, reader_options, or writer_options)", required=true)
	public List<ListableItem> toList;

}