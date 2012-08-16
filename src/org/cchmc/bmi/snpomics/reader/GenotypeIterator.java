package org.cchmc.bmi.snpomics.reader;

import java.util.List;

import org.cchmc.bmi.snpomics.Genotype;

/**
 * An InputIterator that can also parse genotypes for each sample
 * @author dexzb9
 *
 */
public interface GenotypeIterator extends InputIterator {
	
	/**
	 * <p>Not every input format that can include genotypes is required to include them.  
	 * The most obvious example is a sites-only VCF file.  GenotypeImporters specify
	 * through this method whether the file they are parsing actually includes
	 * genotypes.</p>
	 * <p>If this method returns false, both getSamples and getGenotypes
	 * should return empty lists (ie, not exceptions!).  If a derived class unconditionally
	 * returns false, it should implement the InputIterator interface and not this one</p>
	 * @return
	 */
	boolean hasGenotypes();
	
	/**
	 * The (ordered) list of sample names.  This list should be constant for an entire
	 * file, it is not necessary for Readers to query it for each input value
	 * @return
	 */
	List<String> getSamples();
	
	/**
	 * The list of genotypes for the current "line" of input, in an order that corresponds to
	 * {@link #getSamples()}
	 * @return
	 */
	List<? extends Genotype> getGenotypes();
}
