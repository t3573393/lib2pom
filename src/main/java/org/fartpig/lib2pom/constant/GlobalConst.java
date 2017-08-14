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

	// union set
	public static final String SET_UNION = "union";
	// conflict set
	public static final String SET_CONFLICT = "conflict";
	// unknown set
	public static final String SET_UNKNOWN = "unknown";

	// attrs
	public static final String ATTR_URL = "url";
	public static final String ATTR_SYSTEMPATH = "systemPath";

	public static final String SPECIAL_PREFIX_FILE_NAME = "special_prefix.tb";
	public static final String COMMON_PACKAGE_PREFIX_FILE_NAME = "common_package.tb";

	public static String LINE_SEPARATOR = "\r\n";

}
