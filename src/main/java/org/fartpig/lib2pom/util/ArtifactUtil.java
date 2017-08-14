package org.fartpig.lib2pom.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fartpig.lib2pom.archivahelper.ArchivaBrowseHelper;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.phase.ResolveFileNamesAction;

public class ArtifactUtil {

	public static boolean isVersionNew(String versionA, String versionB) {
		// just hack this
		return versionA.compareTo(versionB) > 0;
	}

	public static void filterOutSpecialPrefix() {
		// 使用仓库中的artifactId来分析出可能解析不出来的特殊jar，将其导入到special_prefix.tb文件中，
		ArchivaBrowseHelper browseHelper = new ArchivaBrowseHelper();
		ToolLogger log = ToolLogger.getInstance();

		List<ArtifactObj> objs = browseHelper.getAllArtifactByRepositoryId("internal");
		StringBuilder sb = new StringBuilder();
		ResolveFileNamesAction action = new ResolveFileNamesAction();
		for (ArtifactObj aObj : objs) {
			if (aObj.getPackaging().equals("jar")) {
				String origName = String.format("%s-%s.%s", aObj.getArtifactId(), aObj.getVersion(),
						aObj.getPackaging());
				log.info("deal origName: " + origName);
				FileObj resolveObj = action.resloveOneFileName(origName);
				if (resolveObj != null) {
					if (resolveObj instanceof ArtifactObj) {
						ArtifactObj targetObj = (ArtifactObj) resolveObj;
						if (!targetObj.getArtifactId().equalsIgnoreCase(aObj.getArtifactId())) {
							log.warning("origName:" + origName);
							log.warning("artifact name not resolve : true[" + aObj.getArtifactId() + "]- false["
									+ targetObj.getArtifactId() + "]");
							sb.append(aObj.getArtifactId());
							sb.append(GlobalConst.LINE_SEPARATOR);
						}
					}
				}
			}
		}
		if (sb.length() > 0) {
			try {
				URL url = Thread.currentThread().getContextClassLoader()
						.getResource(GlobalConst.SPECIAL_PREFIX_FILE_NAME);
				FileUtils.write(new File(url.getFile()), sb.toString(), Charset.forName("UTF-8"));
			} catch (IOException e) {
				ToolLogger.getInstance().error("error:", e);
			}
		}

	}
}
