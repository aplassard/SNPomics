package org.cchmc.bmi.snpomics.annotation;

import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;


/**
 * <p>Conceptually (though this is not currently codified in the object model) there are
 * two types of Annotations.  <b>Reference</b> annotations are some sort of genomic "truth", 
 * they don't intrinsically depend on a variation.  Examples include gene models, dbSNP, regions
 * of synteny, etc.  <b>Interactive</b> annotations exist because of the variation, and
 * they'll generally be dependent on one or more reference annotation.  Examples include
 * an HGVS-style RNA or Protein name.  I think there's nothing keeping an annotation from
 * being both, which is why I'm reluctant to enforce separation.  But I can't think of any
 * examples.</p>
 * 
 * <p>When adding new Annotations, the helper classes to write will depend on whether the
 * annotation is reference or interactive.  For reference annotations:</p>
 * <ul>
 *  <li>Create a new {@link AnnotationLoader} and {@link AnnotationImporter}, and add them to
 *  {@link AnnotationFactory#getLoader(Class, String)} and {@link AnnotationFactory#getImporter},
 *  respectively (rinse and repeat for each AnnotationFactory you want to support)</li>
 * </ul>
 * <p>Interactive annotations are simpler (as they are generated dynamically instead of stored in a db):</p>
 * <ul>
 *  <li>Create a new {@link Annotator} and add it to 
 *  {@link Annotate#getAnnotator(Class, AnnotationFactory)}.  Since Annotate is concrete, you
 *  only have to do it once</li>
 * </ul>
 * @author dexzb9
 *
 */
public interface Annotation {

	/**
	 * The name of the annotation is a (non-unique) human-readable string
	 */
	String getName();
	
	/**
	 * The ID of an annotation must be unique, might or might not be human-readable
	 */
	String getID();
	
}
