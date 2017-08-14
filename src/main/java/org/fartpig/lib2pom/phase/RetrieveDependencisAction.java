package org.fartpig.lib2pom.phase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.fartpig.lib2pom.archivahelper.ArchivaBrowseHelper;
import org.fartpig.lib2pom.archivahelper.ArchivaSearchHelper;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.util.ArchivaUtil;
import org.fartpig.lib2pom.util.ToolLogger;

// 根据信息调用接口获取依赖关系和完善自身信息，可能有会返回多个需要解决冲突
public class RetrieveDependencisAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_RETRIEVE_DEPENDENCIS;

	private static int PRIORITY_MAX = 999;
	private static String[] REPOSITORY_PRIORITY = { "internal", "central" };

	private int reposityPriorityIndex(String repositoryId) {
		for (int i = 0; i < REPOSITORY_PRIORITY.length; i++) {
			if (REPOSITORY_PRIORITY[i].equals(repositoryId)) {
				return i;
			}
		}
		return PRIORITY_MAX;
	}

	public RetrieveDependencisAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public List<ArtifactObj> retrieveDependencis(List<ArtifactObj> artifactObjs) {
		List<ArtifactObj> extraArtifactObjs = new ArrayList<ArtifactObj>();
		Set<String> resolveArtifactSet = new HashSet<String>();

		ArchivaSearchHelper archivaSearchHelper = new ArchivaSearchHelper();
		ArchivaBrowseHelper archivaBrowseHelper = new ArchivaBrowseHelper();
		// 先解析自身，然后通过自身获取所依赖的artifact信息， 并将依赖的结果返回
		for (ArtifactObj aObj : artifactObjs) {
			List<ArtifactObj> searchResult = archivaSearchHelper.searchByArtifactObj(aObj);
			if (searchResult.size() != 0) {
				// 如果找不到jar结果，默认给一个jar的结果
				boolean isFindJar = false;
				for (ArtifactObj aResult : searchResult) {
					if (isFindJar) {
						// 这里需要解决把source test-source等之类的无效
						if (aResult.getClassifier() != null) {
							continue;
						}
					}

					if (aResult.getArtifactId().equals(aObj.getArtifactId())
							&& aResult.getVersion().equals(aObj.getVersion())
							&& aResult.getPackaging().equals(aObj.getPackaging())) {
						// priority override
						int newIndex = reposityPriorityIndex(aResult.getRepositoryId());
						int oldIndex = reposityPriorityIndex(aObj.getRepositoryId());
						if (newIndex < oldIndex || aObj.getRepositoryId() == null) {
							ArtifactObj firstObj = aResult;
							aObj.setArtifactId(firstObj.getArtifactId());
							aObj.setClassifier(firstObj.getClassifier());
							aObj.setGroupId(firstObj.getGroupId());
							aObj.setPackaging(firstObj.getPackaging());
							aObj.setVersion(firstObj.getVersion());
							aObj.setRepositoryId(firstObj.getRepositoryId());
							aObj.setResolve(true);
							ToolLogger.getInstance().info("resolve:" + aObj.formateFileName());
						}

						if (aResult.getClassifier() == null) {
							isFindJar = true;
						}

						resolveArtifactSet.add(aObj.uniqueName());
					}
				}

				if (!isFindJar) {
					aObj.setPackaging("jar");
				}

			} else {
				aObj.setScope("system");
			}
		}

		Queue<ArtifactObj> artifactObjQueue = new LinkedList<ArtifactObj>();
		for (ArtifactObj aObj : artifactObjs) {
			artifactObjQueue.add(aObj);
		}

		while (!artifactObjQueue.isEmpty()) {
			ArtifactObj aObj = artifactObjQueue.poll();

			if (aObj.isResolve()) {
				ToolLogger.getInstance()
						.info("getFirstLevelTreeEntriesByArtifactObj parent: " + aObj.formateFileName());
				List<ArtifactObj> childArtifactObjs = archivaBrowseHelper.getFirstLevelTreeEntriesByArtifactObj(aObj);
				for (ArtifactObj aArtifactObj : childArtifactObjs) {
					ToolLogger.getInstance().info(
							"getFirstLevelTreeEntriesByArtifactObj child result: " + aArtifactObj.formateFileName());
					aArtifactObj.setResolve(true);
					if (!resolveArtifactSet.contains(aArtifactObj.uniqueName())) {
						extraArtifactObjs.add(aArtifactObj);
						artifactObjQueue.add(aArtifactObj);
						ToolLogger.getInstance().info("resolve : " + aArtifactObj.formateFileName() + " get dependency:"
								+ aObj.formateFileName());
					}
				}
				ArchivaUtil.fillDependencisToArtifactObj(aObj, childArtifactObjs);
			}
		}

		return extraArtifactObjs;
	}
}
