package org.cchmc.bmi.snpomics.reader;

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
	 * Advances the cursor to the next variant
	 * @return true on success, false if no variants are left
	 */
	boolean next();
	
	/**
	 * Creates a variant from the current position in the file.  Does not move the cursor
	 * @return either a {@link Variant}, or null if there's an error (like the cursor hasn't moved yet)
	 */
	Variant getVariant();
}
