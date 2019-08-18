package org.fartpig.lib2pom.phase;

import java.util.ArrayList;
import java.util.List;

import org.fartpig.lib2pom.entity.ArtifactObj;

import junit.framework.TestCase;

public class TestRetrieveDependencisAction extends TestCase {
	
	public void testRetrieveDependencis() {
		List<ArtifactObj> artifactObjs = new ArrayList<>();
		ArtifactObj obj = new ArtifactObj();
		obj.setPackaging("jar");
		obj.setArtifactId("zipxml-maven-plugin");
		obj.setVersion("1.0-RELEASE");
		artifactObjs.add(obj);
		
		RetrieveDependencisAction action = new RetrieveDependencisAction();
		action.retrieveDependencis(artifactObjs);
	}

}
