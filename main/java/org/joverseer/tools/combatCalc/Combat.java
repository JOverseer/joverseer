package org.joverseer.tools.combatCalc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;

import sun.security.action.GetLongAction;

/**
 * Represents a land combat for the combat calculator.
 * 
 * It holds all information pertinent to the execution of a combat, such as
 * - terrain
 * - climate
 * - involved armies and pop centers
 * - relations
 * 
 * 
 * @author Marios Skounakis
 *
 */
public class Combat implements Serializable, IHasMapLocation {
    private static final long serialVersionUID = 6784272689637435343L;
    static int maxArmies = 10;
    static int maxAll = 11;
    
    HexTerrainEnum terrain;
    ClimateEnum climate;
    
    int hexNo;
    String description;

    CombatArmy[] side1 = new CombatArmy[maxAll];
    CombatArmy[] side2 = new CombatArmy[maxAll];
    
    NationRelationsEnum[][] side1Relations = new NationRelationsEnum[maxAll][maxAll];
    NationRelationsEnum[][] side2Relations = new NationRelationsEnum[maxAll][maxAll];
     
    boolean[][] side1Attack = new boolean[maxAll][maxAll];
    boolean[][] side2Attack = new boolean[maxAll][maxAll];
    
    CombatPopCenter side1Pc = null;
    CombatPopCenter side2Pc = null;
    
    int rounds = 0;
    
    boolean attackPopCenter = true;

    int maxRounds;
    
    String log;
    
    public Combat() {
        for (int i=0; i<maxAll; i++) {
            for (int j=0; j<maxAll; j++) {
                side1Relations[i][j] = NationRelationsEnum.Disliked;
                side2Relations[i][j] = NationRelationsEnum.Disliked;
                side1Attack[i][j] = true;
                side2Attack[i][j] = true;
            }
            side1[i] = null;
            side2[i] = null;
        }
        terrain = HexTerrainEnum.plains;
        climate = ClimateEnum.Cool;
    }
    
    
    
    public static int computeNativeArmyStrength(CombatArmy ca, HexTerrainEnum terrain, ClimateEnum climate, boolean againstPopCenter) {
        return computeNativeArmyStrength(ca, terrain, climate, null, againstPopCenter);
    }
    
    public static int computeNativeArmyStrength(CombatArmy ca, HexTerrainEnum terrain, ClimateEnum climate, Double lossesOverride, boolean againstPopCenter) {
        int strength = 0;
        boolean wm = false;
        for (ArmyElement ae : ca.getElements()) {
            Integer s = InfoUtils.getTroopStrength(ae.getArmyElementType(), "Attack");
            if (ae.getArmyElementType() == ArmyElementType.WarMachimes && againstPopCenter) {
            	s = 200;
            	wm = true;
            }
            if (s == null)
                continue;
            
            int tacticMod = !wm ? InfoUtils.getTroopTacticModifier(ae.getArmyElementType(), ca.getTactic()) : 100;
            int terrainMod = !wm ? InfoUtils.getTroopTerrainModifier(ae.getArmyElementType(), terrain) : 100;
            double mod = (double) (ae.getTraining() + ae.getWeapons() + tacticMod + terrainMod) / 400d;
            
            strength += s * ae.getNumber() * mod;
        }
        //System.out.println("Str before mods: " + strength);
        strength = strength
                * (ca.getCommandRank() + ca.getMorale() + CombatModifiers.getModifierFor(
                        ca.getNationNo(), terrain, climate) * 2) / 400;
        if (lossesOverride == null) {
            lossesOverride = ca.getLosses();
        }
        strength = (int)(strength * (double)(100 - lossesOverride) / 100d);
        return strength;
    }
    
    public static int computNativeArmyConstitution(CombatArmy ca) {
        return computNativeArmyConstitution(ca, null);
    }

    public static int computNativeArmyConstitution(CombatArmy ca, Double lossesOverride) {
        int constit = 0;
        for (ArmyElement ae : ca.getElements()) {
            Integer s = InfoUtils.getTroopStrength(ae.getArmyElementType(), "Defense");
            if (s == null)
                continue;
            constit += s * ae.getNumber() * (double) (100 + ae.getArmor()) / 100d;
        }
        if (lossesOverride == null) {
            lossesOverride = ca.getLosses();
        }
        constit = (int)(constit * (double)(100 - lossesOverride) / 100d);
        return constit;
    }

    public static int computeModifiedArmyStrength(HexTerrainEnum terrain, ClimateEnum climate, CombatArmy att,
            CombatArmy def) {
        int s = computeNativeArmyStrength(att, terrain, climate, false);
        int tacticVsTacticMod = InfoUtils.getTacticVsTacticModifier(att.getTactic(), def.getTactic());
        s = (int) (s * (double) tacticVsTacticMod / 100d);
        return s;
    }

