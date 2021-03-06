package org.fartpig.lib2pom.phase;

import java.util.List;
import java.util.Map;

import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.DummyObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.util.ToolLogger;

public class PrintOutMergeResultAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_PRINTOUT_MERGE_RESULT;

	public PrintOutMergeResultAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public void printOutMergeResult(Map<String, List<FileObj>> mergeResult) {
		// output the merget result to console
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, List<FileObj>> aEntry : mergeResult.entrySet()) {
			sb.append(aEntry.getKey());
			sb.append("->");
			sb.append(GlobalConst.LINE_SEPARATOR);

			for (FileObj aFileObj : aEntry.getValue()) {
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

		}

		System.out.print(sb.toString());
	}
}
