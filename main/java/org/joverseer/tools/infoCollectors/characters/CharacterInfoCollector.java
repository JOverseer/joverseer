package org.joverseer.tools.infoCollectors.characters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Company;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.RumorInfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactInfoCollector;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.EnemyCharacterRumorWrapper;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;


public class CharacterInfoCollector implements ApplicationListener {

    HashMap<Integer, Container> turnInfo = new HashMap<Integer, Container>();
    
    public static CharacterInfoCollector instance() {
        return (CharacterInfoCollector)Application.instance().getApplicationContext().getBean("characterInfoCollector");
    }
    
    public ArrayList getWrappers() {
            return (ArrayList)getWrappersForTurn(-1);
    }
    
    public ArrayList getWrappersForTurn(int turnNo) {
        Container ret = new Container(new String[] {"name", "turnNo", "id"});
        if (!GameHolder.hasInitializedGame())
            return ret.getItems();
        Game game = GameHolder.instance().getGame();
        if (turnNo == -1) turnNo = game.getCurrentTurn();
        if (!turnInfo.containsKey(turnNo)) {
            turnInfo.put(turnNo, computeWrappersForTurn(turnNo));
        }
        return turnInfo.get(turnNo).getItems();
    }
    
    public AdvancedCharacterWrapper getCharacterForTurn(String name, int turnNo) {
        if (!GameHolder.hasInitializedGame()) {
            return null;
        }
        getWrappersForTurn(turnNo);
        Container ret = turnInfo.get(turnNo);
        return (AdvancedCharacterWrapper)ret.findFirstByProperty("name", name);
    }
    
    public Container computeWrappersForTurn(int turnNo) {
        Container ret = new Container(new String[] {"name", "turnNo", "id"});
        if (!GameHolder.hasInitializedGame())
            return ret;
        Game game = GameHolder.instance().getGame();
        if (turnNo == -1) turnNo = game.getCurrentTurn();
        for (int i = turnNo; i >= 0; i--) {
            Turn t = game.getTurn(i);
            for (Character c : (ArrayList<Character>) t.getContainer(TurnElementsEnum.Character).getItems()) {
                ArrayList<AdvancedCharacterWrapper> matches = 
                    (ArrayList<AdvancedCharacterWrapper>)ret.findAllByProperty("id", c.getId());
                
                AdvancedCharacterWrapper cw = null;
                int smallestTurnNo = 1000;
                for (AdvancedCharacterWrapper cwi : matches) {
                    if (cw == null || smallestTurnNo > cwi.getTurnNo()) {
                        cw = cwi;
                        smallestTurnNo = cw.getTurnNo();
                    }
                }
                if (cw == null || c.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
                    cw = new AdvancedCharacterWrapper();
                    cw.setId(c.getId());
                    cw.setName(c.getName());
                    cw.setNationNo(c.getNationNo());
                    cw.setHexNo(c.getHexNo());
                    cw.setTurnNo(t.getTurnNo());
                    cw.setInfoSource(c.getInfoSource());
                    cw.setDeathReason(c.getDeathReason());
                    if (c.getInformationSource() == InformationSourceEnum.exhaustive) {
                        addStats(cw, c, c.getInfoSource(), t.getTurnNo());
                    } else {
                        getStartStats(cw);
                        guessStatsFromTitle(cw, c, t.getTurnNo());
                    }
                    if (i == game.getCurrentTurn() && c.getOrderResults() != null && !c.getOrderResults().equals("")) {
                        cw.setOrderResults(c.getOrderResults());
                    }
                    ret.addItem(cw);
                } else {
                    guessStatsFromTitle(cw, c, t.getTurnNo());
                }
            }
            
            // only for latest turn
            if (i == game.getCurrentTurn()) {
                    // assign companies 
                    for (Company comp : (ArrayList<Company>)t.getContainer(TurnElementsEnum.Company).getItems()) {
                        AdvancedCharacterWrapper cw = (AdvancedCharacterWrapper) ret.findFirstByProperty("name", comp.getCommander());
                        if (cw != null) {
                                cw.setCompany(comp);
                        }
                        for (String member : comp.getMembers()) {
                                cw = (AdvancedCharacterWrapper) ret.findFirstByProperty("name", member);
                                if (cw != null) {
                                        cw.setCompany(comp);
                                }
                        }
                    }
                    
                    // assign armies
                    for (Army army : (ArrayList<Army>)t.getContainer(TurnElementsEnum.Army).getItems()) {
                        AdvancedCharacterWrapper cw = (AdvancedCharacterWrapper) ret.findFirstByProperty("name", army.getCommanderName());
                        if (cw != null) {
                                cw.setArmy(army);
                        }
                        for (String travellingWith : (ArrayList<String>)army.getCharacters()) {
                                cw = (AdvancedCharacterWrapper) ret.findFirstByProperty("name", travellingWith);
                                if (cw != null) {
                                        cw.setArmy(army);
                                }
                        }
                    }
            }

            Container thieves = EnemyCharacterRumorWrapper.getAgentWrappers();
            for (EnemyCharacterRumorWrapper eaw : (ArrayList<EnemyCharacterRumorWrapper>) thieves.getItems()) {
                if (eaw.getTurnNo() != i) continue;
                AdvancedCharacterWrapper cw = (AdvancedCharacterWrapper) ret.findFirstByProperty("name", eaw.getName());
                if (cw == null) {
                    cw = new AdvancedCharacterWrapper();
                    cw.setId(Character.getIdFromName(eaw.getName()));
                    cw.setName(eaw.getName());
                    cw.setHexNo(eaw.getHexNo());
                    cw.setTurnNo(eaw.getTurnNo());
                    getStartStats(cw);
                    if (cw.getNationNo() == null) {
                        cw.setNationNo(0);
                    }
                    RumorInfoSource ris = new RumorInfoSource();
                    ris.setTurnNo(eaw.getTurnNo());
                    cw.setInfoSource(ris);
                    if (eaw.getCharType().equals("agent")) {
                        cw.setAttributeMax(new CharacterAttributeWrapper("agent", 10, eaw.getTurnNo(),
                                        new RumorActionInfoSource(eaw.getReportedTurns())));
                    } else {
                        cw.setAttributeMax(new CharacterAttributeWrapper("emmisary", 10, eaw.getTurnNo(),
                                        new RumorActionInfoSource(eaw.getReportedTurns())));
                    }
                    ret.addItem(cw);
                } else {
                    if (eaw.getCharType().equals("agent")) {
                        cw.setAttributeMax(new CharacterAttributeWrapper("agent", 10, eaw.getTurnNo(),
                                        new RumorActionInfoSource(eaw.getReportedTurns())));
                    } else {
                        cw.setAttributeMax(new CharacterAttributeWrapper("emmisary", 10, eaw.getTurnNo(),
                                        new RumorActionInfoSource(eaw.getReportedTurns())));
                    }
                }
            }
        }

        for (ArtifactWrapper aw : (ArrayList<ArtifactWrapper>) ArtifactInfoCollector.instance().getWrappersForTurn(game.getCurrentTurn())) {
            if (aw.getOwner() != null) {
                AdvancedCharacterWrapper cw = (AdvancedCharacterWrapper) ret.findFirstByProperty("name", aw.getOwner());
                if (cw != null) {
                    cw.getArtifacts().add(aw);
                }
            }
        }

        for (AdvancedCharacterWrapper acw : (ArrayList<AdvancedCharacterWrapper>) ret.getItems()) {
            if (acw.getChallenge() == null || acw.getChallenge().getInfoSource() == null
                    || acw.getChallenge().getInfoSource().getTurnNo() < game.getCurrentTurn()) {
                computeChallengeRank(acw, game.getCurrentTurn());
            }
        }
        
        return ret;
    }
    
