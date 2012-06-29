package org.cchmc.bmi.snpomics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.factory.JdbcFactory;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.reader.VCFReader;
import org.cchmc.bmi.snpomics.writer.TsvWriter;
import org.cchmc.bmi.snpomics.writer.VariantWriter;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SnpomicsEngine.setProperty("jdbc.drivers", "com.mysql.jdbc.Driver");
			SnpomicsEngine.setProperty("jdbc.url", "jdbc:mysql://localhost/snpomics");
			SnpomicsEngine.setProperty("jdbc.username", "root");
			SnpomicsEngine.setProperty("jdbc.password", "");

			AnnotationFactory factory = new JdbcFactory();
			/*factory.initializeEmptyBackend();
			
			Genome g = new Genome("GRCh37", "Homo sapiens", 9606, 
					"http://www.ncbi.nlm.nih.gov/projects/genome/assembly/grc/human/index.shtml");
			g.setTransTableId(1);
			g.setAltTransTableId(2);
			g.addAltTransChromosome("MT");
			factory.createGenome(g);
			
			ReferenceMetadata<?> rmd = new ReferenceMetadata<TranscriptAnnotation>(TranscriptAnnotation.class, "GRCh37", "ucsc");
			rmd.setSource("http://genome.ucsc.edu/cgi-bin/hgTables");
			rmd.setUpdateDate(new Date());
			factory.importData(new FileInputStream("/Users/dexzb9/Downloads/genes_GRCh37.txt"), rmd);*/
			factory.setGenome("GRCh37");
			InputIterator input = new VCFReader(new BufferedReader(new FileReader(args[0])));
			VariantWriter output = new TsvWriter(new PrintWriter(new FileWriter(args[1])));
			//VariantWriter output = new TsvWriter(new PrintWriter(System.out));
			
			Map<String, OutputField> potentialFields = SnpomicsEngine.getAllowedOutput();
			
			List<OutputField> desiredAnnotations = new ArrayList<OutputField>();
			desiredAnnotations.add(potentialFields.get("Gene Name"));
			
			SnpomicsEngine.run(input, output, factory, desiredAnnotations);
			
			
/*			AnnotationFactory factory = new DummyFactory();
			factory.setGenome("dummy");*/
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
