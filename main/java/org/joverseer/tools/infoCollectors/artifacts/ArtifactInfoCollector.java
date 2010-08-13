package org.joverseer.tools.infoCollectors.artifacts;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;

/**
 * Collects information from all turns for artifacts and stores them in a container.
 * 
 * This class scans all turns and collates all information found with respect to artifacts. 
 * More specifically it:
 * - initializes the information from the artifact metadata
 * - updates powers, hex numbers and owners from each turn, keeping the latest info each time
 * - makes heavy use of info sources to maintain the source of each piece of information
 * 
 * In the end of the day it aggregates all intelligence for artifacts as found in:
 * - xml turns
 * - LA/LATs
 * - starting info
 * 
 * @author Marios Skounakis
 *
 */
public class ArtifactInfoCollector implements ApplicationListener {
    static Logger logger = Logger.getLogger(ArtifactInfoCollector.class);
    
    HashMap<Integer, Container> turnInfo = new HashMap<Integer, Container>();
    
    public static ArtifactInfoCollector instance() {
        return (ArtifactInfoCollector)Application.instance().getApplicationContext().getBean("artifactInfoCollector");
    }
    
    public ArrayList getWrappers() {
            return getWrappersForTurn(-1);
    }
    
    public ArrayList getWrappersForTurn(int turnNo) {
        Container ret = new Container(new String[] {"name", "turnNo", "number"});
        if (!GameHolder.hasInitializedGame())
            return ret.getItems();
        Game game = GameHolder.instance().getGame();
        if (turnNo == -1) turnNo = game.getCurrentTurn();
        if (!turnInfo.containsKey(turnNo)) {
            turnInfo.put(turnNo, computeWrappersForTurn(turnNo));
        }
        return turnInfo.get(turnNo).getItems();
    }
    
    public ArtifactWrapper getArtifactForTurn(int number, int turnNo) {
        if (!GameHolder.hasInitializedGame()) {
            return null;
        }
        getWrappersForTurn(turnNo);
        Container ret = turnInfo.get(turnNo);
        return (ArtifactWrapper)ret.findFirstByProperty("number", number);
    }
    
    public ArrayList<ArtifactWrapper> getArtifactsForOwnerAndTurn(String owner, int turnNo) {
        if (!GameHolder.hasInitializedGame()) {
            return new ArrayList<ArtifactWrapper>();
        }
        getWrappersForTurn(turnNo);
        Container ret = turnInfo.get(turnNo);
        return (ArrayList<ArtifactWrapper>)ret.findAllByProperty("owner", owner);
    }
    
    public Container computeWrappersForTurn(int turnNo) {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();

        MetadataSource ms = new MetadataSource();
        Container aws = new Container(new String[]{"number"});
        for (ArtifactInfo ai : (ArrayList<ArtifactInfo>)g.getMetadata().getArtifacts().getItems()) {
            ArtifactWrapper aw = new ArtifactWrapper();
            aw.setNumber(ai.getNo());
            aw.setName(ai.getName());
            aw.setOwner(ai.getOwner());
            aw.setPower1(ai.getPower1());
            aw.setPower2(ai.getPower2());
            aw.setInfoSource(ms);
            aw.setAlignment(ai.getAlignment());
            aw.setTurnNo(0);
            aws.addItem(aw);
        }
        
        // loop over turns
        for (int i=0; i<=turnNo; i++) {
            Turn t = g.getTurn(i);
            if (t == null) continue;
            for (Artifact a : (ArrayList<Artifact>)t.getContainer(TurnElementsEnum.Artifact).getItems()) {
                ArtifactWrapper aw = (ArtifactWrapper)aws.findFirstByProperty("number", a.getNumber());
                if (aw == null || a.getNumber() == 0) {
                    // try to find by name
                    aw = (ArtifactWrapper)aws.findFirstByProperty("name", a.getName());
                    if (aw == null) {
                        logger.error("Failed to find artifact wrapper for #" + a.getNumber() + " " + a.getName());
                        // create new
                        aw = new ArtifactWrapper();
                        aw.setNumber(a.getNumber());
                        aw.setName(a.getName());
                        aw.setTurnNo(i);
                        aws.addItem(aw);
                    }
                }
                
                aw.setOwner(a.getOwner());
                aw.setHexNo(a.getHexNo());
                aw.setInfoSource(a.getInfoSource());
                aw.setTurnNo(t.getTurnNo());
                Character c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", aw.getOwner());
                if (c != null) {
                    aw.setNationNo(c.getNationNo());
                    if (c.getNationNo() == null || c.getNationNo() == 0) {
                        c = (Character)g.getMetadata().getCharacters().findFirstByProperty("id", c.getId());
                        if (c != null) {
                            aw.setNationNo(c.getNationNo());
                        }
                    }
                }
                // owner is a nation (hidden artifact)
                Nation n = g.getMetadata().getNationByName(a.getOwner());
                if (n != null) {
                    aw.setNationNo(n.getNumber());
                }
            }
            
            for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
                for (Integer aid : c.getArtifacts()) {
                    ArtifactWrapper aw = (ArtifactWrapper)aws.findFirstByProperty("number", aid);
                    if (aw == null) continue;
                    aw.setOwner(c.getName());
                    aw.setHexNo(c.getHexNo());
                    aw.setInfoSource(c.getInfoSource());
                    aw.setNationNo(c.getNationNo());
                    aw.setTurnNo(t.getTurnNo());
                }
            }
        }
        return aws;
        
    }
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                turnInfo.clear();
            } 
        }
    }


}
