package org.fartpig.lib2pom.entity;

public abstract class FileObj {

	private String fileFullName;

	private String fileName;
	private String fileEx;

	private boolean needExclude = false;
	private String scope = "compile";

	public String getFileFullName() {
		return fileFullName;
	}

	public void setFileFullName(String fileName) {
		this.fileFullName = fileName;
	}

	public String formateFileName() {
		return fileFullName;
	}

	public abstract String uniqueName();

	public boolean isEqual(FileObj other) {

		if (other == null) {
			return false;
		}

		if (this.getFileFullName() == null) {
			return false;
		}

		return this.getFileFullName().equals(other.getFileFullName());
	}

	public boolean isNeedExclude() {
		return needExclude;
	}

	public void setNeedExclude(boolean needExclude) {
		this.needExclude = needExclude;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileEx() {
		return fileEx;
	}

	public void setFileEx(String fileEx) {
		this.fileEx = fileEx;
	}

}
