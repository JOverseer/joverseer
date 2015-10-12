package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.domain.structuredOrderResults.IStructuredOrderResult;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.AsciiUtils;
import org.joverseer.support.NationMap;
import org.joverseer.support.infoSources.InfoSource;

/**
 * Stores information about a character reported in the turn results.
 * 
 * @author Marios Skounakis
 * 
 */
public class Character implements IBelongsToNation, IHasMapLocation, IMaintenanceCost, IEngineObject, Serializable {

	private static final long serialVersionUID = 2372359979734224557L;
	String id;
	String name;
	String title;

	Integer nationNo;

	int command;
	int commandTotal;
	int agent;
	int agentTotal;
	int emmisary;
	int emmisaryTotal;
	int mage;
	int mageTotal;
	int stealth;
	int stealthTotal;
	int challenge;
	Integer health;
	InfoSourceValue healthEstimate;
	ArrayList<IStructuredOrderResult> structuredOrderResults = new ArrayList<IStructuredOrderResult>();

	int x;
	int y;

	Boolean isHostage = null;

	ArrayList<Integer> artifacts = new ArrayList<Integer>();
	ArrayList<SpellProficiency> spells = new ArrayList<SpellProficiency>();
	ArrayList<String> hostages = new ArrayList<String>();
	int artifactInUse;

	InformationSourceEnum informationSource;
	InfoSource infoSource;

	Order[] orders = new Order[] { new Order(this), new Order(this), new Order(this) };

	int numberOfOrders = 2;

	String orderResults;
	String encounter;
	boolean startInfoDummy = false;

	CharacterDeathReasonEnum deathReason = CharacterDeathReasonEnum.NotDead;

	boolean refusingChallenges = false; // engine
	boolean inChallengeFight = false;

	public int getAgent() {
		return this.agent;
	}

	public void setAgent(int agent) {
		this.agent = agent;
	}

	public int getAgentTotal() {
		return this.agentTotal;
	}

	public void setAgentTotal(int agentTotal) {
		this.agentTotal = agentTotal;
	}

	public ArrayList<Integer> getArtifacts() {
		return this.artifacts;
	}

	public void setArtifacts(ArrayList<Integer> artifacts) {
		this.artifacts = artifacts;
	}

	public int getChallenge() {
		return this.challenge;
	}

	public void setChallenge(int challenge) {
		this.challenge = challenge;
	}

