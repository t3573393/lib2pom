package org.fartpig.lib2pom.util;

import java.util.Objects;
import java.util.StringJoiner;

public class StringUtil {

	public static String join(CharSequence delimiter, CharSequence... elements) {
		Objects.requireNonNull(delimiter);
		Objects.requireNonNull(elements);
		// Number of elements not likely worth Arrays.stream overhead.
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence cs : elements) {
			joiner.add(cs);
		}
		return joiner.toString();
	}

	public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
		Objects.requireNonNull(delimiter);
		Objects.requireNonNull(elements);
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence cs : elements) {
			joiner.add(cs);
		}
		return joiner.toString();
	}
}
