package org.cchmc.bmi.snpomics.annotation;

import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.annotation.annotator.Annotator;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;

/**
 * A factory class that provides {@link Annotator}s appropriate to {@link Annotation}s
 * @author dexzb9
 *
 */
public class Annotate {

	/**
	 * The actual workhorse that creates the Annotator
	 * @param cls the desired annotation
	 * @param factory a factory that provides access to other reference annotations
	 * @return an Annotator<T>, or null if none exist for the requested Annotation
	 */
	public static <T extends Annotation> Annotator<T> getAnnotator(Class<T> cls, AnnotationFactory factory) {
		return null;
	}
	
	/**
	 * A convenience class to directly annotate a variant.  Large-scale annotation
	 * efforts should instead use getAnnotator and call annotate() directly on those, to
	 * save the expense of looking up and constructing Annotators
	 * @param variant to be annotated
	 * @param cls annotation
	 * @param factory
	 * @return the result of {@link Annotator#annotate(Variant, AnnotationFactory)}, or 0 if no 
	 * Annotator could be created
	 */
	public static int annotate(Variant variant, Class<? extends Annotation> cls, AnnotationFactory factory) {
		Annotator<?> annotator = getAnnotator(cls, factory);
		if (annotator == null)
			return 0;
		return annotator.annotate(variant, factory);
	}
}
