package org.cchmc.bmi.snpomics.annotation.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cchmc.bmi.snpomics.Genome;
import org.cchmc.bmi.snpomics.ReferenceMetadata;
import org.cchmc.bmi.snpomics.annotation.importer.AnnotationImporter;
import org.cchmc.bmi.snpomics.annotation.loader.AnnotationLoader;
import org.cchmc.bmi.snpomics.annotation.reference.ReferenceAnnotation;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;
import org.cchmc.bmi.snpomics.exception.GenomeNotSetException;
import org.cchmc.bmi.snpomics.exception.UnknownGenomeException;

public class DummyFactory extends AnnotationFactory {
	private Map<String, Genome> validGenomes;
	private String currentGenome;
	public DummyFactory() {
		validGenomes = new HashMap<String, Genome>();
		validGenomes.put("dummy", new Genome("dummy", "Dum dummicus", 0, "http://localhost"));
		currentGenome = null;
	}

	@Override
	public Set<Genome> getAvailableGenomes() {
		return new HashSet<Genome>(validGenomes.values());
	}

	@Override
	public void setGenome(String genome) {
		if (!validGenomes.containsKey(genome))
			throw new UnknownGenomeException();
		currentGenome = genome;
	}

	@Override
	public void createGenome(Genome newGenome) {
		validGenomes.put(newGenome.getName(), newGenome);
		currentGenome = newGenome.getName();
	}
	
	@Override
	public Genome getGenome() {
		if (currentGenome == null)
			return null;
		return validGenomes.get(currentGenome);
	}

	@Override
	public <T extends ReferenceAnnotation> AnnotationLoader<T> getLoader(Class<T> cls,
			String version) throws AnnotationNotFoundException {
		if (currentGenome == null)
			throw new GenomeNotSetException();
		//No reference annotations!
		throw new AnnotationNotFoundException();
	}

	@Override
	public <T extends ReferenceAnnotation> List<ReferenceMetadata<T>> getAvailableVersions(Class<T> cls) {
		return Collections.emptyList();
	}

	@Override
	public <T extends ReferenceAnnotation> ReferenceMetadata<T> getDefaultVersion(Class<T> cls) {
		return null;
	}

	@Override
	public void setDefaultVersion(Class<? extends ReferenceAnnotation> cls, String version) {
	}

	@Override
	protected <T extends ReferenceAnnotation> AnnotationImporter<T> getImporter(
			ReferenceMetadata<T> ref) {
		return null;
	}

	@Override
	public void makeVersionPermanentDefault(
			Class<? extends ReferenceAnnotation> cls, String version) {
	}

}
