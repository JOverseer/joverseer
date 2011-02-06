package org.joverseer.support;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.info.InfoUtils;

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

	public static String[] extractCharacterTitle(String text) {
		String[] ret = null;
		String foundResult = "";
		ArrayList<String> charTitles = InfoUtils.getAllCharacterTitles();
		for (int j = 0; j < charTitles.size(); j++) {
			String title = charTitles.get(j);
			if (text.contains(title)) {
				if (!foundResult.equals("") && !foundResult.equals(title))
					return null;
				foundResult = title;
				text = text.replace(title, "#title#");
				if (ret == null)
					ret = new String[2];
				ret[0] = text;
				ret[1] = title;
			}
		}
		return ret;
	}

	public static String[] extractNation(String text) {
		String[] ret = null;
		String foundNation = "";
		for (int j = 0; j < 26; j++) {
			Nation n = NationMap.getNationFromNo(j);
			String nationName = n.getName();
			String nationNamePlusThe = "the " + n.getName();
			if (text.contains(nationNamePlusThe)) {
				if (!foundNation.equals("") && !foundNation.equals(nationName))
					return null;
				foundNation = nationName;
				text = text.replace(nationNamePlusThe, "#nation#");
				if (ret == null)
					ret = new String[2];
				ret[0] = text;
				ret[1] = nationName;
			}
			if (text.contains(nationName)) {
				if (!foundNation.equals("") && !foundNation.equals(nationName))
					return null;
				foundNation = nationName;
				text = text.replace(nationName, "#nation#");
				if (ret == null)
					ret = new String[2];
				ret[0] = text;
				ret[1] = nationName;
			}
		}
		return ret;
	}

	public static String getUniquePart(String text, String start, String end, boolean includeStart, boolean includeEnd) {
		ArrayList<String> ret = getParts(text, start, end, includeStart, includeEnd);
		if (ret.size() == 0)
			return null;
		if (ret.size() > 1)
			throw new RuntimeException("Not Unique Part");
		return ret.get(0);
	}

	public static ArrayList<String> getParts(String text, String start, String end, boolean includeStart, boolean includeEnd) {
		ArrayList<String> ret = new ArrayList<String>();
		int i = 0;
		do {
			MatchResult mrs = match(text, start, i);
			if (mrs == null)
				return ret;
			i = mrs.start();
			int j = i + mrs.group().length();
			int matchStart = i;
			if (!includeStart)
				matchStart += mrs.group().length();

			if (end == null) {
				ret.add(text.substring(matchStart));
				return ret;
			}
			MatchResult mre = match(text, end, j);
			if (mre == null)
				return ret;
			j = mre.start();
			int matchEnd = j;
			if (includeEnd)
				matchEnd += mre.group().length();

			String fragment = text.substring(matchStart, matchEnd).trim();
			ret.add(fragment);
			i = j;
		} while (i > -1);
		return ret;
	}

	protected static MatchResult match(String text, String pattern, int startIndex) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		if (m.find(startIndex))
			return m.toMatchResult();
		return null;
	}

	public static String getUniqueRegexMatch(String text, String pattern) {
		ArrayList<String> ret = getRegexMatches(text, pattern);
		if (ret.size() == 0)
			return null;
		if (ret.size() > 1)
			throw new RuntimeException("Not Unique Match");
		return ret.get(0);
	}

	public static ArrayList<String> getRegexMatches(String text, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		ArrayList<String> ret = new ArrayList<String>();
		int start = 0;
		while (m.find(start)) {
			ret.add(m.group(1));
			start = m.end();
		}
		return ret;
	}

	public static String stripFirstWord(String text) {
		int i = text.indexOf(" ");
		if (i > -1)
			return text.substring(i + 1);
		return text;
	}

	public static String stripFirstWordCond(String text, String firstWord) {
		try {
			int i = text.indexOf(" ");
			if (i > -1) {
				if (!text.substring(0, i).equals(firstWord))
					return text;
				return text.substring(i).trim();
			}
			return text;
		} catch (Exception e) {
			int a = 1;
		}
		return text;
	}

	public static String getFirstWord(String text) {
		int i = text.indexOf(" ");
		if (i > -1)
			return text.substring(0, i);
		return "";
	}

	public static String replaceNationNames(String text) {
		for (int i = 1; i <= 25; i++) {
			Nation n = NationMap.getNationFromNo(i);
			text = text.replace(n.getName(), "Nation_" + i);
		}
		return text;
	}

	public static int parseNationCode(String nation) {
		nation = nation.replace("Nation_", "");
		return Integer.parseInt(nation);
	}

	public static Nation getFromNationCode(String nation) {
		return NationMap.getNationFromNo(parseNationCode(nation));
	}
}
