package org.cchmc.bmi.snpomics.annotation;

import java.util.List;

public interface AnnotationFactory {

	List<String> getAvailableGenomes();
	<T extends Annotation> AnnotationLoader<T> getLoader(String genome, Class<T> cls);
	
	List<String> getAvailableLoaders(String genome, Class<? extends Annotation> cls);
	void setDefaultLoader(String genome, Class<? extends Annotation> cls, String loader);
}
