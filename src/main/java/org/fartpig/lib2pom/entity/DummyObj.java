package org.fartpig.lib2pom.entity;

public class DummyObj extends FileObj {

	public DummyObj() {
		super();
		this.setScope("system");
	}

	public String uniqueName() {
		// hacking this
		return String.format("filename[%s]", getFileFullName());
	}
}
