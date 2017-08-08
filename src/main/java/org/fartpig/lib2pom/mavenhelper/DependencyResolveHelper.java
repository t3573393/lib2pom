package org.fartpig.lib2pom.mavenhelper;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystemSession;

public class DependencyResolveHelper {

	MavenProject project;
	MavenSession session;
	RepositorySystemSession repositorySession;
	BuildPluginManager pluginManager;

	public void initEnvironment() {

		// session = new MavenSession();

		project = new MavenProject();
		project.setFile(new File("dummyPom.xml"));

	}

	public void resolveDependency(String groupId, String artifactId, String version) {
		try {
			executeMojo(plugin(groupId(groupId), artifactId(artifactId), version(version)), goal("resolve"),
					configuration(element(name("outputFile"), "resolveDependency.txt")),
					executionEnvironment(project, session, pluginManager));
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
