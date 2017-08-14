package org.fartpig.lib2pom.archivahelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.archiva.maven2.model.Artifact;
import org.apache.archiva.rest.api.model.SearchRequest;
import org.apache.archiva.rest.api.services.ArchivaRestServiceException;
import org.apache.archiva.rest.api.services.SearchService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.fartpig.lib2pom.constant.GlobalConfig;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.util.ArchivaUtil;
import org.fartpig.lib2pom.util.ToolLogger;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class ArchivaSearchHelper extends CxfBaseHelper {

	// guest with an empty password
	private String authzHeader = "Basic " + org.apache.cxf.common.util.Base64Utility.encode(("guest" + ":").getBytes());

	public ArchivaSearchHelper() {
		super();
		GlobalConfig config = GlobalConfig.instance();
		authzHeader = "Basic " + org.apache.cxf.common.util.Base64Utility
				.encode((config.getArchivaUser() + ":" + config.getArchivaPassword()).getBytes());
	}

	private String getBaseUrl() {
		return GlobalConfig.instance().getArchivaBaseUrl();
	}

	private String getRestServicesPath() {
		return GlobalConfig.instance().getArchivaRestServicesPath();
	}

	public List<ArtifactObj> searchByArtifactObj(ArtifactObj artifactObj) {
		SearchService searchService = getSearchServer();
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setArtifactId(artifactObj.getArtifactId());
		searchRequest.setVersion(artifactObj.getVersion());
		searchRequest.setPackaging(artifactObj.getPackaging());
		List<ArtifactObj> result = new ArrayList<ArtifactObj>();

		try {
			List<Artifact> artifacts = searchService.searchArtifacts(searchRequest);
			for (Artifact aArtifact : artifacts) {
				ToolLogger.getInstance().info(aArtifact.toString());
				ArtifactObj aObj = new ArtifactObj();
				ArchivaUtil.copyArtifactToArtifactObj(aArtifact, aObj);
				result.add(aObj);
			}
		} catch (ArchivaRestServiceException e) {
			ToolLogger.getInstance().error("error:", e);
		}
		return result;
	}

	public SearchService getSearchServer() {
		SearchService service = JAXRSClientFactory.create(
				getBaseUrl() + "/" + getRestServicesPath() + "/archivaServices/", SearchService.class,
				Collections.singletonList(new JacksonJaxbJsonProvider()));
		// to add authentification
		if (authzHeader != null) {
			WebClient.client(service).header("Authorization", authzHeader);
		}
		// to configure read timeout
		WebClient.getConfig(service).getHttpConduit().getClient().setReceiveTimeout(100000000);
		// if you want to use json as exchange format xml is supported too
		WebClient.client(service).accept(MediaType.APPLICATION_JSON_TYPE);
		WebClient.client(service).type(MediaType.APPLICATION_JSON_TYPE);
		return service;
	}

}
