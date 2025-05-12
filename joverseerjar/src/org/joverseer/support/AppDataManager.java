package org.joverseer.support;

import java.io.File;

public class AppDataManager {
	static String FOLDER_NAME = "JOverseer";
	
	public static String getPath() {
		String filePath = null;		
		String os = System.getProperty("os.name").toUpperCase();
		if (os.contains("WIN")) {
			filePath = System.getenv("APPDATA") + File.separator + FOLDER_NAME;
		}
		else if (os.contains("MAC")) {
		    filePath = System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + FOLDER_NAME;
		}
		
		if(filePath == null) return null;
		//System.out.println("Searching for resource folder");
		File directory = new File(filePath);

		if (directory.exists()) {
		    return filePath;
		}

		if(directory.mkdir()) {
			return filePath;
		}
		
		return null;
	}
}