    public static double computeLosses(CombatArmy ca, int enemyStrength) {
        int constit = computNativeArmyConstitution(ca, 0d);
        return Math.min((double)enemyStrength * 100 / (double)Math.max(constit,1), 100);
    }
    
    protected void addToLog(String msg) {
        log += msg + "\n";
    }
    
    public void runPcBattle(int attackerSide, int round) {
        int defenderSide = (attackerSide == 0 ? 1 : 0);
        
        // compute str for attacker
        int wms = 0;
        int attackerStr = 0;
        int totalCon = 0;
        CombatPopCenter pc = (attackerSide == 0 ? side2Pc : side1Pc);
        double[] losses = new double[maxArmies];
        for (int i=0; i<maxArmies; i++) {
            if (attackerSide == 0) {
                if (side1[i] == null) continue;
                int str = computeNativeArmyStrength(side1[i], terrain, climate, true);
                // adjust for relations
                int relMod = CombatModifiers.getRelationModifier(side1Relations[i][maxAll-1]);
                str = (int)(str * (double)relMod / 100d);
                attackerStr += str;
                int wm = side1[i].getWM().getNumber(); 
                wms += wm;
                totalCon += computNativeArmyConstitution(side1[i]);
                losses[i] = side1[i].getLosses();
            } else {
                if (side2[i] == null) continue;
                int str = computeNativeArmyStrength(side2[i], terrain, climate, true);
                // adjust for relations
                int relMod = CombatModifiers.getRelationModifier(side2Relations[i][maxAll-1]);
                str = (int)(str * (double)relMod / 100d);
                attackerStr += str;
                wms += side2[i].getWM().getNumber();
                totalCon += computNativeArmyConstitution(side2[i]);
                losses[i] = side2[i].getLosses();
            }
        }
        
        // compute pop center defense and attack
        int popCenterStr = computePopCenterStrength(pc, wms);
        pc.setCaptured(popCenterStr <= attackerStr);
        pc.setStrengthOfAttackingArmies(attackerStr);
        for (int i=0; i<maxArmies; i++) {
            if (attackerSide == 0) {
                if (side1[i] == null) continue;
                double l = computeNewLossesFromPopCenter(side1[i], pc, side2Relations[maxAll - 1][i], totalCon, wms, round);
                side1[i].setLosses(side1[i].getLosses() + l);
            } else {
                if (side2[i] == null) continue;
                double l = computeNewLossesFromPopCenter(side2[i], pc, side1Relations[maxAll - 1][i], totalCon, wms, round);
                side2[i].setLosses(side2[i].getLosses() + l);
            }
        }
    }

    public void runArmyBattle() {
        rounds = 0;
        boolean finished = false;
        log = "";
        do {
            addToLog("Starting round " + rounds);
            double[] side1Losses = new double[maxArmies];
            double[] side2Losses = new double[maxArmies];

            // compute constitution for each side
            int side1Con = 0;
            int side2Con = 0;
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca = side1[i];
                if (ca == null) continue;
                int constit = computNativeArmyConstitution(ca);
                int sconstit = computNativeArmyConstitution(ca, 0d);
                addToLog("Side 1, army " + i + " con: " + constit + "/" + sconstit);
                side1Con += constit;
                side1Losses[i] = ca.getLosses();
            }
            addToLog("Total Side 1 con: " + side1Con);
            addToLog("");
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca = side2[i];
                if (ca == null) continue;
                int constit = computNativeArmyConstitution(ca);
                int sconstit = computNativeArmyConstitution(ca, 0d);
                addToLog("Side 2, army " + i + " con: " + constit + "/" + sconstit);
                side2Con += constit;
                side2Losses[i] = ca.getLosses();
            }
            addToLog("Total Side 2 con: " + side2Con);
            side1Con = Math.max(side1Con, 1);
            side2Con = Math.max(side2Con, 1);
            //if (side1Con == 1 || side2Con == 1) return;
            
            addToLog("");

            boolean side1Alive = false;
            boolean side2Alive = false;
            // compute losses for each army
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca1 = side1[i];
                if (ca1 == null) continue;
                
