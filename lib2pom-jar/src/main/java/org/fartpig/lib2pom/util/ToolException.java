package org.fartpig.lib2pom.util;

public class ToolException extends RuntimeException {
	
	private String phase;
	
	public ToolException(String phase, String message) {
		super(message);
		this.phase = phase;
	}
	
	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}
	
	

}
