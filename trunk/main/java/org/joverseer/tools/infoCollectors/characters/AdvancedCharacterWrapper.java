package org.joverseer.tools.infoCollectors.characters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.joverseer.domain.Army;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Company;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;
import org.joverseer.domain.InfoSourceValue;
import org.joverseer.support.infoSources.DerivedFromArmyInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.DerivedFromWoundsInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.ui.domain.CompanyWrapper;

/**
 * Wraps information about a character. Used by the character info collector.
 * 
 * Character attributes are stored as CharacterAttributeWrappers.
 * 
 * This class stores the character's: - name - location - nation - id - whether
 * it is a starting char or not - death reason - company the char is travelling
 * with - army the char is commanding - artifacts the char is carrying - health
 * estimates (from wounds) - order results
 * 
 * @author Marios Skounakis
 * 
 */
public class AdvancedCharacterWrapper implements IHasMapLocation, IBelongsToNation, IHasTurnNumber {

	String name;
	int hexNo;
	Integer nationNo;
	int turnNo;
	InfoSource infoSource;
	String id;
	boolean hostage = false;
	String hostageHolderName = null;

	boolean isStartChar = false;

	HashMap<String, CharacterAttributeWrapper> attributes = new HashMap<String, CharacterAttributeWrapper>();

	ArrayList<ArtifactWrapper> artifacts = new ArrayList<ArtifactWrapper>();

	Company company;
	Army army;

	CharacterDeathReasonEnum deathReason;

	String orderResults;
	InfoSourceValue healthEstimate;

	boolean isChampion = false;

	public Army getArmy() {
		return this.army;
	}

	public CharacterDeathReasonEnum getDeathReason() {
		return this.deathReason;
	}

	public void setDeathReason(CharacterDeathReasonEnum deathReason) {
		this.deathReason = deathReason;
	}

	public void setArmy(Army army) {
		this.army = army;
	}

