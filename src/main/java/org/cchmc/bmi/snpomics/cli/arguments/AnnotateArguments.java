package org.cchmc.bmi.snpomics.cli.arguments;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames="annot") 
public class AnnotateArguments {
	
	@Parameter(names={"-i", "--input"}, description="Input file, - for stdin", required=true)
	public File inputFile;
	
	@Parameter(names="--input-type", description="Type of input file, will auto-detect if not specified")
	public String inputType;

	@Parameter(names={"-o", "--output"}, description="Output file, - for stdout", required=true)
	public File outputFile;
	
	@Parameter(names="--output-type", description="Type of output file, will auto-detect if not specified")
	public String outputType;

	@Parameter(names={"-f", "--fields"}, description="Annotation fields to include in output")
	public List<String> fields = Collections.emptyList();
}