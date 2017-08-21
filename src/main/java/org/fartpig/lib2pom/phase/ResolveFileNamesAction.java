package org.fartpig.lib2pom.phase;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.DummyObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.util.StringUtil;
import org.fartpig.lib2pom.util.ToolLogger;

//resolve the artifact object info by file name
public class ResolveFileNamesAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_RESOLVE_FILE_NAMES;
	private static String[] SPECIAL_VERSION_TAG = { "alpha", "ga", "release", "snapshot", "final", "build" };
	private static List<String> specialFileNamePrefix = null;

	public ResolveFileNamesAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);

		loadSpecialPrefix();
	}

	private void loadSpecialPrefix() {
		// load special atrifact id prefix fro the special file name
		try {
			InputStream input = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(GlobalConst.SPECIAL_PREFIX_FILE_NAME);
			specialFileNamePrefix = IOUtils.readLines(input, Charset.forName("UTF-8"));
			// specialFileNamePrefix = FileUtils.readLines(new
			// File(url.getFile()), Charset.forName("UTF-8"));
		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}

	}

	public boolean isVersionPrefix(String segment) {
		return Character.isDigit(segment.charAt(0)) || segment.startsWith("build");
	}

	public boolean isVersionTag(String aTag) {
		for (String aStr : SPECIAL_VERSION_TAG) {
			if (aStr.equalsIgnoreCase(aTag)) {
				return true;
			}
		}

		return false;
	}

	public String retrieveVersionTag(String version, String defaultClassifier) {
		String lowerCase = version.toLowerCase();
		for (String aStr : SPECIAL_VERSION_TAG) {
			if (lowerCase.contains(aStr)) {
				return aStr.toUpperCase();
			}
		}
		return defaultClassifier;
	}

	public FileObj specialFileNamePrefixHandle(String fileFullName) {
		if (specialFileNamePrefix == null) {
			return null;
		}
		ToolLogger log = ToolLogger.getInstance();
		String fileNameLower = fileFullName.toLowerCase();
		for (String aPrefixName : specialFileNamePrefix) {
			String prefixNameLower = aPrefixName.toLowerCase();
			if (fileNameLower.startsWith(prefixNameLower)) {
				log.info(String.format("handle special prefix file name:%s", fileFullName));
				int extIndex = fileFullName.lastIndexOf(".");
				String fileName = fileFullName.substring(0, extIndex);
				String packaging = fileFullName.substring(extIndex + 1);

				int versionIndex = prefixNameLower.length() - 1;
				String version = null;
				if (fileFullName.charAt(versionIndex + 1) == '-') {
					version = fileFullName.substring(versionIndex + 2, extIndex);
				} else {
					version = fileFullName.substring(versionIndex + 1, extIndex);
				}

				ArtifactObj obj = new ArtifactObj();
				obj.setFileFullName(fileFullName);
				obj.setFileName(fileName);
				obj.setFileEx(packaging);
				obj.setArtifactId(aPrefixName);
				obj.setPackaging(packaging);
				obj.setVersion(version);
				return obj;
			}
		}

		return null;
	}

	public FileObj resloveOneFileName(String aFileName) {
		ToolLogger log = ToolLogger.getInstance();

		String artifactId = null;
		String version = null;
		String packaging = null;

		int extIndex = aFileName.lastIndexOf(".");
		String fileName = aFileName.substring(0, extIndex);
		packaging = aFileName.substring(extIndex + 1);
		log.info(String.format("packaging:%s", packaging));
		// reverse the list
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
					if (!isVersionTag(preTag)) {
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

		return aFileObj;
	}

	public List<FileObj> resolveFileNames(List<String> fileNames) {
		List<FileObj> fileObjs = new ArrayList<FileObj>();
		ToolLogger log = ToolLogger.getInstance();
		for (String aFileName : fileNames) {
			log.info(String.format("deal with file name:%s", aFileName));

			FileObj specialObj = specialFileNamePrefixHandle(aFileName);
			if (specialObj != null) {
				fileObjs.add(specialObj);
				continue;
			}
			FileObj aFileObj = resloveOneFileName(aFileName);
			if (aFileObj != null) {
				fileObjs.add(aFileObj);
			}

		}
		return fileObjs;
	}

}
