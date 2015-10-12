package org.joverseer.tools.infoCollectors.characters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.joverseer.domain.Army;
import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Company;
import org.joverseer.domain.InfoSourceValue;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.support.infoSources.DerivedFromArmyInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.DerivedFromWoundsInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.PdfTurnInfoSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.RumorInfoSource;
import org.joverseer.support.readers.pdf.CombatWrapper;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactInfoCollector;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.EnemyCharacterRumorWrapper;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;

/**
 * Collects information about characters from each turn and aggregates into a
 * comprehensive report.
 * 
 * More specifically: - It starts from the current turn and goes back one turn
 * at a time - Every time a piece of information is found, it is updated on the
 * respective char (e.g. a title found for the character)
 * 
 * @author Marios Skounakis
 * 
 */
public class CharacterInfoCollector implements ApplicationListener {

	HashMap<Integer, Container<AdvancedCharacterWrapper>> turnInfo = new HashMap<Integer, Container<AdvancedCharacterWrapper>>();

	public static CharacterInfoCollector instance() {
		return (CharacterInfoCollector) Application.instance().getApplicationContext().getBean("characterInfoCollector");
	}

	public ArrayList<AdvancedCharacterWrapper> getWrappers() {
		return getWrappersForTurn(-1);
	}

	public ArrayList<AdvancedCharacterWrapper> getWrappersForTurn(int turnNo) {
		Container<AdvancedCharacterWrapper> ret = new Container<AdvancedCharacterWrapper>(new String[] { "name", "turnNo", "id" });
		if (!GameHolder.hasInitializedGame())
			return ret.getItems();
		Game game = GameHolder.instance().getGame();
		if (turnNo == -1)
			turnNo = game.getCurrentTurn();
		if (!this.turnInfo.containsKey(turnNo)) {
			this.turnInfo.put(turnNo, computeWrappersForTurn(turnNo));
		}
		return this.turnInfo.get(turnNo).getItems();
	}

	public AdvancedCharacterWrapper getCharacterForTurn(String name, int turnNo) {
		if (!GameHolder.hasInitializedGame()) {
			return null;
		}
		getWrappersForTurn(turnNo);
		Container<AdvancedCharacterWrapper> ret = this.turnInfo.get(turnNo);
		return ret.findFirstByProperty("name", name);
	}

	public AdvancedCharacterWrapper getLatestCharacter(String name, int maxTurnNo) {
		if (!GameHolder.hasInitializedGame()) {
			return null;
		}
		if (maxTurnNo == -1)
			maxTurnNo = GameHolder.instance().getGame().getCurrentTurn();
		while (maxTurnNo > 0) {
			AdvancedCharacterWrapper acw = getCharacterForTurn(name, maxTurnNo);
			if (acw != null)
				return acw;
			maxTurnNo--;
		}
		return null;
	}

