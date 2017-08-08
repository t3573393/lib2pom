package org.fartpig.lib2pom.entity;

import java.util.ArrayList;
import java.util.List;

public class TreeEntryObj {

	private List<TreeEntryObj> childs = new ArrayList<TreeEntryObj>();

	private ArtifactObj artifact;

	private TreeEntryObj parent;

	public List<TreeEntryObj> getChilds() {
		return childs;
	}

	public void setChilds(List<TreeEntryObj> childs) {
		this.childs = childs;
	}

	public ArtifactObj getArtifact() {
		return artifact;
	}

	public void setArtifact(ArtifactObj artifact) {
		this.artifact = artifact;
	}

	public TreeEntryObj getParent() {
		return parent;
	}

	public void setParent(TreeEntryObj parent) {
		this.parent = parent;
	}

}
