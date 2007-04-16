package org.joverseer.support.info;

public class InfoUtils {
	public static Boolean isDragon(String charName) {
		Info info = InfoRegistry.instance().getInfo("dragons");
		if (info == null) return null;
		if (info.getRowIdx(charName) > -1) return true;
		return false;
	}
}
