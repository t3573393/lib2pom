package org.fartpig.lib2pom;

import org.fartpig.lib2pom.constant.GlobalConfig;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.phase.InflateLibsAction;
import org.fartpig.lib2pom.phase.ScanClasspathFileNamesAction;
import org.fartpig.lib2pom.util.ArtifactUtil;
import org.fartpig.lib2pom.util.ToolException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	public void testAppNormal() {
		App app = new App();
		String[] args = {};
		app.main(args);
	}

	public void testAppArgs1() {
		App app = new App();
		// String[] args = { "-if",
		// "D:\\workspace-my\\lib2pom\\target\\test-classes\\targetlib", "-p",
		// "D:\\workspace-my\\test-classes\\lib" };
		String[] args = { "-if", "D:\\test\\target-lib", "-p", "D:\\test\\lib" };
		app.main(args);
	}

	public void testAppArgsWithClassPath() {
		App app = new App();
		// String[] args = { "-if",
		// "D:\\workspace-my\\lib2pom\\target\\test-classes\\targetlib", "-p",
		// "D:\\workspace-my\\test-classes\\lib" };
		// String[] args = { "-if",
		// "D:\\test\\target-lib", "-p",
		// "D:\\test\\libs" };

		String[] args = { "-cpf", "D:\\test\\.classpath", "-if", "D:\\test\\target-lib", "-p", "D:\\test\\libs" };
		app.main(args);
	}

	public void testFilterOutSpecialPrefix() {
		ArtifactUtil.filterOutSpecialPrefix();
	}

	public void testInflateLibsAction() {
		InflateLibsAction action = new InflateLibsAction();
		action.inflateLibs("D:\\workspace-my\\lib2pom\\target\\classes\\pom.xml",
				"D:\\ecif-web\\module-web\\target\\ecifconsole-web\\WEB-INF\\lib",
				"D:\\workspace-my\\lib2pom\\target\\test-classes\\targetlib");
	}

	public void testScanClassPath() {
		GlobalConfig config = GlobalConfig.instance();
		ScanClasspathFileNamesAction action = new ScanClasspathFileNamesAction();
		action.scanClasspathFileNames("D:\\.classpath");
	}

	public void testAppArgs2() {
		App app = new App();
		try {
			String[] args = { "-o" };
			app.main(args);
			fail("Expected a ToolException to be throw");
		} catch (ToolException e) {
			e.printStackTrace();
			assertEquals(GlobalConst.PHASE_INIT_PARAMS, e.getPhase());
			assertEquals("please set the outPutPomFileName", e.getMessage());
		}
	}

	public void testAppArgs3() {
		App app = new App();
		String[] args = { "-o", "D:\\workspace-my\\test-classes" };
		app.main(args);
	}

	public void testAppArgs4() {
		App app = new App();
		String[] args = { "--print" };
		app.main(args);
	}

	public void testAppArgs5() {
		App app = new App();
		String[] args = { "-p" };
		app.main(args);
	}

	public void testAppArgs6() {
		App app = new App();
		try {
			String[] args = { "--inflate" };
			app.main(args);
			fail("Expected a ToolException to be throw");
		} catch (ToolException e) {
			e.printStackTrace();
			assertEquals(GlobalConst.PHASE_INIT_PARAMS, e.getPhase());
			assertEquals("please set the inflateOutPath", e.getMessage());
		}
	}

	public void testAppArgs7() {
		App app = new App();
		try {
			String[] args = { "-if" };
			app.main(args);
			fail("Expected a ToolException to be throw");
		} catch (ToolException e) {
			e.printStackTrace();
			assertEquals(GlobalConst.PHASE_INIT_PARAMS, e.getPhase());
			assertEquals("please set the inflateOutPath", e.getMessage());
		}
	}

	public void testAppArgs8() {
		App app = new App();
		String[] args = { "--inflate", "D:\\workspace-my\\lib2pom\\target\\test-classes\\targetlib" };
		app.main(args);
	}

	public void testAppArgs9() {
		App app = new App();
		String[] args = { "-if", "D:\\workspace-my\\lib2pom\\target\\test-classes\\targetlib" };
		app.main(args);
	}

	public void testAppArgs10() {
		App app = new App();
		String[] args = { "-if", "D:\\workspace-my\\lib2pom\\target\\test-classes\\targetlib",
				"D:\\workspace-my\\lib2pom\\target\\test-classes\\lib" };
		app.main(args);
	}
}
