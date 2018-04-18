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
        int i = str.lastIndexOf(".");
        return str.substring(0, i+1);
    }
    
    private String stripMapTail(String str) {
        String pattern = "(?:\\d{4}\\s?\\n?){8,9}";
        return str.replaceAll(pattern, "");
    }
    
    @Override
	protected void resetMatching() {
        Pattern p = Pattern.compile(this.namePattern);
        String str = this.chars.toString();
        String datePattern = "\\d{1,2}/\\d{1,2}/\\d{4}  Game \\d+  Player \\d+  Turn \\d+  Page \\d+";
        str = str.replaceAll(datePattern, "");
        this.remainder = "";
        Matcher m1 = p.matcher(str);
        while (m1.find()) {
            this.matchIdxs.add(m1.start());
        }
        for (int i=0; i<this.matchIdxs.size() - 1; i++) {
            this.matches.add(stripTail(str.substring(this.matchIdxs.get(i), this.matchIdxs.get(i+1))));
        }
        if (this.matchIdxs.size() > 0) {
            this.matches.add(stripMapTail(str.substring(this.matchIdxs.get(this.matchIdxs.size()-1))));
        }
    }

    @Override
	protected boolean findMatch() {
        if (this.matchCount < this.matches.size()) {
            this.match = this.matches.get(this.matchCount);
            this.matchCount++;
            return true;
        }
        return false;
    }

    @Override
	protected CharSequence getMatchedText() {
        return this.match;
    }

    @Override
	protected CharSequence getRemainderText() {
        return this.remainder;
    }
    
    public static void main(String[] args) throws IOException {
        CharacterOrdersProcessor p = new CharacterOrdersProcessor();
        InputStreamReader is = new InputStreamReader(new FileInputStream("c:\\tst.txt"), "UTF-8");
        char[] b = new char[50000];
        is.read(b);
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
        is.close();
    }
    

}
