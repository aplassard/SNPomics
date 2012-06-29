package org.cchmc.bmi.snpomics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cchmc.bmi.snpomics.annotation.Annotate;
import org.cchmc.bmi.snpomics.annotation.AnnotationType;
import org.cchmc.bmi.snpomics.annotation.InteractiveAnnotation;
import org.cchmc.bmi.snpomics.annotation.ReferenceAnnotation;
import org.cchmc.bmi.snpomics.annotation.annotator.Annotator;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.writer.VariantWriter;
import org.reflections.Reflections;

public class SnpomicsEngine {
	public static void run(InputIterator input, VariantWriter output, AnnotationFactory factory, 
			List<OutputField> fields) throws AnnotationNotFoundException {
		
		//Initialize the output
		output.pairWithInput(input);
		output.writeHeaders(fields);

		//First find the non-redundant set of Annotations we want
		Set<Class<? extends InteractiveAnnotation>> annotSet = new HashSet<Class<? extends InteractiveAnnotation>>();
		for (OutputField f : fields) {
			annotSet.add(f.getDeclaringClass());
		}
		
		//Create and store the Annotators
		List<Annotator<? extends InteractiveAnnotation>> annotators = new ArrayList<Annotator<?>>();
		for (Class<? extends InteractiveAnnotation> ann : annotSet)
			annotators.add(Annotate.getAnnotator(ann, factory));
		
		//Cycle through the input
		while (input.next()) {
			Variant annotated = input.getVariant();
			//For each variant, annotate each allele with each annotation
			for (Annotator<? extends InteractiveAnnotation> ann : annotators) {
				for (int i=0; i<annotated.getAlt().size(); i++) {
					SimpleVariant sv = annotated.getSimpleVariant(i);
					annotated.addAnnotation(ann.getAnnotationClass(), ann.annotate(sv, factory), i);
				}
			}
			//And save the results
			output.writeVariant(annotated);
		}
		output.close();
	}
	
	/**
	 * Determines all of the available output fields (annotated methods of InteractiveAnnotations),
	 * and returns them in a Map with ShortNames as the keys
	 * @return
	 */
	public static Map<String, OutputField> getAllowedOutput() {
		Map<String, OutputField> result = new HashMap<String, OutputField>();
		Reflections reflections = new Reflections("org.cchmc.bmi.snpomics.annotation");
		for (Class<? extends InteractiveAnnotation> cls : reflections.getSubTypesOf(InteractiveAnnotation.class)) {
			for (Method meth : cls.getMethods()) {
				if (OutputField.isOutputField(meth)) {
					OutputField field = new OutputField(meth);
					if (result.containsKey(field.getShortName())) {
						throw new RuntimeException("Duplicate ShortNames: "+meth.getName()+" and "+
								result.get(field.getShortName()).getInternalName());
					}
					result.put(field.getShortName(), field);
				}
			}
		}
		return result;
	}
	
	public static Map<String, Class<? extends ReferenceAnnotation>> getAnnotations() {
		Map<String, Class<? extends ReferenceAnnotation>> result = new HashMap<String, Class<? extends ReferenceAnnotation>>();
		Reflections reflections = new Reflections("org.cchmc.bmi.snpomics.annotation");
		for (Class<? extends ReferenceAnnotation> cls : reflections.getSubTypesOf(ReferenceAnnotation.class)) {
			AnnotationType name = cls.getAnnotation(AnnotationType.class);
			if (name != null) {
				if (result.containsKey(name.value())) {
					throw new RuntimeException("Duplicate AnnotationTypes: "+cls.getCanonicalName()+
							" and "+result.get(name.value()).getCanonicalName());
				}
				result.put(name.value(), cls);
			}
		}
		return result;
	}
	
	public static String getProperty(String key) {
		return prop.getProperty(key);
	}
	public static String getProperty(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}
	public static Object setProperty(String key, String value) {
		return prop.setProperty(key, value);
	}
	public static Properties getProperties() {
		return prop;
	}
	
	private static final Properties prop = new Properties();
}
