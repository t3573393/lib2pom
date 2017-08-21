package org.fartpig.lib2pom.phase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.jarinfo.JarArtifactInfo;
import org.fartpig.lib2pom.jarinfo.JarInfoManagement;
import org.fartpig.lib2pom.util.ToolLogger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class InflateLibsAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_INFLATE_LIBS;

	public InflateLibsAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public void inflateLibs(String pomFileName, String inputLibPath, String inflateOutPath) {
		// fetch all lib by the result pom content
		List<ArtifactObj> artifactObjs = resolveArtifactObjByPom(pomFileName);

		File inflateFilePath = new File(inflateOutPath);
		if (!inflateFilePath.exists()) {
			inflateFilePath.mkdirs();
		}

		JarArtifactInfo jarArtifactInfo = JarInfoManagement.getJarArtifactInfo();
		for (ArtifactObj artifactObj : artifactObjs) {
			if (artifactObj.isResolve()) {
				jarArtifactInfo.getArtifactDownloadInfos(artifactObj);
				// download artifact fail, then copy the file locally
				if (!downloadArtifactObj(artifactObj, inflateOutPath)) {
					copyArtifactObj(artifactObj, inputLibPath, inflateOutPath);
				}
			} else {
				copyArtifactObj(artifactObj, inputLibPath, inflateOutPath);
			}
		}
	}

	private String retrieveChildText(Element element, String childTag) {
		Element child = element.getChild(childTag);
		if (child == null) {
			return null;
		}
		return child.getText();
	}

	public List<ArtifactObj> resolveArtifactObjByPom(String pomFileName) {
		Map<String, String> propertyMap = new HashMap<String, String>();
		List<ArtifactObj> result = new ArrayList<ArtifactObj>();

		SAXBuilder jdomBuilder = new SAXBuilder();
		try {
			Document dom = jdomBuilder.build(pomFileName);
			XPathFactory factory = XPathFactory.instance();
			XPathExpression<Element> exprProperty = factory.compile("/project/properties", Filters.element());
			List<Element> propertyElements = exprProperty.evaluate(dom);
			if (propertyElements.size() > 0) {
				Element propertiesRootElement = propertyElements.get(0);
				for (Element aPropertyElement : propertiesRootElement.getChildren()) {
					propertyMap.put(aPropertyElement.getName(), aPropertyElement.getText());
				}
			}

			XPathExpression<Element> exprDependency = factory.compile("/project/dependencies/dependency",
					Filters.element());
			List<Element> elements = exprDependency.evaluate(dom);
			for (Element dependencyElement : elements) {
				ArtifactObj aArtifactObj = new ArtifactObj();
				aArtifactObj.setGroupId(retrieveChildText(dependencyElement, "groupId"));
				aArtifactObj.setArtifactId(retrieveChildText(dependencyElement, "artifactId"));
				aArtifactObj.setVersion(retrieveChildText(dependencyElement, "version"));
				// replace the property version value
				if (aArtifactObj.getVersion().startsWith("${")) {
					String tempVersion = aArtifactObj.getVersion();
					tempVersion = tempVersion.substring(2, tempVersion.length() - 1);
					if (propertyMap.containsKey(tempVersion)) {
						aArtifactObj.setVersion(propertyMap.get(tempVersion));
					}
				}
				aArtifactObj.setScope(retrieveChildText(dependencyElement, "scope"));
				aArtifactObj.setClassifier(retrieveChildText(dependencyElement, "classifier"));
				aArtifactObj.setPackaging(retrieveChildText(dependencyElement, "type"));
				if ("system".equals(aArtifactObj.getScope())) {
					aArtifactObj.getExtraInfo().put(GlobalConst.ATTR_SYSTEMPATH,
							retrieveChildText(dependencyElement, "systemPath"));
					aArtifactObj.setResolve(false);
				} else {
					aArtifactObj.setResolve(true);
				}

				if (aArtifactObj.getPackaging() == null) {
					aArtifactObj.setPackaging("jar");
				}

				ToolLogger.getInstance().info("resolve from pom file:" + aArtifactObj.formateFileName());
				result.add(aArtifactObj);
			}
		} catch (JDOMException e) {
			ToolLogger.getInstance().error("error:", e);
		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}

		return result;
	}

	public void copyArtifactObj(ArtifactObj artifactObj, String srcPath, String inflateOutPath) {
		try {
			String fileFullName = artifactObj.getFileFullName();
			if ("system".equals(artifactObj.getScope())) {
				String systemPath = artifactObj.getExtraInfo().get(GlobalConst.ATTR_SYSTEMPATH);
				int fileNameIndex = systemPath.lastIndexOf("/");
				fileFullName = systemPath.substring(fileNameIndex + 1);
			}
			String srcFileName = String.format("%s/%s", srcPath, fileFullName);
			String destFileName = String.format("%s/%s", inflateOutPath, fileFullName);
			ToolLogger.getInstance()
					.info("copy artifactObj srcFileName:" + srcFileName + "- destFileName:" + destFileName);
			FileUtils.copyFile(new File(srcFileName), new File(destFileName));
		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}
	}

	public boolean downloadArtifactObj(ArtifactObj artifactObj, String inflateOutPath) {
		String url = artifactObj.getExtraInfo().get(GlobalConst.ATTR_URL);
		if (url == null || url.length() == 0) {
			ToolLogger.getInstance().info("artifactObj:" + artifactObj.uniqueName() + "- not found url");
			return false;
		}

		HttpGet httpGet = new HttpGet(url);
		HttpClient httpClient = HttpClients.createDefault();
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String outputFileName = String.format("%s/%s", inflateOutPath, artifactObj.getFileFullName());
				ToolLogger.getInstance().info("outputFileName:" + outputFileName);
				FileOutputStream os = new FileOutputStream(new File(outputFileName));
				entity.writeTo(os);
			}
			return true;
		} catch (ClientProtocolException e) {
			ToolLogger.getInstance().error("error:", e);
		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}
		return false;
	}
}
