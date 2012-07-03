package org.cchmc.bmi.snpomics.annotation.annotator;

import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.SimpleVariant;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.interactive.OverlappingGeneAnnotation;
import org.cchmc.bmi.snpomics.annotation.loader.TranscriptLoader;
import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;

public class OverlappingAnnotator implements
		Annotator<OverlappingGeneAnnotation> {

	@Override
	public List<OverlappingGeneAnnotation> annotate(SimpleVariant variant,
			AnnotationFactory factory) throws AnnotationNotFoundException {
		TranscriptLoader loader = (TranscriptLoader) factory.getLoader(TranscriptAnnotation.class);
		List<OverlappingGeneAnnotation> result = new ArrayList<OverlappingGeneAnnotation>();
		for (TranscriptAnnotation tx : loader.loadByOverlappingPosition(variant.getPosition()))
			result.add(new OverlappingGeneAnnotation(tx));
		return result;
	}

	@Override
	public Class<OverlappingGeneAnnotation> getAnnotationClass() {
		return OverlappingGeneAnnotation.class;
	}

}
