package org.joverseer.support;

import java.util.ArrayList;
import java.util.Locale;
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
			Nation n = NationMap.getNationFromNo(Integer.valueOf(j));
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
		int i = text.indexOf(" ");
		if (i > -1) {
			if (!text.substring(0, i).equals(firstWord))
				return text;
			return text.substring(i).trim();
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
			Nation n = NationMap.getNationFromNo(Integer.valueOf(i));
			text = text.replace(n.getName(), "Nation_" + i);
		}
		return text;
	}

	public static int parseNationCode(String nation) {
		nation = nation.replace("Nation_", "");
		return Integer.parseInt(nation);
	}

	public static Nation getFromNationCode(String nation) {
		return NationMap.getNationFromNo(Integer.valueOf(parseNationCode(nation)));
	}
	public static String trimLeading(String s) {
		int i;
		for (i=0;i<s.length();i++) {
			if ( !Character.isWhitespace(s.charAt(i))) {
				return s.substring(i);
			}
		}
		return "";
	}
	/**
	 * return a character id hash based on the accented (or not) character name.
	 * basically strips off accents and converts to lowercase independently of current locale.
	 * @param s
	 * @return
	 */
	public static String toCharacterId(String s) {
		/*
		 * from Character.class scriptStarts and wikipedia https://en.wikipedia.org/wiki/List_of_Unicode_characters
		 * 00-40 = common
		 * 41-5A = latin1 uppercase no accents
		 * 5B-60 = common punctuation including grave accent
		 * 61-7A = latin1 lowercase no accents
		 * 7B-A9 = common incl &nbsp
		 * AA-AA = latin1 &ordf
		 * AB-B9 = common
		 * BA-BA = latin1 &ordm
		 * BB-BF = common punctuation incl &&cedil and acute accent
		 * C0-D6 = latin1 uppercase accents
		 * D7-D7 = common &times
		 * D8-F6 = latin1 uppercase accented
		 * F7-F7 = common &divide
		 * F8-FF = latin1 lowercase accented 
		 */
		int candidateIndex = 0;
		int outputIndex = 0;
		char output[] = new char[5];
		int candidateCodepoint;
		int lastInputStringIndex = s.length()-1;
		while (outputIndex < 5) {
			if (candidateIndex > lastInputStringIndex) {
				//we've run out so pad with spaces to 5 characters.
				candidateCodepoint = 0x0020; 
			} else {
				candidateCodepoint = Character.codePointAt(s,candidateIndex);
			}
			// most likely first
			if (Character.UnicodeBlock.of(candidateCodepoint).equals(Character.UnicodeBlock.BASIC_LATIN)) {
				// no accents, just to lower needed
				output[outputIndex++] = Character.toLowerCase((char)candidateCodepoint);
				continue;
			}
			if (Character.UnicodeBlock.of(candidateCodepoint).equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT)) {
				//accents
				boolean isRecognised = true;
				switch (candidateCodepoint) {
				case 0x00C0: // &Agrave;
				case 0x00C1:
				case 0x00C2:
				case 0x00C3:
				case 0x00C4:
				case 0x00C5:
				case 0x00E0: // &agrave;
				case 0x00E1:
				case 0x00E2:
				case 0x00E3:
				case 0x00E4:
				case 0x00E5:
							output[outputIndex++] = 'a';
							break;
				case 0x00C7: // &CCedil;
				case 0x00E7: // &ccedil;
					output[outputIndex++] = 'c';
					break;
				case 0x00C8: // &EGrave;
				case 0x00C9:
				case 0x00CA:
				case 0x00CB:
				case 0x00E8: // &egrave;
				case 0x00E9:
				case 0x00EA:
				case 0x00EB:
						output[outputIndex++] = 'e';
						break;
				case 0x00CC: // &IGrave;
				case 0x00CD:
				case 0x00CE:
				case 0x00CF:
				case 0x00EC: // &igrave;
				case 0x00ED:
				case 0x00EE:
				case 0x00EF:
						output[outputIndex++] = 'i';
						break;
				case 0x00D0: // &ETH;
					output[outputIndex++] = 'd';
					break;
				case 0x00D1: // &Ntilde;
				case 0x00F1:
					output[outputIndex++] = 'n';
					break;
				case 0x00D2: // &Ograve;
				case 0x00D3:
				case 0x00D4:
				case 0x00D5:
				case 0x00D6:
				case 0x00D8:
				case 0x00F0:
				case 0x00F2:
				case 0x00F3:
				case 0x00F4:
				case 0x00F5:
				case 0x00F6:
				case 0x00F8:
						output[outputIndex++] = 'o';
						break;
				case 0x00D9: // &Ugrave;
				case 0x00DA:
				case 0x00DB:
				case 0x00DC:
				case 0x00F9: // &ugrave;
				case 0x00FA:
				case 0x00FB:
				case 0x00FC:
						output[outputIndex++] = 'u';
						break;
				case 0x00DD: // &Yacute;
					output[outputIndex++] = 'y';
					break;
				default:
					isRecognised = false;
					break;
				}
				if (isRecognised) {
					continue;
				}
			}
		
			// here - we don't recognise it - skip
			candidateIndex++;
		}
		
		return String.valueOf(output); 
	}
	
}
