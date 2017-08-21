package org.fartpig.lib2pom.jarinfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.util.ToolLogger;

public class DefaultJarFileInfo implements JarFileInfo {

	private String[] artifactIdAttrs = { "Implementation-Title" };
	private String[] versionAttrs = { "Implementation-Version" };
	private List<String> commonPackagePrefix = null;

	public DefaultJarFileInfo() {
		loadCommonPackagePrefix();
	}

	private void loadCommonPackagePrefix() {
		// load the common package prefix file to mem
		try {
			InputStream input = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(GlobalConst.COMMON_PACKAGE_PREFIX_FILE_NAME);
			commonPackagePrefix = IOUtils.readLines(input, Charset.forName("UTF-8"));
		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}

	}

	public String commonPackagePrefixHandle(String artifactId) {
		if (commonPackagePrefix == null) {
			return artifactId.replaceAll("[.]", "-");
		}
		ToolLogger log = ToolLogger.getInstance();
		String artifactIdLower = artifactId.toLowerCase();
		for (String aCommonPackage : commonPackagePrefix) {
			String prefixLower = aCommonPackage.toLowerCase();
			if (artifactIdLower.startsWith(prefixLower)) {
				log.info(String.format("handle common package prefix file name:%s", artifactId));

				int prefixIndex = prefixLower.length() - 1;
				String artifactIdTemp = null;
				if (artifactId.charAt(prefixIndex + 1) == '.') {
					artifactIdTemp = artifactId.substring(prefixIndex + 2);
				} else {
					artifactIdTemp = artifactId.substring(prefixIndex + 1);
				}

				return artifactIdTemp.replaceAll("[.]", "-");
			}
		}

		return artifactId.replaceAll("[.]", "-");
	}

	@Override
	public ArtifactObj resolveByManifest(File file) {
		// try use the jar META-INF file to retrieve the artifactId and version
		// use the common package prefix table
		try {
			JarFile jarFile = new JarFile(file);
			Manifest manifest = jarFile.getManifest();
			String artifactId = null;
			String version = null;

			Attributes attributes = manifest.getMainAttributes();
			for (Object aKey : attributes.keySet()) {
				for (String aAttr : artifactIdAttrs) {
					if (aKey.toString().equals(aAttr)) {
						artifactId = attributes.getValue(aAttr);
						break;
					}
				}
			}

			for (Object aKey : attributes.keySet()) {
				for (String aAttr : versionAttrs) {
					if (aKey.toString().equals(aAttr)) {
						version = attributes.getValue(aAttr);
						break;
					}
				}
			}

			// try get the entry package info ,use the min path strategy
			if (artifactId == null) {
				String[] minPathDepths = null;
				// TODO need use the file directory, clip the size
				String lastDirectoryPath = null;
				for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
					JarEntry entry = entries.nextElement();
					String name = entry.getName();
					ToolLogger.getInstance().info("entry name:" + name);

					if (entry.isDirectory()) {
						continue;
					}

					if (name.startsWith("META-INF")) {
						continue;
					}

					if (name.endsWith(".class")) {

						if (name.lastIndexOf("/") == -1) {
							continue;
						}

						String path = name.substring(0, name.lastIndexOf("/"));
						String[] pathDepths = path.split("/");
						minPathDepths = pathDepths;

						if (lastDirectoryPath == null) {
							lastDirectoryPath = path;
							break;
						}

					}
				}

				if (minPathDepths != null) {
					artifactId = String.join(".", minPathDepths);
				}

				// give the default one
				if (version == null) {
					version = "1.0";
				}
			}

			if (artifactId != null && version != null) {

				String artifactIdTemp = commonPackagePrefixHandle(artifactId);

				ArtifactObj obj = new ArtifactObj();
				if (artifactIdTemp != null) {
					obj.setArtifactId(artifactIdTemp);
				} else {
					obj.setArtifactId(artifactId);
				}

				obj.setPackaging("jar");
				obj.setVersion(version);
				return obj;
			}

		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}

		return null;
	}

}
