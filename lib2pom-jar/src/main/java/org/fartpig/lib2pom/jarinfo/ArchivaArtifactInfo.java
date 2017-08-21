package org.fartpig.lib2pom.jarinfo;

import java.util.List;

import org.fartpig.lib2pom.archivahelper.ArchivaBrowseHelper;
import org.fartpig.lib2pom.archivahelper.ArchivaSearchHelper;
import org.fartpig.lib2pom.entity.ArtifactObj;

public class ArchivaArtifactInfo implements JarArtifactInfo {

	private ArchivaBrowseHelper browseHelper;
	private ArchivaSearchHelper searchHelper;

	public ArchivaArtifactInfo() {
		browseHelper = new ArchivaBrowseHelper();
		searchHelper = new ArchivaSearchHelper();
	}

	@Override
	public List<ArtifactObj> getFirstLevelTreeEntriesByArtifactObj(ArtifactObj artifactObj) {
		return browseHelper.getFirstLevelTreeEntriesByArtifactObj(artifactObj);
	}

	@Override
	public List<ArtifactObj> getAllArtifactByRepositoryId(String repositoryId) {
		return browseHelper.getAllArtifactByRepositoryId(repositoryId);
	}

	@Override
	public void getArtifactDownloadInfos(ArtifactObj artifactObj) {
		browseHelper.getArtifactDownloadInfos(artifactObj);
	}

	@Override
	public List<ArtifactObj> searchByArtifactObj(ArtifactObj artifactObj) {
		return searchHelper.searchByArtifactObj(artifactObj);
	}

}
