package org.cchmc.bmi.snpomics.annotation.interactive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cchmc.bmi.snpomics.annotation.reference.ReferenceAnnotation;

/**
 * The MetaAnnotation provides annotations about InteractiveAnnotations.  Information provided
 * here includes display information (like a name/description) and selection information
 * (name/group)
 * @author dexzb9
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaAnnotation {
	/**
	 * A short (one word) name for this annotation.  By convention, should not include
	 * whitespace and should be in CamelCase if necessary
	 */
	String name();

	/**
	 * A one sentence description
	 */
	String description();

	/**
	 * The ReferenceAnnotations required to calculate this annotation
	 */
	Class<? extends ReferenceAnnotation>[] ref() default {};
}
