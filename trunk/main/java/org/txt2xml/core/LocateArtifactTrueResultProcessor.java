package org.txt2xml.core;

public class LocateArtifactTrueResultProcessor extends LocateArtifactTrueWithOwnerResultProcessor {
	String matchedText = null;
	String remainder = null;

	@Override
	protected void resetMatching() {
		matchedText = null;
		remainder = null;
		super.resetMatching();
	}

	@Override
	protected boolean findMatch() {
		if (matchedText != null)
			return false;
		String str = chars.toString();
		remainder = str;
		str = str.replace("\r\n", " ").replace("\n", " ").replace("  ", " ");
		String prefix = "was ordered to cast a lore spell. Locate Artifact True - ";
		int i1 = str.indexOf(prefix);
		if (i1 < 0)
			return false;
		int i2 = str.indexOf(".", i1 + prefix.length());
		if (i2 < 0)
			return false;
		if (str.indexOf("is possessed", i1 + prefix.length()) >= 0) {
			return false;
		}
		matchedText = str.substring(i1 + prefix.length(), i2);
		remainder = str.substring(i2 + 1);
		return true;
	}

	@Override
	protected CharSequence getMatchedText() {
		return matchedText;
	}

	@Override
	protected CharSequence getRemainderText() {
		return remainder;
	}

}
