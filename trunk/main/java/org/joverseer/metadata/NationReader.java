package org.joverseer.metadata;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;

import java.io.IOException;
import java.util.ArrayList;


public class NationReader implements MetadataReader {
    String[][] nations_2950 =
            new String[][]{
                    {"Unknown", "Un"},
                    {"Woodmen", "Wm"},
                    {"Northmen", "Nm"},
                    {"Riders of Rohan", "RoR"},
                    {"Dúnadan Rangers", "DR"},
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
                    {"Rhûn Easterlings", "Ea"},
                    {"Dunlendings", "Du"},
                    {"White Wizard", "WW"},
                    {"Khand Easterlings", "Kh"},
            };

    String[][] nations_1650 =
        new String[][]{
                {"Unknown", "Un"},
                {"Woodmen", "Wm"},
                {"Northmen", "Nm"},
                {"Γ‰othraim", "Eo"},
                {"Arthedain", "Ar"},
                {"Cardolan", "Ca"},
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
                {"Haradwaith", "Ha"},
                {"Dunlendings", "Du"},
                {"Rhudaur", "Ru"},
                {"Easterlings", "Ea"},
        };
    
    public void load(GameMetadata gm) throws IOException, MetadataReaderException {
        ArrayList nations = new ArrayList();
        if (gm.getGameType() == GameTypeEnum.game2950) {
            for (int i=0; i<26; i++) {
                String shortName = nations_2950[i][1];
                String name = nations_2950[i][0];
                Nation n = new Nation(i, name, shortName);
                if (n.getNumber() <= 10) {
                    n.setAllegiance(NationAllegianceEnum.FreePeople);
                } else if (n.getNumber() <= 20) {
                    n.setAllegiance(NationAllegianceEnum.DarkServants);
                } else {
                    n.setAllegiance(NationAllegianceEnum.Neutral);
                }
                nations.add(n);
            }
        } else if (gm.getGameType() == GameTypeEnum.game1650) {
            for (int i=0; i<26; i++) {
                String shortName = nations_1650[i][1];
                String name = nations_1650[i][0];
                Nation n = new Nation(i, name, shortName);
                if (n.getNumber() <= 10) {
                    n.setAllegiance(NationAllegianceEnum.FreePeople);
                } else if (n.getNumber() <= 20) {
                    n.setAllegiance(NationAllegianceEnum.DarkServants);
                } else {
                    n.setAllegiance(NationAllegianceEnum.Neutral);
                }
                nations.add(n);
            }
        }
        gm.setNations(nations);

    }
}
