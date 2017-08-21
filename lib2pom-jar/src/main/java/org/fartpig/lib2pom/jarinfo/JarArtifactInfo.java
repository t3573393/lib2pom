package org.fartpig.lib2pom.jarinfo;

import java.util.List;

import org.fartpig.lib2pom.entity.ArtifactObj;

public interface JarArtifactInfo {

	public List<ArtifactObj> getFirstLevelTreeEntriesByArtifactObj(ArtifactObj artifactObj);

	public List<ArtifactObj> getAllArtifactByRepositoryId(String repositoryId);

	public void getArtifactDownloadInfos(ArtifactObj artifactObj);

	public List<ArtifactObj> searchByArtifactObj(ArtifactObj artifactObj);
}
