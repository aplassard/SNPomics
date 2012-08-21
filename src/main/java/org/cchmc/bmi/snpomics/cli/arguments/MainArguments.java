package org.cchmc.bmi.snpomics.cli.arguments;

import java.io.File;

import com.beust.jcommander.Parameter;

public class MainArguments {

	@Parameter(names="--loadConfig", description="Read parameters from FILE and save for future runs")
	public File configFile;
	
	@Parameter(names="--initialize", description="Initialize a new backend (ie, database).  Only needs to be done once.")
	public boolean initializeBackend;
	
	@Parameter(names={"-g", "--genome"}, description="Genome to annotate with")
	public String genome;
	
	@Parameter(names={"-?", "-h", "--help"}, description="help!", help=true)
	public boolean help;
}
