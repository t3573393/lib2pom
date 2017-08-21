package org.fartpig.lib2pom.maven;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class Lib2PomMojoTest extends AbstractMojoTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAppRun() throws Exception {
		File pom = getTestFile("src/test/resources/test-pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Lib2PomMojo mojo = (Lib2PomMojo) lookupMojo("resolve", pom);
		assertNotNull(mojo);
		mojo.execute();

	}
}
