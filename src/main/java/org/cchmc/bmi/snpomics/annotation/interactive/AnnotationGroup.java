package org.cchmc.bmi.snpomics.annotation.interactive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;

public class AnnotationGroup {
	private static Map<String, List<OutputField>> groups;
	public static Set<String> names() { return groups.keySet(); }
	public static List<OutputField> fields(String name) { return groups.get(name); }
	
	static {
		groups = new HashMap<String, List<OutputField>>();
		try {
			
			ArrayList<OutputField> def = new ArrayList<OutputField>();
			Class<?> cls = Class.forName("org.cchmc.bmi.snpomics.annotation.interactive.TranscriptEffectAnnotation");
			def.add(new OutputField(cls.getDeclaredMethod("getGeneName")));
			def.add(new OutputField(cls.getDeclaredMethod("getHgvsCdnaName")));
			def.add(new OutputField(cls.getDeclaredMethod("getHgvsProteinName")));
			groups.put("default", Collections.unmodifiableList(def));
			
		} catch (ClassNotFoundException e) {
			throw new SnpomicsException("Can't load class!", e);
		} catch (SecurityException e) {
			throw new SnpomicsException(e);
		} catch (NoSuchMethodException e) {
			throw new SnpomicsException("Can't load method!", e);
		}

		try {
			
			ArrayList<OutputField> near = new ArrayList<OutputField>();
			Class<?> cls = Class.forName("org.cchmc.bmi.snpomics.annotation.interactive.NearestTranscriptAnnotation");
			near.add(new OutputField(cls.getDeclaredMethod("closestGene")));
			near.add(new OutputField(cls.getDeclaredMethod("closestGeneDistance")));
			near.add(new OutputField(cls.getDeclaredMethod("closestGene2")));
			near.add(new OutputField(cls.getDeclaredMethod("closestGene2Distance")));
			groups.put("NearestGenes", Collections.unmodifiableList(near));
			
		} catch (ClassNotFoundException e) {
			throw new SnpomicsException("Can't load class!", e);
		} catch (SecurityException e) {
			throw new SnpomicsException(e);
		} catch (NoSuchMethodException e) {
			throw new SnpomicsException("Can't load method!", e);
		}
	}
}
