package org.fartpig.lib2pom.phase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.DummyObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.util.StringUtil;
import org.fartpig.lib2pom.util.ToolLogger;

//根据文件名获取对应的文件元信息
public class ResolveFileNamesAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_RESOLVE_FILE_NAMES;
	private static String[] SPECIAL_CLASSIFIER = { "alpha", "ga", "release", "snapshot", "final", "build" };

	public ResolveFileNamesAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public boolean isVersionPrefix(String segment) {
		return Character.isDigit(segment.charAt(0)) || segment.startsWith("build");
	}

	public boolean isClassifierTag(String aTag) {
		for (String aStr : SPECIAL_CLASSIFIER) {
			if (aStr.equalsIgnoreCase(aTag)) {
				return true;
			}
		}

		return false;
	}

	public String retrieveClassifier(String version, String defaultClassifier) {
		String lowerCase = version.toLowerCase();
		for (String aStr : SPECIAL_CLASSIFIER) {
			if (lowerCase.contains(aStr)) {
				return aStr.toUpperCase();
			}
		}
		return defaultClassifier;
	}

	public List<FileObj> resolveFileNames(List<String> fileNames) {
		List<FileObj> fileObjs = new ArrayList<FileObj>();
		ToolLogger log = ToolLogger.getInstance();
		for (String aFileName : fileNames) {
			log.info(String.format("deal with file name:%s", aFileName));
			String artifactId = null;
			String version = null;
			String packaging = null;
			String defaultClassifier = "RELEASE";

			int extIndex = aFileName.lastIndexOf(".");
			String fileName = aFileName.substring(0, extIndex);
			packaging = aFileName.substring(extIndex + 1);
			log.info(String.format("packaging:%s", packaging));
			// 逆序遍历法
			String[] fileSegments = fileName.split("[-]");

			List<String> artifactIdSegments = new LinkedList<String>();
			List<String> versionSegments = new LinkedList<String>();
			boolean findVersion = false;
			// artifactId-version-classifier
			int versionIndex = -1;
			for (int i = fileSegments.length - 1; i >= 0; i--) {
				String aSegment = fileSegments[i];

				if (isVersionPrefix(aSegment)) {
					if (i != 0) {
						String preTag = fileSegments[i - 1];
						if (!isClassifierTag(preTag)) {
							findVersion = true;
							versionIndex = i;
							break;
						}
					}
				}
			}

			for (int i = 0; i < fileSegments.length; i++) {
				String aSegment = fileSegments[i];

				if (i < versionIndex) {
					artifactIdSegments.add(aSegment);
				} else if (i >= versionIndex) {
					versionSegments.add(aSegment);
				}
			}

			FileObj aFileObj = null;
			if (findVersion) {
				artifactId = StringUtil.join("-", artifactIdSegments);
				version = StringUtil.join("-", versionSegments);
				ArtifactObj obj = new ArtifactObj();
				obj.setFileFullName(aFileName);
				obj.setFileName(fileName);
				obj.setFileEx(packaging);
				obj.setArtifactId(artifactId);
				obj.setPackaging(packaging);
				obj.setClassifier(retrieveClassifier(version, defaultClassifier));
				obj.setVersion(version);
				aFileObj = obj;
				if ("RELEASE".equals(obj.getClassifier())) {
					log.info(String.format("from %s:%s", aFileName, aFileObj.formateFileName()));
				} else {
					log.info(String.format("from %s:%s", aFileName, aFileObj.formateFileName()));
				}

			} else {
				DummyObj obj = new DummyObj();
				obj.setFileFullName(aFileName);
				obj.setFileName(fileName);
				obj.setFileEx(packaging);

				aFileObj = obj;
				log.warning(String.format(" DummyObj from %s:%s", aFileName, aFileObj.formateFileName()));
			}
			fileObjs.add(aFileObj);
		}
		return fileObjs;
	}

}
