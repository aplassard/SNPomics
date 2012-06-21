package org.cchmc.bmi.snpomics.annotation.annotator;

import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.SimpleVariant;
import org.cchmc.bmi.snpomics.annotation.DummyAnnotation;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;

public class DummyAnnotator implements Annotator<DummyAnnotation> {

	@Override
	public List<DummyAnnotation> annotate(SimpleVariant variant,
			AnnotationFactory factory) throws AnnotationNotFoundException {
		List<DummyAnnotation> result = new ArrayList<DummyAnnotation>();
		DummyAnnotation annot = new DummyAnnotation();
		StringBuilder sb = new StringBuilder();
		sb.append(variant.getPosition().getChromosome());
		sb.append("_");
		sb.append(variant.getPosition().getStart());
		sb.append("_");
		sb.append(variant.getRef());
		sb.append("->");
		sb.append(variant.getAlt());
		annot.setValue(sb.toString());
		result.add(annot);
		return result;
	}

}
