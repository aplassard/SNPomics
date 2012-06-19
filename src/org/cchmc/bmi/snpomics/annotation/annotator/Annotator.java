package org.cchmc.bmi.snpomics.annotation.annotator;

import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.annotation.Annotation;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;

/**
 * An object which knows how to annotate variants with a specific Annotation
 * @author dexzb9
 */
public interface Annotator<T extends Annotation> {

	/**
	 * Adds zero or more annotations to a variant
	 * @param variant The variant to be annotated
	 * @param factory A valid factory that can be used to look up relevant reference annotations
	 * @return the number of annotations actually written
	 */
	int annotate(Variant variant, AnnotationFactory factory);
}
