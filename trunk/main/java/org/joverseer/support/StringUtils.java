package org.joverseer.support;

/**
 * Class with string manipulation utilities
 * 
 * Extends Spring Framework StringUtils
 * 
 * Contains static methods and cannot be instantiated
 * 
 * @author Marios Skounakis
 */

public class StringUtils extends org.springframework.util.StringUtils {
	
	/**
	 * Removes all new line (\r\n or \n) sequences from the given string
	 */
	public static String removeAllNewline(String str) {
		while (str.contains("\r\n")) {
			str = str.replace("\r\n", " ");
		}
		while (str.contains("\n")) {
			str = str.replace("\n", " ");
		}
		return str;
	}

	/**
	 * Removes all extra spaces from the string, leaving only one space
	 */
	public static String removeExtraspaces(String str) {
		while (str.contains("  ")) {
			str = str.replace("  ", " ");
		}
		return str;
	}
}
