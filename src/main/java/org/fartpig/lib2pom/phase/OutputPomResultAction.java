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
import org.fartpig.lib2pom.util.ToolLogger;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class OutputPomResultAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_OUTPUT_POM_RESULT;

	public OutputPomResultAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public void outputPomResult(List<FileObj> fileObjs, String outputFileName) {
		// 将信息转化成对应的 pom 文件内容， 可以使用freemark的格式
		Configuration cfg = new Configuration();
		try {
			ClassTemplateLoader loader = new ClassTemplateLoader(App.class, "/");
			cfg.setTemplateLoader(loader);

			Template template = cfg.getTemplate("my-pom.ftl");

			Map<String, Object> data = new HashMap<String, Object>();

			List<FileObj> resolveObjs = new ArrayList<FileObj>();
			List<FileObj> systemObjs = new ArrayList<FileObj>();
			for (FileObj aFileObj : fileObjs) {
				if (aFileObj instanceof ArtifactObj && ((ArtifactObj) aFileObj).isResolve()) {
					resolveObjs.add(aFileObj);
				} else {
					systemObjs.add(aFileObj);
				}

			}
			data.put("resolveObjs", resolveObjs);
			data.put("systemObjs", systemObjs);

			// 使用日志输出这两个集合内容
			StringBuilder sb = new StringBuilder();

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

			System.out.print(sb.toString());

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
