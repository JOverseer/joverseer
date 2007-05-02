package org.joverseer.ui.listviews;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.domain.LocateArtifactResult;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;


public class LocateArtifactResultTableModel extends ItemTableModel {
    public LocateArtifactResultTableModel(MessageSource messageSource) {
        super(LocateArtifactResult.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"turnNo", "hexNo", "spellName", "artifactNo", "artifactName", "owner", "artifactPowers"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{Integer.class, Integer.class, String.class, Integer.class, String.class, String.class, String.class};
    }
    
    public LocateArtifactResult getResult(Artifact artifact) {
        LocateArtifactResult lar = new LocateArtifactResult();
        lar.setHexNo(artifact.getHexNo());
        lar.setArtifactName(artifact.getName());
        lar.setArtifactNo(artifact.getNumber());
        lar.setOwner(artifact.getOwner());
        
        if (DerivedFromSpellInfoSource.class.isInstance(artifact.getInfoSource())) {
            DerivedFromSpellInfoSource is = (DerivedFromSpellInfoSource)artifact.getInfoSource();
            lar.setSpellName(is.getSpell() + " - " + is.getCasterName());
        }
        
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        ArtifactInfo ai = (ArtifactInfo)g.getMetadata().getArtifacts().findFirstByProperty("no", artifact.getNumber());
        if (ai != null) {
            lar.setArtifactPowers(ai.getPower1() != null ? ai.getPower1() : "");
            lar.setArtifactPowers(
                    lar.getArtifactPowers() + 
                        (lar.getArtifactPowers().equals("") && ai.getPower2() != null ? "" : ", ") +
                        (ai.getPower2() != null ? ai.getPower2() : ""));
        }
        
        return lar;
    }
    

}
