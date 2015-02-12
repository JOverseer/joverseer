package org.joverseer.tools.ordercheckerIntegration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Company;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;

import com.middleearthgames.orderchecker.Main;
import com.middleearthgames.orderchecker.Map;
import com.middleearthgames.orderchecker.Nation;
import com.middleearthgames.orderchecker.Order;
import com.middleearthgames.orderchecker.PopCenter;
import com.middleearthgames.orderchecker.Ruleset;
import com.middleearthgames.orderchecker.gui.ExtraInfoDlg;
import com.middleearthgames.orderchecker.gui.OCResultTreeRenderer;
import com.middleearthgames.orderchecker.io.Data;
import com.middleearthgames.orderchecker.io.ImportRulesCsv;

/**
 * Class the acts as a proxy between JOverseer and Bernie Geiger's Order Checker
 * 
 * It: - prepares the Order Checker by updating it with the current game's data
 * - runs it (and takes care of showing the appropriate request screens) -
 * provides helper methods for accessing the order results
 * 
 * It heavily depends on the ReflectionUtils class for accessing private fields
 * and method on the Order Checker code.
 * 
 * @author Marios Skounakis
 */
public class OrdercheckerProxy {
	HashMap<com.middleearthgames.orderchecker.Order, org.joverseer.domain.Order> orderMap = new HashMap<com.middleearthgames.orderchecker.Order, org.joverseer.domain.Order>();
	HashMap<org.joverseer.domain.Order, com.middleearthgames.orderchecker.Order> reverseOrderMap = new HashMap<org.joverseer.domain.Order, com.middleearthgames.orderchecker.Order>();
	int nationNo;

	private static class OrdercheckerPaths {
		public String rulesetPath;
		public String terrainPath;
		private static final OrdercheckerPaths instance = new OrdercheckerPaths();
		private OrdercheckerPaths() {
			if (System.getProperty("Debugging") != null) {
				this.rulesetPath = "classes/metadata/orderchecker/ruleset.csv";
				this.terrainPath = "classes/metadata/orderchecker/";
			} else { 
				this.rulesetPath="bin/metadata/orderchecker/ruleset.csv";
				this.terrainPath = "bin/metadata/orderchecker/";
			}
		}
		public static OrdercheckerPaths getInstance()
		{
			return instance;
		}
	}
	
	private static void copyCurrentMap(GameMetadata metadata,Map map)
	{
		com.middleearthgames.orderchecker.Hex ocHex;
		HashMap<HexSideEnum,ArrayList<HexSideElementEnum>> elements;
		
		for(org.joverseer.metadata.domain.Hex meHex:metadata.getHexes()){
			ocHex = new com.middleearthgames.orderchecker.Hex(meHex.getHexNo());
			// TODO: check this the doc says it starts at 0
			ocHex.setTerrain((int)(meHex.getTerrain().ordinal())); // terrain numbers are the same.
			elements = meHex.getSideElements(); 
			
			for(java.util.Map.Entry<HexSideEnum,ArrayList<HexSideElementEnum>> entry:elements.entrySet()){
				for(HexSideElementEnum feature:entry.getValue()) {
					ocHex.addDirection(entry.getKey().ordinal());
					ocHex.addFeature(feature.getElement());
				}
			}
			map.addHex(ocHex);
		}
	}
	
