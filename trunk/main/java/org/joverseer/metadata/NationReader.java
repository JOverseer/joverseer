package org.joverseer.metadata;

import org.joverseer.metadata.domain.Nation;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 20 Οκτ 2006
 * Time: 11:33:58 μμ
 * To change this template use File | Settings | File Templates.
 */
public class NationReader implements MetadataReader {
    String[][] nations_2950 =
            new String[][]{
                    {"Unknown", "Un"},
                    {"Woodmen", "Wm"},
                    {"Northmen", "Nm"},
                    {"Riders of Rohan", "RoR"},
                    {"Dunadan Rangers", "DR"},
                    {"Silvan Elves", "Sil"},
                    {"Northern Gondor", "NG"},
                    {"Southern Gondor", "SG"},
                    {"Dwarves", "Dwa"},
                    {"Sinda Elves", "Sin"},
                    {"Noldo Elves", "No"},
                    {"Witch-king", "WK"},
                    {"Dragon Lord", "DL"},
                    {"Dog Lord", "DoL"},
                    {"Cloud Lord", "CL"},
                    {"Blind Sorcerer", "BS"},
                    {"Ice King", "IK"},
                    {"Quiet Avenger", "QA"},
                    {"Fire King", "FK"},
                    {"Long Rider", "LR"},
                    {"Dark Lieutenants", "DkL"},
                    {"Corsairs", "Co"},
                    {"Rhun Easterlings", "Ea"},
                    {"Dunlendings", "Du"},
                    {"White Wizard", "WW"},
                    {"Khand Eastelings", "Kh"},
            };

    public void load(GameMetadata gm) {
        ArrayList nations = new ArrayList();
        if (gm.getGameType() == GameTypeEnum.game2950) {
            for (int i=0; i<26; i++) {
                String shortName = nations_2950[i][1];
                String name = nations_2950[i][0];
                Nation n = new Nation(i, name, shortName);
                nations.add(n);
            }
        }
        gm.setNations(nations);

    }
}
