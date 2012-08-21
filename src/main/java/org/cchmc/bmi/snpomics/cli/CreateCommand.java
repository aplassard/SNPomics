package org.cchmc.bmi.snpomics.cli;

import org.cchmc.bmi.snpomics.Genome;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.cli.arguments.CreateArguments;

public class CreateCommand {

	public static void run(AnnotationFactory factory, CreateArguments args) {
		Genome g = new Genome(args.name, args.organism, args.taxID, args.sourceURL);
		g.setTransTableId(args.transTable);
		if (args.altTransTable != null) {
			g.setAltTransTableId(args.altTransTable);
			for (String chrom : args.altTransChrom)
				g.addAltTransChromosome(chrom);
		}
		
		factory.createGenome(g);
	}
}
