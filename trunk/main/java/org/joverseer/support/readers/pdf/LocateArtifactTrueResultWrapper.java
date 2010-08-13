package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.TurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactTrueInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromRevealCharacterInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;

/**
 * Holds information about Locate Artifact True order results
 * 
 * @author Marios Skounakis
 */

public class LocateArtifactTrueResultWrapper extends LocateArtifactResultWrapper {
    public void updateGame(Game game, Turn turn, int nationNo, String casterName) {
        int hexNo = getHexNo();

        if (getOwner() != null && !getOwner().equals("")) {
            DerivedFromLocateArtifactTrueInfoSource is1 = new DerivedFromLocateArtifactTrueInfoSource(turn.getTurnNo(), nationNo, casterName, getHexNo());
            Container chars = turn.getContainer(TurnElementsEnum.Character);
            Character c = (Character)chars.findFirstByProperty("name", getOwner());
            if (c == null) {
                // character not found, add
                c = new Character();
                c.setName(getOwner());
                c.setId(Character.getIdFromName(getOwner()));
                c.setHexNo(hexNo);
                c.setInfoSource(is1);
                c.setNationNo(0);
                chars.addItem(c);
            } else {
                // character found
                // examine info source
                InfoSource is = c.getInfoSource();
                if (TurnInfoSource.class.isInstance(is)) {
                    // turn import, do nothing
                } else if (DerivedFromSpellInfoSource.class.isInstance(is)) {
                    // spell
                    // check if it is LA or RC
                    if (DerivedFromLocateArtifactInfoSource.class.isInstance(is) ||
                        DerivedFromRevealCharacterInfoSource.class.isInstance(is)) {
                        // replace info source and hexNo
                        c.setHexNo(hexNo);
                        c.setInfoSource(is1);
                    } else {
                        // info source is LAT or RCT
                        // add
                        if (!((DerivedFromSpellInfoSource)is).contains(is1)) {
                            ((DerivedFromSpellInfoSource)is).addInfoSource(is1);
                        }
                    }
                } 
            }
        }
        
        DerivedFromLocateArtifactTrueInfoSource is1 = new DerivedFromLocateArtifactTrueInfoSource(turn.getTurnNo(), nationNo, casterName, getHexNo());
        Container artis = turn.getContainer(TurnElementsEnum.Artifact);
        Artifact a = (Artifact)artis.findFirstByProperty("number", getArtifactNo());
        
        String artifactName = getArtifactName();
        if (artifactName.equals("artifact")) {
            // dummy name
            // see if you can retrieve from ArtifactInfo
            ArtifactInfo ai = (ArtifactInfo)GameHolder.instance().getGame().getMetadata().getArtifacts().findFirstByProperty("no", getArtifactNo());
            if (ai != null) {
                artifactName = ai.getName();
            }
        }
        
        if (a == null) {
            // artifact not found, add
            a = new Artifact();
            a.setNumber(getArtifactNo());
            a.setName(artifactName);
            a.setOwner(getOwner());
            a.setHexNo(getHexNo());
            a.setInfoSource(is1);
            artis.addItem(a);
        } else {
            // artifact found, check info source
            InfoSource is = a.getInfoSource();
            if (a.getName().equals("artifact")) {
                a.setName(artifactName);
            }
            if (TurnInfoSource.class.isInstance(is)) {
                // turn import, do nothing
                return;
            } else if (DerivedFromSpellInfoSource.class.isInstance(is)) {
                // spell
                // check if it is LA or RC
                if (DerivedFromLocateArtifactInfoSource.class.isInstance(is) ||
                    DerivedFromRevealCharacterInfoSource.class.isInstance(is)) {
                    // replace info source and hexNo
                    a.setHexNo(getHexNo());
                    a.setInfoSource(is1);
                } 
                // add
                if (!((DerivedFromSpellInfoSource)is).contains(is1)) {
                    ((DerivedFromSpellInfoSource)is).addInfoSource(is1);
                }
            } 
        }
    }
}
