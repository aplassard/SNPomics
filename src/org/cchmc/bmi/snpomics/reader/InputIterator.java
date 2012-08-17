package org.cchmc.bmi.snpomics.reader;

import java.io.BufferedReader;
import java.util.Set;

import org.cchmc.bmi.snpomics.Variant;

/**
 * The base interface for all classes that read variants from some source.
 * 
 * <p>This is fundamentally similar to an {@link java.util.Iterator Iterator&lt;Variant&gt;},
 * but this interface allows subclasses to retrieve extra data at each position of the "cursor"</p>
 * @author dexzb9
 *
 */
public interface InputIterator {
	
	/**
	 * Sets the Reader to be used by this InputIterator
	 * @param input
	 */
	void setInput(BufferedReader input);
	
	/**
	 * Advances the cursor to the next variant
	 * @return true on success, false if no variants are left
	 */
	boolean next();
	
	/**
	 * Creates a variant from the current position in the file.  Does not move the cursor
	 * @return either a {@link Variant}, or null if there's an error (like the cursor hasn't moved yet)
	 */
	Variant getVariant();
	
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
	 * <p>The secondary extensions associated with this reader.  These are meant for the auto-detector
	 * to be able to identify file types, but can always be overridden by the user and therefore
	 * does not have to be exhaustive or authoritative.  For example, a TsvReader might return 'txt'
	 * as the preferredExtension and 'tsv' here.  An empty set should be returned if there
	 * are no secondary extensions</p>
	 * <p>Extensions should be lower case and not include the '.', and the preferredExtension should
	 * not be reported here.</p> 
	 * @return
	 */
	Set<String> allowedExtensions();
}
