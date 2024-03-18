package org.joverseer.support;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Filter files matching the specified game
 * @author Dave
 * Edited. Extended functionality whilst allowing it still to be used the same way as before.
 * Now, if the second constructor is used, it will properly filter out files from a different game number and only read in files for results of the next turn
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
			if(this.turnNoAsString == "-1") return true;	//With this if it retains the same functionality as the class did before
			if(name.contains(this.turnNoAsString) && name.contains(this.gameNoAsString)) return true;

		}
		return false;
	}
}