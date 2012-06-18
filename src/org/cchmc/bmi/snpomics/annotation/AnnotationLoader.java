package org.cchmc.bmi.snpomics.annotation;

import java.util.List;

public interface AnnotationLoader<T extends Annotation> {

	T loadByID(String id);
	List<T> loadByName(String name);
}
