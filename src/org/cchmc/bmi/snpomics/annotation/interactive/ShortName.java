package org.cchmc.bmi.snpomics.annotation.interactive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The ShortName is used to specify {@link OutputField OutputFields} and should be short and descriptive, 
 * with no likely delimiters (tabs or commas).  Most likely place to see this is in column headers of a 
 * tab-separated file
 * @author dexzb9
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShortName {
	String value();
}
