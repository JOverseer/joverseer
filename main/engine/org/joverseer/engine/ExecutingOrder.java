package org.joverseer.engine;

import java.util.ArrayList;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.LogManager;
import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.Company;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;

public abstract class ExecutingOrder implements Comparable<ExecutingOrder> {
	Character character;
	boolean executed = false;
	Order order;
	boolean error = false;
	String errorMessage = null;
	int startHex;
	int endHex;
	PopulationCenter populationCenter;
	PopulationCenter populationCenter2;
	Army army;
	Company company;
	Character character2;
	Army army2;
	boolean valid = true;
	boolean success = false;
	SpellProficiency spellProficiency;
	SpellInfo spellInfo;

	ArrayList<String> messages = new ArrayList<String>();

	public ExecutingOrder(Order order) {
		super();
		this.character = order.getCharacter();
		this.order = order;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public InfoSource getInfoSource(Turn turn) {
		return new XmlTurnInfoSource(turn.getTurnNo(), getNationNo());
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Character getCharacter() {
		return character;
	}

	public void setCharacter(Character character) {
		this.character = character;
	}

	public String getName() {
		return getCharacter().getName();
	}

	public int getStartHex() {
		return startHex;
	}

	public void setStartHex(int startHex) {
		this.startHex = startHex;
	}

	public int getEndHex() {
		return endHex;
	}

	public void setEndHex(int endHex) {
		this.endHex = endHex;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public int getNationNo() {
		return getCharacter().getNationNo();
	}

	public int getHex() {
		return getCharacter().getHexNo();
	}

	public int getOrderNo() {
		return getOrder().getOrderNo();
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public PopulationCenter getPop() {
		return populationCenter;
	}

	public void setPop(PopulationCenter populationCenter) {
		this.populationCenter = populationCenter;
	}

	public PopulationCenter getPop2() {
		return populationCenter2;
	}

	public void setPop2(PopulationCenter populationCenter2) {
		this.populationCenter2 = populationCenter2;
	}

	public Army getArmy() {
		return army;
	}

	public void setArmy(Army army) {
		this.army = army;
	}

	public Character getCharacter2() {
		return character2;
	}

	public void setCharacter2(Character character2) {
		this.character2 = character2;
	}

	public Army getArmy2() {
		return army2;
	}

	public void setArmy2(Army army2) {
		this.army2 = army2;
	}

	public String getParameter(int i) {
		return getOrder().getParameter(i);
	}

	public int getParameterInt(int i) {
		return getOrder().getParameterInt(i);
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setError(boolean error, String message) {
		setError(error);
		setErrorMessage(message);
		addMessage(message);
	}

	public void addMessage(String msg) {
		messages.add(renderMessage(msg));
	}

	public String renderMessage(String msg) {
		msg = renderVariable(msg, "{char}", getCharacter().getName());
		msg = renderVariable(msg, "{hex}", String.valueOf(getHex()));
		msg = renderVariable(msg, "{endhex}", String.valueOf(getEndHex()));
		msg = renderVariable(msg, "{starthex}", String.valueOf(getStartHex()));
		msg = renderVariable(msg, "{order}", String.valueOf(getOrderNo()));
		if (getCharacter2() != null)
			msg = renderVariable(msg, "{char2}", getCharacter2().getName());
		if (getPop() != null)
			msg = renderVariable(msg, "{pc}", getPop().getName());
		if (getPop2() != null)
			msg = renderVariable(msg, "{pc2}", getPop2().getName());
		if (getSpellInfo() != null)
			msg = renderVariable(msg, "{spell}", getSpellInfo().getName());

		String genderPrefix = "He";
		msg = renderVariable(msg, "{gp}", genderPrefix);
		msg = renderVariable(msg, "{gp2}", genderPrefix);
		return msg;
	}

	public String renderVariable(String msg, String variable, int value) {
		return msg.replace(variable, String.valueOf(value));
	}

	public String renderVariable(String msg, String variable, String value) {
		return msg.replace(variable, value);
	}

	public ArrayList<String> getMessages() {
		return messages;
	}

	public void execute(Game game, Turn turn) {
		setStartHex(getHex());
		try {
			if (getCharacter().getHealth() > 0)
				doExecute(game, turn);
		} catch (ErrorException exc) {
			setError(true, exc.getMessage());
		} catch (RuntimeException exc) {
			LogManager.getLogger(getClass()).error("Unexpected error executing order " + getCharacter().getName() + " " + getOrderNo() + ":" + exc.getMessage());
			LogManager.getLogger(getClass()).error(exc);
			exc.printStackTrace();
		}
		appendOrderResults();
	}

	public void consumeCost(Game game, Turn turn) throws ErrorException {
		int cost = new OrderCostCalculator().getOrderCost(getOrder(), turn);
		if (cost > 0) {
			NationEconomy ne = ExecutingOrderUtils.getNationEconomy(turn, getNationNo());
			int availableGold = ne.getAvailableGold();
			if (cost > availableGold) {
				throw new ErrorException("Not enough gold.");
			} else {
				ne.setAvailableGold(ne.getAvailableGold() - cost);
			}
		}
	}

	public String getOrderResults() {
		String ret = "";
		for (String msg : getMessages()) {
			ret += (ret.equals("") ? "" : " ") + msg;
		}
		return ret;
	}

	public void appendOrderResults() {
		String cor = getCharacter().getOrderResults();
		String or = getOrderResults();
		if (or == null || or.equals(""))
			return;
		cor += (cor.equals("") ? "" : "  ") + or;
		getCharacter().setOrderResults(cor);
	}

	public abstract void doExecute(Game game, Turn turn) throws ErrorException;

	public void checkParamInt(int value, String msg) throws ErrorException {
		if (value < 0)
			throw new ErrorException(msg);
	}

	public int getSequence() {
		return getOrderNo();
	}

	public int compareTo(ExecutingOrder eo) {
		return getSequence() - eo.getSequence();
	}

	public void clearMessages() {
		messages.clear();
	}

	public boolean loadArmyByCommander(Turn turn) {
		return (army = ExecutingOrderUtils.getArmy(turn, getHex(), getName())) != null;
	}

	public boolean loadArmy2ByCommander(Turn turn) {
		return (army2 = ExecutingOrderUtils.getArmy(turn, getCharacter2().getHexNo(), getCharacter2().getName())) != null;
	}

	public boolean loadArmyByMember(Turn turn) {
		return (army = ExecutingOrderUtils.findArmy(turn, getCharacter())) != null;
	}

	public boolean loadCompanyByCommander(Turn turn) {
		return (company = ExecutingOrderUtils.getCompany(turn, getName())) != null;
	}

	public boolean loadCompanyByMember(Turn turn) {
		return (company = ExecutingOrderUtils.findCompany(turn, getCharacter())) != null;
	}

	public boolean isArmyCommander(Turn turn) {
		return ExecutingOrderUtils.getArmy(turn, getHex(), getName()) != null;
	}

	public boolean isArmyCommander2(Turn turn) {
		return ExecutingOrderUtils.getArmy(turn, getCharacter2().getHexNo(), getCharacter2().getName()) != null;
	}

	public boolean isCompanyCommander(Turn turn) {
		return ExecutingOrderUtils.getCompany(turn, getName()) != null;
	}

	public boolean isCompanyCommander2(Turn turn) {
		return ExecutingOrderUtils.getCompany(turn, getCharacter2().getName()) != null;
	}

	public boolean loadPopCenter(Turn turn) {
		return loadPopCenter(turn, getHex());
	}

	public boolean loadPopCenter(Turn turn, int hexNo) {
		return (populationCenter = ExecutingOrderUtils.getPopCenter(turn, hexNo)) != null;
	}

	public boolean loadPopCenter2(Turn turn, int hexNo) {
		return (populationCenter2 = ExecutingOrderUtils.getPopCenter(turn, hexNo)) != null;
	}

	public boolean isAtCapital() {
		return isPopCenterOfNation() && populationCenter.getCapital();
	}

	public boolean isPopCenterOfNation() {
		return populationCenter != null && ExecutingOrderUtils.checkNation(populationCenter, getCharacter());
	}

	public boolean loadSpell(int spellId) {
		for (SpellProficiency sp : getCharacter().getSpells()) {
			if (sp.getSpellId() == spellId) {
				spellProficiency = sp;
				break;
			}
		}
		spellInfo = GameHolder.instance().getGame().getMetadata().getSpells().findFirstByProperty("number", spellId);
		return spellProficiency != null;
	}

	public SpellProficiency getSpellProficiency() {
		return spellProficiency;
	}

	public SpellInfo getSpellInfo() {
		return spellInfo;
	}

	public boolean isCommander() {
		boolean ret = getCharacter().getCommand() > 0;
		if (!ret) {
			addMessage("{char} could not complete the order because {gp} is not a commander.");
		}
		return ret;
	}

	public boolean isEmissary() {
		boolean ret = getCharacter().getEmmisary() > 0;
		if (!ret) {
			addMessage("{char} could not complete the order because {gp} is not an emissary.");
		}
		return ret;
	}

	public boolean isMage() {
		boolean ret = getCharacter().getMage() > 0;
		if (!ret) {
			addMessage("{char} could not complete the order because {gp} is not a mage.");
		}
		return ret;
	}

	public boolean isAgent() {
		boolean ret = getCharacter().getAgent() > 0;
		if (!ret) {
			addMessage("{char} could not complete the order because {gp} is not an agent.");
		}
		return ret;
	}

	public boolean isPopNotSieged() {
		boolean ret = getPop().isSieged();
		if (ret) {
			addMessage("{char} could not complete the order because {pc} is under siege.");
		}
		return !ret;
	}

	public boolean isPopCenter2OfNation() {
		return ExecutingOrderUtils.checkNation(populationCenter2, getCharacter());
	}

	public boolean areCharsOfSameNation() {
		return ExecutingOrderUtils.checkNation(getCharacter(), getCharacter2());
	}

	public NationEconomy getNationEconomy(Turn turn) {
		return ExecutingOrderUtils.getNationEconomy(turn, getNationNo());
	}

	public boolean loadCharacter2(Turn turn, String id) {
		setCharacter2(ExecutingOrderUtils.getCharacterById(turn, id));
		return getCharacter2() != null;
	}

	public boolean areCharsAtSameHex() {
		return ExecutingOrderUtils.checkSameHex(getCharacter(), getCharacter2());
	}

	public void modifyProperty(Object obj, String propertyName, int delta, int min, int max) {
		try {
			Object v = PropertyUtils.getProperty(obj, propertyName);
			Integer vi = (Integer) v;
			if (vi == null)
				vi = 0;
			vi += delta;
			if (vi < min)
				vi = min;
			if (vi > max)
				vi = max;
			PropertyUtils.setProperty(obj, propertyName, vi);
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	public void removeCharacter2FromGroups(Turn turn) {
		Company c = ExecutingOrderUtils.findCompany(turn, getCharacter2());
		if (c != null)
			ExecutingOrderUtils.removeCharacterFromCompany(turn, c, getCharacter2());

		Army a = ExecutingOrderUtils.findArmy(turn, getCharacter2());
		if (a != null)
			ExecutingOrderUtils.removeCharacterFromArmy(turn, a, getCharacter2());
	}

	public void removeCharacterFromGroups(Turn turn) {
		Company c = ExecutingOrderUtils.findCompany(turn, getCharacter());
		if (c != null) {
			addMessage("{char} left the company has was with.");
			ExecutingOrderUtils.removeCharacterFromCompany(turn, c, getCharacter());
		}

		Army a = ExecutingOrderUtils.findArmy(turn, getCharacter());
		if (a != null) {
			addMessage("{char} left the army has was with.");
			ExecutingOrderUtils.removeCharacterFromArmy(turn, a, getCharacter());
		}
	}

}
