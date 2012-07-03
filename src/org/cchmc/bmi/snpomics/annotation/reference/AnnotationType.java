package org.cchmc.bmi.snpomics.annotation.reference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cchmc.bmi.snpomics.SnpomicsEngine;

/**
 * Intended for {@link ReferenceAnnotation ReferenceAnnotations}, the AnnotationType is the label
 * to use for the annotation any time you talk to the user (as opposed to the Class, which is
 * what you use in the program).  {@link SnpomicsEngine#getAnnotations()} will construct a map for you
 * @author dexzb9
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationType {
	String value();
}
