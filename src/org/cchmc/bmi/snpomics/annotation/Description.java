package org.cchmc.bmi.snpomics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intended for Annotations, the Description should be short (one sentence) and suitable for 
 * display to the user 
 * @author dexzb9
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
	String value();
}
