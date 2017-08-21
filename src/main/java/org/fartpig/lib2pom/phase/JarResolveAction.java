package org.fartpig.lib2pom.phase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.DummyObj;
import org.fartpig.lib2pom.jarinfo.JarFileInfo;
import org.fartpig.lib2pom.jarinfo.JarInfoManagement;
import org.fartpig.lib2pom.util.ToolLogger;

public class JarResolveAction {

	public static List<ArtifactObj> resolveJarFiles(String inputPath, List<DummyObj> dummyObjs) {
		ToolLogger log = ToolLogger.getInstance();
		JarFileInfo jarFileInfo = JarInfoManagement.getJarFileInfo();
		File file;
		List<ArtifactObj> result = new ArrayList<ArtifactObj>();
		for (DummyObj aDummyObj : dummyObjs) {
			file = new File(String.format("%s/%s", inputPath, aDummyObj.getFileFullName()));
			ArtifactObj aFileObj = jarFileInfo.resolveByManifest(file);
			if (aFileObj != null) {
				aFileObj.setFileFullName(aDummyObj.getFileFullName());
				aFileObj.setFileName(aDummyObj.getFileName());
				aFileObj.setFileEx(aDummyObj.getFileEx());
				result.add(aFileObj);
				log.info(String.format("from %s:%s", aDummyObj.getFileFullName(), aFileObj.formateFileName()));
			}
		}

		return result;
	}
}