	public HashMap<String, CharacterAttributeWrapper> getAttributes() {
		return this.attributes;
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

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public InfoSource getInfoSource() {
		return this.infoSource;
	}

	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}

	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}

	public String getOrderResults() {
		return this.orderResults;
	}

	public void setOrderResults(String orderResults) {
		this.orderResults = orderResults;
	}

	@Override
	public int getTurnNo() {
		return this.turnNo;
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

	public Integer getDragonPotential() {
		int dragonPotential = 0;
		Object command = getCommand() == null ? null : getCommand().getTotalValue();
		if (command != null) {
			dragonPotential += Integer.parseInt(command.toString());
		}
		Object agent = getAgent() == null ? null : getAgent().getTotalValue();
		if (agent != null) {
			dragonPotential += Integer.parseInt(agent.toString());
		}
		Object emissary = getEmmisary() == null ? null : getEmmisary().getTotalValue();
		if (emissary != null) {
			dragonPotential += Integer.parseInt(emissary.toString());
		}
		Object mage = getMage() == null ? null : getMage().getTotalValue();
		if (mage != null) {
			dragonPotential += Integer.parseInt(mage.toString());
		}
		return dragonPotential;
	}

	public ArrayList<ArtifactWrapper> getArtifacts() {
		return this.artifacts;
	}

	private ArtifactWrapper getArtifact(int i) {
		Collections.sort(this.artifacts, new Comparator<ArtifactWrapper>() {

			@Override
			public int compare(ArtifactWrapper o1, ArtifactWrapper o2) {
				try {
					return o1.getNumber() - o2.getNumber();
				} catch (Exception exc) {
				}
				;
				return 0;
			}
		});
		if (this.artifacts.size() > i) {
			return this.artifacts.get(i);
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
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int getX() {
		return getHexNo() / 100;
	}

	@Override
	public int getY() {
		return getHexNo() % 100;
	}

	public String getTravellingWith() {
		if (getCompany() != null) {
			CompanyWrapper cw = new CompanyWrapper(getCompany());
			return "Company: " + cw.getCommander() + " - " + cw.getMemberStr();
		}
		;
		if (getArmy() != null) {
			String chars = "";
			for (String c : getArmy().getCharacters()) {
				chars += (chars.equals("") ? "" : ", ") + c;
			}
			return "Army: " + getArmy().getCommanderName() + (chars.equals("") ? "" : " - " + chars);
		}
		if (isHostage()) {
			if (getHostageHolderName().equals("unknown"))
				return "Hostage";
			return "Hostage of " + getHostageHolderName();
		}
		return "";
	}

	public boolean getStartChar() {
		return this.isStartChar;
	}

	public void setStartChar(boolean isStartChar) {
		this.isStartChar = isStartChar;
	}

	public InfoSourceValue getHealthEstimate() {
		return this.healthEstimate;
	}

	public void setHealthEstimate(InfoSourceValue healthEstimate) {
		this.healthEstimate = healthEstimate;
	}

	public boolean isHostage() {
		return this.hostage;
	}

	public String getHostageHolderName() {
		return this.hostageHolderName;
	}

	public void setHostage(boolean hostage, String holder) {
		this.hostage = hostage;
		this.hostageHolderName = holder;
	}

	public boolean isChampion() {
		return this.isChampion;
	}

	public void setChampion(boolean isChampion) {
		this.isChampion = isChampion;
	}

	public String getStatString() {
		AdvancedCharacterWrapper acw = this;
		String txt = "";
		txt += getStatTextFromCharacterWrapper("C", acw.getCommand());
		txt += getStatTextFromCharacterWrapper("A", acw.getAgent());
		txt += getStatTextFromCharacterWrapper("E", acw.getEmmisary());
		txt += getStatTextFromCharacterWrapper("M", acw.getMage());
		txt += getStatTextFromCharacterWrapper("S", acw.getStealth());
		txt += getStatTextFromCharacterWrapper("Cr", acw.getChallenge());
		String healthTxt = getStatTextFromCharacterWrapper("H", acw.getHealth());
		;
		if (!healthTxt.equals("")) {
			txt += healthTxt;
		} else if (acw.getHealthEstimate() != null) {
			InfoSourceValue isv = acw.getHealthEstimate();
			DerivedFromWoundsInfoSource dwis = (DerivedFromWoundsInfoSource) isv.getInfoSource();
			if (dwis.getTurnNo() + 2 >= acw.getTurnNo()) {
				txt += " " + isv.getValue() + "(t" + dwis.getTurnNo() + ") ";
			}
		}
		if (acw.getDeathReason() != null && acw.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
			txt += " (" + acw.getDeathReason().toString() + ")";
		}
		return txt;
	}

	private String getStatTextFromCharacterWrapper(String prefix, CharacterAttributeWrapper caw) {
		String v = caw == null || caw.getValue() == null ? "" : caw.getValue().toString();
		if (v.equals("0"))
			v = "";
		if (caw != null && caw.getValue() != null) {
			InfoSource is = caw.getInfoSource();
			if (DerivedFromTitleInfoSource.class.isInstance(is)) {
				v += "+";
			} else if (RumorActionInfoSource.class.isInstance(is)) {
				v += "+";
			} else if (DerivedFromArmyInfoSource.class.isInstance(is)) {
				v += "+";
			}
		}
		if (caw != null && caw.getTotalValue() != null) {
			if (!caw.getTotalValue().toString().equals(caw.getValue().toString()) && !caw.getTotalValue().toString().equals("0")) {
				v += "(" + caw.getTotalValue().toString() + ")";
			}
		}
		if (!v.equals("")) {
			v = prefix + v + " ";
		}
		return v;
	}

	public void appendOrderResult(String text) {
		String oc = getOrderResults();
		if (oc == null)
			oc = "";
		oc += (oc.equals("") ? "" : "\n") + text;
		setOrderResults(oc);
	}
}
