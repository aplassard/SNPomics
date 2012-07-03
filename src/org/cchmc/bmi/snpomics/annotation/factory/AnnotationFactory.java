package org.cchmc.bmi.snpomics.annotation.factory;

import java.io.Reader;
import java.util.List;
import java.util.Set;

import org.cchmc.bmi.snpomics.Genome;
import org.cchmc.bmi.snpomics.ReferenceMetadata;
import org.cchmc.bmi.snpomics.annotation.ReferenceAnnotation;
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
	 * Performs whatever tasks are necessary to create a new factory-specific data store
	 * @return true on success
	 */
	public boolean initializeEmptyBackend() { return true; }
	/**
	 * Retrieves all of the genomes that this Factory knows about (ie, are stored in the db)
	 * @return a list of genomes
	 */
	public abstract Set<Genome> getAvailableGenomes();
	/**
	 * Sets the genome build that this factory will use.  This method must be called 
	 * before any of the Loader or DefaultTable methods.
	 * @param genome the name of the genome to use.  Must be a member of {@link #getAvailableGenomes()}
	 */
	public abstract void setGenome(String genome);
	/**
	 * Returns detailed information about the currently selected genome
	 * @return null if no genome is set, otherwise a Genome
	 */
	public abstract Genome getGenome();
	/**
	 * Stores this genome as a new valid entry in the backend, and sets it as the current genome
	 */
	public abstract void createGenome(Genome newGenome);
	
	/**
	 * Returns an {@link AnnotationLoader} specific to the appropriate annotation in the current genome.
	 * The current "best" table of annotations will be chosen, control with {@link #setDefaultVersion(Class, String)}
	 * or {@link #getLoader(Class, String)}
	 * @param cls The class of annotation to load
	 * @return A suitable AnnotationLoader
	 * @throws AnnotationNotFoundException the requested annotation is not present in this store, or no 
	 * Loader suitable for this store exists
	 */
	public <T extends ReferenceAnnotation> AnnotationLoader<T> getLoader(Class<T> cls) throws AnnotationNotFoundException {
		return getLoader(cls, getDefaultVersion(cls).getVersion());
	}
	/**
	 * Returns an {@link AnnotationLoader} specific to the appropriate annotation in the current genome.
	 * The specified version of annotations will be used
	 * @param cls The class of annotation to load
	 * @param version The version of annotations to use
	 * @return A suitable AnnotationLoader
	 * @throws AnnotationNotFoundException the requested annotation is not present in this store, or no 
	 * Loader suitable for this store exists
	 */
	public abstract <T extends ReferenceAnnotation> AnnotationLoader<T> getLoader(Class<T> cls, String version) throws AnnotationNotFoundException;
	
	/**
	 * List all of the sources of this annotation in the current genome (ie, refseq vs ensembl
	 * gene models, versions of dbSNP)
	 * @param cls The class of annotation we're interested in
	 * @return A list (possibly empty) of available versions for this annotation
	 */
	public abstract <T extends ReferenceAnnotation> List<ReferenceMetadata<T>> getAvailableVersions(Class<T> cls);
	/**
	 * Returns the default Version, either specified by the genome or a previous call to
	 * {@link #setDefaultVersion(Class, String)}, that will be used in a call to
	 * {@link #getLoader(Class)}
	 * @param cls The class of annotation we're interested in
	 * @return The name of the "default" table for this annotation
	 */
	public abstract <T extends ReferenceAnnotation> ReferenceMetadata<T> getDefaultVersion(Class<T> cls);
	/**
	 * Sets the default version that will be used in calls to {@link #getLoader(Class)},
	 * overriding the defaults specified by the genome
	 * @param cls The class of annotation we're interested in
	 * @param version The name of the new default version to use
	 */
	public abstract void setDefaultVersion(Class<? extends ReferenceAnnotation> cls, String version);
	/**
	 * Functionally identical to {@link #setDefaultVersion(Class, String)}, except that the chosen
	 * version is made default in the backend, for everyone, until the end of time! (or at least until
	 * someone else invokes this method)
	 * @param cls
	 * @param version
	 */
	public abstract void makeVersionPermanentDefault(Class<? extends ReferenceAnnotation> cls, String version);
	
	/**
	 * Helper function to construct an Importer appropriate to the Annotation.  This is where Importers
	 * should be added to concrete factories
	 * @param ref The metadata associated with this annotation
	 * @return an Importer.  Duh.
	 */
	protected abstract <T extends ReferenceAnnotation> AnnotationImporter<T> getImporter(ReferenceMetadata<T> ref);
	/**
	 * loads reference annotations into the appropriate datastore.  The {@link ReferenceMetadata#isDefault()}
	 * flag is ignored (ie, always false) - set with {@link #makeVersionPermanentDefault(Class, String)} after
	 * importing
	 * @param input the actual annotations
	 * @param ref the metadata for the data to be imported
	 * @return true on success
	 */
	public boolean importData(Reader input, ReferenceMetadata<? extends ReferenceAnnotation> ref) {
		return getImporter(ref).importAnnotations(input);
	}
}
