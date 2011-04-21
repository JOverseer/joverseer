package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Artifact;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.domain.LocateArtifactResult;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;

/**
 * Table model for LA/LAT results
 * 
 * @author Marios Skounakis
 */
public class LocateArtifactResultTableModel extends ItemTableModel {
	public LocateArtifactResultTableModel(MessageSource messageSource) {
		super(LocateArtifactResult.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "turnNo", "hexNo", "spellName", "artifactNo", "artifactName", "owner", "artifactPowers" };
	}

	@Override
	@SuppressWarnings("unchecked")
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

		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		ArtifactInfo ai = g.getMetadata().getArtifacts().findFirstByProperty("no", artifact.getNumber());
		if (ai != null) {
			lar.setArtifactPowers(ai.getPower1() != null ? ai.getPower1() : "");
			lar.setArtifactPowers(lar.getArtifactPowers() + (lar.getArtifactPowers().equals("") && ai.getPower2() != null ? "" : ", ") + (ai.getPower2() != null ? ai.getPower2() : ""));
		}

		return lar;
	}

}