    private static int getStatValue(CharacterAttributeWrapper caw) {
        if (caw == null) return 0;
        if (caw.getTotalValue() == null) {
            if (caw.getValue() == null) {
                return 0;
            }
            return (Integer)caw.getValue();
        }
        return Math.max((Integer)caw.getTotalValue(), (Integer)caw.getValue());
    }

    public static void computeChallengeRank(AdvancedCharacterWrapper cw, int turnNo) {
        int c = getStatValue(cw.getCommand());
        int a = getStatValue(cw.getAgent());
        int e = getStatValue(cw.getEmmisary());
        int m = getStatValue(cw.getMage());
        a = a * 3 / 4;
        e = e / 2;

        int[] stats = new int[] {c, a, e, m};
        Arrays.sort(stats);
        int challenge = stats[3] + (stats[0] + stats[1] + stats[2]) / 4;
        cw.setAttribute(new CharacterAttributeWrapper("challenge", challenge, challenge, turnNo,
                new ComputedInfoSource()));
    }

    public static void guessStatsFromTitle(AdvancedCharacterWrapper cw, Character c, int turnNo) {
        String statRange = InfoUtils.getCharacterStatsFromTitle(c.getTitle());
        String statType = InfoUtils.getCharacterStatsTypeFromTitle(c.getTitle());
        if (statRange == null)
            return;
        String[] parts = statRange.split("-");
        int stat = Integer.parseInt(parts[0]);
        DerivedFromTitleInfoSource tis = new DerivedFromTitleInfoSource(c.getTitle());
        tis.setTurnNo(turnNo);
        if (statType.equals("Commander")) {
            cw.setAttributeMax(new CharacterAttributeWrapper("command", stat, stat, turnNo, tis));
        } else if (statType.equals("Agent")) {
            cw.setAttributeMax(new CharacterAttributeWrapper("agent", stat, stat, turnNo, tis));
        } else if (statType.equals("Mage")) {
            cw.setAttributeMax(new CharacterAttributeWrapper("mage", stat, stat, turnNo, tis));
        } else if (statType.equals("Emmissary")) {
            cw.setAttributeMax(new CharacterAttributeWrapper("emmisary", stat, stat, turnNo, tis));
        };
    }

    public static void getStartStats(AdvancedCharacterWrapper cw) {
        Character c = (Character) GameHolder.instance().getGame().getMetadata().getCharacters().findFirstByProperty(
                "id", cw.getId());
        if (c != null) {
            addStats(cw, c, new MetadataSource(), 0);
            if (cw.getNationNo() == null || cw.getNationNo() == 0) {
                cw.setNationNo(c.getNationNo());
            }
            cw.setStartChar(true);
        }
    }

    public static void addStats(AdvancedCharacterWrapper cw, Character c, InfoSource is, int turnNo) {
        cw.setAttribute(new CharacterAttributeWrapper("command", c.getCommand(), c.getCommandTotal(), turnNo, is));

        cw.setAttribute(new CharacterAttributeWrapper("agent", c.getAgent(), c.getAgentTotal(), turnNo, is));

        cw.setAttribute(new CharacterAttributeWrapper("emmisary", c.getEmmisary(), c.getEmmisaryTotal(), turnNo, is));

        cw.setAttribute(new CharacterAttributeWrapper("mage", c.getMage(), c.getMageTotal(), turnNo, is));

        cw.setAttribute(new CharacterAttributeWrapper("stealth", c.getStealth(), c.getStealthTotal(), turnNo, is));

        cw.setAttribute(new CharacterAttributeWrapper("challenge", c.getChallenge(), turnNo, is));

        cw.setAttribute(new CharacterAttributeWrapper("health", c.getHealth(), turnNo, is));
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
