package org.cchmc.bmi.snpomics.cli.arguments;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames="import")
public class ImportArguments {
	
	@Parameter(names="--type", description="Type of annotation to import, options are available via 'list references'", required=true)
	public String type;
	
	@Parameter(names="--version", description="Differentiates between multiple references of the same type (ie, refseq vs ensembl genes)", required=true)
	public String version;
	
	@Parameter(names="--source", description="URL that data was acquired from", required=true)
	public String sourceURL;
	
	@Parameter(names="--date", description="Date of last update to the reference, in YYYY-MM-DD format (default=today)")
	public Date updateDate = new Date();
	
	@Parameter(names="--linkTemplate", description="URL for lookup of items, with the ID replaced by '{?}'")
	public String templateURL;
	
	@Parameter(names="--default", description="Make this the new default version")
	public Boolean isDefault;
	
	@Parameter(description="File to import")
	public List<File> filesToImport;
}