	public void runOrderchecker() {
		Data data = Main.main.getData();
		Main.main.setRuleSet(new Ruleset());
		ImportRulesCsv rules = new ImportRulesCsv(OrdercheckerPaths.getInstance().rulesetPath, Main.main.getRuleSet());
		boolean result = rules.getRules();
		if (!result) {
			rules.closeFile();
			Main.displayErrorMessage("The rules file (" + data.getRulesPath() + ") could not be opened!");
			return;
		}
		String error = rules.parseRules();
		rules.closeFile();
		if (error != null) {
			error = error + "\n\nOrder checking cancelled.";
			Main.displayErrorMessage(error);
			return;
		}
		if (!Main.main.getRuleSet().isRuleSetComplete()) {
			Main.displayErrorMessage("The rules file was processed but appears to be missing data!");
			return;
		}
		Main.main.setMap(new Map());
		String gt = "1650";
		Game g = GameHolder.instance().getGame();
		if (g.getMetadata().getGameType() == GameTypeEnum.game1650) {
			Main.main.getData().setGameType("1650");
			gt = "1650";
		} else if (g.getMetadata().getGameType() == GameTypeEnum.game2950) {
			Main.main.getData().setGameType("2950");
			gt = "2950";
		} else if (g.getMetadata().getGameType() == GameTypeEnum.gameBOFA) {
			Main.main.getData().setGameType("BOFA");
			gt = "bofa";
		} else if (g.getMetadata().getGameType() == GameTypeEnum.gameFA) {
			Main.main.getData().setGameType("Fourth Age");
			gt = "fa";
		} else if (g.getMetadata().getGameType() == GameTypeEnum.gameUW) {
			Main.main.getData().setGameType("Untold War");
			gt = "uw";
		} else if (g.getMetadata().getGameType() == GameTypeEnum.gameKS) {
			Main.main.getData().setGameType("Kin Strife");
			gt = "ks";
		}
		copyCurrentMap(g.getMetadata(),Main.main.getMap());
/*		
		ImportTerrainCsv terrain = new ImportTerrainCsv(OrdercheckerPaths.getInstance().terrainPath + gt + ".game", Main.main.getMap());
		result = terrain.getMapInformation();
		if (!result) {
			terrain.closeFile();
			Main.displayErrorMessage("The terrain file (" + data.getTerrainPath() + ") could not be opened!");
			return;
		}
		error = terrain.parseTerrain();
		terrain.closeFile();
		if (error != null) {
			error = error + "\n\nOrder checking cancelled.";
			Main.displayErrorMessage(error);
			return;
		}
*/
		if (!Main.main.getMap().isMapComplete()) {
			Main.displayErrorMessage("The map file was processed but appears to be missing data!");
			return;
		} else {
			// splitPane.resetToPreferredSizes();
			// Main.main.processOrders();
			try {
				processOrders(Main.main);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			return;
		}
	}

	protected void processOrders(Main main) throws Exception {
		final ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");

		final DefaultMutableTreeNode root = main.getWindow().getRoot();
		JTree tree = main.getWindow().getTree();
		if (tree.getCellRenderer() instanceof OCResultTreeRenderer) {
			final OCResultTreeRenderer renderer = (OCResultTreeRenderer)tree.getCellRenderer();
			renderer.setRedIcon(new ImageIcon(imgSource.getImage("orderchecker.red.image")));
			renderer.setYellowIcon(new ImageIcon(imgSource.getImage("orderchecker.yellow.image")));
			renderer.setGreenIcon(new ImageIcon(imgSource.getImage("orderchecker.green.image")));
			renderer.setOrderIcon(new ImageIcon(imgSource.getImage("orderchecker.order.image")));
			renderer.setCharIcon(new ImageIcon(imgSource.getImage("orderchecker.character.image")));
		}

		String error = main.getNation().implementPhase(1, main);
		if (error != null) {
			Main.displayErrorMessage(error);
			return;
		}
		boolean done;
		int safety;
		Vector requests = main.getNation().getArmyRequests();
		parseInfoRequests(requests);
		new ExtraInfoDlg(Main.mainFrame, main.getNation(), main.getData(), requests);
		main.getNation().processArmyRequests( requests );
		done = false;
		safety = 0;
		do {
			if (done || safety >= 20) {
				break;
			}
			safety++;
			error = main.getNation().implementPhase( 2, main );
			if (error != null) {
				Main.displayErrorMessage(error);
				return;
			}
			requests = main.getNation().getInfoRequests();
			parseInfoRequests(requests);
			if (requests.size() > 0) {
				new ExtraInfoDlg(Main.mainFrame, main.getNation(), main.getData(), requests);
			}
			done = main.getNation().isProcessingDone();
		} while (true);
		if (safety == 20) {
			throw new RuntimeException("Maximum state iterations reached.");
		}
		try {
			main.getWindow().createResultsTree();
			// root.removeAllChildren();
			// Main.main.getNation().addTreeNodes(tree, root);
			// ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(root);
			// javax.swing.tree.TreeNode path[];
			// for(Enumeration allNodes = root.depthFirstEnumeration();
			// allNodes.hasMoreElements(); tree.expandPath(new TreePath(path)))
			// {
			// DefaultMutableTreeNode node =
			// (DefaultMutableTreeNode)allNodes.nextElement();
			// OCTreeNode n = (OCTreeNode)node;
			// int result = (Integer)ReflectionUtils.invokeMethod(n,
			// "getResultType", new Object[]{});
			// if (result == 1) {
			//                    
			// n.setIcon(new
			// ImageIcon(imgSource.getImage("orderchecker.green.image")));
			// }
			// path = node.getPath();
			// }
		} catch (Exception ex) {
			StringBuffer desc = new StringBuffer();
			desc.append(Main.getVersionString() + ", " + Main.getVersionDate() + "\n\n");
			desc.append("Critical error encountered...please send the  contents\nof this message plus your XML and order file to: bernout1@adelphia.net\n\n");
			String message = ex.getMessage();
			desc.append(message + "\n");
			StackTraceElement trace[] = ex.getStackTrace();
			for (int i = 0; i < trace.length && i < 10; i++) {
				desc.append(trace[i] + "\n");
			}

			Main.displayErrorMessage(desc.toString());
		}
	}

	private static Nation updateNation(Game g,int nationNo1,Turn t) {
		
		PlayerInfo pi = (PlayerInfo) t.getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo1);

		Nation nation = new Nation();
		nation.SetNation(nationNo1);
		nation.setGame(g.getMetadata().getGameNo());
		nation.setTurn(t.getTurnNo());
		// TODO complete
		if (g.getMetadata().getGameType() == GameTypeEnum.game1650) {
			nation.setGameType("1650");
		} else if (g.getMetadata().getGameType() == GameTypeEnum.game2950) {
			nation.setGameType("2950");
		} else if (g.getMetadata().getGameType() == GameTypeEnum.gameBOFA) {
			nation.setGameType("BOFA");
		} else if (g.getMetadata().getGameType() == GameTypeEnum.gameFA) {
			nation.setGameType("Fourth Age");
		} else if (g.getMetadata().getGameType() == GameTypeEnum.gameUW) {
			nation.setGameType("Untold War");
		} else if (g.getMetadata().getGameType() == GameTypeEnum.gameKS) {
			nation.setGameType("Kin Strife");
		}

		nation.setSecret(Integer.parseInt(pi.getSecret()));
		nation.setPlayer(pi.getPlayerName());
		nation.setDueDate(pi.getDueDate());
		for (org.joverseer.metadata.domain.Nation n : g.getMetadata().getNations()) {
			nation.addNation(n.getName());
		}
		return nation;
	}