	public int getCommand() {
		return this.command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public int getCommandTotal() {
		return this.commandTotal;
	}

	public void setCommandTotal(int commandTotal) {
		this.commandTotal = commandTotal;
	}

	public int getEmmisary() {
		return this.emmisary;
	}

	public void setEmmisary(int emmisary) {
		this.emmisary = emmisary;
	}

	public int getEmmisaryTotal() {
		return this.emmisaryTotal;
	}

	public void setEmmisaryTotal(int emmisaryTotal) {
		this.emmisaryTotal = emmisaryTotal;
	}

	public Integer getHealth() {
		return this.health;
	}

	public void setHealth(Integer health) {
		this.health = health;
	}

	public InfoSourceValue getHealthEstimate() {
		return this.healthEstimate;
	}

	public void setHealthEstimate(InfoSourceValue healthEstimate) {
		this.healthEstimate = healthEstimate;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public InfoSource getInfoSource() {
		return this.infoSource;
	}

	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}

	public int getMage() {
		return this.mage;
	}

	public void setMage(int mage) {
		this.mage = mage;
	}

	public int getMageTotal() {
		return this.mageTotal;
	}

	public void setMageTotal(int mageTotal) {
		this.mageTotal = mageTotal;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<SpellProficiency> getSpells() {
		return this.spells;
	}

	public void setSpells(ArrayList<SpellProficiency> spells) {
		this.spells = spells;
	}

	public int getStealth() {
		return this.stealth;
	}

	public void setStealth(int stealth) {
		this.stealth = stealth;
	}

	public int getStealthTotal() {
		return this.stealthTotal;
	}

	public void setStealthTotal(int stealthTotal) {
		this.stealthTotal = stealthTotal;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public InformationSourceEnum getInformationSource() {
		return this.informationSource;
	}

	public void setInformationSource(InformationSourceEnum informationSource) {
		this.informationSource = informationSource;
	}

	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}

	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}

	public int getHexNo() {
		return getX() * 100 + getY();
	}

	public void setHexNo(int hexNo) {
		setX(hexNo / 100);
		setY(hexNo % 100);
	}

	public Order[] getOrders() {
		if (this.orders.length == 2) {
			this.orders = new Order[] { this.orders[0], this.orders[1], new Order(this) };
		}
		return this.orders;
	}

	public void setOrders(Order[] orders) {
		this.orders = orders;
	}

	public static String getIdFromName(String name) {
		String id = name.toLowerCase().substring(0, Math.min(5, name.length()));
		return AsciiUtils.convertNonAscii(id);
	}

	public static String getSpacePaddedIdFromId(String id) {
		return id + "     ".substring(0, 5 - id.length());
	}

	public String getEncounter() {
		return this.encounter;
	}

	public void setEncounter(String encounter) {
		this.encounter = encounter;
	}

	public boolean hasOrderResults() {
		return getOrderResults() != null;
	}

	public String getOrderResults() {
		return this.orderResults;
	}

	public String getCleanOrderResults() {
		if (getOrderResults() == null)
			return "";
		return getOrderResults().replace("\r\n", " ").replace("\n", " ").replace("  ", " ");
	}

	public void setOrderResults(String orderResults) {
		this.orderResults = orderResults;
	}

	public CharacterDeathReasonEnum getDeathReason() {
		return this.deathReason;
	}

	public void setDeathReason(CharacterDeathReasonEnum deathReason) {
		this.deathReason = deathReason;
	}

	public static void main(String[] args) {
		System.out.println(Character.getIdFromName("Michèle"));
	}

	public Boolean getHostage() {
		return this.isHostage;
	}

	public void setHostage(Boolean isHostage) {
		this.isHostage = isHostage;
	}

	public int getArtifactInUse() {
		return this.artifactInUse;
	}

	public void setArtifactInUse(int artifactInUse) {
		this.artifactInUse = artifactInUse;
	}

	public Nation getNation() {
		return NationMap.getNationFromNo(getNationNo());
	}

	public void setNation(Nation nation) {
		if (nation == null) {
			setNationNo(new Integer(0));
		} else {
			setNationNo(nation.getNumber());
		}
	}

	public ArrayList<IStructuredOrderResult> getStructuredOrderResults() {
		if (this.structuredOrderResults == null) {
			this.structuredOrderResults = new ArrayList<IStructuredOrderResult>();
		}
		return this.structuredOrderResults;
	}

	public ArrayList<String> getHostages() {
		if (this.hostages == null) {
			this.hostages = new ArrayList<String>();
		}
		return this.hostages;
	}

	public void setHostages(ArrayList<String> hostages) {
		this.hostages = hostages;
	}

	public int getNumberOfOrders() {
		if (this.numberOfOrders == 0)
			this.numberOfOrders = 2;
		return this.numberOfOrders;
	}

	public void setNumberOfOrders(int numberOfOrders) {
		this.numberOfOrders = numberOfOrders;
	}

	public boolean isStartInfoDummy() {
		return this.startInfoDummy;
	}

	public void setStartInfoDummy(boolean startInfoDummy) {
		this.startInfoDummy = startInfoDummy;
	}

	@Override
	public Integer getMaintenance() {
		return new Integer((getCommand() + getMage() + getAgent() + getEmmisary()) * 20);
	}

	@Override
	public Character clone() {
		Character c = new Character();
		c.setName(getName());
		c.setId(getId());
		c.setNationNo(getNationNo());
		c.setCommand(getCommand());
		c.setCommandTotal(getCommandTotal());
		c.setAgent(getAgent());
		c.setAgentTotal(getAgentTotal());
		c.setMage(getMage());
		c.setMageTotal(getMageTotal());
		c.setEmmisary(getEmmisary());
		c.setEmmisaryTotal(getEmmisaryTotal());
		c.setHealth(getHealth());
		c.setChallenge(getChallenge());
		c.setTitle(getTitle());
		c.setHexNo(getHexNo());
		c.setDeathReason(getDeathReason());
		c.setNumberOfOrders(getNumberOfOrders());
		c.setArtifactInUse(getArtifactInUse());
		c.setStealth(getStealth());
		c.setStealthTotal(getStealthTotal());
		c.setInfoSource(getInfoSource());
		c.setInformationSource(getInformationSource());
		c.setStartInfoDummy(isStartInfoDummy());
		c.setArtifacts((ArrayList<Integer>) getArtifacts().clone());
		for (SpellProficiency sp : getSpells()) {
			c.getSpells().add(new SpellProficiency(sp.getSpellId(), sp.getProficiency(), sp.getName()));
		}
		return c;
	}

	@Override
	public void initialize() {
		this.refusingChallenges = false;
	}

	public boolean isRefusingChallenges() {
		return this.refusingChallenges;
	}

	public void setRefusingChallenges(boolean refusingChallenges) {
		this.refusingChallenges = refusingChallenges;
	}

	public boolean isInChallengeFight() {
		return this.inChallengeFight;
	}

	public void setInChallengeFight(boolean inChallengeFight) {
		this.inChallengeFight = inChallengeFight;
	}

	public String getStatString() {
		Character c = this;
		String txt = "";
		txt += getStatText("C", c.getCommand(), c.getCommandTotal());
		txt += getStatText("A", c.getAgent(), c.getAgentTotal());
		txt += getStatText("E", c.getEmmisary(), c.getEmmisaryTotal());
		txt += getStatText("M", c.getMage(), c.getMageTotal());
		txt += getStatText("S", c.getStealth(), c.getStealthTotal());
		txt += getStatText("Cr", c.getChallenge(), c.getChallenge());
		if (c.getHealth() != null) {
			txt += " H" + c.getHealth();
		}
		if (c.getDeathReason() != null && c.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
			txt += " (" + c.getDeathReason().toString() + ")";
		}

		return txt;
	}

	private String getStatText(String prefix, int skill, int skillTotal) {
		if (skillTotal == 0 && skill == 0)
			return "";
		return prefix + skill + (skillTotal > skill ? "(" + skillTotal + ")" : "") + " ";
	}

	public boolean isDead() {
		return getDeathReason() != null && !getDeathReason().equals(CharacterDeathReasonEnum.NotDead);
	}

	public void addHostage(@SuppressWarnings("hiding") String name) {
		if (!getHostages().contains(name))
			getHostages().add(name);
	}

}
