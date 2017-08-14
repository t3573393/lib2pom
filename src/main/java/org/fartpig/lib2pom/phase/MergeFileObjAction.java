package org.fartpig.lib2pom.phase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.DummyObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.util.ArtifactUtil;
import org.fartpig.lib2pom.util.ToolLogger;

public class MergeFileObjAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_MERGE_FILEOBJ;

	public MergeFileObjAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public Map<String, List<FileObj>> mergeFileObj(List<FileObj> fileObjs) {
		Map<String, FileObj> unionArtifactMap = new HashMap<String, FileObj>();
		Map<String, FileObj> conflictArtifactMap = new HashMap<String, FileObj>();
		Map<String, FileObj> unknownArtifactMap = new HashMap<String, FileObj>();

		ToolLogger log = ToolLogger.getInstance();
		// merge the query result by artifactId, version and all jar info
		// unionSet: the whole resolve artifact, with the largest version by
		// conflict jar
		// conflictSet: the conflict jar set, include the same artifactIds
		// unknownSet: resolve fail jar files
		for (FileObj aFileObj : fileObjs) {
			if (aFileObj instanceof ArtifactObj) {
				ArtifactObj newFileObj = (ArtifactObj) aFileObj;
				if (!newFileObj.isResolve()) {
					log.info("unknownArtifact:" + aFileObj.uniqueName() + "-fileName:" + aFileObj.getFileFullName());
					unknownArtifactMap.put(aFileObj.uniqueName(), newFileObj);
				} else {
					String artifactId = newFileObj.getArtifactId();
					if (!unionArtifactMap.containsKey(artifactId)) {
						log.info("unionArtifactMap:" + artifactId);
						unionArtifactMap.put(artifactId, aFileObj);
					} else {
						ArtifactObj oldFileObj = (ArtifactObj) unionArtifactMap.get(artifactId);
						log.info("conflictArtifactMap:" + artifactId + "-old:" + oldFileObj.uniqueName() + "-new:"
								+ newFileObj.uniqueName());
						if (ArtifactUtil.isVersionNew(newFileObj.getVersion(), oldFileObj.getVersion())) {
							unionArtifactMap.put(artifactId, newFileObj);
						}
						conflictArtifactMap.put(oldFileObj.uniqueName(), oldFileObj);
						conflictArtifactMap.put(newFileObj.uniqueName(), newFileObj);
					}
				}

			} else if (aFileObj instanceof DummyObj) {
				unknownArtifactMap.put(aFileObj.uniqueName(), aFileObj);
			}
		}

		Map<String, List<FileObj>> result = new HashMap<String, List<FileObj>>();
		result.put(GlobalConst.SET_UNION, new ArrayList<FileObj>(unionArtifactMap.values()));
		result.put(GlobalConst.SET_CONFLICT, new ArrayList<FileObj>(conflictArtifactMap.values()));
		result.put(GlobalConst.SET_UNKNOWN, new ArrayList<FileObj>(unknownArtifactMap.values()));
		return result;
	}
}
