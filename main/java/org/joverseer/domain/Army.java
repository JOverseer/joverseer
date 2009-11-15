package org.joverseer.domain;

import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.tools.CombatUtils;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Sores information about armies reported in the turn.
 * 
 * In addition to armies, Anchored Ships are represented as armies.
 * 
 * @author Marios Skounakis
 *
 */
public class Army implements IBelongsToNation, IHasMapLocation, IMaintenanceCost, Serializable {

    private static final long serialVersionUID = 5781285200976386027L;
    Integer nationNo;
    int x;
    int y;

    String commanderName;
    String commanderTitle;

    NationAllegianceEnum nationAllegiance;

    InformationSourceEnum informationSource;
    InfoSource infoSource;

    ArmySizeEnum size;
    int troopCount;
    boolean navy;
    
    int morale;
    Integer enHI;

    ArrayList<ArmyElement> elements = new ArrayList<ArmyElement>();

    ArrayList characters = new ArrayList();

    Integer food = null; // null when food not set
    Boolean fed = null; // null when fed not set
    Boolean cavalry = null; // null when cavalry not set

    public ArrayList getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList characters) {
        this.characters = characters;
    }

    public String getCommanderName() {
        return commanderName;
    }

    public void setCommanderName(String commanderName) {
        this.commanderName = commanderName;
        enHI = null;
    }

    public InformationSourceEnum getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(InformationSourceEnum informationSource) {
        this.informationSource = informationSource;
    }

    public InfoSource getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }

    public NationAllegianceEnum getNationAllegiance() {
        return nationAllegiance;
    }

    public void setNationAllegiance(NationAllegianceEnum nationAllegiance) {
        this.nationAllegiance = nationAllegiance;
    }

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public boolean isNavy() {
        return navy;
    }

    public void setNavy(boolean navy) {
        this.navy = navy;
    }

    public ArmySizeEnum getSize() {
        return size;
    }

    public void setSize(ArmySizeEnum size) {
        this.size = size;
    }

    public int getTroopCount() {
        return troopCount;
    }

    public void setTroopCount(int troopCount) {
        this.troopCount = troopCount;
    }
    
    

    
    public int getMorale() {
        return morale;
    }

    
    public void setMorale(int morale) {
        this.morale = morale;
        enHI = null;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getCommanderTitle() {
        return commanderTitle;
    }

    public void setCommanderTitle(String commanderTitle) {
        this.commanderTitle = commanderTitle;
    }

    public ArrayList<ArmyElement> getElements() {
        return elements;
    }
    
    public ArmyElement getElement(ArmyElementType type) {
        for (ArmyElement ae : getElements()) {
            if (ae.getArmyElementType() == type) {
                return ae;
            }
        }
        return null;
    }
    
    public void removeElement(ArmyElementType type) {
        ArmyElement e = null;
        for (ArmyElement ae : getElements()) {
            if (ae.getArmyElementType() == type) {
                e = ae;
                break;
            }
        }
        if (e != null) {
            getElements().remove(e);
        }
        enHI = null;
    }
    
    public void setElement(ArmyElementType type, int count) {
        if (count == 0) {
            removeElement(type);
            return;
        }
        for (ArmyElement ae : getElements()) {
            if (ae.getArmyElementType() == type) {
                ae.setNumber(count);
                return;
            }
        }
        getElements().add(new ArmyElement(type, count));
        enHI = null;
    }

    /**
     * Returns true if the army is fed, null if unknown, false if unfed.
     */
    public Boolean isFed() {
        return fed;
    }

    public void setFed(boolean fed) {
        this.fed = fed;
    }

    /**
     * Gets the value for the amount of food for this army. Null if unknown.
     */
    public Integer getFood() {
        return food;
    }

    public void setFood(Integer food) {
        this.food = food;
    }

    /**
     * Computes whether the army is fed.
     * - if isFed/setFed has been set, it returns this value
     * - if uses the food amount if known to compute if the army is fed
     * - otherwise returns null if food unknown or if it cannot compute the food consumption for the army
     * @return
     */
    public Boolean computeFed() {
        if (isFed() != null) {
            return isFed();
        }
        Integer foodConsumption = computeFoodConsumption();
        if (foodConsumption != null && getFood() != null) {
            return getFood() >= computeFoodConsumption();
        }
        return null;
    }
    
    /**
     * Computes the amount of food the army consumes (needs in order to be fed)
     * If this is not possible (i.e. elements are not known) return null 
     */
    public Integer computeFoodConsumption() {
        if (getElements().size() == 0) return null;
        int ret = 0;
        for (ArmyElement ae : getElements()) {
            ret += ae.getNumber() * ae.getArmyElementType().foodConsumption();
        }
        return ret;
    }

    public String getHexNo() {
        return String.valueOf(getX() * 100 + getY());
    }

    public void setHexNo(String hexNo) {
        int hexN = Integer.parseInt(hexNo);
        setX(hexN / 100);
        setY(hexN % 100);
    }

    /**
     * Computes whether the army is cavalry as follows:
     * - checks the isCavalry/setCavalry (if set, it returns this value)
     * - if above is null, checks the elements (if possible)
     * - if elements are null or empty it returns null 
     */
    public Boolean computeCavalry() {
        if (isCavalry() != null) {
            return isCavalry();
        }
        if (getElements().size() == 0) return null;
        // compute cavalry with respect to troop synthesis
        for (ArmyElement ae : getElements()) {
            if (ae.getArmyElementType() == ArmyElementType.WarMachimes) continue;
            if (!ae.getArmyElementType().isCavalry()) return false;
        }
        return true;
    }

    /**
     * Override for cavalry 
     */
    public Boolean isCavalry() {
        return cavalry;
    }

    /**
     * Override for cavalry 
     */
    public void setCavalry(Boolean cavalry) {
        this.cavalry = cavalry;
    }

    /**
     * Computes the number of men in the army
     * Returns 0 if no elements are known for this army
     */
    public int computeNumberOfMen() {
        int ret = 0;
        for (ArmyElement ae : getElements()) {
            if (ae.getArmyElementType() == ArmyElementType.WarMachimes ||
                    ae.getArmyElementType() == ArmyElementType.Warships ||
                    ae.getArmyElementType() == ArmyElementType.Transports) continue;
            ret += ae.getNumber();
        }
        return ret;
    }
    
    /**
     * Computes the number of ships in the army
     * Returns 0 if no elements are known for this army
     */
    public int computeNumberOfShips() {
        int ret = 0;
        for (ArmyElement ae : getElements()) {
            if (ae.getArmyElementType() == ArmyElementType.Warships ||
                    ae.getArmyElementType() == ArmyElementType.Transports) {
                ret += ae.getNumber();
            }
        }
        return ret;
    }
    
    /**
     * Deep clone for this army
     */
    public Army clone() {
    	try {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ObjectOutputStream os = new ObjectOutputStream(baos);
    		os.writeObject(this);
    		os.close();
    		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
    		Army a = (Army)is.readObject();
    		return a;
    	}
    	catch (Exception exc) {
    		return null;
    	}
    }
    
    /**
     * Returns true if this army represents Anchored Ships
     */
    public static boolean isAnchoredShips(Army a) {
        return a.getCommanderName().equals("[Anchored Ships]");
    }
    
    public int getInformationAmount(int turnNo) {
    	if (MetadataSource.class.isInstance(getInfoSource())) {
    		return -1;
    	}
    	int infoAmount = 0;
    	if (!getCommanderName().equals("Unknown (Map Icon)")) {
    		infoAmount++;
    	}
    	if (getNationNo() > 0) {
    		infoAmount++;
    	}
    	if (getElements().size() > 0) {
    		return 10;
    	}
    	if (infoAmount == 0 && turnNo == 0) {
			return -2;
    	}

    	return infoAmount;
    }
    
    public Integer getENHI() {
    	if (enHI == null) {
    		enHI = CombatUtils.getNakedHeavyInfantryEquivalent3(this);
    	}
    	return enHI;
    }
    
    public Integer getMaintenance() {
    	int cost = 0;
    	for (ArmyElement ae : getElements()) {
    		cost += ae.getMaintentance();
    	}
    	return cost;
    
    }
}
