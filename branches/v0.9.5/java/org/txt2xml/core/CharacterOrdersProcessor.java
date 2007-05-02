package org.txt2xml.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CharacterOrdersProcessor extends Processor {
    String namePattern="((?:\\p{L}[\\s\\r ]+$)?\\p{L}+ Ranks\\s+:\\s+Command \\d+(?: \\(\\d+\\))?\\s+Agent \\d+(?: \\(\\d+\\))?\\s+Emissary \\d+(?: \\(\\d+\\))?\\s+Mage \\d+(?: \\(\\d+\\))?)";
    String namePattern2="(\\p{L}+(?:[\\s\\-']\\p{L}+)*) was located in the";
    Matcher m;
    String match;
    String remainder;
    ArrayList<Integer> matchIdxs = new ArrayList<Integer>();
    ArrayList<String> matches = new ArrayList<String>();
    int matchCount = 0;
    
    private String stripTail(String str) {
        int i = str.lastIndexOf(". ");
        return str.substring(0, i+1);
    }
    
    private String stripMapTail(String str) {
        String pattern = "(?:\\d{4}\\s?\\n?){8,9}";
        return str.replaceAll(pattern, "");
    }
    
    protected void resetMatching() {
        Pattern p = Pattern.compile(namePattern);
        String str = chars.toString();
        String datePattern = "\\d{1,2}/\\d{1,2}/\\d{4}  Game \\d+  Player \\d+  Turn \\d+  Page \\d+";
        str = str.replaceAll(datePattern, "");
        remainder = "";
        Matcher m = p.matcher(str);
        while (m.find()) {
            matchIdxs.add(m.start());
        }
        for (int i=0; i<matchIdxs.size() - 1; i++) {
            matches.add(stripTail(str.substring(matchIdxs.get(i), matchIdxs.get(i+1))));
        }
        if (matchIdxs.size() > 0) {
            matches.add(stripMapTail(str.substring(matchIdxs.get(matchIdxs.size()-1))));
        }
    }

    protected boolean findMatch() {
        if (matchCount < matches.size()) {
            match = matches.get(matchCount);
            matchCount++;
            return true;
        }
        return false;
    }

    protected CharSequence getMatchedText() {
        return match;
    }

    protected CharSequence getRemainderText() {
        return remainder;
    }
    
    public static void main(String[] args) throws IOException {
        CharacterOrdersProcessor p = new CharacterOrdersProcessor();
        InputStreamReader is = new InputStreamReader(new FileInputStream("c:\\tst.txt"), "UTF-8");
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