	private static void updatePC(Turn t,Nation nation)
	{
		for (PopulationCenter popCenter : (ArrayList<PopulationCenter>) t.getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
			if (popCenter.getSize() == PopulationCenterSizeEnum.ruins)
				continue;
			PopCenter pc = new PopCenter(popCenter.getHexNo());
			pc.setCapital(popCenter.getCapital() ? 1 : 0);
			if (popCenter.getHarbor().equals(HarborSizeEnum.none)) {
				pc.setDock(0);
			} else if (popCenter.getHarbor().equals(HarborSizeEnum.port)) {
				pc.setDock(2);
			} else if (popCenter.getHarbor().equals(HarborSizeEnum.harbor)) {
				pc.setDock(1);
			}
			;
			pc.setName(popCenter.getName());
			pc.setFortification(popCenter.getFortification().getSize());
			pc.setSize(popCenter.getSize().getCode());
			pc.setNation(popCenter.getNationNo());
			pc.setHidden(popCenter.getHidden() ? 1 : 0);
			pc.setLoyalty(popCenter.getLoyalty());
			nation.addPopulationCenter(pc);

			if (popCenter.getCapital() && popCenter.getNationNo() == nation.getNation()) {
				nation.setCapital(popCenter.getHexNo());
			}
		}
		
	}
	private void updateCharacters(Game g,Turn t, Nation nation) {
		for (Character ch : (ArrayList<Character>) t.getContainer(TurnElementsEnum.Character).getItems()) {
			if (ch.getDeathReason() != CharacterDeathReasonEnum.NotDead)
				continue;
			if (ch.getId() == null) { 
				System.out.println(ch.getName() + " has no Id");
				continue;
			}
			com.middleearthgames.orderchecker.Character mc = new com.middleearthgames.orderchecker.Character(ch.getId() + "     ".substring(0, 5 - ch.getId().length()));
			mc.setNation(ch.getNationNo());
			mc.setName(ch.getName());
			mc.setAgentRank(ch.getAgent());
			mc.setTotalAgentRank(ch.getAgentTotal());
			mc.setCommandRank(ch.getCommand());
			mc.setTotalCommandRank(ch.getCommandTotal());
			mc.setMageRank(ch.getMage());
			mc.setTotalMageRank(ch.getMageTotal());
			mc.setEmissaryRank(ch.getEmmisary());
			mc.setTotalEmissaryRank(ch.getEmmisaryTotal());
			mc.setChallenge(ch.getChallenge());
			mc.setHealth(ch.getHealth() == null ? 0 : ch.getHealth());
			mc.setStealth(ch.getStealth());
			mc.setTotalStealth(ch.getStealthTotal());
			mc.setLocation(ch.getHexNo());
			// TODO double check
			for (Integer artiNo : ch.getArtifacts()) {
				ArtifactInfo ai = g.getMetadata().getArtifacts().findFirstByProperty("no", artiNo);
				if (ai == null)
					return;
				mc.addArtifact(ai.getNo(), ai.getName());
			}

			// TODO double check
			for (SpellProficiency sp : ch.getSpells()) {
				mc.addSpell(sp.getSpellId(), sp.getName());
			}
			nation.addCharacter(mc);

			for (org.joverseer.domain.Order o : ch.getOrders()) {
				if (o.isBlank())
					continue;
				com.middleearthgames.orderchecker.Order mo = new com.middleearthgames.orderchecker.Order(mc, o.getOrderNo());
				ArrayList<String> params = new ArrayList<String>();
				int lastParam = -1;
				for (int i = 0; i < 17; i++) {
					if (o.getParameter(i) == null || o.getParameter(i).equals("--") || o.getParameter(i).equals("-")) {
						params.add(o.getParameter(i));
					} else {
						params.add(o.getParameter(i));
						lastParam = i;
					}
				}
				// fix army/navy movement order
				// basically move last param to front
				if (o.getOrderNo() == 830 || o.getOrderNo() == 850 || o.getOrderNo() == 860) {
					String moveType = params.get(lastParam);
					for (int i = o.getLastParamIndex(); i > 0; i--) {
						params.set(i, params.get(i - 1));
					}
					params.set(0, moveType);
				}
				for (int i = 0; i <= lastParam; i++) {
					mo.addParameter(params.get(i));
				}
				this.orderMap.put(mo, o);
				this.reverseOrderMap.put(o, mo);
				mc.addOrder(mo);
			}
		}
	}
	private static void updateArmies(Turn t,Nation nation) {
		for (Army army : (ArrayList<Army>) t.getContainer(TurnElementsEnum.Army).getItems()) {
			if (Army.isAnchoredShips(army))
				continue;
			com.middleearthgames.orderchecker.Army a = new com.middleearthgames.orderchecker.Army(Integer.parseInt(army.getHexNo()));
			a.setCommander(army.getCommanderName());
			a.setNation(army.getNationNo());
			a.setTroopAmount(army.getTroopCount());
			String extraInfo = "";
			for (ArmyElement element : army.getElements()) {
				extraInfo = extraInfo + (extraInfo.equals("") ? "" : " ") + element.getDescription();
			}
			a.setExtraInfo(extraInfo);
			String charsWith = "";
			for (String ch : army.getCharacters()) {
				charsWith += (charsWith.equals("") ? "" : ",") + ch;
			}
			a.setCharactersWith(charsWith);
			a.setNavy(army.isNavy() ? 1 : 0);
			nation.addArmy(a);
		}
	}
	
