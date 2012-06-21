package org.cchmc.bmi.snpomics.writer;

import java.util.List;

import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.Variant;
import org.cchmc.bmi.snpomics.reader.InputIterator;

/**
 * The base interface for output classes.  Major functionality that should be implemented
 * include the ability to pair with an InputIterator (to process information from specific 
 * subtypes, if possible) and the ability to process metadata from Annotations (ie, for column headers)
 * @author dexzb9
 *
 */
public interface VariantWriter {
	void pairWithInput(InputIterator input);
	void writeHeaders(List<OutputField> fields);
	void writeVariant(Variant annotatedVariant);
	void close();
}
