package org.cchmc.bmi.snpomics.writer;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.reader.InputIterator;

/**
 * The base interface for output classes.  Major functionality that should be implemented
 * include the ability to pair with an InputIterator (to process information from specific 
 * subtypes, if possible) and the ability to process metadata from Annotations (ie, for column headers)
 * @author dexzb9
 *
 */
public interface VariantWriter {
	void setOutput(PrintWriter writer);
	void pairWithInput(InputIterator input);
	void writeHeaders(List<OutputField> fields);
	void writeVariant(Variant annotatedVariant);
	void close();
	
	/**
	 * This method provides a mechanism for individual writers to specify their own custom options.
	 * For instance, a VCF file doesn't have to contain genotypes, so we might want to expose an
	 * option to the user to suppress genotypes (in the interest of speed or size, for instance).
	 * But such an option wouldn't make sense for file formats that either must have or cannot
	 * have genotypes
	 * @param param
	 */
	void setDynamicParameters(Map<String, String> param);
	
	/**
	 * Allows an engine to discover the DynamicParameters this writer recognizes.  The returned
	 * Map should include the parameter itself as the key and a short, one-sentence description
	 * as the value
	 * @return
	 */
	Map<String, String> getAvailableParameters();

	/**
	 * The name of the reader, specified, eg, on the command line.  Probably, but not necessarily,
	 * related to the extension of the files it reads
	 * @return
	 */
	String name();
	
	/**
	 * A short description of the reader - what file types does it read?
	 * @return
	 */
	String description();
	
	/**
	 * The file extension (lower case, no '.') most closely associated with this reader.  If there is
	 * no single extension strongly associated, should return null here and all the possibilities
	 * in allowedExtensions
	 * @return
	 */
	String preferredExtension();
	
	/**
	 * <p>The secondary extensions associated with this writer.  These are meant for the auto-detector
	 * to be able to identify file types, but can always be overridden by the user and therefore
	 * does not have to be exhaustive or authoritative.  For example, a TsvWriter might return 'txt'
	 * as the preferredExtension and 'tsv' here.  An empty set should be returned if there
	 * are no secondary extensions</p>
	 * <p>Extensions should be lower case and not include the '.', and the preferredExtension should
	 * not be reported here.</p> 
	 * @return
	 */
	Set<String> allowedExtensions();
}
