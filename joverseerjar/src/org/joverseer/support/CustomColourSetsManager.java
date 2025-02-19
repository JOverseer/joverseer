package org.joverseer.support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.joverseer.metadata.GameTypeEnum;

public class CustomColourSetsManager {
	static final String PATH_NAME = File.separator + "Colour Sets";
	static final String FILE_ENDING = ".properties";
	static FilenameFilter filter = (dir, name) -> name.endsWith(FILE_ENDING);
	
	public static ArrayList<String> getColourSets(String defaultName, GameTypeEnum gt) {
		String dir = getDir();
		if (dir == null) return null;
		
		File f = new File(dir);

		String[] ls = f.list(filter);
		
		if (defaultName == null) return removeNameEndList(f.list(filter));
		
		createNewColourSetFile(defaultName, gt);
		
		return removeNameEndList(f.list(filter));
	}
	
	public static ArrayList<String> getColourSets(){
		return getColourSets(null, null);
	}
	
	public static boolean createNewColourSetFile(String name, GameTypeEnum gt) {
		String dir = getDir();
		if (dir == null) return false;
		
		File f = new File(dir + File.separator + name + FILE_ENDING);
		try {			
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(getDefaultContents(gt));
			writer.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static String getDefaultContents(GameTypeEnum gt) {
		if(gt == GameTypeEnum.gameBOFA) return defaultBOFAColour;
		if(gt == GameTypeEnum.gameCME) return defaultCMEColour;
		if(gt == GameTypeEnum.gameCMF) return defaultCMFColour;
		if(gt == GameTypeEnum.gameKS) return defaultKSColour;
		return defaultFileContents;
	}
	
	public static String getDir() {
		//String dir = PreferenceRegistry.instance().getPreferenceValue("map.colorFolder");
		
		String dir = AppDataManager.getPath();
		if (dir == null) {
			System.out.println("AppData Not found");
			return null;
		}
		
		dir = dir.concat(PATH_NAME);
		if(!doesDirExist(dir)) makeDir(dir);
		
		return dir;
	}
	
	public static boolean deleteFile(String fileName) {
		File f = new File(getDir() + File.separator + fileName + FILE_ENDING);

		return f.delete();
	}
	
	/*
	 * Wont work
	 */
	public static boolean renameFile(String fileName, String newName) {
		File f = new File(getDir() + File.separator + fileName + FILE_ENDING);
		File newF = new File(getDir() + File.separator + newName + FILE_ENDING);
		return f.renameTo(newF);
	}
	
	public static boolean makeDir(String dir) {
		File f = new File(dir);
		return f.mkdir();
	}
	
	public static boolean doesDirExist(String dir) {
		File f = new File(dir);
		return f.exists();
	}
	
	public static ArrayList<String> removeNameEndList(String[] inpList){
		ArrayList<String> out = new ArrayList<String>();
		for (String s : inpList) {
			out.add(removeNameEnd(s));
		}
		return out;
	}
	
	public static String removeNameEnd(String inp) {
		return inp.substring(0, inp.length() - PATH_NAME.length() + 1);
	}
	
    public static String getColor1(String fileName, int nationNum) {
        Properties p = new Properties();
        String filePath = getDir() + File.separator + fileName + FILE_ENDING;

        try (FileReader reader = new FileReader(filePath)) {
            p.load(reader);
            String s = p.getProperty("nation." + nationNum + ".color");
            if(s == null) return "NO_NATION";
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Consider logging instead of printing
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging instead of printing
        }

        return null; // Return null if an exception occurs
    }
	
    public static String getColor2(String fileName, int nationNum) {
        Properties p = new Properties();
        String filePath = getDir() + File.separator + fileName + FILE_ENDING;

        try (FileReader reader = new FileReader(filePath)) {
            p.load(reader);
            String s = p.getProperty("nation." + nationNum + ".color2");
            if(s == null) return "NO_NATION";
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Consider logging instead of printing
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging instead of printing
        }

        return null; // Return null if an exception occurs
    }
	
    public static void setColor1(String fileName, int nationNum, String newColour) {
        Properties p = new Properties();
        String filePath = getDir() + File.separator + fileName + FILE_ENDING;

        try (FileReader reader = new FileReader(filePath)) {
            
            p.load(reader);
            p.setProperty("nation." + nationNum + ".color", newColour);
            
            FileWriter writer = new FileWriter(filePath);
            
            p.store(writer, null);
            writer.close();
           
            
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Consider logging instead
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging instead
        }
    }
    
    public static void setColor2(String fileName, int nationNum, String newColour) {
        Properties p = new Properties();
        String filePath = getDir() + File.separator + fileName + FILE_ENDING;

        try (FileReader reader = new FileReader(filePath)) {
            
            p.load(reader);
            p.setProperty("nation." + nationNum + ".color2", newColour);
            FileWriter writer = new FileWriter(filePath);
            
            p.store(writer, null);
            writer.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Consider logging instead
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging instead
        }
    }
	
	static final String defaultFileContents = "nation.0.color=#999999\r\n"
			+ "nation.1.color=#ff867b\r\n"
			+ "nation.2.color=#633063\r\n"
			+ "nation.3.color=#ff0000\r\n"
			+ "nation.4.color=#633000\r\n"
			+ "nation.5.color=#ff9a00\r\n"
			+ "nation.6.color=#ffffff\r\n"
			+ "nation.7.color=#ffff00\r\n"
			+ "nation.8.color=#0000ff\r\n"
			+ "nation.9.color=#00ff00\r\n"
			+ "nation.10.color=#f700ff\r\n"
			+ "nation.11.color=#000001\r\n"
			+ "nation.12.color=#000001\r\n"
			+ "nation.13.color=#000001\r\n"
			+ "nation.14.color=#000001\r\n"
			+ "nation.15.color=#000001\r\n"
			+ "nation.16.color=#000001\r\n"
			+ "nation.17.color=#000001\r\n"
			+ "nation.18.color=#000001\r\n"
			+ "nation.19.color=#000001\r\n"
			+ "nation.20.color=#000001\r\n"
			+ "nation.21.color=#aaaaff\r\n"
			+ "nation.22.color=#AA7777\r\n"
			+ "nation.23.color=#53b27f\r\n"
			+ "nation.24.color=#73b2bd\r\n"
			+ "nation.25.color=#73b2bd\r\n"
			+ "nation.26.color=#333399\r\n"
			+ "nation.27.color=#000001\r\n"
			+ "\r\n"
			+ "nation.0.color2=#000001\r\n"
			+ "nation.1.color2=#000001\r\n"
			+ "nation.2.color2=#000001\r\n"
			+ "nation.3.color2=#000001\r\n"
			+ "nation.4.color2=#000001\r\n"
			+ "nation.5.color2=#000001\r\n"
			+ "nation.6.color2=#000001\r\n"
			+ "nation.7.color2=#000001\r\n"
			+ "nation.8.color2=#000001\r\n"
			+ "nation.9.color2=#000001\r\n"
			+ "nation.10.color2=#000001\r\n"
			+ "nation.11.color2=#528427\r\n"
			+ "nation.12.color2=#633000\r\n"
			+ "nation.13.color2=#ff8000\r\n"
			+ "nation.14.color2=#633063\r\n"
			+ "nation.15.color2=#0000ff\r\n"
			+ "nation.16.color2=#ffffff\r\n"
			+ "nation.17.color2=#ffff00\r\n"
			+ "nation.18.color2=#ff0000\r\n"
			+ "nation.19.color2=#ff867b\r\n"
			+ "nation.20.color2=#00ff00\r\n"
			+ "nation.21.color2=#0000aa\r\n"
			+ "nation.22.color2=#63020d\r\n"
			+ "nation.23.color2=#107710\r\n"
			+ "nation.24.color2=#ffffff\r\n"
			+ "nation.25.color2=#007800\r\n"
			+ "nation.26.color2=#73b2bd\r\n"
			+ "nation.27.color2=#000001";
	
	static final String defaultBOFAColour = "nation.1.color=#000001\r\n"
			+ "nation.2.color=#000001\r\n"
			+ "nation.3.color=#00ff00\r\n"
			+ "nation.4.color=#0000ff\r\n"
			+ "nation.5.color=#633063\r\n"
			+ "\r\n"
			+ "nation.1.color2=#ff0000\r\n"
			+ "nation.2.color2=#528427\r\n"
			+ "nation.3.color2=#000001\r\n"
			+ "nation.4.color2=#000001\r\n"
			+ "nation.5.color2=#000001";
	
	static final String defaultCMEColour = "nation.1.color=#e6ec3c\r\n"
			+ "nation.26.color=#fcfecb\r\n"
			+ "nation.2.color=#ff0202\r\n"
			+ "nation.27.color=#fe8787\r\n"
			+ "\r\n"
			+ "nation.1.color2=#000001\r\n"
			+ "nation.26.color2=#000001\r\n"
			+ "nation.2.color2=#000001\r\n"
			+ "nation.27.color2=#000001";
	
	static final String defaultCMFColour = "nation.1.color=#e6ec3c\r\n"
			+ "nation.1.color2=#000001\r\n"
			+ "nation.2.color=#7ef936\r\n"
			+ "nation.2.color2=#000001\r\n"
			+ "\r\n"
			+ "nation.26.color=#fcfecb\r\n"
			+ "nation.26.color2=#000001\r\n"
			+ "\r\n"
			+ "nation.11.color=#ff0202\r\n"
			+ "nation.11.color2=#000001\r\n"
			+ "nation.12.color=#9714fc\r\n"
			+ "nation.12.color2=#000001\r\n"
			+ "\r\n"
			+ "nation.27.color=#fe8787\r\n"
			+ "nation.27.color2=#000001";
	
	static final String defaultKSColour = "nation.1.color=#ff867b\r\n"
			+ "nation.1.color2=#000001\r\n"
			+ "nation.2.color=#D67FFF\r\n"
			+ "nation.2.color2=#000001\r\n"
			+ "nation.3.color=#ff0000\r\n"
			+ "nation.3.color2=#000001\r\n"
			+ "nation.4.color=#11E109\r\n"
			+ "nation.4.color2=#000001\r\n"
			+ "nation.5.color=#ff9a00\r\n"
			+ "nation.5.color2=#000001\r\n"
			+ "nation.6.color=#ffffff\r\n"
			+ "nation.6.color2=#000001\r\n"
			+ "\r\n"
			+ "nation.11.color=#000001\r\n"
			+ "nation.11.color2=#528427\r\n"
			+ "nation.12.color=#000001\r\n"
			+ "nation.12.color2=#A55000\r\n"
			+ "nation.13.color=#000001\r\n"
			+ "nation.13.color2=#ff8000\r\n"
			+ "nation.14.color=#000001\r\n"
			+ "nation.14.color2=#A30FFF\r\n"
			+ "nation.15.color=#000001\r\n"
			+ "nation.15.color2=#FFFF00\r\n"
			+ "nation.16.color=#000001\r\n"
			+ "nation.16.color2=#ffffff\r\n"
			+ "\r\n"
			+ "nation.21.color=#aaaaff\r\n"
			+ "nation.21.color2=#0000aa\r\n"
			+ "nation.22.color=#07EAD0\r\n"
			+ "nation.22.color2=#63020d\r\n"
			+ "\r\n"
			+ "nation.26.color=#333399\r\n"
			+ "nation.26.color2=#73b2bd";
}
