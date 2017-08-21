package org.fartpig.lib2pom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fartpig.lib2pom.constant.GlobalConfig;
import org.fartpig.lib2pom.constant.GlobalConst;
import org.fartpig.lib2pom.entity.ArtifactObj;
import org.fartpig.lib2pom.entity.DummyObj;
import org.fartpig.lib2pom.entity.FileObj;
import org.fartpig.lib2pom.phase.CompactFileObjsAction;
import org.fartpig.lib2pom.phase.FetchFileNamesAction;
import org.fartpig.lib2pom.phase.InflateLibsAction;
import org.fartpig.lib2pom.phase.JarResolveAction;
import org.fartpig.lib2pom.phase.MergeFileObjAction;
import org.fartpig.lib2pom.phase.OutputPomResultAction;
import org.fartpig.lib2pom.phase.PrintOutMergeResultAction;
import org.fartpig.lib2pom.phase.ResolveFileNamesAction;
import org.fartpig.lib2pom.phase.RetrieveDependencisAction;
import org.fartpig.lib2pom.util.ToolException;
import org.fartpig.lib2pom.util.ToolLogger;

/**
 * application entry
 *
 */
public class App {

	public static void main(String[] args) {
		try {
			String phase = GlobalConst.PHASE_INIT_PARAMS;
			ToolLogger log = ToolLogger.getInstance();
			log.setCurrentPhase(phase);

			GlobalConfig config = GlobalConfig.instance();
			String appRootPath = GlobalConfig.getApprootpath();
			String inputLibPath = config.getInputLibPath();
			String outPutPomFileName = config.getOutPutPomFileName();
			String inflateOutPath = config.getInflateOutPath();

			boolean needInflate = config.isNeedInflate();
			boolean needPrintoutResult = config.isNeedPrintoutResult();

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

			config.setInflateOutPath(inflateOutPath);
			config.setInputLibPath(inputLibPath);
			config.setOutPutPomFileName(outPutPomFileName);
			config.setNeedInflate(needInflate);
			config.setNeedPrintoutResult(needPrintoutResult);

			invokeByGlobalConfig(config);

		} catch (Exception e) {
			ToolLogger.getInstance().error("error:", e);
		}
	}

	public static void invokeByGlobalConfig(GlobalConfig config) {

		String inputLibPath = config.getInputLibPath();
		String outPutPomFileName = config.getOutPutPomFileName();
		String inflateOutPath = config.getInflateOutPath();

		boolean needInflate = config.isNeedInflate();
		boolean needPrintoutResult = config.isNeedPrintoutResult();

		List<String> libFileNames = fetchFileNames(inputLibPath);
		List<FileObj> fileObjs = resolveFileNames(libFileNames);
		// the DummyObj, try use the jar resolve to do, by
		// resolveJarFiles
		List<DummyObj> dummyObjs = filterArtifactObjWithMatcher(fileObjs, new FilterMatcher() {
			@Override
			public boolean match(FileObj fileObj) {
				return fileObj instanceof DummyObj;
			}
		});
		List<ArtifactObj> jarResolveArtifactObjs = resolveJarFiles(inputLibPath, dummyObjs);
		// replace the jar resolve result in the fileObjs
		for (int i = 0; i < fileObjs.size(); i++) {
			for (ArtifactObj aJarResolveObj : jarResolveArtifactObjs) {
				if (fileObjs.get(i).getFileFullName().equals(aJarResolveObj.getFileFullName())) {
					fileObjs.set(i, aJarResolveObj);
					break;
				}
			}
		}

		List<FileObj> origObjs = new ArrayList<FileObj>(fileObjs);
		List<ArtifactObj> resolveArtifactObjs = filterArtifactObjWithMatcher(fileObjs, new FilterMatcher() {
			@Override
			public boolean match(FileObj fileObj) {
				return fileObj instanceof ArtifactObj;
			}
		});

		List<ArtifactObj> extraArtifactObjs = retrieveDependencis(resolveArtifactObjs);
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

	public static List<ArtifactObj> resolveJarFiles(String inputLibPath, List<DummyObj> dummyObjs) {
		JarResolveAction action = new JarResolveAction();
		return action.resolveJarFiles(inputLibPath, dummyObjs);
	}

	public interface FilterMatcher {
		public boolean match(FileObj fileObj);
	}

	public static <T extends FileObj> List<T> filterArtifactObjWithMatcher(List<FileObj> fileObjs,
			FilterMatcher matcher) {
		List<T> result = new ArrayList<T>(fileObjs.size());
		for (FileObj aFileObj : fileObjs) {
			if (matcher.match(aFileObj)) {
				result.add((T) aFileObj);
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
