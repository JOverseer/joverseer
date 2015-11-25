package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.TurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;

/**
 * Holds information about Locate Artifact order results
 * 
 * @author Marios Skounakis
 */
public class LocateArtifactResultWrapper implements OrderResult {
	int hexNo;
	int artifactNo;
	String owner;
	String artifactName;

	public String getArtifactName() {
		return this.artifactName;
	}

	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}

	public int getArtifactNo() {
		return this.artifactNo;
	}

	public void setArtifactNo(int artifactNo) {
		this.artifactNo = artifactNo;
	}

	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public void updateGame(Game game, Turn turn, int nationNo, String casterName) {
		Character c = null;
		if (getOwner() != null && !getOwner().equals("")) {
			DerivedFromLocateArtifactInfoSource is1 = new DerivedFromLocateArtifactInfoSource(turn.getTurnNo(), nationNo, casterName, getHexNo());

			Container<Character> chars = turn.getCharacters();
			c = chars.findFirstByProperty("name", getOwner());
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
					// return;
				} else if (DerivedFromSpellInfoSource.class.isInstance(is)) {
					// spell
					// add info source...
					if (!((DerivedFromSpellInfoSource) is).contains(is1)) {
						((DerivedFromSpellInfoSource) is).addInfoSource(is1);
					}
				}
			}
		}
		DerivedFromLocateArtifactInfoSource is1 = new DerivedFromLocateArtifactInfoSource(turn.getTurnNo(), nationNo, casterName, getHexNo());

		Artifact a = turn.getArtifacts().findFirstByProperty("number", getArtifactNo());

		String artifactName1 = getArtifactName();
		if (artifactName1.equals("artifact")) {
			// dummy name
			// see if you can retrieve from ArtifactInfo
			ArtifactInfo ai = GameHolder.instance().getGame().getMetadata().getArtifacts().findFirstByProperty("no", getArtifactNo());
			if (ai != null) {
				artifactName1 = ai.getName();
			}
		}

		if (a == null) {
			// artifact not found, add
			a = new Artifact();
			a.setNumber(getArtifactNo());
			a.setName(artifactName1);
			a.setOwner(getOwner());
			a.setHexNo(c == null ? getHexNo() : c.getHexNo());
			a.setInfoSource(is1);
			turn.getArtifacts().addItem(a);
		} else {
			// artifact found, check info source
			InfoSource is = a.getInfoSource();
			if (a.getName().equals("artifact")) {
				a.setName(artifactName1);
			}
			if (TurnInfoSource.class.isInstance(is)) {
				// turn import, do nothing
				return;
			} else if (DerivedFromSpellInfoSource.class.isInstance(is)) {
				// spell
				// add info source...
				if (!((DerivedFromSpellInfoSource) is).contains(is1)) {
					((DerivedFromSpellInfoSource) is).addInfoSource(is1);
				}
			}
		}

	}
}
