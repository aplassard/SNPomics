package org.cchmc.bmi.snpomics.cli.arguments;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames="create")
public class CreateArguments {

	//This is specifically not a @Parameter - we'll use -g for that.  However, we'll
	//keep it here for encapsulation
	public String name;
	
	@Parameter(names="--organism", description="Organism described by the genome (ie, \"Homo sapiens\"", required=true)
	public String organism;
	
	@Parameter(names="--taxid", description="NCBI Taxonomy ID (ie, 9606)", required=true)
	public Integer taxID;
	
	@Parameter(names="--url", description="Source URL of assembly", required=true)
	public String sourceURL;
	
	@Parameter(names="--transTable", description="Translation Table in use (default 1, see http://ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/index.cgi?chapter=cgencodes)")
	public Integer transTable = 1;
	
	@Parameter(names="--altTransTable", description="Alternate Translation Table used, eg by mitochondria")
	public Integer altTransTable;
	
	@Parameter(names="--altTransChrom", description="Chromosomes that use the alternate translation table")
	public List<String> altTransChrom;
}
