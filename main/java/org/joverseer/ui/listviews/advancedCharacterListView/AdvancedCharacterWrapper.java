package org.joverseer.ui.listviews.advancedCharacterListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.support.infoSources.AgentActionInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.RumorInfoSource;
import org.joverseer.ui.domain.ArtifactWrapper;
import org.joverseer.ui.domain.EnemyAgentWrapper;

public class AdvancedCharacterWrapper implements IHasMapLocation, IBelongsToNation, IHasTurnNumber {

    String name;
    int hexNo;
    Integer nationNo;
    int turnNo;
    InfoSource infoSource;
    String id;

    HashMap<String, CharacterAttributeWrapper> attributes = new HashMap<String, CharacterAttributeWrapper>();

    ArrayList<ArtifactWrapper> artifacts = new ArrayList<ArtifactWrapper>();

    boolean companyCommander;
    boolean armyCommander;
    boolean companyMember;

    String orderResults;

    public boolean isArmyCommander() {
        return armyCommander;
    }

    public void setArmyCommander(boolean armyCommander) {
        this.armyCommander = armyCommander;
    }

    public HashMap<String, CharacterAttributeWrapper> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, CharacterAttributeWrapper> attributes) {
        this.attributes = attributes;
    }

    public void setAttribute(CharacterAttributeWrapper value) {
        CharacterAttributeWrapper cw = getAttribute(value.getAttribute());
        if (cw != null && cw.getTurnNo() > value.getTurnNo())
            return;
        getAttributes().put(value.getAttribute(), value);
    }

    public void setAttributeMax(CharacterAttributeWrapper value) {
        CharacterAttributeWrapper cw = getAttribute(value.getAttribute());
        if (cw != null) {
            if (Integer.class.isInstance(cw.getValue()) && Integer.class.isInstance(value.getValue())) {
                Integer oldValue = (Integer) cw.getValue();
                Integer newValue = (Integer) value.getValue();
                if (newValue > oldValue) {
                    getAttributes().put(value.getAttribute(), value);
                }
                return;
            }
        }
        getAttributes().put(value.getAttribute(), value);
    }

    public CharacterAttributeWrapper getAttribute(String attribute) {
        return getAttributes().get(attribute);
    }

    public boolean isCompanyCommander() {
        return companyCommander;
    }

    public void setCompanyCommander(boolean companyCommander) {
        this.companyCommander = companyCommander;
    }

    public boolean isCompanyMember() {
        return companyMember;
    }

    public void setCompanyMember(boolean companyMember) {
        this.companyMember = companyMember;
    }

    public int getHexNo() {
        return hexNo;
    }

    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    public InfoSource getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public String getOrderResults() {
        return orderResults;
    }

    public void setOrderResults(String orderResults) {
        this.orderResults = orderResults;
    }

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    public CharacterAttributeWrapper getCommand() {
        return getAttribute("command");
    }

    public CharacterAttributeWrapper getAgent() {
        return getAttribute("agent");
    }

    public CharacterAttributeWrapper getEmmisary() {
        return getAttribute("emmisary");
    }

    public CharacterAttributeWrapper getMage() {
        return getAttribute("mage");
    }

    public CharacterAttributeWrapper getStealth() {
        return getAttribute("stealth");
    }

    public CharacterAttributeWrapper getChallenge() {
        return getAttribute("challenge");
    }

    public CharacterAttributeWrapper getHealth() {
        return getAttribute("health");
    }

    public ArrayList<ArtifactWrapper> getArtifacts() {
        return artifacts;
    }

    private ArtifactWrapper getArtifact(int i) {
        Collections.sort(artifacts, new Comparator() {

            public int compare(Object o1, Object o2) {
                try {
                    return ((ArtifactWrapper) o1).getNumber() - ((ArtifactWrapper) o2).getNumber();
                } catch (Exception exc) {
                }
                ;
                return 0;
            }
        });
        if (artifacts.size() > i) {
            return artifacts.get(i);
        }
        return null;
    }

    public ArtifactWrapper getA0() {
        return getArtifact(0);
    }

    public ArtifactWrapper getA1() {
        return getArtifact(1);
    }

    public ArtifactWrapper getA2() {
        return getArtifact(2);
    }

    public ArtifactWrapper getA3() {
        return getArtifact(3);
    }

    public ArtifactWrapper getA4() {
        return getArtifact(4);
    }

    public ArtifactWrapper getA5() {
        return getArtifact(5);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return getHexNo() / 100;
    }

    public int getY() {
        return getHexNo() % 100;
    }

    public static ArrayList getWrappers() {
        Container ret = new Container(new String[] {"name", "turnNo", "id"});
        if (!GameHolder.hasInitializedGame())
            return ret.getItems();
        Game game = GameHolder.instance().getGame();
        for (int i = game.getCurrentTurn(); i >= 0; i--) {
            Turn t = game.getTurn(i);
            for (Character c : (ArrayList<Character>) t.getContainer(TurnElementsEnum.Character).getItems()) {
                AdvancedCharacterWrapper cw = (AdvancedCharacterWrapper) ret.findFirstByProperty("id", c.getId());
                if (cw == null) {
                    cw = new AdvancedCharacterWrapper();
                    cw.setId(c.getId());
                    cw.setName(c.getName());
                    cw.setNationNo(c.getNationNo());
                    cw.setHexNo(c.getHexNo());
                    cw.setTurnNo(t.getTurnNo());
                    cw.setInfoSource(c.getInfoSource());
                    if (c.getInformationSource() == InformationSourceEnum.exhaustive) {
                        addStats(cw, c, c.getInfoSource(), t.getTurnNo());
                    } else {
                        getStartStats(cw);
                        guessStatsFromTitle(cw, c, t.getTurnNo());
                    }
                    ret.addItem(cw);
                } else {
                    guessStatsFromTitle(cw, c, t.getTurnNo());
                }
            }

            Container thieves = EnemyAgentWrapper.getAgentWrappers();
            for (EnemyAgentWrapper eaw : (ArrayList<EnemyAgentWrapper>) thieves.getItems()) {
                AdvancedCharacterWrapper cw = (AdvancedCharacterWrapper) ret.findFirstByProperty("name", eaw.getName());
                if (cw == null) {
                    cw = new AdvancedCharacterWrapper();
                    cw.setId(Character.getIdFromName(eaw.getName()));
                    cw.setName(eaw.getName());
                    cw.setHexNo(eaw.getHexNo());
                    cw.setTurnNo(eaw.getTurnNo());
                    getStartStats(cw);
                    RumorInfoSource ris = new RumorInfoSource();
                    ris.setTurnNo(eaw.getTurnNo());
                    cw.setInfoSource(ris);
                    cw.setAttributeMax(new CharacterAttributeWrapper("agent", 10, eaw.getTurnNo(),
                            new AgentActionInfoSource(eaw.getReportedTurns())));
                    ret.addItem(cw);
                } else {
                    cw.setAttributeMax(new CharacterAttributeWrapper("agent", 10, eaw.getTurnNo(),
                            new AgentActionInfoSource(eaw.getReportedTurns())));
                }
            }
        }

        for (ArtifactWrapper aw : (ArrayList<ArtifactWrapper>) ArtifactWrapper.getArtifactWrappers().getItems()) {
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
        
        return ret.getItems();
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
        }
        ;
    }

    public static void getStartStats(AdvancedCharacterWrapper cw) {
        Character c = (Character) GameHolder.instance().getGame().getMetadata().getCharacters().findFirstByProperty(
                "id", cw.getId());
        if (c != null) {
            addStats(cw, c, new MetadataSource(), 0);
            if (cw.getNationNo() == null || cw.getNationNo() == 0) {
                cw.setNationNo(c.getNationNo());
            }
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


}
