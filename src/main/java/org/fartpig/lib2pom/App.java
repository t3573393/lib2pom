package org.fartpig.lib2pom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.phase.CompactFileObjsAction;
import org.fartpig.lib2pom.phase.FetchFileNamesAction;
import org.fartpig.lib2pom.phase.InflateLibsAction;
import org.fartpig.lib2pom.phase.MergeFileObjAction;
import org.fartpig.lib2pom.phase.OutputPomResultAction;
import org.fartpig.lib2pom.phase.PrintOutMergeResultAction;
import org.fartpig.lib2pom.phase.ResolveFileNamesAction;
import org.fartpig.lib2pom.phase.RetrieveDependencisAction;
import org.fartpig.lib2pom.util.PathUtil;
import org.fartpig.lib2pom.util.ToolException;
import org.fartpig.lib2pom.util.ToolLogger;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		String phase = GlobalConst.PHASE_INIT_PARAMS;
		ToolLogger log = ToolLogger.getInstance();
		log.setCurrentPhase(phase);

		String appRootPath = PathUtil.getProjectPath();
		String inputLibPath = String.format("%s%slib", appRootPath, File.separator);
		String outPutPomFileName = String.format("%s%spom.xml", appRootPath, File.separator);
		String inflateOutPath = String.format("%s%stargetlib", appRootPath, File.separator);

		boolean needInflate = false;
		boolean needPrintoutResult = false;

		if (args.length == 0) {
			inputLibPath = String.format("%s%slib", appRootPath, File.separator);
			log.info(String.format("set inputLibPath:%s", inputLibPath));
		} else if (args.length == 1 && !args[0].startsWith("-")) {
			inputLibPath = args[0];
			log.info(String.format("set inputLibPath by args:%s", inputLibPath));
		} else {
			int lastIndex = args.length - 1;
			boolean useDefaultLib = false;
			for (int i = 0; i < args.length; i++) {
				if ("-o".equals(args[i])) {
					if ((i + 1) >= args.length) {
						throw new ToolException(phase, "please set the outPutPomFileName");
					}
					outPutPomFileName = String.format("%s%spom.xml", args[i + 1], File.separator);
					useDefaultLib = useDefaultLib || (i + 1) == lastIndex;
					log.info(String.format("set outPutPomFileName by args:%s", outPutPomFileName));
				} else if ("--print".equals(args[i]) || "-p".equals(args[i])) {
					needPrintoutResult = true;
					useDefaultLib = useDefaultLib || i == lastIndex;
					log.info(String.format("set needPrintoutResult: true"));
				} else if ("--inflate".equals(args[i]) || "-if".equals(args[i])) {
					if ((i + 1) >= args.length) {
						throw new ToolException(phase, "please set the inflateOutPath");
					}
					needInflate = true;
					inflateOutPath = args[i + 1];
					useDefaultLib = useDefaultLib || (i + 1) == lastIndex;
					log.info(String.format("set inflateOutPath by args:%s", inflateOutPath));
					log.info(String.format("set needInflate: true"));
				}
			}

			if (useDefaultLib == false) {
				inputLibPath = args[args.length - 1];
				log.info(String.format("set inputLibPath: %s", inputLibPath));
			}
		}

		List<String> libFileNames = fetchFileNames(inputLibPath);
		List<FileObj> fileObjs = resolveFileNames(libFileNames);
		List<FileObj> origObjs = new ArrayList<FileObj>(fileObjs);

		List<ArtifactObj> extraArtifactObjs = retrieveDependencis(filterArtifactObj(fileObjs));
		fileObjs.addAll(extraArtifactObjs);
		Map<String, List<FileObj>> mergetResult = mergeFileObj(fileObjs);
		if (needPrintoutResult) {
			printOutMergeResult(mergetResult);
		}

		List<FileObj> compactResult = compactFileObjs(origObjs, mergetResult);
		outputPomResult(compactResult, outPutPomFileName);
		if (needInflate) {
			inflateLibs(outPutPomFileName, inputLibPath, inflateOutPath);
		}

	}

	public static List<String> fetchFileNames(String path) {
		FetchFileNamesAction action = new FetchFileNamesAction();
		return action.fetchFileNames(path);
	}

	public static List<FileObj> resolveFileNames(List<String> fileNames) {
		ResolveFileNamesAction action = new ResolveFileNamesAction();
		return action.resolveFileNames(fileNames);
	}

	public static List<ArtifactObj> filterArtifactObj(List<FileObj> fileObjs) {
		List<ArtifactObj> result = new ArrayList<ArtifactObj>(fileObjs.size());
		for (FileObj aFileObj : fileObjs) {
			if (aFileObj instanceof ArtifactObj) {
				result.add((ArtifactObj) aFileObj);
			}
		}
		return result;
	}

	public static List<ArtifactObj> retrieveDependencis(List<ArtifactObj> artifactObjs) {
		RetrieveDependencisAction action = new RetrieveDependencisAction();
		return action.retrieveDependencis(artifactObjs);
	}

	public static Map<String, List<FileObj>> mergeFileObj(List<FileObj> fileObjs) {
		MergeFileObjAction action = new MergeFileObjAction();
		return action.mergeFileObj(fileObjs);
	}

	public static void printOutMergeResult(Map<String, List<FileObj>> mergeResult) {
		PrintOutMergeResultAction action = new PrintOutMergeResultAction();
		action.printOutMergeResult(mergeResult);
	}

	public static List<FileObj> compactFileObjs(List<FileObj> origObjs, Map<String, List<FileObj>> mergeResult) {
		CompactFileObjsAction action = new CompactFileObjsAction();
		return action.compactFileObjs(origObjs, mergeResult);
	}

	public static void outputPomResult(List<FileObj> fileObjs, String outputFileName) {
		OutputPomResultAction action = new OutputPomResultAction();
		action.outputPomResult(fileObjs, outputFileName);
	}

	public static void inflateLibs(String pomFileName, String inputLibPath, String inflateOutPath) {
		InflateLibsAction action = new InflateLibsAction();
		action.inflateLibs(pomFileName, inputLibPath, inflateOutPath);
	}
}
