package org.fartpig.lib2pom.entity;

import java.util.HashMap;
import java.util.Map;

public class ArtifactObj extends FileObj {

	private String groupId;
	private String artifactId;
	private String version;
	private String packaging;
	private String classifier;
	private String repositoryId;
	private Map<String, String> extraInfo = new HashMap<String, String>();
	private boolean isResolve;

	private Map<String, ArtifactObj> dependencis = new HashMap<String, ArtifactObj>();

	public String formateFileName() {
		return String.format("repositoryId[%s]-artifactId[%s]-version[%s]-classifier[%s]-packaging[%s]", repositoryId, artifactId, version, classifier,
				packaging);
	}

	public String getFileFullName() {
		String fileFullName = super.getFileFullName();
		if (fileFullName == null || fileFullName.length() == 0) {
			if (version == null) {
				fileFullName = String.format("%s.%s", artifactId, packaging);
			} else {
				fileFullName = String.format("%s-%s.%s", artifactId, version, packaging);
			}
		}
		return fileFullName;
	}

	public String toString() {
		return String.format("repositoryId[%s]-artifactId[%s]-version[%s]-packaging[%s]", repositoryId, artifactId, version, packaging);
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPackaging() {
		return packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public Map<String, ArtifactObj> getDependencis() {
		return dependencis;
	}

	public void setDependencis(Map<String, ArtifactObj> dependencis) {
		this.dependencis = dependencis;
	}

	public Map<String, String> getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(Map<String, String> extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public boolean isResolve() {
		return isResolve;
	}

	public void setResolve(boolean isResolve) {
		this.isResolve = isResolve;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public boolean isEqual(FileObj other) {

		if (!(other instanceof ArtifactObj)) {
			return false;
		}

		ArtifactObj b = (ArtifactObj) other;

		if (!this.isResolve()) {
			return this.getFileFullName().equals(other.getFileFullName());
		}

		if (this.getArtifactId().equals(b.getArtifactId()) && this.getGroupId().equals(b.getGroupId())
				&& this.getVersion().equals(b.getVersion())) {
			return true;
		}

		return false;
	}

	public boolean isOnlyVersionDiff(ArtifactObj b) {
		if (this.getArtifactId().equals(b.getArtifactId()) && this.getGroupId().equals(b.getGroupId())) {
			return true;
		}

		return false;
	}

	public String uniqueName() {
		// hacking this
		return String.format("artifactId[%s]-version[%s]", artifactId, version);
	}

}
