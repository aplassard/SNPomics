package org.cchmc.bmi.snpomics.annotation.annotator;

import java.util.List;

import org.cchmc.bmi.snpomics.SimpleVariant;
import org.cchmc.bmi.snpomics.annotation.Annotation;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;

/**
 * An object which knows how to annotate variants with a specific Annotation
 * @author dexzb9
 */
public interface Annotator<T extends Annotation> {

	/**
	 * Calculates "interactive" annotations appropriate to a variant
	 * @param variant The variant to be annotated
	 * @param factory A valid factory that can be used to look up relevant reference annotations
	 * @return the list of annotations
	 */
	List<T> annotate(SimpleVariant variant, AnnotationFactory factory);
}
