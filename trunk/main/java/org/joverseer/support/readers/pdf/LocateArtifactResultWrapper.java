package org.joverseer.support.readers.pdf;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.TurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactTrueInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;

public class LocateArtifactResultWrapper implements OrderResult {
	int hexNo;
	int artifactNo;
	String owner;
	String artifactName;
	
	public String getArtifactName() {
		return artifactName;
	}
	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}
	public int getArtifactNo() {
		return artifactNo;
	}
	public void setArtifactNo(int artifactNo) {
		this.artifactNo = artifactNo;
	}
	public int getHexNo() {
		return hexNo;
	}
	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void updateGame(Turn turn, int nationNo, String casterName) {
            if (getOwner() != null && !getOwner().equals("")) {
                DerivedFromLocateArtifactInfoSource is1 = new DerivedFromLocateArtifactInfoSource(turn.getTurnNo(), nationNo, casterName, getHexNo());

                Container chars = turn.getContainer(TurnElementsEnum.Character);
                Character c = (Character)chars.findFirstByProperty("name", getOwner());
                if (c == null) {
                    // character not found, add
                    c = new Character();
                    c.setName(getOwner());
                    c.setId(Character.getIdFromName(getOwner()));
                    c.setHexNo(getHexNo());
                    c.setInfoSource(is1);
                    c.setNationNo(0);
                    chars.addItem(c);
                } else {
                    // character found
                    // examine info source
                    InfoSource is = c.getInfoSource();
                    if (TurnInfoSource.class.isInstance(is)) {
                        // turn import, do nothing
                        return;
                    } else if (DerivedFromSpellInfoSource.class.isInstance(is)) {
                        // spell
                        // add info source...
                        if (!((DerivedFromSpellInfoSource)is).contains(is1)) {
                            ((DerivedFromSpellInfoSource)is).addInfoSource(is1);
                        }
                    } 
                }
            }
            DerivedFromLocateArtifactInfoSource is1 = new DerivedFromLocateArtifactInfoSource(turn.getTurnNo(), nationNo, casterName, getHexNo());

            Container artis = turn.getContainer(TurnElementsEnum.Artifact);
            Artifact a = (Artifact)artis.findFirstByProperty("number", getArtifactNo());
            if (a == null) {
                // artifact not found, add
                a = new Artifact();
                a.setNumber(getArtifactNo());
                a.setName(getArtifactName());
                a.setOwner(getOwner());
                a.setHexNo(getHexNo());
                a.setInfoSource(is1);
                artis.addItem(a);
            } else {
                // artifact found, check info source
                InfoSource is = a.getInfoSource();
                if (TurnInfoSource.class.isInstance(is)) {
                    // turn import, do nothing
                    return;
                } else if (DerivedFromSpellInfoSource.class.isInstance(is)) {
                    // spell
                    // add info source...
                    if (!((DerivedFromSpellInfoSource)is).contains(is1)) {
                        ((DerivedFromSpellInfoSource)is).addInfoSource(is1);
                    }
                } 
            }

        }
}
