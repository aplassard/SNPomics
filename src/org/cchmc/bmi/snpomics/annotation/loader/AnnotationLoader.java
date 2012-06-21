package org.cchmc.bmi.snpomics.annotation.loader;

import java.util.List;

import org.cchmc.bmi.snpomics.annotation.ReferenceAnnotation;
import org.cchmc.bmi.snpomics.annotation.importer.AnnotationImporter;

/**
 * An interface to the underlying data store that can retrieve specific annotations.
 * This should be very lightweight, the expectation is that these will be created/destroyed
 * freely.
 * 
 * The interface that is used to, for example, populate a database of reference annotations
 * is {@link AnnotationImporter}, not AnnotationLoader
 * @author dexzb9
 */
public interface AnnotationLoader<T extends ReferenceAnnotation> {

	/**
	 * Loads a single Annotation based on its unique ID
	 */
	T loadByID(String id);
	
	/**
	 * Load (potentially several) Annotation(s) based on their name
	 */
	List<T> loadByName(String name);
}
