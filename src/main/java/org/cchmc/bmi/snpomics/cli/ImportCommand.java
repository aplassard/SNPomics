package org.cchmc.bmi.snpomics.cli;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.cchmc.bmi.snpomics.ReferenceMetadata;
import org.cchmc.bmi.snpomics.SnpomicsEngine;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.reference.ReferenceAnnotation;
import org.cchmc.bmi.snpomics.cli.arguments.ImportArguments;

public class ImportCommand {

	@SuppressWarnings("unchecked")
	public static void run(AnnotationFactory factory, ImportArguments args) {
		Map<String, Class<? extends ReferenceAnnotation>> refs = SnpomicsEngine.getAnnotations();
		Class<? extends ReferenceAnnotation> refClass = refs.get(args.type);
		//TODO: Error if args.type isn't correct!
		
		//Eww, compiler ickiness.  I can't figure out how to make this a, for
		//example, ReferenceMetadata<TranscriptAnnotation>.  So it's raw and gross
		@SuppressWarnings("rawtypes")
		ReferenceMetadata rmd = new ReferenceMetadata(refClass, factory.getGenome().getName(), args.version);
		rmd.setSource(args.sourceURL);
		rmd.setUpdateDate(args.updateDate);
		rmd.setLinkTemplate(args.templateURL);
		try {
			factory.importData(new FileReader(args.filesToImport.get(0)), rmd);
			//TODO: Handle filesToImport being empty
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (args.isDefault)
			factory.makeVersionPermanentDefault(refClass, args.version);
	}
}
