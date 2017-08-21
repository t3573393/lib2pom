package org.fartpig.lib2pom.phase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fartpig.lib2pom.App;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.DummyObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.entity.PomProperty;
import org.fartpig.lib2pom.util.ToolLogger;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class OutputPomResultAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_OUTPUT_POM_RESULT;
	private static String PROPERTY_SUFFIX = ".version";

	public OutputPomResultAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public void outputPomResult(List<FileObj> fileObjs, String outputFileName) {
		// render the jar info to the pom file, with freemarker template
		Configuration cfg = new Configuration();
		try {
			ClassTemplateLoader loader = new ClassTemplateLoader(App.class, "/");
			cfg.setTemplateLoader(loader);

			Template template = cfg.getTemplate("my-pom.ftl");

			Map<String, Object> data = new HashMap<String, Object>();
			// group the artifactObj by the groupId, use the same version
			// in the same groupId; generate the version list ${project.version}

			Map<String, String> groupIdVersion = new HashMap<String, String>();
			Map<String, Boolean> sameGroupIds = new HashMap<String, Boolean>();

			List<FileObj> resolveObjs = new ArrayList<FileObj>();
			List<FileObj> systemObjs = new ArrayList<FileObj>();
			List<PomProperty> propertyObjs = new ArrayList<PomProperty>();

			for (FileObj aFileObj : fileObjs) {
				if (aFileObj instanceof ArtifactObj && ((ArtifactObj) aFileObj).isResolve()) {
					resolveObjs.add(aFileObj);

					ArtifactObj aArtifactObj = (ArtifactObj) aFileObj;
					String groupId = aArtifactObj.getGroupId();
					if (!groupIdVersion.containsKey(groupId)) {
						groupIdVersion.put(groupId, aArtifactObj.getVersion());
					} else {
						// with the same group id and version, then add to the
						// same group id
						if (groupIdVersion.get(groupId).equals(aArtifactObj.getVersion())) {
							if (!sameGroupIds.containsKey(groupId)) {
								sameGroupIds.put(groupId, Boolean.TRUE);
							}
						} else {
							// not the same, mark the false
							sameGroupIds.put(groupId, Boolean.FALSE);
						}
					}

				} else {
					systemObjs.add(aFileObj);
				}
			}

			// generate properties
			for (Map.Entry<String, Boolean> aEntry : sameGroupIds.entrySet()) {
				if (aEntry.getValue().booleanValue()) {
					PomProperty pomProperty = new PomProperty();
					pomProperty.setProperty(aEntry.getKey() + PROPERTY_SUFFIX);
					pomProperty.setValue(groupIdVersion.get(aEntry.getKey()));
					propertyObjs.add(pomProperty);

					// replace the groupid versions
					for (FileObj aFileObj : resolveObjs) {
						ArtifactObj aArtifactObj = (ArtifactObj) aFileObj;
						String groupId = aArtifactObj.getGroupId();
						if (aEntry.getKey().equals(groupId)) {
							aArtifactObj.setVersion(String.format("${%s}", pomProperty.getProperty()));
						}
					}
				}
			}

			data.put("propertyObjs", propertyObjs);
			data.put("resolveObjs", resolveObjs);
			data.put("systemObjs", systemObjs);

			// output the render data to console
			StringBuilder sb = new StringBuilder();

			sb.append("---------------");
			sb.append(GlobalConst.LINE_SEPARATOR);

			sb.append("propertyObjs");
			sb.append("->");
			sb.append("size:" + propertyObjs.size());
			sb.append(GlobalConst.LINE_SEPARATOR);

			for (PomProperty aPomProperty : propertyObjs) {
				sb.append(aPomProperty.getProperty());
				sb.append(":");
				sb.append(aPomProperty.getValue());
				sb.append(GlobalConst.LINE_SEPARATOR);
			}
			sb.append("---------------");
			sb.append(GlobalConst.LINE_SEPARATOR);

			sb.append("resolveObjs");
			sb.append("->");
			sb.append("size:" + resolveObjs.size());
			sb.append(GlobalConst.LINE_SEPARATOR);

			for (FileObj aFileObj : resolveObjs) {
				if (aFileObj instanceof ArtifactObj) {
					ArtifactObj artifactObj = (ArtifactObj) aFileObj;
					sb.append(artifactObj.formateFileName());
					if (artifactObj.getFileFullName() != null) {
						sb.append("-fileName:");
						sb.append(artifactObj.getFileFullName());
					}
				} else if (aFileObj instanceof DummyObj) {
					DummyObj dummyObj = (DummyObj) aFileObj;
					sb.append(dummyObj.formateFileName());
				}
				sb.append(GlobalConst.LINE_SEPARATOR);
			}
			sb.append("---------------");
			sb.append(GlobalConst.LINE_SEPARATOR);

			sb.append("systemObjs");
			sb.append("->");
			sb.append("size:" + systemObjs.size());
			sb.append(GlobalConst.LINE_SEPARATOR);

			for (FileObj aFileObj : systemObjs) {
				if (aFileObj instanceof ArtifactObj) {
					ArtifactObj artifactObj = (ArtifactObj) aFileObj;
					sb.append(artifactObj.formateFileName());
					if (artifactObj.getFileFullName() != null) {
						sb.append("-fileName:");
						sb.append(artifactObj.getFileFullName());
					}
				} else if (aFileObj instanceof DummyObj) {
					DummyObj dummyObj = (DummyObj) aFileObj;
					sb.append(dummyObj.formateFileName());
				}
				sb.append(GlobalConst.LINE_SEPARATOR);
			}
			sb.append("---------------");
			sb.append(GlobalConst.LINE_SEPARATOR);
			ToolLogger.getInstance().info(sb.toString());

			Writer file = new FileWriter(new File(outputFileName));
			template.process(data, file);
			file.flush();
			file.close();

		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		} catch (TemplateException e) {
			ToolLogger.getInstance().error("error:", e);
		}

	}

}
