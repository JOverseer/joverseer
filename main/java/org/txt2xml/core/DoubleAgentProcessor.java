package org.txt2xml.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleAgentProcessor extends Processor {

	String match = null;
	String remainder = null;
	@Override
	
	protected void resetMatching() {
		remainder = chars.toString();
	}
	
	protected boolean findMatch() {
		String str = remainder;
		String namePattern="(\\p{L}+(?:[\\s\\-']\\p{L}+)*) of [the ]?\\p{L}+(?:[\\s\\-]\\p{L}+)* @ \\d{4}";
		Pattern p = Pattern.compile(namePattern);
		Matcher m = p.matcher(str);
		int i = -1;
        if (m.find()) {
        	i = m.start();
        }
        int j = -1;
        if (m.find()) {
        	j = m.start();
        }
        if (i > -1 && j > -1) {
        	match = str.substring(i, j);
        	remainder = str.substring(j);
        	return true;
        } else if (i > -1 && j == -1)  
        {
        	match = str.substring(i);
        	remainder = "";
        	return true;
        } else {
        	remainder = str;
        	return false;
        }
	}

	@Override
	protected CharSequence getMatchedText() {
		return match;
	}

	@Override
	protected CharSequence getRemainderText() {
		return null;
	}

	
	public static void main(String[] args) throws IOException {
        DoubleAgentProcessor p = new DoubleAgentProcessor();
        InputStreamReader is = new InputStreamReader(new FileInputStream("c:\\users\\mscoon\\desktop\\tst.txt"), "UTF-8");
        char[] b = new char[50000];
        int l = is.read(b);
        String str = new String(b);
        //str = str.substring(20000);
        String datePattern = "\\d{1,2}/\\d{1,2}/\\d{4}  Game \\d+  Player \\d+  Turn \\d+  Page \\d+";
        str = str.replaceAll(datePattern, "");
        //str = str.replace("\r\n", " ");
        p.chars = str;
        p.resetMatching();
        while (p.findMatch()) {
            System.out.println("*************************");
            System.out.println(p.getMatchedText());
        }
    }
}
