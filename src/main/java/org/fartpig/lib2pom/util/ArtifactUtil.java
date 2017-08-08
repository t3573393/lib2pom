package org.fartpig.lib2pom.util;

public class ArtifactUtil {

	public static boolean isVersionNew(String versionA, String versionB) {
		// just hack this
		return versionA.compareTo(versionB) > 0;
	}
}
