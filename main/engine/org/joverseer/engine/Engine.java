package org.joverseer.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.LogManager;
import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.IEngineObject;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.orders.MoveArmyOrder;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;

public class Engine {
	ArrayList<ExecutingOrder> orders = new ArrayList<ExecutingOrder>();
	Turn turn;

	public void executeOrders(Game game) {
		try {
			prepareTurn(game);
			initializeTurn();
			createOrders();
			doExecuteOrdersPhase10(game);
			runChallenges();
			doExecuteOrdersPhase20(game);
			runCombats();
			doExecuteOrdersPhase30(game);
			initializeEconomies();
			produceProducts();
			doExecuteOrdersPhase40(game);
			prepareArmiesForMovement();
			doExecuteOrdersPhase50(game);
			moveArmies(game);
			doExecuteOrdersPhase60(game);
			postProcessEconomies();
			postprocessCharacters(game);
			postprocessArmies();
			postprocessTurn();
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), game, this));
		} catch (RuntimeException exc) {
			LogManager.getLogger(getClass()).error("Unexpected error :" + exc.getMessage());
			LogManager.getLogger(getClass()).error(exc);
			exc.printStackTrace();
			throw exc;
		}
	}

	protected void initializeTurn() {
		for (IEngineObject eo : (ArrayList<IEngineObject>) turn.getContainer(TurnElementsEnum.Character).getItems()) {
			eo.initialize();
		}
	}

	protected void moveArmies(Game game) {
		// get all move orders
		ArrayList<MoveArmyOrder> moveOrders = new ArrayList<MoveArmyOrder>();
		ArrayList<Army> sittingArmies = new ArrayList<Army>();
		sittingArmies.addAll(turn.getContainer(TurnElementsEnum.Army).getItems());
		ArrayList<Army> movingArmies = new ArrayList<Army>();
		for (ExecutingOrder eo : orders) {
			if (MoveArmyOrder.class.isInstance(eo)) {
				MoveArmyOrder mo = (MoveArmyOrder) eo;
				try {
					mo.doExecute(game, turn);
				} catch (ErrorException e) {
					mo.addMessage(e.getMessage());
				}
				if (mo.isValid()) {
					moveOrders.add(mo);
					movingArmies.add(mo.getArmy());
					// sitting armies are armies without a valid movement order
					sittingArmies.remove(mo.getArmy());
				}
			}
		}

		ArrayList<Nation> nations = new ArrayList<Nation>();
		for (int i = 1; i <= 25; i++) {
			Nation n = game.getMetadata().getNationByNum(i);
			nations.add(n);
		}
		Collections.shuffle(nations);
		// we check against other move orders and sitting armies
		// we don't need to check against pop centers, this has been taken care
		// of by MoveArmyOrder.prepareMoves,
		// which checks for pop centers
		for (int i = 0; i < 14; i++) {
			for (Nation n : nations) {
				for (MoveArmyOrder mo : moveOrders) {
					if (mo.getNationNo() == n.getNumber()) {
						if ((mo.getOrderNo() == 840 || mo.getOrderNo() == 850) && i == 0) {
							ExecutingOrderUtils.anchorShips(turn, mo.getArmy());
						}
						mo.doMove(game, turn, i);
						int d1 = mo.getCurrentDay();
						Character a = mo.getCharacter();
						System.out.println("Day " + d1 + " army " + mo.getCharacter().getName() + " hex " + mo.getCurrentHex());
						// check intercepts
						for (MoveArmyOrder nmo : moveOrders) {
							if (nmo == mo)
								continue;
							int d2 = nmo.getCurrentDay();
							if (d2 > d1)
								continue;
							Character b = nmo.getCharacter();
							if (nmo.getCurrentHex() == mo.getCurrentHex()) {
								// potential intercept
								if (!ExecutingOrderUtils.checkNonHostile(turn, a, b)) {
									if (d2 == d1) {
										// nmo has already moved, so this is an
										// intercept
										System.out.println("Intercept between " + mo.getName() + " and " + nmo.getName());
										mo.stopBecauseOfIntercept();
										nmo.stopBecauseOfIntercept();
									} else if (d2 < d1) {
										// nmo has not moved
										int nextDayHex = nmo.getHexAtDay(d2 + 1);
										if (nextDayHex == mo.getCurrentHex() || nextDayHex == mo.getHexAtDay(d1 - 1)) {
											System.out.println("Intercept between " + mo.getName() + " and " + nmo.getName());
											// nmo does change hex this day or
											// moves opposite to mo
											mo.stopBecauseOfIntercept();
											nmo.stopBecauseOfIntercept();
										}

									}
								}
							}
						}
						// check against sitting armies
						for (Army sa : sittingArmies) {
							if (sa.getHexNo().equals(String.valueOf(mo.getCurrentHex()))) {
								if (!ExecutingOrderUtils.checkNonHostile(turn, a, sa)) {
									mo.stopBecauseOfIntercept();
									System.out.println("Intercept between " + mo.getName() + " and " + sa.getCommanderName());
									break;
								}
							}
						}

					}
				}
			}
		}

		for (MoveArmyOrder mo : moveOrders) {
			mo.finished(game, turn);
		}
	}

	protected void doExecuteOrdersPhase10(Game game) {
		doExecuteOrders(game, 0, 215);
	}

	protected void doExecuteOrdersPhase20(Game game) {
		doExecuteOrders(game, 215, 270);
	}

	protected void doExecuteOrdersPhase30(Game game) {
		doExecuteOrders(game, 270, 325);
	}

	protected void doExecuteOrdersPhase40(Game game) {
		doExecuteOrders(game, 325, 420); // TODO change this just after
		// recruiting
	}

	protected void doExecuteOrdersPhase50(Game game) {
		doExecuteOrders(game, 420, 829);
	}

	protected void doExecuteOrdersPhase60(Game game) {
		doExecuteOrders(game, 860, 1000);
	}

	protected void doExecuteOrders(Game game, int minOrderNo, int maxOrderNo) {
		for (ExecutingOrder eo : orders) {
			if (eo.getOrderNo() <= maxOrderNo && eo.getOrderNo() > minOrderNo) {
				eo.execute(game, turn);
			}
		}
	}

	protected void runChallenges() {
		for (ChallengeFight cf : (ArrayList<ChallengeFight>) turn.getContainer(TurnElementsEnum.ChallengeFights).getItems()) {
			ExecutingOrderUtils.appendMessage(cf.getC2(), "{char} was challenged to personal combat by " + cf.getC1().getName() + ". See combat messages.");
			cf.run();

			// check dead
			if (cf.getC1().getHealth() == 0) {
				// character 1 died
				ExecutingOrderUtils.appendMessage(cf.getC1(), "{char} died in personal combat.");
				ExecutingOrderUtils.characterDied(turn, cf.getC1(), CharacterDeathReasonEnum.Dead);
				ExecutingOrderUtils.plunderArtifacts(turn, cf.getC2(), cf.getC1());
				Army a = ExecutingOrderUtils.getArmy(turn, cf.getHexNo(), cf.getC2().getName());
				if (a != null) {
					// increase morale
					a.setMorale(Math.min(a.getMorale() + 10, 100));
				}
			}
			if (cf.getC2().getHealth() == 0) {
				// character 2 died
				ExecutingOrderUtils.appendMessage(cf.getC2(), "{char} died in personal combat.");
				ExecutingOrderUtils.characterDied(turn, cf.getC2(), CharacterDeathReasonEnum.Dead);
				ExecutingOrderUtils.plunderArtifacts(turn, cf.getC1(), cf.getC2());
				Army a = ExecutingOrderUtils.getArmy(turn, cf.getHexNo(), cf.getC1().getName());
				if (a != null) {
					// increase morale
					a.setMorale(Math.min(a.getMorale() + 10, 100));
				}
			}

			Challenge challenge = new Challenge();
			challenge.setCharacter(cf.getC1().getName());
			challenge.setHexNo(cf.getHexNo());
			challenge.setDescription(cf.getDescription());
			turn.getContainer(TurnElementsEnum.Challenge).addItem(challenge);

			challenge = new Challenge();
			challenge.setCharacter(cf.getC2().getName());
			challenge.setHexNo(cf.getHexNo());
			challenge.setDescription(cf.getDescription());
			turn.getContainer(TurnElementsEnum.Challenge).addItem(challenge);
		}
	}

	protected void runCombats() {
		// TODO lost of stuff missing
		for (Combat combat : (ArrayList<Combat>) turn.getContainer(TurnElementsEnum.CombatCalcCombats).getItems()) {
			System.out.println("Running combat at " + combat.getHexNo());
			for (int i = 0; i < 2; i++) {
				System.out.println("side " + (i + 1));
				CombatArmy[] side = i == 0 ? combat.getSide1() : combat.getSide2();
				for (CombatArmy ca : side) {
					if (ca == null)
						continue;
					Nation n = GameHolder.instance().getGame().getMetadata().getNationByNum(ca.getNationNo());
					System.out.println(ca.getCommander() + "(" + n.getShortName() + ") with " + ca.computeNumberOfTroops() + " troops");
				}
			}
			combat.autoSetRelationsToHated();
			combat.runWholeCombat();
			CombatArmy winner = null;
			Army winnerArmy = null;
			String narration = "";
			for (int i = 0; i < 2; i++) {
				CombatArmy[] side = i == 0 ? combat.getSide1() : combat.getSide2();
				for (CombatArmy ca : side) {
					if (ca == null)
						continue;
					Army a = ExecutingOrderUtils.getArmy(turn, combat.getHexNo(), ca.getCommander());
					Character c = ExecutingOrderUtils.getCommander(turn, a, false);
					if (ca.getLosses() >= 100) {
						ExecutingOrderUtils.removeArmy(turn, a);
						narration += (narration.equals("") ? "" : "\n") + a.getCommanderName() + "'s army was destroyed.";
					} else {
						for (ArmyElement ae : a.getElements()) {
							if (ae.getArmyElementType().isTroop()) {
								ae.setNumber((int) (ae.getNumber() * (100 - ca.getLosses()) / 100));
							}
						}
						if (winner == null) {
							winner = ca;
							winnerArmy = a;
						} else if (winner.computeNumberOfTroops() < ca.computeNumberOfTroops()) {
							winner = ca;
							winnerArmy = a;
						}
						narration += (narration.equals("") ? "" : "\n") + a.getCommanderName() + "'s army suffered " + Math.round(ca.getLosses()) + "% losses.";
						if (a.computeNumberOfMen() < 100) {
							narration += (narration.equals("") ? "" : "\n") + a.getCommanderName() + "'s was disbanded because it was left with less than 100 men.";
						}
						ExecutingOrderUtils.cleanupArmy(turn, a);

					}
				}
			}
			if (winner != null && combat.getAttackPopCenter() && combat.getSide2Pc() != null && combat.getSide2Pc().isCaptured()) {
				PopulationCenter pc = ExecutingOrderUtils.getPopCenter(turn, combat.getHexNo());
				PopulationCenterSizeEnum size = PopulationCenterSizeEnum.getFromCode(pc.getSize().getCode() - 1);
				pc.setSize(size);
				if (!size.equals(PopulationCenterSizeEnum.ruins)) {
					pc.setNationNo(winner.getNationNo());
					Character c = ExecutingOrderUtils.getCommander(turn, winnerArmy, false);
					pc.setLoyalty(c.getCommand() / 2);
					narration += (narration.equals("") ? "" : "\n") + pc.getName() + " was captured by " + c.getNation().getName() + ".";
					// downgrade docks as appropriate
					if (!pc.getHarbor().equals(HarborSizeEnum.none)) {
						if (size.getCode() < PopulationCenterSizeEnum.majorTown.getCode()) {
							if (size.equals(PopulationCenterSizeEnum.town)) {
								if (pc.getHarbor() == HarborSizeEnum.port) {
									pc.setHarbor(HarborSizeEnum.harbor);
									narration += (narration.equals("") ? "" : "\n") + "The harbor was reduced to a port.";
								}
							} else {
								pc.setHarbor(HarborSizeEnum.none);
								narration += (narration.equals("") ? "" : "\n") + "The docks were destroyed.";
							}
						}
					}

				} else {
					pc.setHarbor(HarborSizeEnum.none);
					pc.setNationNo(0);
					pc.setLoyalty(0);
					narration += (narration.equals("") ? "" : "\n") + pc.getName() + " was reduced to ruins.";
				}
				ExecutingOrderUtils.refreshItem(turn, TurnElementsEnum.PopulationCenter, pc);

			}
			org.joverseer.domain.Combat c = new org.joverseer.domain.Combat();
			c.setHexNo(combat.getHexNo());
			if (winnerArmy != null) {
				c.addNarration(winnerArmy.getNationNo(), narration);
			}
			turn.getContainer(TurnElementsEnum.Combat).addItem(c);
		}
	}

	protected void prepareArmiesForMovement() {
		for (Army a : (ArrayList<Army>) turn.getContainer(TurnElementsEnum.Army).getItems()) {
			// handle food consumption
			Integer requiredFood = a.computeFoodConsumption();
			if (requiredFood == null)
				requiredFood = 0;
			PopulationCenter pc = ExecutingOrderUtils.getPopCenter(turn, Integer.parseInt(a.getHexNo()));
			int popUsedFood = 0;
			if (pc != null) {
				if (ExecutingOrderUtils.checkFriendly(turn, pc, a)) {
					// can consume food
					int availableFood = pc.getFoodCapacity();
					popUsedFood = Math.min(availableFood, requiredFood);
					// consume food from pop center
					pc.setFoodCapacity(pc.getFoodCapacity() - popUsedFood);
				}
			}

			Integer armyAvailableFood = a.getFood();
			if (armyAvailableFood == null)
				armyAvailableFood = 0;

			int armyUsedFood = Math.min(armyAvailableFood, requiredFood - popUsedFood);
			a.setFood(armyAvailableFood - armyUsedFood);
			a.setFed(a.getFood() > 0);
			// TODO handle possible morale loss

			// compute cavalry
			a.setCavalry(a.computeCavalry());

		}
	}

	protected void initializeEconomies() {
		for (NationEconomy ne : (ArrayList<NationEconomy>) turn.getContainer(TurnElementsEnum.NationEconomy).getItems()) {
			int maintenanceCost = computeTotalMaintenance(ne);
			int goldProduction = computeGoldProduction(ne);
			int reserves = ne.getAvailableGold() + ne.getReserve();
			int taxRevenue;
			do {
				// loop and increase tax rate until gold > maintenance
				taxRevenue = computeTaxRevenue(ne);
				if (reserves + taxRevenue + goldProduction - maintenanceCost < 0) {
					ne.setTaxRate(ne.getTaxRate() + 1);
					if (ne.getTaxRate() == 100) {
						break;
					}
				} else {
					break;
				}
			} while (true);
			ne.setAvailableGold(goldProduction + taxRevenue + reserves - maintenanceCost);
		}
	}

	protected int computeTaxRevenue(NationEconomy ne) {
		int taxRevenue = 0;
		int taxRate = ne.getTaxRate();
		for (PopulationCenter pc : (ArrayList<PopulationCenter>) turn.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", ne.getNationNo())) {
			if (pc.getSize().getCode() > 1) {
				int taxes = (pc.getSize().getCode() - 1) * 2500 * taxRate / 100;
				taxRevenue += taxes;
			}
		}
		return taxRevenue;
	}

	protected int computeTotalMaintenance(NationEconomy ne) {
		int cost = computePopCenterMaintenanceCosts(ne);
		cost += computeArmyMaintenanceCosts(ne);
		cost += computeCharacterMaintenanceCosts(ne);
		return cost;
	}

	protected int computePopCenterMaintenanceCosts(NationEconomy ne) {
		int cost = 0;
		for (PopulationCenter pc : (ArrayList<PopulationCenter>) turn.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", ne.getNationNo())) {
			cost += pc.getMaintenance();
		}
		return cost;
	}

	protected int computeArmyMaintenanceCosts(NationEconomy ne) {
		int cost = 0;
		ArrayList<Army> as = turn.getContainer(TurnElementsEnum.Army).findAllByProperty("nationNo", ne.getNationNo());
		for (Army i : as) {
			cost += i.getMaintenance();
		}
		return cost;
	}

	protected int computeCharacterMaintenanceCosts(NationEconomy ne) {
		int cost = 0;
		for (Character i : (ArrayList<Character>) turn.getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", ne.getNationNo())) {
			cost += i.getMaintenance();
		}
		return cost;
	}

	protected void postProcessEconomies() {
		for (NationEconomy ne : (ArrayList<NationEconomy>) turn.getContainer(TurnElementsEnum.NationEconomy).getItems()) {
			int taxBase = 0;
			int goldProduction = 0;

			for (ProductEnum product : ProductEnum.values()) {
				ne.setStores(product, ExecutingOrderUtils.getTotalStoresForNation(turn, product, ne.getNationNo()));
				ne.setProduction(product, ExecutingOrderUtils.getTotalProductionForNation(turn, product, ne.getNationNo()));
			}
			for (PopulationCenter pc : (ArrayList<PopulationCenter>) turn.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", ne.getNationNo())) {
				if (pc.getSize().getCode() > 1) {
					taxBase += pc.getSize().getCode() - 1;
				}
			}
			ne.setTaxBase(taxBase);
			ne.setGoldProduction(computeGoldProduction(ne));
			ne.setReserve(ne.getAvailableGold());
			ne.setArmyMaintenance(computeArmyMaintenanceCosts(ne));
			ne.setPopMaintenance(computePopCenterMaintenanceCosts(ne));
			ne.setCharMaintenance(computeCharacterMaintenanceCosts(ne));
			ne.setTotalMaintenance(ne.getArmyMaintenance() + ne.getPopMaintenance() + ne.getCharMaintenance());
			int taxRevenue = ne.getTaxBase() * 500 * ne.getTaxRate() / 100;
			ne.setSurplus(taxRevenue + ne.getGoldProduction() - ne.getTotalMaintenance());

			EconomyCalculatorData ecd = (EconomyCalculatorData) turn.getContainer(TurnElementsEnum.EconomyCalucatorData).findFirstByProperty("nationNo", ne.getNationNo());
			ecd.setOrdersCost(0);
			for (ProductEnum p : ProductEnum.values()) {
				ecd.setBidPrice(p, null);
				ecd.setBidUnits(p, null);
				ecd.setBuyUnits(p, null);
				ecd.setSellPct(p, null);
				ecd.setSellUnits(p, null);
			}
		}
	}

	protected int computeGoldProduction(NationEconomy ne) {
		int goldProduction = 0;
		for (PopulationCenter pc : (ArrayList<PopulationCenter>) turn.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", ne.getNationNo())) {
			Integer gp = pc.getProduction(ProductEnum.Gold);
			if (gp == null)
				gp = 0;
			goldProduction += gp;
		}
		return goldProduction;
	}

	protected void produceProducts() {
		for (PopulationCenter pc : (ArrayList<PopulationCenter>) turn.getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
			for (ProductEnum p : ProductEnum.values()) {
				if (!p.equals(ProductEnum.Gold)) { // skip gold
					Integer stores = pc.getStores(p);
					if (stores == null)
						stores = 0;
					Integer production = pc.getProduction(p);
					if (production == null)
						production = 0;
					pc.setStores(p, stores + production);
				}
			}
			pc.initialize();
		}
	}

	protected void postprocessCharacters(Game game) {
		for (Character c : (ArrayList<Character>) turn.getContainer(TurnElementsEnum.Character).getItems()) {
			for (Order o : c.getOrders()) {
				o.clear();
			}
			ExecutingOrderUtils.setCharacterStatTotals(game, turn, c);
			ExecutingOrderUtils.setCharacterTitle(turn, c);
			ExecutingOrderUtils.healCharacter(turn, c);
		}
	}

	protected void postprocessArmies() {
		for (Army a : (ArrayList<Army>) turn.getContainer(TurnElementsEnum.Army).getItems()) {
			a.resetENHI();
			a.resetFed();
			sortArmyElements(a);
			Character c = ExecutingOrderUtils.getCommander(turn, a, false);
			if (c != null)
				a.setCommanderTitle(c.getTitle());

		}
	}

	protected void sortArmyElements(Army a) {
		Collections.sort(a.getElements(), new Comparator<ArmyElement>() {
			public int compare(ArmyElement o1, ArmyElement o2) {
				return o1.getArmyElementType().getSortOrder() - o2.getArmyElementType().getSortOrder();
			}

		});
	}

	protected void prepareTurn(Game game) {
		Turn currentTurn = game.getTurn();
		turn = currentTurn.clone();
		turn.setTurnNo(turn.getTurnNo() + 1);
		turn.getContainer(TurnElementsEnum.ChallengeFights).clear();
		turn.getContainer(TurnElementsEnum.Combat).clear();
		turn.getContainer(TurnElementsEnum.Challenge).clear();
		turn.getContainer(TurnElementsEnum.CombatCalcCombats).clear();
		turn.getContainer(TurnElementsEnum.Notes).clear();
		try {
			game.addTurn(turn);
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	protected void postprocessTurn() {
		turn.getContainers().remove(TurnElementsEnum.ChallengeFights);
	}

	protected void createOrders() {
		ExecutingOrderFactory eof = new ExecutingOrderFactory();
		for (Character c : (ArrayList<Character>) turn.getContainer(TurnElementsEnum.Character).getItems()) {
			for (Order o : c.getOrders()) {
				if (o.isBlank())
					continue;
				ExecutingOrder eo = eof.createOrder(o);
				orders.add(eo);
			}
			c.setOrderResults("");
		}
		Collections.sort(orders);
	}

}
