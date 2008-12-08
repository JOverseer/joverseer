package org.joverseer.support.readers.pdf;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.Character;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.support.readers.xml.TurnXmlReader;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

/**
 * Stores information about characters.
 * 
 * This information is complementary to character info read from the xmls. It contains:
 * - name
 * - order results
 * - hex number
 * - various death flags (assassinated, cursed, executed)
 * - artifacts
 * 
 * @author Marios Skounakis
 */
public class CharacterWrapper {
	String name;
	String orders;
	int hexNo;
    boolean assassinated;
    boolean cursed;
    boolean executed;
	String artifacts;
        
	ArrayList<OrderResult> orderResults = new ArrayList<OrderResult>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<OrderResult> getOrderResults() {
		return orderResults;
	}

	public void setOrderResults(ArrayList orderResults) {
		this.orderResults = (ArrayList<OrderResult>)orderResults;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}
	
	public void addOrderResult(OrderResult result) {
		orderResults.add(result);
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	
	public void updateCharacter(Character c) {
            c.setOrderResults(getOrders());
            // update artifacts
            int idx = getArtifacts().indexOf("β��"); 
            if (idx > -1) {
                // there is an artifact in use
                int i = getArtifacts().lastIndexOf("#", idx);
                int j = getArtifacts().indexOf(" ", i);
                String no = getArtifacts().substring(i+1, j).trim();
                int artiNo = Integer.parseInt(no);
                c.setArtifactInUse(artiNo);
            }
        }

    
        public boolean getAssassinated() {
            return assassinated;
        }
    
        
        public void setAssassinatedOn() {
            this.assassinated = true;
        }
        
        public boolean getCursed() {
            return cursed;
        }
    
        
        public void setCursedOn() {
            this.cursed = true;
        }

        public boolean getExecuted() {
            return executed;
        }
    
        
        public void setExecutedOn() {
            this.executed = true;
        }

        
        public String getArtifacts() {
            return artifacts;
        }

        
        public void setArtifacts(String artifacts) {
            this.artifacts = artifacts;
        }
        
        public void parsePopCenter(Game game, InfoSource infoSource, Character c) {
        	String orders = getCleanOrders();
        	if (orders == null) return;
        	int i = orders.substring(0, orders.length()-1).lastIndexOf(".");
        	if (i == -1) return;
        	String lastLine = orders.substring(i+2);
        	if (!lastLine.endsWith("is here.")) return;
        	
        	PopulationCenterSizeEnum size = null;
        	FortificationSizeEnum fort = FortificationSizeEnum.none;
        	if (lastLine.indexOf("Ruins") > -1) size = PopulationCenterSizeEnum.ruins;
        	if (lastLine.indexOf("Camp") > -1) size = PopulationCenterSizeEnum.camp;
        	if (lastLine.indexOf("Village") > -1) size = PopulationCenterSizeEnum.village;
        	if (lastLine.indexOf("Town") > -1) size = PopulationCenterSizeEnum.town;
        	if (lastLine.indexOf("Major Town") > -1) size = PopulationCenterSizeEnum.majorTown;
        	if (lastLine.indexOf("City") > -1) size = PopulationCenterSizeEnum.city;
        	
        	if (lastLine.indexOf("Tower") > -1) fort = FortificationSizeEnum.tower;
        	if (lastLine.indexOf("Fort") > -1) fort = FortificationSizeEnum.fort;
        	if (lastLine.indexOf("Keep") > -1) fort = FortificationSizeEnum.keep;
        	if (lastLine.indexOf("Castle") > -1) fort = FortificationSizeEnum.castle;
        	if (lastLine.indexOf("Citadel") > -1) fort = FortificationSizeEnum.citadel;
        	
        	Integer nationNo = null;
        	if (lastLine.indexOf("un-owned")>-1) nationNo = 0;
        	if (nationNo == null) {
        		for (int j=1; j<=25; j++) {
        			Nation n = game.getMetadata().getNationByNum(j);
        			if (lastLine.indexOf(n.getName()) > -1) {
        				nationNo = j;
        				break;
        			}
        		}
        	}
        	
        	int j = lastLine.indexOf(" of ");
        	int k = lastLine.indexOf(" flying ");
        	if (k == -1) 
    		{
    			k = lastLine.indexOf(" is here.");
    		}
        	String name = lastLine.substring(j + 4, k);
        	
        	if (name != null && size != null && nationNo != null) {        	
	        	PopulationCenter popCenter = new PopulationCenter();
	        	popCenter.setName(name);
	        	popCenter.setSize(size);
	        	popCenter.setFortification(fort);
	        	popCenter.setHarbor(HarborSizeEnum.none);
	        	popCenter.setNationNo(nationNo);
	        	popCenter.setInfoSource(new XmlTurnInfoSource(infoSource.getTurnNo(), 0));
	        	popCenter.setHexNo(c.getHexNo());
	        	popCenter.setInformationSource(InformationSourceEnum.detailed);
	        	
	        	PopulationCenter pc = (PopulationCenter)game.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", c.getHexNo());
	        	if (pc == null || pc.getInformationSource().getValue() < popCenter.getInformationSource().getValue()) {
	        		if (pc != null) game.getTurn().getContainer(TurnElementsEnum.PopulationCenter).removeItem(pc);
	        		game.getTurn().getContainer(TurnElementsEnum.PopulationCenter).addItem(popCenter);
	        	}
        	}
        	
        }
        
        public void parseScoHexOrScoPop(Game game, InfoSource infoSource, Character ch) {
        	if (getOrders() == null) return;
        	
        	parseArmiesFromScoHexOrScoPop(game, infoSource, ch);
        	parsePopCenterFromScoHexOrScoPop(game, infoSource, ch);
        }
        
        public String getCleanOrders() {
        	String orders = getOrders();
        	orders = orders.replace("\r\n", " ");
        	while (orders.contains("  ")) {
        		orders = orders.replace("  ", " ");
        	}
        	return orders;
        }
        
        public void parsePopCenterFromScoHexOrScoPop(Game game, InfoSource infoSource, Character ch) {
        	String scoHex = "He was ordered to scout the hex.";
        	String orders = getCleanOrders();
        	int i = orders.indexOf(scoHex);
        	if (i == -1) {
        		scoHex = "He was ordered to scout the population center.";
            	i = orders.indexOf(scoHex);
        	}
        	if (i > -1) {
        		String p = orders.substring(i + scoHex.length());
        		int j = p.indexOf("He was ordered", i + scoHex.length());
        		if (j > -1) {
        			p = p.substring(0, j);
        		}
        		
        		String up = p.toUpperCase();
        		
        		PopulationCenterSizeEnum size = null;
        		
        		if (up.contains(" MAJOR TOWN ")) {
        			size = PopulationCenterSizeEnum.majorTown;
        		} else if (up.contains(" TOWN ")) {
        			size = PopulationCenterSizeEnum.town;
        		} else if (up.contains(" CITY ")) {
        			size = PopulationCenterSizeEnum.city;
        		} else if (up.contains(" CAMP ")) {
        			size = PopulationCenterSizeEnum.camp;
        		} else if (up.contains(" VILLAGE ")) {
        			size = PopulationCenterSizeEnum.village;
        		}else if (up.contains(" RUINS ")) {
        			size = PopulationCenterSizeEnum.ruins;
        		}
        		
        		FortificationSizeEnum fort = null;
        		if (up.contains(" TOWER ")) {
        			fort = FortificationSizeEnum.tower;
        		} else if (up.contains(" FORT ")) {
        			fort = FortificationSizeEnum.fort;
        		} else if (up.contains(" CASTLE ")) {
        			fort = FortificationSizeEnum.castle;
        		} else if (up.contains(" KEEP ")) {
        			fort = FortificationSizeEnum.keep;
        		} else if (up.contains(" CITADEL ")) {
        			fort = FortificationSizeEnum.citadel;
        		} 
        		
        		String pcName = null;
        		j = p.indexOf("named");
        		if (j > -1) {
        			int k = p.indexOf("is here");
        			if (k > -1) {
        				pcName = p.substring(j, k).trim();
        			}
        		}
        		
        		HarborSizeEnum harbor = null;
        		if (up.contains(" PORT ")) {
        			harbor = HarborSizeEnum.port;
        		} else if (up.contains(" HARBOR ")) {
        			harbor = HarborSizeEnum.harbor;
        		}
        		
        		Integer nationNo = null;
        		for (int ni=1; ni<=25; ni++) 
        		{
        			if (p.contains("is owned by " + game.getMetadata().getNationByNum(ni).getName()) ||
        					p.contains("is owned by the " + game.getMetadata().getNationByNum(ni).getName())) {
        				nationNo = ni;
        			}
        		}
        		if (p.contains("un-owned")) {
        			nationNo = 0;
        		}
        		
        		Integer loyalty = null;
        		String loyaltyStr = "loyalty = ";
        		j = p.indexOf(loyaltyStr);
        		if (j > -1) {
        			int k = p.indexOf(".", j);
        			if (k > -1) {
        				try {
        					loyalty = Integer.parseInt(p.substring(j + loyaltyStr.length(), k).trim());
        				}
        				catch (Exception exc) {
        					// do nothing
        				}
        			}
        		}
        		
        		PopulationCenter pc = (PopulationCenter)game.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", ch.getHexNo());
        		if (pc == null) {
        			if (pcName != null) {
        				pc = new PopulationCenter();
        				pc.setHexNo(ch.getHexNo());
        				pc.setName(pcName);
        				pc.setFortification(FortificationSizeEnum.none);
        				pc.setSize(size);
        				pc.setInfoSource(infoSource);
        				pc.setInformationSource(InformationSourceEnum.someMore);
        				pc.setNationNo(nationNo);
        				game.getTurn().getContainer(TurnElementsEnum.PopulationCenter).addItem(pc);
        			}
        		}
        		if (pc != null && pc.getInformationSource().getValue() <= InformationSourceEnum.someMore.getValue()) {
        			if (harbor != null) {
        				pc.setHarbor(harbor);
        			}
        			if (nationNo != null) {
        				pc.setNationNo(nationNo);
        			}
        			if (fort != null) {
        				pc.setFortification(fort);
        			}
        			if (loyalty != null) {
        				pc.setLoyalty(loyalty);
        			}
        		}
        	}
        }
        
        public void parseArmiesFromScoHexOrScoPop(Game game, InfoSource infoSource, Character ch) {
        	String orders = getCleanOrders();
        	String scoutHex= "was ordered to scout the hex";
        	String scoutPopCenter= "was ordered to scout the population center";
        	
        	if (orders.indexOf(scoutHex) == -1 && orders.indexOf(scoutPopCenter) == -1) return;
        	
        	String foreignForcesPresent = "Foreign forces present:";
        	
        	
        	int i = orders.indexOf(foreignForcesPresent);
        	if (i == -1) {
        		foreignForcesPresent = "Foreign armies present:";
        		i = orders.indexOf(foreignForcesPresent);
        	}
        	if (i == -1) return;
        	
        	int j = orders.substring(i + foreignForcesPresent.length()).indexOf(".");
        	
        	String p = orders.substring(i + foreignForcesPresent.length());
        	p = p.substring(0, j);
        	String[] parts = p.split(" - ");
        	for (String part : parts) { 
        		Army a = null;
        		boolean addCharacter = true;
        		
        		part = part.replace("-", "");
        		String parseString = " of the ";
        		int k = part.indexOf(parseString);
        		if (k == -1) {
        			parseString = " of ";
        			k = part.indexOf(parseString);
        		}
        		if (k > -1) { // name and nation
        			String name = part.substring(0, k).trim();
        			String nation = part.substring(k+parseString.length()).trim();
    				Nation n = game.getMetadata().getNationByName(nation);
    				if (n != null) {
    					a = new Army();
        				a.setCommanderName(name);
        				a.setCommanderTitle("");
        				a.setInfoSource(infoSource);
        				a.setInformationSource(InformationSourceEnum.someMore);
        				a.setHexNo(String.valueOf(ch.getHexNo()));
        				a.setSize(ArmySizeEnum.unknown);
    					a.setNationNo(n.getNumber());
    					a.setNationAllegiance(n.getAllegiance());
    				}
        		} else {
        			// nation only
        			String nation = part.trim();
        			Nation n = game.getMetadata().getNationByName(nation);
    				if (n != null) {
    					a = new Army();
        				a.setCommanderName("Unknown (Map Icon)");
        				addCharacter = false;
        				a.setCommanderTitle("");
        				a.setInfoSource(infoSource);
        				a.setInformationSource(InformationSourceEnum.someMore);
        				a.setHexNo(String.valueOf(ch.getHexNo()));
        				a.setSize(ArmySizeEnum.unknown);
    					a.setNationNo(n.getNumber());
    					a.setNationAllegiance(n.getAllegiance());
    				}
    				
        		}
        		
        		if (a != null) {
        			Army oldA = null;
        			if (addCharacter) {
        				oldA = (Army)game.getTurn().getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", name);
        			}
        			if (oldA == null) {
        				// add army
        				new TurnXmlReader(game).addArmy(a, game.getTurn(), addCharacter);
        			}
        		}
        	}
        	
        	
        }
        
        public void parseArmiesFromDivineNationForces(Game game, InfoSource infoSource, Character ch) {
        	String orders = getCleanOrders();
        	String dnf = "was ordered to cast a lore spell. Divine Nation Forces - ";
        	int i = orders.indexOf(dnf);
        	if (i == -1) return;
    		String p = orders.substring(i + dnf.length());
    		int k = p.indexOf(".");
    		if (k == -1) return;
    		p = p.substring(0, k);
    		String split = " - ";
    		k = p.indexOf(split);
    		if (k == -1) return;
			String firstPart = p.substring(0, k);
			String armiesPart = p.substring(k + split.length());
			int l = firstPart.indexOf("forces near");
			String nation = firstPart.substring(0, l).trim();
			Nation n = game.getMetadata().getNationByName(nation);
			if (n == null) return;
			
			String[] armies = armiesPart.split(" at \\d{4}");
			for (String army : armies) {
				String name = army.trim();
				if (name.length() < 5) continue;
				String namePlusAt = name + " at ";
				int namei = armiesPart.indexOf(namePlusAt);
				String hexNoStr = armiesPart.substring(namei + namePlusAt.length(), namei + namePlusAt.length()+4);
				int hexNo;
				try {
					hexNo = Integer.parseInt(hexNoStr);
				}
				catch (Exception e) {
					continue;
				}
				Army oldArmy = (Army)(Army)game.getTurn().getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", name);
				if (oldArmy == null) {
					Army a = new Army();
    				a.setCommanderName(name);
    				a.setCommanderTitle("");
    				a.setInfoSource(infoSource);
    				a.setInformationSource(InformationSourceEnum.someMore);
    				a.setHexNo(String.valueOf(hexNo));
    				a.setSize(ArmySizeEnum.unknown);
					a.setNationNo(n.getNumber());
					a.setNationAllegiance(n.getAllegiance());
					new TurnXmlReader(game).addArmyBeta(a, game.getTurn());
				}
			}
        }

        public void parseDivineCharsWithForces(Game game, InfoSource infoSource, Character ch) {
        	String orders = getCleanOrders();
        	String dnf = "was ordered to cast a lore spell. Divine Characters w/Forces - ";
        	int i = orders.indexOf(dnf);
        	if (i == -1) return;
    		String p = orders.substring(i + dnf.length());
    		int k = p.indexOf(".");
    		if (k == -1) return;
    		p = p.substring(0, k);
    		String commandedBy = "commanded by ";
    		k = p.indexOf(commandedBy);
    		int l = p.indexOf(" : - ");
    		if (k == -1 || l == -1) return;
    		String commanderName = p.substring(k + commandedBy.length(), l).trim();
    		Army a = (Army)game.getTurn().getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", commanderName);
    		if (a == null) {
    			return;
    			// not easy to deduce army's nation or allegiance, so skipping the army altogether
    		}
    		String charsStr = p.substring(l+5);
    		String[] chars = charsStr.split("-");
    		for (String c : chars) {
    			c = c.trim();
    			if (!a.getCharacters().contains(c)) {
    				a.getCharacters().add(c);
    			}
    			Character ec = (Character)game.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", c);
    			if (ec == null) {
    				// add character
    				ec = new Character();
    				ec.setName(c);
    				ec.setNationNo(a.getNationNo());
    				ec.setInfoSource(infoSource);
    				ec.setInformationSource(InformationSourceEnum.limited);
    				ec.setHexNo(Integer.parseInt(a.getHexNo()));
    				game.getTurn().getContainer(TurnElementsEnum.Character).addItem(ec);
    			}
    		}
    		
    		
        }
        

}
