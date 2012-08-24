package org.cchmc.bmi.snpomics;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cchmc.bmi.snpomics.annotation.Annotate;
import org.cchmc.bmi.snpomics.annotation.annotator.Annotator;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.interactive.InteractiveAnnotation;
import org.cchmc.bmi.snpomics.annotation.reference.AnnotationType;
import org.cchmc.bmi.snpomics.annotation.reference.ReferenceAnnotation;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.writer.VariantWriter;
import org.reflections.Reflections;

public class SnpomicsEngine {
	public static void run(InputIterator input, VariantWriter output, AnnotationFactory factory, 
			List<OutputField> fields)  {
		
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
		Reflections reflections = new Reflections("org.cchmc.bmi.snpomics.annotation.interactive");
		for (Class<? extends InteractiveAnnotation> cls : reflections.getSubTypesOf(InteractiveAnnotation.class)) {
			for (Method meth : cls.getMethods()) {
				if (OutputField.isOutputField(meth)) {
					OutputField field = new OutputField(meth);
					if (result.containsKey(field.getName())) {
						throw new SnpomicsException("Duplicate OutputField Names: "+meth.getName()+" and "+
								result.get(field.getName()).getInternalName());
					}
					result.put(field.getName(), field);
				}
			}
		}
		return result;
	}
	
	public static Map<String, Class<? extends ReferenceAnnotation>> getAnnotations() {
		Map<String, Class<? extends ReferenceAnnotation>> result = new HashMap<String, Class<? extends ReferenceAnnotation>>();
		Reflections reflections = new Reflections("org.cchmc.bmi.snpomics.annotation.reference");
		for (Class<? extends ReferenceAnnotation> cls : reflections.getSubTypesOf(ReferenceAnnotation.class)) {
			AnnotationType name = cls.getAnnotation(AnnotationType.class);
			if (name != null) {
				if (result.containsKey(name.value())) {
					throw new SnpomicsException("Duplicate AnnotationTypes: "+cls.getCanonicalName()+
							" and "+result.get(name.value()).getCanonicalName());
				}
				result.put(name.value(), cls);
			}
		}
		return result;
	}
	
	public static Map<String, Class<? extends InputIterator>> getReaders() {
		Map<String, Class<? extends InputIterator>> result = new HashMap<String, Class<? extends InputIterator>>();
		Reflections reflections = new Reflections("org.cchmc.bmi.snpomics.reader");
		for (Class<? extends InputIterator> cls : reflections.getSubTypesOf(InputIterator.class)) {
			if (!cls.isInterface()) {
				try {
					String name = cls.newInstance().name();
					Class<?> oldVal = result.put(name, cls);
					if (oldVal != null)
						throw new SnpomicsException("Duplicate InputIterator names: "+oldVal.getCanonicalName()+
								" and "+cls.getCanonicalName());
				} catch (Exception e) {
					throw new SnpomicsException("Can't instantiate InputIterator", e);
				}
			}
		}
		return result;
	}
	
	public static InputIterator getBestInputIteratorForFile(File toRead) {
		InputIterator result = null;
		String filename = toRead.getName();
		int extStart = filename.lastIndexOf('.');
		//If there's no extension, we can't guess
		if (extStart < 0)
			return null;
		String extension = filename.substring(extStart+1).toLowerCase();
		boolean isPreferredProvider = false;
		boolean multipleSecondaries = false;
		Reflections reflections = new Reflections("org.cchmc.bmi.snpomics.reader");
		for (Class<? extends InputIterator> cls : reflections.getSubTypesOf(InputIterator.class)) {
			if (!cls.isInterface()) {
				try {
					InputIterator interim = cls.newInstance();
					if (interim.preferredExtension().equals(extension)) {
						if (result == null || !isPreferredProvider) {
							result = interim;
							isPreferredProvider = true;
							multipleSecondaries = false;
						} else {
							//Multiple InputIterators list this extension as preferred!  Can't choose!
							return null;
						}
					}
					//Only need to check the secondaries if we haven't found a primary
					if (!isPreferredProvider && interim.allowedExtensions().contains(extension)) {
						//If we've found multiple secondaries, note that.  If we don't eventually
						//see a primary, we won't be able to choose
						if (result == null)
							result = interim;
						else
							multipleSecondaries = true;
					}
				} catch (Exception e) {
					throw new SnpomicsException("Can't instantiate InputIterator", e);
				}
			}
		}
		//If something is stored in result, make sure it's not one of several equals
		if (multipleSecondaries)
			return null;
		return result;
	}

	public static Map<String, Class<? extends VariantWriter>> getWriters() {
		Map<String, Class<? extends VariantWriter>> result = new HashMap<String, Class<? extends VariantWriter>>();
		Reflections reflections = new Reflections("org.cchmc.bmi.snpomics.writer");
		for (Class<? extends VariantWriter> cls : reflections.getSubTypesOf(VariantWriter.class)) {
			if (!cls.isInterface()) {
				try {
					String name = cls.newInstance().name();
					Class<?> oldVal = result.put(name, cls);
					if (oldVal != null)
						throw new RuntimeException("Duplicate VariantWriter names: "+oldVal.getCanonicalName()+
								" and "+cls.getCanonicalName());
				} catch (Exception e) {
					throw new SnpomicsException("Can't instantiate VariantWriter", e);
				}
			}
		}
		return result;
	}
	
	public static VariantWriter getBestVariantWriterForFile(File toWrite) {
		VariantWriter result = null;
		String filename = toWrite.getName();
		int extStart = filename.lastIndexOf('.');
		//If there's no extension, we can't guess
		if (extStart < 0)
			return null;
		String extension = filename.substring(extStart+1).toLowerCase();
		boolean isPreferredProvider = false;
		boolean multipleSecondaries = false;
		Reflections reflections = new Reflections("org.cchmc.bmi.snpomics.writer");
		for (Class<? extends VariantWriter> cls : reflections.getSubTypesOf(VariantWriter.class)) {
			if (!cls.isInterface()) {
				try {
					VariantWriter interim = cls.newInstance();
					if (interim.preferredExtension().equals(extension)) {
						if (result == null || !isPreferredProvider) {
							result = interim;
							isPreferredProvider = true;
							multipleSecondaries = false;
						} else {
							//Multiple VariantWriters list this extension as preferred!  Can't choose!
							return null;
						}
					}
					//Only need to check the secondaries if we haven't found a primary
					if (!isPreferredProvider && interim.allowedExtensions().contains(extension)) {
						//If we've found multiple secondaries, note that.  If we don't eventually
						//see a primary, we won't be able to choose
						if (result == null)
							result = interim;
						else
							multipleSecondaries = true;
					}
				} catch (Exception e) {
					throw new SnpomicsException("Can't instantiate VariantWriter", e);
				}
			}
		}
		//If something is stored in result, make sure it's not one of several equals
		if (multipleSecondaries)
			return null;
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
