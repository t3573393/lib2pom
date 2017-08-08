package org.fartpig.lib2pom.util;

import java.util.List;

import org.apache.archiva.maven2.model.Artifact;
import org.fartpig.lib2pom.entity.ArtifactObj;

public final class ArchivaUtil {

	public static void copyArtifactToArtifactObj(Artifact artifact, ArtifactObj artifactObj) {
		artifactObj.setArtifactId(artifact.getArtifactId());
		artifactObj.setClassifier(artifact.getClassifier());
		artifactObj.setGroupId(artifact.getGroupId());
		artifactObj.setPackaging(artifact.getPackaging());
		artifactObj.setVersion(artifact.getVersion());
		artifactObj.setRepositoryId(artifact.getRepositoryId());
	}

	public static void fillDependencisToArtifactObj(ArtifactObj artifactObj, List<ArtifactObj> dependencis) {
		for (ArtifactObj aArtifactObj : dependencis) {
			artifactObj.getDependencis().put(aArtifactObj.uniqueName(), aArtifactObj);
		}

	}

}
