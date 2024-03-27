package org.joverseer.support;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
/**
 * Filter files matching the specified game
 * @author Dave
 * Edited. Extended functionality whilst allowing it still to be used the same way as before.
 * Now filters the files by game name, only importing those from the same game. Commented out code is the beginning of a turn number filter as well, but too many weird edge cases.
 * @author samue
 *
 */
public class XmlAndPdfFileFilter implements FileFilter {
	String gameNoAsString;
	String turnNoAsString;
	public XmlAndPdfFileFilter(int gameNo) {
		this.gameNoAsString = String.format("%d", gameNo);
		this.turnNoAsString = "-1";
	}
	public XmlAndPdfFileFilter(int gameNo, int turnNo) {
		this.gameNoAsString = String.format("%d", gameNo);
		this.turnNoAsString = String.format("%03d", turnNo+1); // add 0 padding
	}
	@Override
	public boolean accept(File file) {
		String name = file.getName();
		if (!file.isDirectory() && Pattern.matches("g0*\\d{1,}n\\d{2,}t\\d{2,}.(xml|pdf)", name)) {
			if(name.substring(name.indexOf("g")+1, name.indexOf("n")).equals(this.gameNoAsString)) return true;	//Filters by game name
//			if(this.turnNoAsString == "-1") return true;	//With this if it retains the same functionality as the class did before
//			if(name.substring(name.indexOf("g")+1, name.indexOf("n")).equals(this.gameNoAsString)) {
//				if(this.turnNoAsString == "000") this.turnNoAsString = "001"; //Allows importing of files on turn 0 for turn 0
//				if(name.substring(name.indexOf("t")+1, name.indexOf(".")).equals(this.turnNoAsString)) return true;
//			}

		}
		return false;
	}
}