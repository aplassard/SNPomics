package org.cchmc.bmi.snpomics;

import org.cchmc.bmi.snpomics.annotation.GenomicSequenceAnnotation;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.factory.JdbcFactory;
import org.cchmc.bmi.snpomics.annotation.importer.GenomicSequenceLoader;

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

			SnpomicsEngine.setProperty("fastapath", "/Volumes/Macintosh HD 2/GATK");
			
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
			factory.importData(new FileReader("/Users/dexzb9/Downloads/genes_GRCh37.txt"), rmd);*/
			factory.setGenome("GRCh37");
			/*ReferenceMetadata<?> rmd = new ReferenceMetadata<GenomicSequenceAnnotation>(GenomicSequenceAnnotation.class, "GRCh37", "");
			rmd.setSource("http://www.ncbi.nlm.nih.gov/projects/genome/assembly/grc/human/index.shtml");
			rmd.setUpdateDate(new Date());
			factory.importData(new StringReader("human_g1k_v37.fasta"), rmd);*/
			GenomicSequenceLoader loader = (GenomicSequenceLoader) factory.getLoader(GenomicSequenceAnnotation.class);
			getSequence(loader, "1:1456623-1456680");
			getSequence(loader, "11:1456623-1456780");
			getSequence(loader, "2:1456623");

			/*InputIterator input = new VCFReader(new BufferedReader(new FileReader(args[0])));
			VariantWriter output = new TsvWriter(new PrintWriter(new FileWriter(args[1])));
			//VariantWriter output = new TsvWriter(new PrintWriter(System.out));
			
			Map<String, OutputField> potentialFields = SnpomicsEngine.getAllowedOutput();
			
			List<OutputField> desiredAnnotations = new ArrayList<OutputField>();
			desiredAnnotations.add(potentialFields.get("Gene Name"));
			
			SnpomicsEngine.run(input, output, factory, desiredAnnotations);*/
			
			
/*			AnnotationFactory factory = new DummyFactory();
			factory.setGenome("dummy");*/
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	
	public static void getSequence(GenomicSequenceLoader loader, String pos) {
		GenomicSequenceAnnotation annot = loader.loadByID(pos);
		System.out.println(pos+" -> "+annot.getSequence());
	}
}