                for (int j=0; j<maxArmies; j++) {
                    CombatArmy ca2 = side2[j];
                    if (ca2 == null) continue;
                    
                    // losses for ca1
                    addToLog("");
                    addToLog("Computing Losses for 2," + j + " attacking 1," + i);
                    side1Losses[i] += computeNewLosses(terrain, climate, ca2, ca1, side2Relations[j][i], side1Con, rounds);
                    if (side1Losses[i] < 99.5) {
                        side1Alive = true;
                    }
                    
                    // losses for ca2
                    addToLog("");
                    addToLog("Computing Losses for 1," + i + " attacking 2," + j);
                    side2Losses[j] += computeNewLosses(terrain, climate, ca1, ca2, side1Relations[i][j], side2Con, rounds);
                    if (side2Losses[j] < 99.5) {
                        side2Alive = true;
                    }
                }
            }
            
            // assign losses to armies
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca1 = side1[i];
                if (ca1 == null) continue;
                ca1.setLosses(Math.min(side1Losses[i], 100));
                addToLog("Side 1 army " + i + " new con : " + computNativeArmyConstitution(ca1));
            }
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca2 = side2[i];
                if (ca2 == null) continue;
                ca2.setLosses(Math.min(side2Losses[i], 100));
                addToLog("Side 2 army " + i + " new con : " + computNativeArmyConstitution(ca2));
            }        
            
            rounds++;
            finished = !(side1Alive && side2Alive) || rounds >= maxRounds;
            addToLog("");
            addToLog("");
        } while (!finished);
        System.out.println(log);
    }
    
    public int computePopCenterStrength(CombatPopCenter pc) {
        return computePopCenterStrength(pc, 0);
    }
    
    public int computePopCenterStrength(CombatPopCenter pc, int numberOfWarMachines) {
        int popDef = new int[]{200, 500, 1000, 2500, 5000}[pc.getSize().getCode()-1];
        int fortDef = new int[]{0, 2000, 6000, 10000, 16000, 24000}[pc.getFort().getSize()];
        int wmStr = numberOfWarMachines * 200;
        fortDef = Math.max(fortDef - wmStr, 0);
        return (int)Math.round(((double)popDef + (double)fortDef) * (100d + (double)pc.getLoyalty()) / 100d);
    }
    
    public double computeNewLossesFromPopCenter(CombatArmy army,
            CombatPopCenter pc,
            NationRelationsEnum relations,
            int armySideTotalCon,
            int armyTotalWMs,
            int round) {
        int relMod = CombatModifiers.getRelationModifier(relations);
        int defCon = computNativeArmyConstitution(army);
        int attStr = computePopCenterStrength(pc, armyTotalWMs);
        int defBonus = 0;
        // adjust by relations
        attStr = (int)((double)attStr * (double)relMod / 100d);
        // handle first round
        if (round == 0) {
            defBonus = army.getDefensiveAddOns();
        }
        double lossesFactor = (double)defCon / (double)armySideTotalCon;
        addToLog("Defender loss factor: " + lossesFactor);
        attStr = (int)(attStr * lossesFactor) - defBonus;
        if (attStr < 0) attStr = 0;
        double l = computeLosses(army, attStr);
        addToLog("New losses: " + l);
        return l;
    }
    
    public double computeNewLosses(HexTerrainEnum terrain,
                                            ClimateEnum climate,
                                            CombatArmy att,
                                            CombatArmy def,
                                            NationRelationsEnum relations,
                                            int defenderSideTotalCon,
                                            int round) {
        int relMod = CombatModifiers.getRelationModifier(relations);
        int defCon = computNativeArmyConstitution(def);
        int attStr = computeModifiedArmyStrength(terrain, climate, att, def);
        addToLog("Relations mod: " + relMod);
        addToLog("Attacker modified str: " + attStr);
        int attBonus = 0;
        int defBonus = 0;
        // adjust by relations
        attStr = (int)((double)attStr * (double)relMod / 100d);
        // handle first round
        if (round == 0) {
            attBonus = att.getOffensiveAddOns();
            defBonus = def.getDefensiveAddOns();
            attStr += attBonus;
            addToLog("First round - str: " + attBonus + " con: " + defBonus);
        }
        double lossesFactor = (double)defCon / (double)defenderSideTotalCon;
        addToLog("Defender loss factor: " + lossesFactor);
        attStr = (int)(attStr * lossesFactor) - defBonus;
        if (attStr < 0) attStr = 0;
        double l = computeLosses(def, attStr);
        addToLog("New losses: " + l);
        return l;
    }

    
    public ClimateEnum getClimate() {
        return climate;
    }

    
    public void setClimate(ClimateEnum climate) {
        this.climate = climate;
    }

    
    public CombatArmy[] getSide1() {
        return side1;
    }

    
    public void setSide1(CombatArmy[] side1) {
        this.side1 = side1;
    }

    
    public boolean[][] getSide1Attack() {
        return side1Attack;
    }

    
    public void setSide1Attack(boolean[][] side1Attack) {
        this.side1Attack = side1Attack;
    }

    
    public NationRelationsEnum[][] getSide1Relations() {
        return side1Relations;
    }

    
    public void setSide1Relations(NationRelationsEnum[][] side1Relations) {
        this.side1Relations = side1Relations;
    }

    
    public CombatArmy[] getSide2() {
        return side2;
    }

    
    public void setSide2(CombatArmy[] side2) {
        this.side2 = side2;
    }

    
    public boolean[][] getSide2Attack() {
        return side2Attack;
    }

    
    public void setSide2Attack(boolean[][] side2Attack) {
        this.side2Attack = side2Attack;
    }

    
    public NationRelationsEnum[][] getSide2Relations() {
        return side2Relations;
    }

    
    public void setSide2Relations(NationRelationsEnum[][] side2Relations) {
        this.side2Relations = side2Relations;
    }

    
    public HexTerrainEnum getTerrain() {
        return terrain;
    }

    
    public void setTerrain(HexTerrainEnum terrain) {
        this.terrain = terrain;
    }

    public boolean addToSide(int side, CombatArmy ca) {
        if (side == 0) {
            for (int i=0; i<side1.length; i++) {
                if (side1[i] == null) {
                    side1[i] = ca;
                    return true;
                }
            }
            return false;
        } else {
            for (int i=0; i<side2.length; i++) {
                if (side2[i] == null) {
                    side2[i] = ca;
                    return true;
                }
            }
            return false;

        }
    }
    
    public boolean removeFromSide(int side, CombatArmy ca) {
        boolean found = false;
        if (side == 0) {
            for (int i=0; i<side1.length; i++) {
                if (side1[i] == ca) {
                    side1[i] = null;
                    found = true;
                }
                if (i > 0 && side1[i-1] == null && side1[i] != null) {
                    side1[i-1] = side1[i];
                    side1[i] = null;
                }
            }
            return found;
        } else {
            for (int i=0; i<side2.length; i++) {
                if (side2[i] == ca) {
                    side2[i] = null;
                    found = true;
                }
                if (i > 0 && side2[i-1] == null && side2[i] != null) {
                    side2[i-1] = side2[i];
                    side2[i] = null;
                }
            }
            return found;

        }
    }
    
    public NationAllegianceEnum estimateAllegianceForSide(int side) {
        NationAllegianceEnum ret = null;
        Game g = GameHolder.instance().getGame();
        CombatArmy[] cas = (side == 0 ? side1 : side2);
        for (CombatArmy ca : cas) {
            if (ca == null) continue;
            if (ca.getNationNo() > 0) {
                NationRelations nr = (NationRelations)g.getTurn().getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", ca.getNationNo());
                if (nr != null) {
                    if (ret == null) {
                        ret = nr.getAllegiance();
                    } else if (nr.getAllegiance() != ret) {
                        ret = null;
                        return ret;
                    } else {
                        // allegiances match, do nothing
                    }
                }
            }
        }
        // no allegiance given
        return ret;
    }

    
    public int getMaxRounds() {
        return maxRounds;
    }

    
    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    
    public int getHexNo() {
        return hexNo;
    }

    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public int getArmyIndex(int side, CombatArmy a) {
        CombatArmy[] cas = (side == 0 ? side1 : side2);
        for (int i=0; i<cas.length; i++) {
            if (cas[i] == a) return i;
        }
        return -1;
    }
    
    public int getX() {
    	return getHexNo() / 100;
    }
    
    public int getY() {
    	return getHexNo() % 100;
    }

    
    public CombatPopCenter getSide1Pc() {
        return side1Pc;
    }

    
    public void setSide1Pc(CombatPopCenter side1Pc) {
        this.side1Pc = side1Pc;
    }

    
    public CombatPopCenter getSide2Pc() {
        return side2Pc;
    }

    
    public void setSide2Pc(CombatPopCenter side2Pc) {
        this.side2Pc = side2Pc;
    }

    public void loadTerrainAndClimateFromHex() {
    	HexInfo hi = (HexInfo)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", getHexNo());
    	if (hi != null) {
    		setClimate(hi.getClimate());
    	}
    	Hex hex = GameHolder.instance().getGame().getMetadata().getHex(getHexNo());
    	if (hex != null) {
    		setTerrain(hex.getTerrain());
    	}
    }
    
    public void autoSetRelationsToHated() {
    	
    	for (int i=0; i<maxAll; i++) {
    		for (int j=0; j<maxAll; j++) {
    			side1Relations[i][j] = NationRelationsEnum.Hated;
    			side2Relations[i][j] = NationRelationsEnum.Hated;
    		}
    	}
    }



	public boolean getAttackPopCenter() {
		return attackPopCenter;
	}



	public void setAttackPopCenter(boolean attackPopCenter) {
		this.attackPopCenter = attackPopCenter;
	}
    
}