	private static void updateNationRelations(Game g,Data data,Turn t,Nation nation) {
		for (int i = 1; i <= 25; i++) {
			NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", i);
			if (nr != null) {
				data.setNationAlignment(nr.getNationNo()-1, -1, nation);
				if (nr.getAllegiance() == NationAllegianceEnum.FreePeople) {
					data.setNationAlignment(nr.getNationNo() - 1, 1, nation);
				} else if (nr.getAllegiance() == NationAllegianceEnum.DarkServants) {
					data.setNationAlignment(nr.getNationNo() - 1, 2, nation);
				} else if (nr.getAllegiance() == NationAllegianceEnum.Neutral) {
					data.setNationAlignment(nr.getNationNo() - 1, 0, nation);
				}
			} else {
				org.joverseer.metadata.domain.Nation n = g.getMetadata().getNationByNum(i);
				if (n != null) {
					if (n.getAllegiance() == NationAllegianceEnum.FreePeople) {
						data.setNationAlignment(n.getNumber() - 1, 1, nation);
					} else if (n.getAllegiance() == NationAllegianceEnum.DarkServants) {
						data.setNationAlignment(n.getNumber() - 1, 2, nation);
					} else if (n.getAllegiance() == NationAllegianceEnum.Neutral) {
						data.setNationAlignment(n.getNumber() - 1, 0, nation);
					}
				}
			}
		}
	}
	public void updateOrdercheckerGameData(int nationNo1) throws Exception {
		setNationNo(nationNo1);
		Main.mainFrame = new JFrame();
//		Resource res = Application.instance().getApplicationContext().getResource("classpath:metadata/orderchecker/orderchecker.dat");

//		final Main main = new Main(res.getInputStream());
		Data data = new Data();
		final Main main = new Main(true,data);
		Main.main = main;
		

		Game g = GameHolder.instance().getGame();
		Turn t = g.getTurn();

		Nation nation = updateNation(g, nationNo1, t);

		updatePC(t, nation);
		this.updateCharacters(g,t, nation);
		updateArmies(t, nation);

		//ReflectionUtils.invokeMethod(data, "findGame", new Object[] { nation });
		data.findGame(nation);

		updateNationRelations(g,data, t, nation);

		Object artifactList = ReflectionUtils.retrieveField(Main.main, "artifacts");
		ReflectionUtils.invokeMethod(artifactList, "configureList", new Object[] {});

		Main.main.setNation(nation);
	}

