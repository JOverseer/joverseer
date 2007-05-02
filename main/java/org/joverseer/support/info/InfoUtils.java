package org.joverseer.support.info;

public class InfoUtils {

    public static Boolean isDragon(String charName) {
        Info info = InfoRegistry.instance().getInfo("dragons");
        if (info == null)
            return null;
        if (info.getRowIdx(charName) > -1)
            return true;
        return false;
    }

    public static String getCharacterStatsFromTitle(String title) {
        Info info = InfoRegistry.instance().getInfo("characterTitles");
        if (info == null)
            return null;
        for (int i = 1; i < info.getColumnHeaders().size(); i++) {
            for (int j = 1; j < info.getRowHeaders().size(); j++) {
                if (info.getValue(j, i).equals(title)) {
                    return info.getValue(j, 0);
                }
            }
        }
        return null;
    }

    public static String getCharacterStatsTypeFromTitle(String title) {
        Info info = InfoRegistry.instance().getInfo("characterTitles");
        if (info == null)
            return null;
        for (int i = 1; i < info.getColumnHeaders().size(); i++) {
            for (int j = 1; j < info.getRowHeaders().size(); j++) {
                if (info.getValue(j, i).equals(title)) {
                    return info.getValue(0, i);
                }
            }
        }
        return null;
    }

    public static String getTroopTypeFromDescription(String description) {
        Info info = InfoRegistry.instance().getInfo("troopTypeDescriptions");
        if (info == null)
            return null;
        for (int j = 1; j < info.getRowHeaders().size(); j++) {
            if (info.getValue(j, 3).equals("description")) {
                return info.getValue(j, 2);
            }
        }
        return null;
    }
}
