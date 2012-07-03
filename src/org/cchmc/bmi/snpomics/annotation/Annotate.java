package org.cchmc.bmi.snpomics.annotation;

import java.util.Collections;
import java.util.List;

import org.cchmc.bmi.snpomics.SimpleVariant;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.annotation.annotator.Annotator;
import org.cchmc.bmi.snpomics.annotation.annotator.DummyAnnotator;
import org.cchmc.bmi.snpomics.annotation.annotator.OverlappingAnnotator;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.interactive.DummyAnnotation;
import org.cchmc.bmi.snpomics.annotation.interactive.InteractiveAnnotation;
import org.cchmc.bmi.snpomics.annotation.interactive.OverlappingGeneAnnotation;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;

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
	public static Annotator<? extends InteractiveAnnotation> getAnnotator(Class<? extends InteractiveAnnotation> cls, AnnotationFactory factory) {
		if (cls == DummyAnnotation.class)
			return new DummyAnnotator();
		if (cls == OverlappingGeneAnnotation.class)
			return new OverlappingAnnotator();
		return null;
	}
	
	/**
	 * A convenience method to directly calculate annotations.  Large-scale annotation
	 * efforts should instead use getAnnotator and call annotate() directly on those, to
	 * save the expense of looking up and constructing Annotators
	 * @param variant to be annotated
	 * @param cls annotation
	 * @param factory
	 * @return the result of {@link Annotator#annotate(Variant, AnnotationFactory)}, or an empty list
	 * if no Annotator could be created
	 * @throws AnnotationNotFoundException a required reference annotation could not be loaded
	 */
	public static List<? extends InteractiveAnnotation> annotate(SimpleVariant variant, Class<? extends InteractiveAnnotation> cls, 
			AnnotationFactory factory) throws AnnotationNotFoundException {
		Annotator<? extends InteractiveAnnotation> annotator = getAnnotator(cls, factory);
		if (annotator == null)
			return Collections.emptyList();
		return annotator.annotate(variant, factory);
	}
}
