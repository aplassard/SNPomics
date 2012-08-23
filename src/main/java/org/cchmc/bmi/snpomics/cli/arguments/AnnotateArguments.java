package org.cchmc.bmi.snpomics.cli.arguments;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
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
	
	@DynamicParameter(names="-I", description="Optional parameters for reader")
	public Map<String, String> inputOptions = new HashMap<String, String>();

	@DynamicParameter(names="-O", description="Optional parameters for writer")
	public Map<String, String> outputOptions = new HashMap<String, String>();
}