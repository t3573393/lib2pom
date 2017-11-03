package org.fartpig.lib2pom.util;

public class StringUtil {

	public static String join(CharSequence delimiter, CharSequence... elements) {
		if (delimiter == null || delimiter.length() == 0) {
			return "";
		}

		if (elements == null) {
			return "";
		}

		// Number of elements not likely worth Arrays.stream overhead.
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (CharSequence cs : elements) {
			if (!isFirst) {
				sb.append(delimiter);
			}
			sb.append(cs);
			if (isFirst) {
				isFirst = false;
			}
		}
		return sb.toString();
	}

	public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
		if (delimiter == null || delimiter.length() == 0) {
			return "";
		}

		if (elements == null) {
			return "";
		}

		// Number of elements not likely worth Arrays.stream overhead.
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (CharSequence cs : elements) {
			if (!isFirst) {
				sb.append(delimiter);
			}
			sb.append(cs);
			if (isFirst) {
				isFirst = false;
			}
		}
		return sb.toString();
	}
}
