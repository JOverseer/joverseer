package org.joverseer.support.readers.xml;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.InformationSourceEnum;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 30 Σεπ 2006
 * Time: 11:14:18 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ArmyWrapper {
    int hexID;
    int nation;
    int nationAllegience;
    int size;
    int troopCount;
    String commander;
    String commanderTitle;
    String extraInfo;
    int navy;
    int informationSource;
    String charsTravellingWith;

    public String getCharsTravellingWith() {
        return charsTravellingWith;
    }

    public void setCharsTravellingWith(String charsTravellingWith) {
        this.charsTravellingWith = charsTravellingWith;
    }

    public String getCommander() {
        return commander;
    }

    public void setCommander(String commander) {
        this.commander = commander;
    }

    public String getCommanderTitle() {
        return commanderTitle;
    }

    public void setCommanderTitle(String commanderTitle) {
        this.commanderTitle = commanderTitle;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public int getHexID() {
        return hexID;
    }

    public void setHexID(int hexID) {
        this.hexID = hexID;
    }

    public int getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(int informationSource) {
        this.informationSource = informationSource;
    }

    public int getNation() {
        return nation;
    }

    public void setNation(int nation) {
        this.nation = nation;
    }

    public int getNationAllegience() {
        return nationAllegience;
    }

    public void setNationAllegience(int nationAllegience) {
        this.nationAllegience = nationAllegience;
    }

    public int getNavy() {
        return navy;
    }

    public void setNavy(int navy) {
        this.navy = navy;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTroopCount() {
        return troopCount;
    }

    public void setTroopCount(int troopCount) {
        this.troopCount = troopCount;
    }

    public Army getArmy() {
        Army a = new Army();
        a.setX(getHexID() / 100);
        a.setY(getHexID() % 100);
        a.setCommanderName(getCommander());
        a.setCommanderTitle(getCommanderTitle());
        a.setTroopCount(getTroopCount());
        a.setNavy(getNavy() == 1);
        a.setNationNo(getNation());

        switch (getSize()) {
            case 0:
                a.setSize(ArmySizeEnum.unknown);
            case 2:
                a.setSize(ArmySizeEnum.small);
                break;
            case 3:
                a.setSize(ArmySizeEnum.army);
                break;
            case 4:
                a.setSize(ArmySizeEnum.largeArmy);
                break;
            case 5:
                a.setSize(ArmySizeEnum.hugeArmy);
                break;
            default:
                throw new RuntimeException("Invalid army size " + getSize());
        }

        switch (getInformationSource()) {
            case 0:
                a.setInformationSource(InformationSourceEnum.exhaustive);
                break;
            case 1:
                a.setInformationSource(InformationSourceEnum.detailed);
                break;
            case 3:
                a.setInformationSource(InformationSourceEnum.some);
                break;
            case 4:
                a.setInformationSource(InformationSourceEnum.limited);
                break;
        }

        //todo nation allegiance
        return a;
    }
}