	private String extractString(String source, String start, String end) {
		int i = source.indexOf(start);
		int j = source.indexOf(end);
		if (i > -1 && j > -1) {
			return source.substring(i + start.length(), j);
		}
		return "";
	}

	public void parseInfoRequests(Vector<JCheckBox> requests) {
		Game g = GameHolder.instance().getGame();
		Turn t = g.getTurn();
		for (JCheckBox request : requests) {
			// extract prefix from request
			String reqText = request.getText();
			String prefix = reqText;
			int i = prefix.indexOf(":");
			prefix = prefix.substring(0, i);
			if (prefix.equals("FOOD")) {
				String commander = extractString(reqText, "Will ", "'s army be considered");
				if (commander != null) {
					Army army = t.getArmy(commander);
					if (army != null) {
						if (army.computeFed() != null) {
							request.setSelected(army.computeFed());
						}
					}
				}
				commander = extractString(reqText, "Will ", "'s army have 1 food");
				if (commander != null) {
					Army a = t.getArmy(commander);
					if (a != null) {
						if (a.getFood() != null && a.getFood() > 0)
							request.setSelected(true);
					}
				}
			} else if (prefix.equals("COMPANYCO")) {
				String commander = extractString(reqText, "Is ", " a company commander?");
				Company company = (Company) t.getContainer(TurnElementsEnum.Company).findFirstByProperty("commander", commander);
				if (company != null) {
					request.setSelected(true);
				}
			}

		}
		try {
			Object gameData = ReflectionUtils.invokeMethod(Main.main.getData(), "findGame", new Object[] { Main.main.getNation() });
			ReflectionUtils.assignField(gameData, "checkBoxes", requests.clone());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public HashMap<com.middleearthgames.orderchecker.Order, org.joverseer.domain.Order> getOrderMap() {
		return this.orderMap;
	}

	public void setOrderMap(HashMap<com.middleearthgames.orderchecker.Order, org.joverseer.domain.Order> orderMap) {
		this.orderMap = orderMap;
	}

	public static void main(String[] args) {
	}

	public Vector<com.middleearthgames.orderchecker.Order> getCharacterOrders(com.middleearthgames.orderchecker.Character ch) throws Exception {
		return (Vector<com.middleearthgames.orderchecker.Order>) ReflectionUtils.retrieveField(ch, "orders");
	}

	public Vector<String> getOrderInfoResults(Order o) throws Exception {
		return (Vector<String>) ReflectionUtils.retrieveField(o, "infoResults");
	}

	public Vector<String> getOrderWarnResults(Order o) throws Exception {
		return (Vector<String>) ReflectionUtils.retrieveField(o, "warnResults");
	}

	public Vector<String> getOrderHelpResults(Order o) throws Exception {
		return (Vector<String>) ReflectionUtils.retrieveField(o, "helpResults");
	}

	public Vector<String> getOrderErrorResults(Order o) throws Exception {
		return (Vector<String>) ReflectionUtils.retrieveField(o, "errorResults");
	}

	public HashMap<org.joverseer.domain.Order, com.middleearthgames.orderchecker.Order> getReverseOrderMap() {
		return this.reverseOrderMap;
	}

	public void setReverseOrderMap(HashMap<org.joverseer.domain.Order, com.middleearthgames.orderchecker.Order> reverseOrderMap) {
		this.reverseOrderMap = reverseOrderMap;
	}

	public int getNationNo() {
		return this.nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

}
