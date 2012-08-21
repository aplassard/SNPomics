package org.cchmc.bmi.snpomics;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.cchmc.bmi.snpomics.exception.UncheckedSnpomicsException;

public class Genome {
	
	public Genome(String name, String organism, int taxId, String source) {
		this.name = name;
		this.organism = organism;
		this.taxId = taxId;
		try {
			this.source = new URI(source);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			this.source = null;
		}
		this.transID = 1;
		this.altTransID = 1;
		this.altTransChrom = new HashSet<String>();
	}

	public String getName() {
		return name;
	}
	public String getOrganism() {
		return organism;
	}
	public int getTaxId() {
		return taxId;
	}
	public URI getSourceLink() {
		return source;
	}
	public int getTransTableId() {
		return transID;
	}
	public int getAltTransTableId() {
		return altTransID;
	}
	public Set<String> getAltTransChromosomes() {
		return altTransChrom;
	}
	public int getTransTableId(String chromosome) {
		if (altTransChrom.contains(chromosome))
			return altTransID;
		return transID;
	}
	
	public void setTransTableId(int id) {
		transID = id;
	}
	public void setAltTransTableId(int id) {
		altTransID = id;
	}
	public void addAltTransChromosome(String chrom) {
		altTransChrom.add(chrom);
	}
	
	public URI getTaxLink() {
		try {
			return taxURI.resolve(new URI(null, null, null, "id="+taxId+"&lvl=0", null));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return taxURI;
		}
	}
	public URI getTransLink() {
		return translateURI.resolve("#SG"+transID);
	}
	public URI getAltTransLink() {
		return translateURI.resolve("#SG"+altTransID);
	}
	
	@Override
	final public boolean equals(Object otherObject) {
		if (this == otherObject) return true;
		if (otherObject == null) return false;
		if (!(otherObject instanceof Genome)) return false;
		Genome other = (Genome)otherObject;
		return name.equals(other.name);
	}
	
	@Override
	final public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getName()+"["+name+"]";
	}
	
	public static String getReferencePath() {
		String path = SnpomicsEngine.getProperty("fastapath");
		if (path == null)
			throw new UncheckedSnpomicsException("fastapath not specified");
		return path;
	}
	
	public static File getReferenceDirectory() {
		return new File(getReferencePath());
	}
	
	private final String name;
	private final String organism;
	private final int taxId;
	private URI source;
	private int transID;
	private int altTransID;
	private Set<String> altTransChrom;
	
	private static final URI taxURI;
	private static final URI translateURI;
	
	static {
		taxURI = URI.create("http://ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cg");
		translateURI = URI.create("http://ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/index.cgi?chapter=cgencodes");
	}
}
