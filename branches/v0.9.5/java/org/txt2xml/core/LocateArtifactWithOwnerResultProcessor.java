package org.txt2xml.core;


public class LocateArtifactWithOwnerResultProcessor extends Processor {
    String matchedText = null;
    String remainder = null;
    
    

    protected void resetMatching() {
        matchedText = null;
        String remainder = null;
        super.resetMatching();
    }

    protected boolean findMatch() {
        if (matchedText != null) return false;
        String str = chars.toString();
        remainder = str;
        str = str.replace("\r\n", " ").replace("\n", " ").replace("  ", " ");
        String prefix = "was ordered to cast a lore spell. Locate Artifact - ";
        int i1 = str.indexOf(prefix); 
        if (i1 < 0) return false;
        int i2 = str.indexOf(".", i1 + prefix.length());
        if (i2 < 0) return false;
        if (str.indexOf("is possessed by", i1 + prefix.length()) <0) {
            return false;
        }
        matchedText = str.substring(i1 + prefix.length(), i2);
        remainder = str.substring(i2 + 1);
        return true;
    }

    protected CharSequence getMatchedText() {
        return matchedText;
    }

    protected CharSequence getRemainderText() {
        return remainder;
    }

}
