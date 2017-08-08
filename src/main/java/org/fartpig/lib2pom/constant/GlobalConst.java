package org.fartpig.lib2pom.constant;

public final class GlobalConst {

	public static final String PHASE_INIT_PARAMS = "init_params";
	public static final String PHASE_FETCH_FILE_NAMES = "fetch_file_names";
	public static final String PHASE_RESOLVE_FILE_NAMES = "resolve_file_names";
	public static final String PHASE_RETRIEVE_DEPENDENCIS = "retrieve_dependencis";
	public static final String PHASE_MERGE_FILEOBJ = "merge_fileObj";
	public static final String PHASE_PRINTOUT_MERGE_RESULT = "printout_merge_result";
	public static final String PHASE_COMPACT_FILEOBJS = "compact_fileObjs";
	public static final String PHASE_OUTPUT_POM_RESULT = "output_pom_result";
	public static final String PHASE_INFLATE_LIBS = "inflate_libs";

	// 输出并集
	public static final String SET_UNION = "union";
	// 冲突集合
	public static final String SET_CONFLICT = "conflict";
	// 未解析集合
	public static final String SET_UNKNOWN = "unknown";

}
