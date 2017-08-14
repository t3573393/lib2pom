package org.fartpig.lib2pom.constant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.fartpig.lib2pom.util.PathUtil;
import org.fartpig.lib2pom.util.ToolLogger;

public class GlobalConfig {

	private static final String appRootPath = PathUtil.getProjectPath();
	private static GlobalConfig globalConfig = null;

	public static GlobalConfig instance() {
		if (globalConfig == null) {
			globalConfig = new GlobalConfig("tools.properties");
		}
		return globalConfig;
	}

	private String inputLibPath = String.format("%s%slib", appRootPath, File.separator);
	private String outPutPomFileName = String.format("%s%spom.xml", appRootPath, File.separator);
	private String inflateOutPath = String.format("%s%stargetlib", appRootPath, File.separator);

	private boolean needInflate = true;
	private boolean needPrintoutResult = true;

	private String archivaBaseUrl = "http://192.111.25.32:9080/archiva/";
	private String archivaRestServicesPath = "restServices";
	private String archivaUser = "guest";
	private String archivaPassword = "";

	private GlobalConfig(String configName) {

		Properties configProperties = new Properties();
		try {
			configProperties
					.load(new BufferedInputStream(new FileInputStream(appRootPath + File.separator + configName)));

		} catch (FileNotFoundException e) {
			ToolLogger.getInstance().error("error:", e);
		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}

		inputLibPath = configProperties.getProperty("inputLibPath", inputLibPath);
		outPutPomFileName = configProperties.getProperty("outPutPomFileName", outPutPomFileName);
		inflateOutPath = configProperties.getProperty("inflateOutPath", inflateOutPath);

		needInflate = Boolean.valueOf(configProperties.getProperty("needInflate", String.valueOf(needInflate)));
		needPrintoutResult = Boolean
				.valueOf(configProperties.getProperty("needInflate", String.valueOf(needPrintoutResult)));

		archivaBaseUrl = configProperties.getProperty("archivaBaseUrl", archivaBaseUrl);
		archivaRestServicesPath = configProperties.getProperty("archivaRestServicesPath", archivaRestServicesPath);
		archivaUser = configProperties.getProperty("archivaUser", archivaUser);
		archivaPassword = configProperties.getProperty("archivaPassword", archivaPassword);

		ToolLogger log = ToolLogger.getInstance();
		log.info("inputLibPath:" + inputLibPath);
		log.info("outPutPomFileName:" + outPutPomFileName);
		log.info("inflateOutPath:" + inflateOutPath);
		log.info("needInflate:" + needInflate);
		log.info("needPrintoutResult:" + needPrintoutResult);
		log.info("archivaBaseUrl:" + archivaBaseUrl);
		log.info("archivaRestServicesPath:" + archivaRestServicesPath);
		log.info("archivaUser:" + archivaUser);
		log.info("archivaPassword:" + archivaPassword);
	}

	public String getInputLibPath() {
		return inputLibPath;
	}

	public void setInputLibPath(String inputLibPath) {
		this.inputLibPath = inputLibPath;
	}

	public String getOutPutPomFileName() {
		return outPutPomFileName;
	}

	public void setOutPutPomFileName(String outPutPomFileName) {
		this.outPutPomFileName = outPutPomFileName;
	}

	public String getInflateOutPath() {
		return inflateOutPath;
	}

	public void setInflateOutPath(String inflateOutPath) {
		this.inflateOutPath = inflateOutPath;
	}

	public boolean isNeedInflate() {
		return needInflate;
	}

	public void setNeedInflate(boolean needInflate) {
		this.needInflate = needInflate;
	}

	public boolean isNeedPrintoutResult() {
		return needPrintoutResult;
	}

	public void setNeedPrintoutResult(boolean needPrintoutResult) {
		this.needPrintoutResult = needPrintoutResult;
	}

	public static String getApprootpath() {
		return appRootPath;
	}

	public String getArchivaBaseUrl() {
		return archivaBaseUrl;
	}

	public void setArchivaBaseUrl(String archivaBaseUrl) {
		this.archivaBaseUrl = archivaBaseUrl;
	}

	public String getArchivaRestServicesPath() {
		return archivaRestServicesPath;
	}

	public void setArchivaRestServicesPath(String archivaRestServicesPath) {
		this.archivaRestServicesPath = archivaRestServicesPath;
	}

	public String getArchivaUser() {
		return archivaUser;
	}

	public void setArchivaUser(String archivaUser) {
		this.archivaUser = archivaUser;
	}

	public String getArchivaPassword() {
		return archivaPassword;
	}

	public void setArchivaPassword(String archivaPassword) {
		this.archivaPassword = archivaPassword;
	}

}
