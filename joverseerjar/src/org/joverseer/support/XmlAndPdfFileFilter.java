package org.joverseer.support;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
/**
 * Filter files matching the specified game
 * @author Dave
 *
 */
public class XmlAndPdfFileFilter implements FileFilter {
	String gameNoAsString;
	public XmlAndPdfFileFilter(int gameNo) {
		this.gameNoAsString = String.format("g%d", gameNo);
	}
	@Override
	public boolean accept(File file) {
		String name = file.getName();
		return (!file.isDirectory()) && Pattern.matches("g0*\\d{1,}n\\d{2,}t\\d{2,}.(xml|pdf)", name);
	}
}