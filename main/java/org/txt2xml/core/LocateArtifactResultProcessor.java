package org.txt2xml.core;


public class LocateArtifactResultProcessor extends LocateArtifactWithOwnerResultProcessor {
    @Override
	protected boolean findMatch() {
        if (this.matchedText != null) return false;
        String str = this.chars.toString();
        this.remainder = str;
        str = str.replace("\r\n", " ").replace("\n", " ").replace("  ", " ");
        String prefix = "was ordered to cast a lore spell. Locate Artifact - ";
        int i1 = str.indexOf(prefix); 
        if (i1 < 0) return false;
        int i2 = str.indexOf(".", i1 + prefix.length());
        if (i2 < 0) return false;
        if (str.indexOf("is possessed by", i1 + prefix.length()) >= 0) {
            return false;
        }
        this.matchedText = str.substring(i1 + prefix.length(), i2);
        this.remainder = str.substring(i2 + 1);
        return true;
    }

}
