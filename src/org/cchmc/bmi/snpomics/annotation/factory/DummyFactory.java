package org.cchmc.bmi.snpomics.annotation.factory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cchmc.bmi.snpomics.annotation.Annotation;
import org.cchmc.bmi.snpomics.annotation.importer.AnnotationImporter;
import org.cchmc.bmi.snpomics.annotation.loader.AnnotationLoader;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;
import org.cchmc.bmi.snpomics.exception.GenomeNotSetException;
import org.cchmc.bmi.snpomics.exception.UnknownGenomeException;

public class DummyFactory extends AnnotationFactory {
	private Set<String> validGenomes;
	private String currentGenome;
	public DummyFactory() {
		validGenomes = new HashSet<String>();
		validGenomes.add("dummy");
		currentGenome = null;
	}

	@Override
	public List<String> getAvailableGenomes() {
		return new ArrayList<String>(validGenomes);
	}

	@Override
	public void setGenome(String genome) {
		if (!validGenomes.contains(genome))
			throw new UnknownGenomeException();
		currentGenome = genome;
	}

	@Override
	public <T extends Annotation> AnnotationLoader<T> getLoader(Class<T> cls,
			String table) throws AnnotationNotFoundException {
		if (currentGenome == null)
			throw new GenomeNotSetException();
		//No reference annotations!
		throw new AnnotationNotFoundException();
	}

	@Override
	public List<String> getAvailableTables(Class<? extends Annotation> cls) {
		return Collections.emptyList();
	}

	@Override
	public String getDefaultTable(Class<? extends Annotation> cls) {
		return null;
	}

	@Override
	public void setDefaultTable(Class<? extends Annotation> cls, String table) {
	}

	@Override
	protected <T extends Annotation> AnnotationImporter<T> getImporter(
			Class<T> cls) {
		return null;
	}

	@Override
	public boolean importData(InputStream input, String table,
			Class<? extends Annotation> cls) {
		return false;
	}

}
