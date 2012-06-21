package org.cchmc.bmi.snpomics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intended for Annotations, the Abbreviation should be unique among annotations and contain no
 * whitespace or special characters.  Most likely place to see this is in a VCF file
 * @author dexzb9
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Abbreviation {
	String value();
}