	/**
	 * Computes the wrappers for the given turn In more detail: - Parse all
	 * characters and adds/updates the relevant character wrapper - Parse all
	 * characters and use their hostages to update the relevant character
	 * wrapper (last turn only) - Parse all companies and update character
	 * wrappers - Parse all armies and update army commanders and characters
	 * traveling with armies - Parse all enemy action rumors and update wrappers
	 * as needed
	 */
	public Container<AdvancedCharacterWrapper> computeWrappersForTurn(int turnNo) {
		Container<AdvancedCharacterWrapper> ret = new Container<AdvancedCharacterWrapper>(new String[] { "name", "turnNo", "id" });
		if (!GameHolder.hasInitializedGame())
			return ret;
		Game game = GameHolder.instance().getGame();
		if (turnNo == -1)
			turnNo = game.getCurrentTurn();
		for (int i = turnNo; i >= 0; i--) {
			Turn t = game.getTurn(i);
			if (t == null)
				continue;
			for (Character c : t.getAllCharacters()) {
				if (c.isStartInfoDummy())
					continue;
				ArrayList<AdvancedCharacterWrapper> matches = ret.findAllByProperty("id", c.getId());

				AdvancedCharacterWrapper cw = null;
				int smallestTurnNo = 1000;
				for (AdvancedCharacterWrapper cwi : matches) {
					if (cw == null || smallestTurnNo > cwi.getTurnNo()) {
						cw = cwi;
						smallestTurnNo = cw.getTurnNo();
					}
				}
				if (cw == null) {
					cw = new AdvancedCharacterWrapper();
					cw.setId(c.getId());
					cw.setName(c.getName());
					cw.setNationNo(c.getNationNo());
					cw.setHexNo(c.getHexNo());
					cw.setTurnNo(t.getTurnNo());
					cw.setInfoSource(c.getInfoSource());
					cw.setDeathReason(c.getDeathReason());
					cw.setChampion(c.getNumberOfOrders() == 3);
					if (c.getInformationSource() == InformationSourceEnum.exhaustive) {
						addStats(cw, c, c.getInfoSource(), t.getTurnNo());
						if (c.getHostage() != null && c.getHostage())
							cw.setHostage(c.getHostage(), "unknown");
					} else {
						getStartStats(cw);
						guessStatsFromTitle(cw, c, t.getTurnNo());
						guessStatsIfArmyCommander(cw, c, t.getTurnNo());
						addHealthEstimate(cw, c);
					}
					if (c.getOrderResults() != null && !c.getOrderResults().equals("")) {
						cw.setOrderResults(c.getOrderResults());
					}
					if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
						cw.setDeathReason(c.getDeathReason());
					}
					ret.addItem(cw);
				} else {
					guessStatsFromTitle(cw, c, t.getTurnNo());
					guessStatsIfArmyCommander(cw, c, t.getTurnNo());
					addHealthEstimate(cw, c);
				}
			}

			for (Challenge challenge : t.getChallenges().getItems()) {
				if (challenge.getLoser() != null) {
					AdvancedCharacterWrapper acw = ret.findFirstByProperty("name", challenge.getLoser());
					if (acw == null) {
						acw = new AdvancedCharacterWrapper();
						acw.setName(challenge.getLoser());
						acw.setId(Character.getIdFromName(acw.getName()));
						acw.setHexNo(challenge.getHexNo());
						acw.setTurnNo(t.getTurnNo());
						acw.setInfoSource(new PdfTurnInfoSource(t.getTurnNo(), 0));
						ret.addItem(acw);
					}
					if (acw.getDeathReason() == null || !acw.getDeathReason().equals(CharacterDeathReasonEnum.NotDead)) {
						acw.setDeathReason(CharacterDeathReasonEnum.Challenged);
						acw.appendOrderResult("Killed in challenge at " + challenge.getHexNo() + " by " + challenge.getVictor() + ".");
					}
				}
			}

			for (Combat combat : t.getCombats().getItems()) {
				CombatWrapper cw = new CombatWrapper();
				cw.parseAll(combat.getFirstNarration());
				for (String dc : cw.getKilledCharacters()) {
					AdvancedCharacterWrapper acw = ret.findFirstByProperty("name", dc);
					if (acw == null) {
						acw = new AdvancedCharacterWrapper();
						acw.setName(dc);
						acw.setId(Character.getIdFromName(acw.getName()));
						acw.setHexNo(combat.getHexNo());
						acw.setTurnNo(t.getTurnNo());
						acw.setInfoSource(new PdfTurnInfoSource(t.getTurnNo(), 0));
						ret.addItem(acw);
					}
					if (acw.getDeathReason() == null || acw.getDeathReason().equals(CharacterDeathReasonEnum.NotDead)) {
						acw.setDeathReason(CharacterDeathReasonEnum.Dead);
						acw.appendOrderResult("Killed in combat at " + acw.getHexNo());
					}
				}
			}

			// only for latest turn
			if (i == game.getCurrentTurn()) {
				for (Character c : t.getAllCharacters()) {
					for (String hostageName : c.getHostages()) {
						AdvancedCharacterWrapper cw = ret.findFirstByProperty("name", hostageName);
						if (cw != null) {
							cw.setHexNo(c.getHexNo());
							cw.setHostage(true, c.getName());
							cw.setTurnNo(i);
						} else {
							AdvancedCharacterWrapper ncw = new AdvancedCharacterWrapper();
							ncw.setName(hostageName);
							ncw.setId(Character.getIdFromName(hostageName));
							ncw.setInfoSource(c.getInfoSource());
							ncw.setNationNo(0);
							ncw.setHexNo(0);
							ncw.setTurnNo(i);
							ncw.setHostage(true, c.getName());
							ret.addItem(ncw);
						}
					}
				}

				// assign companies
				for (Company comp : t.getCompanies().getItems()) {
					AdvancedCharacterWrapper cw = ret.findFirstByProperty("name", comp.getCommander());
					if (cw != null) {
						cw.setCompany(comp);
					}
					for (String member : comp.getMembers()) {
						cw = ret.findFirstByProperty("name", member);
						if (cw != null) {
							cw.setCompany(comp);
						}
					}
				}

				// assign armies
				for (Army army : t.getArmies().getItems()) {
					AdvancedCharacterWrapper cw = ret.findFirstByProperty("name", army.getCommanderName());
					if (cw != null) {
						cw.setArmy(army);
					}
					for (String travellingWith : army.getCharacters()) {
						cw = ret.findFirstByProperty("name", travellingWith);
						if (cw != null) {
							cw.setArmy(army);
						}
					}
				}
			}

			Container<EnemyCharacterRumorWrapper> thieves = EnemyCharacterRumorWrapper.getAgentWrappers();
			for (EnemyCharacterRumorWrapper eaw : thieves.getItems()) {
				if (eaw.getTurnNo() != i)
					continue;
				AdvancedCharacterWrapper cw = ret.findFirstByProperty("name", eaw.getName());
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
						cw.setAttributeMax(new CharacterAttributeWrapper("agent", 10, eaw.getTurnNo(), new RumorActionInfoSource(eaw.getReportedTurns())));
					} else {
						cw.setAttributeMax(new CharacterAttributeWrapper("emmisary", 10, eaw.getTurnNo(), new RumorActionInfoSource(eaw.getReportedTurns())));
					}
					ret.addItem(cw);
				} else {
					if (eaw.getCharType().equals("agent")) {
						cw.setAttributeMax(new CharacterAttributeWrapper("agent", 10, eaw.getTurnNo(), new RumorActionInfoSource(eaw.getReportedTurns())));
					} else {
						cw.setAttributeMax(new CharacterAttributeWrapper("emmisary", 10, eaw.getTurnNo(), new RumorActionInfoSource(eaw.getReportedTurns())));
					}
				}
			}
		}

		for (ArtifactWrapper aw : ArtifactInfoCollector.instance().getWrappersForTurn(game.getCurrentTurn())) {
			boolean skipMetadataInfoSource = game.getMetadata().getGameType().equals(GameTypeEnum.gameKS);
			if (aw.getOwner() != null && (!skipMetadataInfoSource || !MetadataSource.class.isInstance(aw.getInfoSource()))) {
				AdvancedCharacterWrapper cw = ret.findFirstByProperty("name", aw.getOwner());
				if (cw != null) {
					cw.getArtifacts().add(aw);
				}
			}
		}

		for (AdvancedCharacterWrapper acw : ret.getItems()) {
			if (acw.getChallenge() == null || acw.getChallenge().getInfoSource() == null || acw.getChallenge().getInfoSource().getTurnNo() < game.getCurrentTurn()) {
				computeChallengeRank(acw, game.getCurrentTurn());
			}
		}

		return ret;
	}

	private static int getStatValue(CharacterAttributeWrapper caw) {
		if (caw == null)
			return 0;
		if (caw.getTotalValue() == null) {
			if (caw.getValue() == null) {
				return 0;
			}
			return (Integer) caw.getValue();
		}
		return Math.max((Integer) caw.getTotalValue(), (Integer) caw.getValue());
	}

	public static void computeChallengeRank(AdvancedCharacterWrapper cw, int turnNo) {
		int c = getStatValue(cw.getCommand());
		int a = getStatValue(cw.getAgent());
		int e = getStatValue(cw.getEmmisary());
		int m = getStatValue(cw.getMage());
		a = a * 3 / 4;
		e = e / 2;

		int[] stats = new int[] { c, a, e, m };
		Arrays.sort(stats);
		int challenge = stats[3] + (stats[0] + stats[1] + stats[2]) / 4;
		cw.setAttribute(new CharacterAttributeWrapper("challenge", challenge, challenge, turnNo, new ComputedInfoSource()));
	}

	public static void guessStatsIfArmyCommander(AdvancedCharacterWrapper cw, Character c, int turnNo) {
		Game game = GameHolder.instance().getGame();
		if (game.getTurn(turnNo).getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", c.getName()) != null) {
			// character is army commander
			DerivedFromArmyInfoSource tis = new DerivedFromArmyInfoSource();
			tis.setTurnNo(turnNo);
			cw.setAttributeMax(new CharacterAttributeWrapper("command", 10, 10, turnNo, tis));
		}
	}

	public static void guessStatsFromTitle(AdvancedCharacterWrapper cw, Character c, int turnNo) {
		String statRange = InfoUtils.getCharacterStatsFromTitle(c.getTitle());
		String statType = InfoUtils.getCharacterStatsTypeFromTitle(c.getTitle());
		if (statRange == null)
			return;
		int stat = 0;
		if (statRange.indexOf("-") > -1) { // parse range eg 50-59
			String[] parts = statRange.split("-");
			stat = Integer.parseInt(parts[0]);
		} else { // parse 100+
			stat = 100;
		}
		DerivedFromTitleInfoSource tis = new DerivedFromTitleInfoSource(c.getTitle());
		tis.setTurnNo(turnNo);
		if (statType.equals("Commander")) {
			cw.setAttributeMax(new CharacterAttributeWrapper("command", stat, stat, turnNo, tis));
		} else if (statType.equals("Agent")) {
			cw.setAttributeMax(new CharacterAttributeWrapper("agent", stat, stat, turnNo, tis));
		} else if (statType.equals("Mage")) {
			cw.setAttributeMax(new CharacterAttributeWrapper("mage", stat, stat, turnNo, tis));
		} else if (statType.equals("Emissary")) {
			cw.setAttributeMax(new CharacterAttributeWrapper("emmisary", stat, stat, turnNo, tis));
		}
		;
	}

	public static void getStartStats(AdvancedCharacterWrapper cw) {
		Character c = GameHolder.instance().getGame().getMetadata().getCharacters().findFirstByProperty("id", cw.getId());
		if (c != null) {
			addStats(cw, c, new MetadataSource(), 0);
			if (cw.getNationNo() == null || cw.getNationNo() == 0) {
				cw.setNationNo(c.getNationNo());
			}
			cw.setStartChar(true);
		}
	}

	public static void addHealthEstimate(AdvancedCharacterWrapper cw, Character c) {
		if ((c.getHealth() == null || c.getHealth() == 0) && c.getHealthEstimate() != null) {
			InfoSourceValue isv = cw.getHealthEstimate();
			if (isv != null) {
				DerivedFromWoundsInfoSource dwis1 = (DerivedFromWoundsInfoSource) isv.getInfoSource();
				DerivedFromWoundsInfoSource dwis2 = (DerivedFromWoundsInfoSource) c.getHealthEstimate().getInfoSource();
				if (dwis1.getTurnNo() < dwis2.getTurnNo()) {
					cw.setHealthEstimate(c.getHealthEstimate());
				}
			} else {
				cw.setHealthEstimate(c.getHealthEstimate());
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

		if (c.getHealth() != null && c.getHealth() > 0) {
			cw.setAttribute(new CharacterAttributeWrapper("health", c.getHealth(), turnNo, is));
		} else if (c.getHealthEstimate() != null) {
			cw.setHealthEstimate(c.getHealthEstimate());
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				this.turnInfo.clear();
			}
		}
	}

}
