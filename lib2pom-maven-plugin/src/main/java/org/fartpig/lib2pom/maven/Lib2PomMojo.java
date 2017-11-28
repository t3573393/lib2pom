package org.fartpig.lib2pom.maven;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.fartpig.lib2pom.App;
import org.fartpig.lib2pom.constant.GlobalConfig;
import org.fartpig.lib2pom.maven.util.Constants;

import com.google.common.base.Throwables;

@Mojo(name = "resolve", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDirectInvocation = true, threadSafe = false)
public class Lib2PomMojo extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info(Constants.PLUGIN_ID + " - resolve");
		try {

			GlobalConfig globalConfig = GlobalConfig.instanceByFile(globalConfigPropertyFile);

			globalConfig.setInputLibPath(inputLibPath);

			if (!StringUtils.isEmpty(outPutPomFileName)) {
				globalConfig.setOutPutPomFileName(outPutPomFileName);
			}

			globalConfig.setNeedInflate(needInflate);
			globalConfig.setNeedPrintoutResult(needPrintoutResult);

			if (!StringUtils.isEmpty(jarInfoSource)) {
				globalConfig.setJarInfoSource(jarInfoSource);
			}

			if (!StringUtils.isEmpty(archivaBaseUrl)) {
				globalConfig.setArchivaBaseUrl(archivaBaseUrl);
			}

			if (!StringUtils.isEmpty(classpathFile)) {
				globalConfig.setClasspathFile(classpathFile);
			}

			if (!StringUtils.isEmpty(archivaRestServicesPath)) {
				globalConfig.setArchivaRestServicesPath(archivaRestServicesPath);
			}

			if (!StringUtils.isEmpty(archivaUser)) {
				globalConfig.setArchivaUser(archivaUser);
			}

			if (!StringUtils.isEmpty(archivaPassword)) {
				globalConfig.setArchivaPassword(archivaPassword);
			}

			App.invokeByGlobalConfig(globalConfig);
			getLog().info(Constants.PLUGIN_ID + " - resolve - end");

		} catch (Throwable t) {
			if (failOnError) {
				throw new MojoFailureException("execute fail ", t);
			} else {
				getLog().error("##############  Exception occurred during deploying to WebSphere  ###############");
				getLog().error(Throwables.getStackTraceAsString(t));
			}
		}
	}

	@Parameter(defaultValue = "${project}", readonly = true)
	protected MavenProject project;

	@Component
	protected MavenProjectHelper projectHelper;

	@Parameter(defaultValue = "${plugin.artifacts}")
	protected List<Artifact> pluginArtifacts;

	@Parameter(defaultValue = "${project.basedir}/tools.properties")
	protected File globalConfigPropertyFile;

	@Parameter(required = true)
	protected String inputLibPath;

	@Parameter
	protected String outPutPomFileName;

	@Parameter
	protected String classpathFile;

	@Parameter
	protected String inflateOutPath;

	@Parameter(defaultValue = "true")
	protected boolean needInflate = true;

	@Parameter(defaultValue = "true")
	protected boolean needPrintoutResult = true;

	@Parameter(defaultValue = "archiva")
	protected String jarInfoSource;

	@Parameter
	protected String archivaBaseUrl;

	@Parameter
	protected String archivaRestServicesPath;

	@Parameter
	protected String archivaUser;

	@Parameter
	protected String archivaPassword;

	@Parameter(defaultValue = "false")
	protected boolean failOnError;

}
