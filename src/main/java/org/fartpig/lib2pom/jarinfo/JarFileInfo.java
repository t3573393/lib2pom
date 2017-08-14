package org.fartpig.lib2pom.jarinfo;

import java.util.jar.JarFile;

import org.fartpig.lib2pom.entity.ArtifactObj;

public interface JarFileInfo {

	public ArtifactObj resolveByManifest(JarFile jarFile);
}
