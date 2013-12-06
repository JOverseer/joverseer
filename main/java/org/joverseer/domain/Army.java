package org.joverseer.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.NationMap;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.tools.CombatUtils;

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

	/**
	 * Returns true if this army represents Anchored Ships
	 */
	public static boolean isAnchoredShips(Army a) {
		return a.getCommanderName().equals("[Anchored Ships]");
	}

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

	ArrayList<String> characters = new ArrayList<String>();
	Integer food = null; // null when food not set
	Boolean fed = null; // null when fed not set

	Boolean cavalry = null; // null when cavalry not set

	/**
	 * Deep clone for this army
	 */
	@Override
	public Army clone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(baos);
			os.writeObject(this);
			os.close();
			ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
			Army a = (Army) is.readObject();
			return a;
		} catch (Exception exc) {
			return null;
		}
	}

	/**
	 * Computes whether the army is cavalry as follows: - checks the
	 * isCavalry/setCavalry (if set, it returns this value) - if above is null,
	 * checks the elements (if possible) - if elements are null or empty it
	 * returns null
	 */
	public Boolean computeCavalry() {
		if (isCavalry() != null) {
			return isCavalry();
		}
		if (getElements() == null || getElements().size() == 0)
			return null;
		// compute cavalry with respect to troop synthesis
		for (ArmyElement ae : getElements()) {
			if (!ae.getArmyElementType().isTroop())
				continue;
			if (!ae.getArmyElementType().isCavalry())
				return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Computes whether the army is fed. - if isFed/setFed has been set, it
	 * returns this value - if uses the food amount if known to compute if the
	 * army is fed - otherwise returns null if food unknown or if it cannot
	 * compute the food consumption for the army
	 * 
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
		if (getElements().size() == 0)
			return new Integer(0);
		int ret = 0;
		for (ArmyElement ae : getElements()) {
			ret += ae.getNumber() * ae.getArmyElementType().foodConsumption();
		}
		return new Integer(ret);
	}

	/**
	 * Computes the number of men in the army Returns 0 if no elements are known
	 * for this army
	 */
	public int computeNumberOfMen() {
		int ret = 0;
		for (ArmyElement ae : getElements()) {
			if (ae.getArmyElementType().isTroop())
				ret += ae.getNumber();
		}
		return ret;
	}

	/**
	 * Computes the number of ships in the army Returns 0 if no elements are
	 * known for this army
	 */
	public int computeNumberOfShips() {
		int ret = 0;
		for (ArmyElement ae : getElements()) {
			if (ae.getArmyElementType() == ArmyElementType.Warships || ae.getArmyElementType() == ArmyElementType.Transports) {
				ret += ae.getNumber();
			}
		}
		return ret;
	}

	public ArrayList<String> getCharacters() {
		return this.characters;
	}

	public String getCommanderName() {
		return this.commanderName;
	}

	public String getCommanderTitle() {
		return this.commanderTitle;
	}

	public ArmyElement getElement(ArmyElementType type) {
		for (ArmyElement ae : getElements()) {
			if (ae.getArmyElementType() == type) {
				return ae;
			}
		}
		return null;
	}

	public ArrayList<ArmyElement> getElements() {
		return this.elements;
	}

	public Integer getENHI() {
		if (this.enHI == null) {
			Character c = (Character) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", getCommanderName());
			if (c != null) {
				this.enHI = new Integer(CombatUtils.getNakedHeavyInfantryEquivalent(this, c));
			} else {
				this.enHI = new Integer(CombatUtils.getNakedHeavyInfantryEquivalent2(this));
			}
		}
		return this.enHI;
	}

	/**
	 * Gets the value for the amount of food for this army. Null if unknown.
	 */
	public Integer getFood() {
		return this.food;
	}

	public String getHexNo() {
		return String.valueOf(getX() * 100 + getY());
	}

	public int getInformationAmount(int turnNo) {
		if (MetadataSource.class.isInstance(getInfoSource())) {
			return -1;
		}
		int infoAmount = 0;
		if (!getCommanderName().equals("Unknown (Map Icon)")) {
			infoAmount++;
		}
		if (getNationNo().intValue() > 0) {
			infoAmount++;
		}
		if (getElements().size() > 0) {
			return 10;
		}
		if (getTroopCount() > 0) {
			infoAmount++;
		}
		if (infoAmount == 0 && turnNo == 0) {
			return -2;
		}

		return infoAmount;
	}

	public InformationSourceEnum getInformationSource() {
		return this.informationSource;
	}

	public InfoSource getInfoSource() {
		return this.infoSource;
	}

	@Override
	public Integer getMaintenance() {
		int cost = 0;
		for (ArmyElement ae : getElements()) {
			cost = ae.getMaintentance();
		}
		return new Integer(cost);

	}

	public int getMorale() {
		return this.morale;
	}

	public Nation getNation() {
		return NationMap.getNationFromNo(getNationNo());
	}

	public NationAllegianceEnum getNationAllegiance() {
		return this.nationAllegiance;
	}

	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}

	public int getNumber(ArmyElementType aet) {
		ArmyElement ae = getElement(aet);
		if (ae == null)
			return 0;
		return ae.getNumber();
	}

	public int getNumberOfRequiredTransports() {
		int requiredTransportCapacity = 0;
		for (ArmyElement ae : getElements()) {
			requiredTransportCapacity += ae.getRequiredTransportCapacity();
		}
		int requiredTransports = (int) Math.ceil(requiredTransportCapacity / 250d);
		return requiredTransports;
	}

	public ArmySizeEnum getSize() {
		return this.size;
	}

	public int getTroopCount() {
		return this.troopCount;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	/**
	 * Override for cavalry
	 */
	public Boolean isCavalry() {
		return this.cavalry;
	}

	/**
	 * Returns true if the army is fed, null if unknown, false if unfed.
	 */
	public Boolean isFed() {
		return this.fed;
	}

	public boolean isNavy() {
		return this.navy;
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
		this.enHI = null;
	}

	public void resetENHI() { // used by engine
		this.enHI = null;
	}

	public void resetFed() {
		this.fed = null;
	}

	/**
	 * Override for cavalry
	 */
	public void setCavalry(Boolean cavalry) {
		this.cavalry = cavalry;
	}

	public void setCharacters(ArrayList<String> characters) {
		this.characters = characters;
	}

	public void setCommanderName(String commanderName) {
		this.commanderName = commanderName;
		this.enHI = null;
	}

	public void setCommanderTitle(String commanderTitle) {
		this.commanderTitle = commanderTitle;
	}

	public void setElement(ArmyElement ae) {
		if (ae == null)
			return;
		ArmyElement e = getElement(ae.getArmyElementType());
		if (e != null)
			this.elements.remove(e);
		this.elements.add(ae);
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
		this.enHI = null;
	}

	public void setFed(boolean fed) {
		this.fed = new Boolean(fed);
	}

	public void setFood(Integer food) {
		this.food = food;
	}

	public void setHexNo(String hexNo) {
		int hexN = Integer.parseInt(hexNo);
		setX(hexN / 100);
		setY(hexN % 100);
	}

	public void setInformationSource(InformationSourceEnum informationSource) {
		this.informationSource = informationSource;
	}

	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}

	public void setMorale(int morale) {
		this.morale = morale;
		this.enHI = null;
	}

	public void setNationAllegiance(NationAllegianceEnum nationAllegiance) {
		this.nationAllegiance = nationAllegiance;
	}

	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}

	public void setNavy(boolean navy) {
		this.navy = navy;
	}

	public void setSize(ArmySizeEnum size) {
		this.size = size;
	}

	public void setTroopCount(int troopCount) {
		this.troopCount = troopCount;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
}
