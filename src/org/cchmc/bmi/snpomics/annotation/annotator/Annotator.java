package org.cchmc.bmi.snpomics.annotation.annotator;

import java.util.List;

import org.cchmc.bmi.snpomics.SimpleVariant;
import org.cchmc.bmi.snpomics.annotation.InteractiveAnnotation;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;

/**
 * An object which knows how to annotate variants with a specific Annotation
 * @author dexzb9
 */
public interface Annotator<T extends InteractiveAnnotation> {

	/**
	 * Calculates "interactive" annotations appropriate to a variant
	 * @param variant The variant to be annotated
	 * @param factory A valid factory that can be used to look up relevant reference annotations
	 * @return the list of annotations
	 * @throws AnnotationNotFoundException a required reference annotation could not be loaded
	 */
	List<T> annotate(SimpleVariant variant, AnnotationFactory factory) throws AnnotationNotFoundException;
	
	/**
	 * Exposes the class of the Annotations we create
	 * @return
	 */
	Class<T> getAnnotationClass();
}
