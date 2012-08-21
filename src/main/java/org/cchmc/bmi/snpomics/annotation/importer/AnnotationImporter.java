package org.cchmc.bmi.snpomics.annotation.importer;

import java.io.Reader;

import org.cchmc.bmi.snpomics.annotation.reference.ReferenceAnnotation;

/**
 * Loads annotations into a datastore (ie, sql database).  This is a very simple interface,
 * the expectation is that store-specific attributes (ie, table name) and access will
 * be handled by subclass-dependent methods and through the appropriate AnnotationFactory
 * @author dexzb9
 *
 * @param <T>
 */
public interface AnnotationImporter<T extends ReferenceAnnotation> {

	/**
	 * The method that actually reads data from input and writes it to the datastore
	 * @param input the actual data to import
	 * @return true on success
	 */
	boolean importAnnotations(Reader input);
}
