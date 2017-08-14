package org.fartpig.lib2pom.archivahelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.MediaType;

import org.apache.archiva.maven2.model.Artifact;
import org.apache.archiva.maven2.model.TreeEntry;
import org.apache.archiva.rest.api.services.ArchivaRestServiceException;
import org.apache.archiva.rest.api.services.BrowseService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.fartpig.lib2pom.constant.GlobalConfig;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.TreeEntryObj;
import org.fartpig.lib2pom.util.ArchivaUtil;
import org.fartpig.lib2pom.util.ToolLogger;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class ArchivaBrowseHelper extends CxfBaseHelper {

	// guest with an empty password
	public String authzHeader = null;

	public ArchivaBrowseHelper() {
		super();
		GlobalConfig config = GlobalConfig.instance();
		authzHeader = "Basic " + org.apache.cxf.common.util.Base64Utility
				.encode((config.getArchivaUser() + ":" + config.getArchivaPassword()).getBytes());
	}

	private Map<String, TreeEntry> localTreeEntryCache = new HashMap<String, TreeEntry>();

	// with an other login/password
	// public String authzHeader = "Basic "
	// + org.apache.cxf.common.util.Base64Utility.encode(("username" +
	// ":password").getBytes());
	// try to add password
	// public String authzHeader = "Basic " +
	// org.apache.cxf.common.util.Base64Utility.encode(("guest" +
	// ":").getBytes());

	private String getBaseUrl() {
		return GlobalConfig.instance().getArchivaBaseUrl();
	}

	private String getRestServicesPath() {
		return GlobalConfig.instance().getArchivaRestServicesPath();
	}

	public List<ArtifactObj> getAllArtifactByRepositoryId(String repositoryId) {
		BrowseService browseService = getBrowseService(false);
		List<ArtifactObj> artifactObjs = new ArrayList<ArtifactObj>();
		try {
			List<Artifact> artifacts = browseService.getArtifacts(repositoryId);
			for (Artifact aArtifact : artifacts) {
				ToolLogger.getInstance().info("artifact: " + aArtifact);
				ArtifactObj aObj = new ArtifactObj();
				ArchivaUtil.copyArtifactToArtifactObj(aArtifact, aObj);
				artifactObjs.add(aObj);
			}
		} catch (ArchivaRestServiceException e) {
			ToolLogger.getInstance().error("error:", e);
		}
		return artifactObjs;
	}

	public void getArtifactDownloadInfos(ArtifactObj artifactObj) {
		BrowseService browseService = getBrowseService(false);
		try {
			List<Artifact> artifactObjs = browseService.getArtifactDownloadInfos(artifactObj.getGroupId(),
					artifactObj.getArtifactId(), artifactObj.getVersion(), artifactObj.getRepositoryId());
			for (Artifact aArtifact : artifactObjs) {
				if ("jar".equals(aArtifact.getPackaging())) {
					artifactObj.getExtraInfo().put(GlobalConst.ATTR_URL, aArtifact.getUrl());
					break;
				}
			}

		} catch (ArchivaRestServiceException e) {
			ToolLogger.getInstance().error("error:", e);
		}
	}

	private void saveToCache(List<TreeEntry> treeEntry) {
		for (TreeEntry aTreeEntry : treeEntry) {
			Artifact artifact = aTreeEntry.getArtifact();
			String key = String.format("%s-%s-%s", artifact.getGroupId(), artifact.getArtifactId(),
					artifact.getVersion());
			// only save one level
			if (!localTreeEntryCache.containsKey(key)) {
				ToolLogger.getInstance().info("save to cache:" + key);
				localTreeEntryCache.put(key, aTreeEntry);
				// saveToCache(aTreeEntry.getChilds());
			}
		}
	}

	private List<TreeEntry> findTreeEntryInCache(ArtifactObj artifactObj) {
		String key = String.format("%s-%s-%s", artifactObj.getGroupId(), artifactObj.getArtifactId(),
				artifactObj.getVersion());
		if (localTreeEntryCache.containsKey(key)) {
			ToolLogger.getInstance().info("find by cache:" + key);
			List<TreeEntry> result = new ArrayList<TreeEntry>();
			result.add(localTreeEntryCache.get(key));
			return result;
		}
		return null;
	}

	public List<ArtifactObj> getFirstLevelTreeEntriesByArtifactObj(ArtifactObj artifactObj) {
		List<ArtifactObj> result = new ArrayList<ArtifactObj>();
		BrowseService browseService = getBrowseService(false);
		List<TreeEntry> treeEntries;
		try {
			treeEntries = findTreeEntryInCache(artifactObj);
			if (treeEntries == null) {
				treeEntries = browseService.getTreeEntries(artifactObj.getGroupId(), artifactObj.getArtifactId(),
						artifactObj.getVersion(), artifactObj.getRepositoryId());
				saveToCache(treeEntries);
			}
			for (TreeEntry aEntry : treeEntries) {
				TreeEntryObj obj = new TreeEntryObj();
				ArtifactObj aObj = new ArtifactObj();
				ArchivaUtil.copyArtifactToArtifactObj(aEntry.getArtifact(), aObj);
				obj.setArtifact(aObj);

				for (TreeEntry aChildEntry : aEntry.getChilds()) {
					TreeEntryObj childObj = new TreeEntryObj();
					ArtifactObj aChildObj = new ArtifactObj();
					ArchivaUtil.copyArtifactToArtifactObj(aChildEntry.getArtifact(), aChildObj);
					childObj.setArtifact(aChildObj);
					obj.getChilds().add(childObj);

					result.add(aChildObj);
				}
			}
		} catch (ArchivaRestServiceException e) {
			ToolLogger.getInstance().error("error:", e);
		} catch (ForbiddenException e) {
			ToolLogger.getInstance().warning("getTreeEntries forbidden:" + artifactObj.formateFileName());
			ToolLogger.getInstance().error("error:", e);
		}
		return result;
	}

	public BrowseService getBrowseService(boolean useXml) {
		// START SNIPPET: cxf-browseservice-creation
		BrowseService service = JAXRSClientFactory.create(
				getBaseUrl() + "/" + getRestServicesPath() + "/archivaServices/", BrowseService.class,
				Collections.singletonList(new JacksonJaxbJsonProvider()));
		// to add authentification
		if (authzHeader != null) {
			WebClient.client(service).header("Authorization", authzHeader);
		}
		// Set the Referer header to your archiva server url
		// WebClient.client(service).header("Referer","http://localhost:"+port);

		WebClient.getConfig(service).getHttpConduit().getClient().setReceiveTimeout(100000000);
		if (useXml) {
			WebClient.client(service).accept(MediaType.APPLICATION_XML_TYPE);
			WebClient.client(service).type(MediaType.APPLICATION_XML_TYPE);
		} else {
			WebClient.client(service).accept(MediaType.APPLICATION_JSON_TYPE);
			WebClient.client(service).type(MediaType.APPLICATION_JSON_TYPE);
		}
		return service;

	}

}
