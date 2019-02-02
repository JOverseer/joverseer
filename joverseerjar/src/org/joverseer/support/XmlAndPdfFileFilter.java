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
		this.gameNoAsString = String.format("g%03d", gameNo);
	}
	@Override
	public boolean accept(File file) {
		String name = file.getName();
		return (!file.isDirectory()) && name.contains(this.gameNoAsString) &&
				((name.endsWith(".pdf") ||
				(Pattern.matches("g\\d{3}n\\d{2}t\\d{3}.xml", name) )));
	}
}