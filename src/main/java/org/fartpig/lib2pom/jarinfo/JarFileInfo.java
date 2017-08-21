package org.fartpig.lib2pom.jarinfo;

import java.io.File;

import org.fartpig.lib2pom.entity.ArtifactObj;

public interface JarFileInfo {

	public ArtifactObj resolveByManifest(File File);
}
