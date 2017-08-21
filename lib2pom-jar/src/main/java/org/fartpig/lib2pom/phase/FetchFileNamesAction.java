package org.fartpig.lib2pom.phase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.util.ToolLogger;

//从目录中递归获取需要解析的文件名
public class FetchFileNamesAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_FETCH_FILE_NAMES;
	private String[] extensions = { "jar" };

	public FetchFileNamesAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public FetchFileNamesAction(String[] extensions) {
		this();
		this.extensions = extensions;
	}

	public List<String> fetchFileNames(String path) {
		List<String> fileNames = new ArrayList<String>();
		Collection<File> files = FileUtils.listFiles(new File(path), extensions, true);
		for (File aFile : files) {
			fileNames.add(aFile.getName());
		}
		return fileNames;
	}
}
