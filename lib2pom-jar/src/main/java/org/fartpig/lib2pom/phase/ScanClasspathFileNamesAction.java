package org.fartpig.lib2pom.phase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.util.ToolLogger;

//从classpath中获取依赖的lib
public class ScanClasspathFileNamesAction {

	private static String CURRENT_PHASE = GlobalConst.PHASE_FETCH_FILE_NAMES;
	private static String CLASSPATHENTRY = "classpathentry";
	private static String KIND = "kind";
	private static String LIB = "lib";
	private static String PATH = "path";

	public ScanClasspathFileNamesAction() {
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(CURRENT_PHASE);
	}

	public ScanClasspathFileNamesAction(String[] extensions) {
		this();
	}

	public List<String> scanClasspathFileNames(String classpathFile) {
		ToolLogger log = ToolLogger.getInstance();

		List<String> fileNames = new ArrayList<String>();
		File classpathFileObj = new File(classpathFile);
		if (classpathFileObj.exists()) {
			SAXReader reader = new SAXReader();
			Document dom;
			try {
				dom = reader.read(classpathFileObj);
				Element root = dom.getRootElement();
				List<Element> elements = root.elements(CLASSPATHENTRY);
				for (Element aElement : elements) {
					Attribute kindAttr = aElement.attribute(KIND);
					if (kindAttr != null && kindAttr.getText().equals(LIB)) {
						Attribute pathAttr = aElement.attribute(PATH);
						log.info(String.format("classpath path entry:%s", pathAttr.getText()));
						String fileName = FilenameUtils.getName(pathAttr.getText());
						log.info(String.format("resolve the file name:%s", fileName));
						fileNames.add(fileName);
					}
				}
			} catch (DocumentException e) {
				log.error("error", e);
			}
		}
		return fileNames;
	}
}
