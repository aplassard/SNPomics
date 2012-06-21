package org.cchmc.bmi.snpomics.util;

import java.util.List;

public class StringUtils {

	public static String join(String delim, List<String> fields) {
		StringBuilder sb = new StringBuilder();
		for (String s : fields) {
			sb.append(s);
			sb.append(delim);
		}
		//Remove the trailing delimiter
		sb.setLength(sb.length()-1);
		return sb.toString();
	}
}
