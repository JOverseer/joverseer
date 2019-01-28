package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Artifact;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.domain.LocateArtifactResult;
import org.springframework.context.MessageSource;

/**
 * Table model for LA/LAT results
 *
 * @author Marios Skounakis
 */
public class LocateArtifactResultTableModel extends ItemTableModel {
	/**
	 *
	 */
	private static final long serialVersionUID = -3536612294036865492L;

	public LocateArtifactResultTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(LocateArtifactResult.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "turnNo", "hexNo", "spellName", "artifactNo", "artifactName", "owner", "artifactPowers" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, Integer.class, String.class, Integer.class, String.class, String.class, String.class };
	}

	public ArrayList<LocateArtifactResult> getResults(Artifact artifact) {
		ArrayList<LocateArtifactResult> lars = new ArrayList<LocateArtifactResult>();
		DerivedFromSpellInfoSource dfsis = (DerivedFromSpellInfoSource) artifact.getInfoSource();
		lars.add(getResult(artifact, dfsis));
		for (InfoSource is : dfsis.getOtherInfoSources()) {
			if (DerivedFromSpellInfoSource.class.isInstance(is)) {
				lars.add(getResult(artifact, (DerivedFromSpellInfoSource) is));
			}
		}
		return lars;
	}

	public LocateArtifactResult getResult(Artifact artifact, DerivedFromSpellInfoSource dfsis) {
		LocateArtifactResult lar = new LocateArtifactResult();
		lar.setHexNo(dfsis.getHexNo());
		lar.setArtifactName(artifact.getName());
		lar.setArtifactNo(artifact.getNumber());
		lar.setOwner(artifact.getOwner());
		lar.setSpellName(dfsis.getSpell() + " - " + dfsis.getCasterName());

		Game g = this.gameHolder.getGame();
		ArtifactInfo ai = g.getMetadata().findFirstArtifactByNumber(artifact.getNumber());
		if (ai != null) {
			lar.setArtifactPowers(ai.getPower1() != null ? ai.getPower1() : "");
			lar.setArtifactPowers(lar.getArtifactPowers() + (lar.getArtifactPowers().equals("") && ai.getPower2() != null ? "" : ", ") + (ai.getPower2() != null ? ai.getPower2() : ""));
		}

		return lar;
	}

}
