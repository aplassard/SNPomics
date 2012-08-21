package org.cchmc.bmi.snpomics.annotation.interactive;

import org.cchmc.bmi.snpomics.annotation.Annotate;

/**
 * <p>An InteractiveAnnotation is one that "interacts" with a Variant -
 * that is, its very existence is dependent on a Variant.  InteractiveVariants
 * have one or more pieces of data that can be potentially output.  For instance,
 * if the InteractiveAnnotation is looking for the nearest gene, we likely want to
 * know the name of the gene and how far away it is, but we don't want two different
 * Annotations for that (as that would mean finding the nearest gene twice for each variant)</p>
 * <p>Methods that are intended to be output must be public, take no parameters, and return a 
 * String.  In addition, they must be annotated (in the Java sense) with
 * {@link ShortName} (which must be unique across all output fields), {@link Abbreviation}, and
 * {@link Description}</p>
 * <p>When creating an InteractiveAnnotation, you must also create an {@link Annotator} and add it to the
 * factory ({@link Annotate})</p>
 * @author dexzb9
 *
 */
public interface InteractiveAnnotation {

}
