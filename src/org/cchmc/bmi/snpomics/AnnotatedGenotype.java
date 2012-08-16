package org.cchmc.bmi.snpomics;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Genotype plus some other stuff.  "Other stuff" is represented as a Map&lt;String,String>
 * @author dexzb9
 *
 */
public class AnnotatedGenotype extends Genotype {

	public AnnotatedGenotype() {
		data = new HashMap<String, String>();
	}
	
	public String setValue(String key, String value) {
		return data.put(key, value);
	}
	
	public String getValue(String key) {
		if (!data.containsKey(key))
			return null;
		return data.get(key);
	}
	
	private Map<String, String> data;
}
