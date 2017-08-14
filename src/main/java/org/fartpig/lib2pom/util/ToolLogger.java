package org.fartpig.lib2pom.util;

import org.apache.log4j.Logger;
import org.fartpig.lib2pom.App;
import org.fartpig.lib2pom.constant.GlobalConst;

public final class ToolLogger {

	private static Logger log = Logger.getLogger(App.class.getName());
	private static ToolLogger toolLogger = new ToolLogger();

	private String currentPhase = GlobalConst.PHASE_INIT_PARAMS;

	public static ToolLogger getInstance() {
		return toolLogger;
	}

	public String getCurrentPhase() {
		return currentPhase;
	}

	public void setCurrentPhase(String currentPhase) {
		this.currentPhase = currentPhase;
	}

	public void info(String msg) {
		log.info(String.format("phase:%s-%s", currentPhase, msg));
	}

	public void warning(String msg) {
		log.warn(String.format("phase:%s-%s", currentPhase, msg));
	}

	public void fine(String msg) {
		log.debug(String.format("phase:%s-%s", currentPhase, msg));
	}

	public void error(String msg, Throwable t) {
		log.error(String.format("phase:%s-%s", currentPhase, msg), t);
	}
}
