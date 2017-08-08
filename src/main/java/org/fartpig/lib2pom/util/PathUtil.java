package org.fartpig.lib2pom.util;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

import org.fartpig.lib2pom.App;

public final class PathUtil {
	
	public static String getProjectPath() {
		URL url = App.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		try
		{
			filePath = URLDecoder.decode(url.getPath(), "utf-8");
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		
		if (filePath.endsWith(".jar")) {
			filePath = filePath.substring(0, filePath.lastIndexOf("/") +1);
		}
		
		File file = new File(filePath);
		return file.getAbsolutePath();
	}
}
