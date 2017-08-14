package org.fartpig.lib2pom.phase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.util.ToolLogger;

public class CompactFileObjsAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_COMPACT_FILEOBJS;

	public CompactFileObjsAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public List<FileObj> compactFileObjs(List<FileObj> origObjs, Map<String, List<FileObj>> mergeResult) {
		// 按照兼容性规则， 将三个集合和原始集合进行合并得到准确的jar列表
		List<FileObj> result = new ArrayList<FileObj>();
		Set<String> artifactKeySet = new HashSet<String>();

		// 使用原始列表对集合中对象打标
		List<FileObj> unionObjs = mergeResult.get(GlobalConst.SET_UNION);
		markFileObjs(origObjs, unionObjs);

		List<FileObj> conflictObjs = mergeResult.get(GlobalConst.SET_CONFLICT);
		markFileObjs(origObjs, conflictObjs);

		List<FileObj> unknownObjs = mergeResult.get(GlobalConst.SET_UNKNOWN);
		markFileObjs(origObjs, unknownObjs);

		// 将合集,冲突以及未知的输出:由于冲突中包含了对应裁剪后的结果
		for (FileObj aFileObj : unionObjs) {
			if (!aFileObj.isNeedExclude() && !artifactKeySet.contains(aFileObj.uniqueName())) {
				artifactKeySet.add(aFileObj.uniqueName());
				result.add(aFileObj);
			}
		}

		for (FileObj aFileObj : conflictObjs) {
			if (!aFileObj.isNeedExclude() && !artifactKeySet.contains(aFileObj.uniqueName())) {
				artifactKeySet.add(aFileObj.uniqueName());
				result.add(aFileObj);
			}
		}

		for (FileObj aFileObj : unknownObjs) {
			if (!aFileObj.isNeedExclude() && !artifactKeySet.contains(aFileObj.uniqueName())) {
				artifactKeySet.add(aFileObj.uniqueName());
				result.add(aFileObj);
			}
		}

		return result;
	}

	private void markFileObjs(List<FileObj> origObjs, List<FileObj> fileObjs) {

		for (FileObj aFileObj : fileObjs) {
			boolean isFind = false;
			for (FileObj aOrigObj : origObjs) {
				if (aOrigObj.isEqual(aFileObj)) {
					isFind = true;
					break;
				}
			}

			if (!isFind) {
				ToolLogger.getInstance().info("exclude obj:" + aFileObj.formateFileName());
				aFileObj.setNeedExclude(true);
			} else {
				if (aFileObj instanceof ArtifactObj) {
					List<FileObj> dependencis = new ArrayList<FileObj>(
							((ArtifactObj) aFileObj).getDependencis().values());
					if (dependencis.size() != 0) {
						markFileObjs(origObjs, dependencis);
					}
				}
			}
		}
	}
}
