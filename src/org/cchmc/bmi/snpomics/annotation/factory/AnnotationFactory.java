package org.cchmc.bmi.snpomics.annotation.factory;

import java.io.InputStream;
import java.util.List;

import org.cchmc.bmi.snpomics.annotation.Annotation;
import org.cchmc.bmi.snpomics.annotation.importer.AnnotationImporter;
import org.cchmc.bmi.snpomics.annotation.loader.AnnotationLoader;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;

/**
 * <p>An AnnotationFactory manages a particular type of database (ie SQL, NoSQL, flat files)
 * containing reference annotations.  Each database contains information about one or more
 * genomes, and each genome has zero or more "tables" containing information about each
 * annotation type.  These details are managed by the Factory.</p>
 * 
 * <p>When you want specific annotations, invoke the {@link #getLoader(Class)} method and use one
 * of the <code>loadBy*</code> methods on the resulting object.</p>
 * 
 * <p>A single factory object should be maintained for the lifetime of the program whenever possible
 * because of construction/destruction costs and to maintain consistency</p>
 * @author dexzb9
 *
 */
public abstract class AnnotationFactory {

	/**
	 * Retrieves all of the genomes that this Factory knows about (ie, is stored in the db)
	 * @return a list of genome names
	 */
	public abstract List<String> getAvailableGenomes();
	/**
	 * Sets the genome build that this factory will use.  This method must be called 
	 * before any of the Loader or DefaultTable methods.
	 * @param genome the name of the genome to use.  Must be a member of {@link #getAvailableGenomes()}
	 */
	public abstract void setGenome(String genome);
	
	/**
	 * Returns an {@link AnnotationLoader} specific to the appropriate annotation in the current genome.
	 * The current "best" table of annotations will be chosen, control with {@link #setDefaultTable(Class, String)}
	 * or {@link #getLoader(Class, String)}
	 * @param cls The class of annotation to load
	 * @return A suitable AnnotationLoader
	 * @throws AnnotationNotFoundException the requested annotation is not present in this store, or no 
	 * Loader suitable for this store exists
	 */
	public <T extends Annotation> AnnotationLoader<T> getLoader(Class<T> cls) throws AnnotationNotFoundException {
		return getLoader(cls, getDefaultTable(cls));
	}
	/**
	 * Returns an {@link AnnotationLoader} specific to the appropriate annotation in the current genome.
	 * The specified table of annotations will be used
	 * @param cls The class of annotation to load
	 * @param table The table of annotations to use
	 * @return A suitable AnnotationLoader
	 * @throws AnnotationNotFoundException the requested annotation is not present in this store, or no 
	 * Loader suitable for this store exists
	 */
	public abstract <T extends Annotation> AnnotationLoader<T> getLoader(Class<T> cls, String table) throws AnnotationNotFoundException;
	
	/**
	 * List all of the sources of this annotation in the current genome (ie, refseq vs ensembl
	 * gene models, versions of dbSNP)
	 * @param cls The class of annotation we're interested in
	 * @return A list (possibly empty) of table names that supply this annotation
	 */
	public abstract List<String> getAvailableTables(Class<? extends Annotation> cls);
	/**
	 * Returns the default table, either specified by the genome or a previous call to
	 * {@link #setDefaultTable(Class, String)}, that will be used in a call to
	 * {@link #getLoader(Class)}
	 * @param cls The class of annotation we're interested in
	 * @return The name of the "default" table for this annotation
	 */
	public abstract String getDefaultTable(Class<? extends Annotation> cls);
	/**
	 * Sets the default table that will be used in calls to {@link #getLoader(Class)},
	 * overriding the defaults specified by the genome
	 * @param cls The class of annotation we're interested in
	 * @param table The name of the new default table to use
	 */
	public abstract void setDefaultTable(Class<? extends Annotation> cls, String table);
	
	/**
	 * internal function to construct an Importer appropriate to the Annotation
	 * @param cls
	 * @return
	 */
	protected abstract <T extends Annotation> AnnotationImporter<T> getImporter(Class<T> cls);
	/**
	 * loads reference annotations into the appropriate datastore
	 * @param input the actual annotations
	 * @param table the table name (cf {@link AnnotationFactory#getAvailableTables(Class)}) to
	 * put the data in
	 * @param cls the type of annotation represented in the data
	 * @return true on success
	 */
	public abstract boolean importData(InputStream input, String table, Class<? extends Annotation> cls);
}
