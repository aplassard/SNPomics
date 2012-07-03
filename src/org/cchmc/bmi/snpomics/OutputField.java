package org.cchmc.bmi.snpomics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cchmc.bmi.snpomics.annotation.interactive.Abbreviation;
import org.cchmc.bmi.snpomics.annotation.interactive.Description;
import org.cchmc.bmi.snpomics.annotation.interactive.InteractiveAnnotation;
import org.cchmc.bmi.snpomics.annotation.interactive.ShortName;

public class OutputField {

	public OutputField(Method source) {
		method = source;
	}
	
	public String getAbbreviation() {
		Abbreviation abbrev = method.getAnnotation(Abbreviation.class);
		if (abbrev == null)
			return "N/A";
		return abbrev.value();
	}
	
	public String getShortName() {
		ShortName name = method.getAnnotation(ShortName.class);
		if (name == null)
			return "None";
		return name.value();
	}
	
	public String getDescription() {
		Description desc = method.getAnnotation(Description.class);
		if (desc == null)
			return "No description";
		return desc.value();
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends InteractiveAnnotation> getDeclaringClass() {
		return (Class<? extends InteractiveAnnotation>) method.getDeclaringClass();
	}
	
	public String getOutput(InteractiveAnnotation annot) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return method.invoke(annot).toString();
	}
	
	public String getInternalName() {
		return method.getName();
	}
	
	public static boolean isOutputField(Method toCheck) {
		return toCheck.isAnnotationPresent(ShortName.class);
	}
	
	private Method method;
}
