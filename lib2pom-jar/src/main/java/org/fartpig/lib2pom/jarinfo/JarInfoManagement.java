package org.fartpig.lib2pom.jarinfo;

import org.fartpig.lib2pom.constant.GlobalConfig;
import org.fartpig.lib2pom.util.ToolException;

public final class JarInfoManagement {

	private static JarArtifactInfo jarArtifactInfo;
	private static JarFileInfo jarFileInfo;

	public static JarArtifactInfo getJarArtifactInfo() {
		if (jarArtifactInfo != null) {
			return jarArtifactInfo;
		}

		String sourceInfo = GlobalConfig.instance().getJarInfoSource();
		if ("archiva".equals(sourceInfo)) {
			jarArtifactInfo = new ArchivaArtifactInfo();
		} else {
			throw new ToolException("init_jar_info_management", "no source info found");
		}

		return jarArtifactInfo;
	}

	public static JarFileInfo getJarFileInfo() {
		if (jarFileInfo != null) {
			return jarFileInfo;
		}

		jarFileInfo = new DefaultJarFileInfo();
		return jarFileInfo;
	}

}
