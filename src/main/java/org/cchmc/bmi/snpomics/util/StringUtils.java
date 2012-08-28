package org.cchmc.bmi.snpomics.util;

import java.util.Collection;

public class StringUtils {

	public static String join(String delim, Collection<?> fields) {
		StringBuilder sb = new StringBuilder();
		if (fields != null)
			for (Object s : fields) {
				sb.append(s);
				sb.append(delim);
			}
		//Remove the trailing delimiter
		if (sb.length() > 0)
			sb.setLength(sb.length()-1);
		return sb.toString();
	}
}
