package org.joverseer.tools.combatCalc;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmyEstimate;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;

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
    public static int MAX_ARMIES = 10;
    public static int MAX_ALL = 11;
    
    HexTerrainEnum terrain;
    ClimateEnum climate;
    
    int hexNo;
    String description;

    CombatArmy[] side1 = new CombatArmy[MAX_ALL];
    CombatArmy[] side2 = new CombatArmy[MAX_ALL];
    CombatArmy[] otherSide = new CombatArmy[MAX_ALL];
    
    NationRelationsEnum[][] side1Relations = new NationRelationsEnum[MAX_ALL][MAX_ALL];
    NationRelationsEnum[][] side2Relations = new NationRelationsEnum[MAX_ALL][MAX_ALL];
    NationRelationsEnum[] popCenterRelations = new NationRelationsEnum[MAX_ALL];
     
    boolean[][] side1Attack = new boolean[MAX_ALL][MAX_ALL];
    boolean[][] side2Attack = new boolean[MAX_ALL][MAX_ALL];
    
    CombatPopCenter side1Pc = null;		//Never used???
    CombatPopCenter side2Pc = null;
    
    int rounds = 0;
    
    boolean attackPopCenter = true;

    private int maxRounds = 100 ; ; // Nice high number. Shouldn't ever get to this round.
    
    public String log;
    
    public String playerLog;
    public boolean addToPlayerLogBool;
    public combatLogClass logger;
    
    public Combat() {
        for (int i=0; i<MAX_ALL; i++) {
            for (int j=0; j<MAX_ALL; j++) {
                this.side1Relations[i][j] = NationRelationsEnum.Disliked;
                this.side2Relations[i][j] = NationRelationsEnum.Disliked;
                this.side1Attack[i][j] = true;
                this.side2Attack[i][j] = true;
            }
            this.side1[i] = null;
            this.side2[i] = null;
            this.popCenterRelations[i] = NationRelationsEnum.Disliked;
        }
        this.terrain = HexTerrainEnum.plains;
        this.climate = ClimateEnum.Cool;
        this.logger = new combatLogClass();
    }
    
    
    
    public int getRounds() {
		return this.rounds;
	}

	public void setRounds(int rounds) {
		this.rounds = rounds;
	}

	public static int computeNativeArmyStrength(CombatArmy ca, HexTerrainEnum terrain, ClimateEnum climate, boolean againstPopCenter) {
        return computeNativeArmyStrength(ca, terrain, climate, null, againstPopCenter);
	}
    
    public static int computeNativeArmyStrength(CombatArmy army, HexTerrainEnum terrain, ClimateEnum climate, Double lossesOverride, boolean againstPopCenter) {
        int strength = 0;
        
        GameMetadata gm = GameHolder.instance().getGame().getMetadata();
        int cm;
        if(gm.getGameType() != GameTypeEnum.gameFA) cm = CombatModifiers.getModifierFor(army.getNationNo(), terrain, climate);
        else cm = CombatModifiers.getModifierFor(InfoUtils.getRegionNumByName(GameTypeEnum.gameFA, army.getRegion()), terrain, climate);
        
        //add up army modifiers
        int armyModifiers = army.getCommandRank() + army.getMorale() + cm;
        
        int troopModifiers = 0;
        
        for (ArmyElement armyElement : army.getElements()) {
        	int tacticMod = 100;
        	int terrainMod = 100;
        	
            Integer troopStrength = InfoUtils.getTroopStrength(armyElement.getArmyElementType(), "Attack");   

//            if (armyElement.getArmyElementType() == ArmyElementType.WarMachimes && againstPopCenter) {
//            	troopStrength = 200;
//            }
            
            if (troopStrength == null || troopStrength == 0)
                continue;
            
            
            if (armyElement.getArmyElementType() != ArmyElementType.WarMachimes) {
                tacticMod = !againstPopCenter ? InfoUtils.getTroopTacticModifier(armyElement.getArmyElementType(), army.getTactic()) : 100;
                terrainMod = InfoUtils.getTroopTerrainModifier(armyElement.getArmyElementType(), terrain);
            }
            
            troopModifiers = armyElement.getTraining() + armyElement.getWeapons() + tacticMod + terrainMod;
            double mod = (double) (armyModifiers + troopModifiers) / 800d;
            
            strength += troopStrength * armyElement.getNumber() * mod;
        }

        if (lossesOverride == null) {
            lossesOverride = army.getLosses();
        }

        strength = (int)(strength * (double)(100 - lossesOverride) / 100d);
        return strength;
    }
    
    /*
     * Non-static version, for logs...
     */
    public int computeNativeArmyStrength(CombatArmy army, Double lossesOverride, boolean againstPopCenter) {
		HexTerrainEnum tr = this.terrain;
		ClimateEnum cl = this.climate;

        int strength = 0;
        String s = "";
        
        GameMetadata gm = GameHolder.instance().getGame().getMetadata();
        int cm;
        if(gm.getGameType() != GameTypeEnum.gameFA) cm = CombatModifiers.getModifierFor(army.getNationNo(), tr, cl);
        else cm = CombatModifiers.getModifierFor(InfoUtils.getRegionNumByName(GameTypeEnum.gameFA, army.getRegion()), tr, cl);
        
        s += "\tCombat (Nation) Terrain + Climate Modifiers: " + cm;
        
        //add up army modifiers
        int armyModifiers = army.getCommandRank() + army.getMorale() + cm;
        
        s += "\n\tArmy Modifiers (Command Rank + Morale + CM): " + armyModifiers;
        
        int troopModifiers = 0;
        
        for (ArmyElement armyElement : army.getElements()) {
        	if(armyElement.getNumber() == 0) continue;
        	int tacticMod = 100;
        	int terrainMod = 100;
        	
            Integer troopStrength = InfoUtils.getTroopStrength(armyElement.getArmyElementType(), "Attack");   

//            if (armyElement.getArmyElementType() == ArmyElementType.WarMachimes && againstPopCenter) {
//            	troopStrength = 200;
//            }
            
            if (troopStrength == null || troopStrength == 0)
                continue;
            
            
            if (armyElement.getArmyElementType() != ArmyElementType.WarMachimes) {
                tacticMod = !againstPopCenter ? InfoUtils.getTroopTacticModifier(armyElement.getArmyElementType(), army.getTactic()) : 100;
                terrainMod = InfoUtils.getTroopTerrainModifier(armyElement.getArmyElementType(), tr);
            }
            
            s += "\n\tTroops: " + armyElement.getNumber() + " " + armyElement.getArmyElementType().toString();
            s += "\n\t\tTroop Strength: " + troopStrength;
            if(!againstPopCenter) s += "\n\t\tTroop Tactic Modifier: " + tacticMod;
            else s += "\n\t(Against PC, no Troop Tactic Mod., default to 100)";
            s += "\n\t\tTroop Terrain Modifier: " + terrainMod;
            
            troopModifiers = armyElement.getTraining() + armyElement.getWeapons() + tacticMod + terrainMod;
            double mod = (double) (armyModifiers + troopModifiers) / 800d;

            s += "\n\t\tTroop Total Modifier (Weapons + Training + Tac. + Terr.): " + troopModifiers;

            strength += troopStrength * armyElement.getNumber() * mod;
            
            s += "\n\t\tTroop Total Strength (Str. * Num. * Mod.): " + troopStrength * armyElement.getNumber() * mod + "\n";
        }

        if (lossesOverride == null) {
            lossesOverride = army.getLosses();
        }

        strength = (int)(strength * (double)(100 - lossesOverride) / 100d);
        s += "\n\tTotal Strength: " + strength;
        this.logger.appendCurrentLog(s);
        
        return strength;
    	
    }
    
    public static int computNativeArmyConstitution(CombatArmy ca) {
        return computNativeArmyConstitution(ca, null);
    }

    /**
     * 
     * @param combatArmy
     * @param lossesOverride - This reduces the constitution by a percentage. If set to 25 then the constitution is reduced by 25% to 75% of what it would otherwise be. 
     * @return
     */
    public static int computNativeArmyConstitution(CombatArmy combatArmy, Double lossesOverride) {
        int constitution = 0;
        for (ArmyElement combatArmyElement : combatArmy.getElements()) {
            Integer unitConstitution = InfoUtils.getTroopStrength(combatArmyElement.getArmyElementType(), "Defense");
            if (unitConstitution == null)
                continue;
            constitution += unitConstitution * combatArmyElement.getNumber() * (double) (100 + combatArmyElement.getArmor()) / 100d;
        }
        if (lossesOverride == null) {
            lossesOverride = combatArmy.getLosses();
        }
        constitution = (int)(constitution * (double)(100 - lossesOverride) / 100d);
        return constitution;
    }

    public static int computeModifiedArmyStrength(HexTerrainEnum terrain, ClimateEnum climate, CombatArmy att,
            CombatArmy def) {
        int s = computeNativeArmyStrength(att, terrain, climate, false);

        int tacticVsTacticMod = InfoUtils.getTacticVsTacticModifier(att.getTactic(), def.getTactic());
        s = (int) (s * (double) tacticVsTacticMod / 100d);

        return s;
    }
    
    /*
     * Non-static version, smoothest way I could make the log system work...
     */
    public int computeModifiedArmyStrength(CombatArmy att, CombatArmy def) {
      int s = this.computeNativeArmyStrength(att, null, false);
      
      int tacticVsTacticMod = InfoUtils.getTacticVsTacticModifier(att.getTactic(), def.getTactic());
      s = (int) (s * (double) tacticVsTacticMod / 100d);
      
      this.logger.appendCurrentLog("	" + att.getTactic().toString() + " vs " + def.getTactic().toString() + " Mod.: " + tacticVsTacticMod);
      this.logger.appendCurrentLog("	Strength after tactics: " + s );
      
      return s;
    }

    public static double computeLosses(CombatArmy ca, int enemyStrength) {
        int constit = computNativeArmyConstitution(ca, 0d);
        return Math.min((double)enemyStrength * 100 / (double)Math.max(constit,1), 100);
    }
    
    protected void addToLog(String msg) {
        this.log += msg + "\n";
    }
    
    public void setPlayerLogUp() {
    	this.logger.reset();
    }
    
    public void addToPlayerLog(String msg) {
    	this.addToPlayerLog(msg, false);
    }
    
    public void addToPlayerLog(String msg, boolean PC) {
    	if(PC) {
    		this.logger.appendPCLog(msg);
    		return;
    	}
    	if(this.rounds != this.logger.round) this.logger.newRound();
    	
    	this.logger.appendCurrentLog(msg);
    }
    
    public void runPcBattle(int attackerSide, int round) {		//I believe attackerSide is always 0, could remove redundant code
//        int defenderSide = (attackerSide == 0 ? 1 : 0);
    	
        // compute str for attacker
        int warMachines = 0;
        int attackerStr = 0;
        int totalCon = 0;
        CombatPopCenter pc = (attackerSide == 0 ? this.side2Pc : this.side1Pc);
        if (pc == null) {	//this should never happen as function only is ever run when there is a PC
        	this.addToPlayerLog("Error, no PC");
        	return;
        }
        double[] losses = new double[MAX_ARMIES];
        
        if (pc != null) this.addToPlayerLog("Combat PopCenter " + pc.getName() + ", Nation " + pc.getNationNo(), true);

        for (int i=0; i<MAX_ARMIES; i++) {
            if (attackerSide == 0) {
                if (this.side1[i] == null) continue;
                this.addToPlayerLog("Combat Army, " + this.side1[i].getCommander() + " N" + this.side1[i].getNationNo() + ": ", true);
                int str = this.computeNativeArmyStrength(this.side1[i], null, true);
                
                // adjust for relations
                int relMod = CombatModifiers.getRelationModifier(this.side1Relations[i][MAX_ALL-1]);
                str = (int)(str * (double)relMod / 100d);
                attackerStr += str;
                
                this.addToPlayerLog("\tRelation Modifier,  " + relMod + ", new Str.: " + str, true);

                ArmyElement wmEl = this.side1[i].getWM(); 
                int wm = 0;
                if (wmEl != null)  wm = wmEl.getNumber(); 
                warMachines += wm;
                this.addToPlayerLog("\tWarmachines: " + wm + " (" + warMachines + " total so far)", true);
                totalCon += computNativeArmyConstitution(this.side1[i]);
                losses[i] = this.side1[i].getLosses();
                //if (round == 0) {
                	attackerStr += this.side1[i].getOffensiveAddOns();
                	this.addToPlayerLog("\tOffense Add Ons: " + this.side1[i].getOffensiveAddOns(), true);
                //}
            } else {
                if (this.side2[i] == null) continue;
                this.addToPlayerLog("Combat Army, " + this.side2[i].getCommander() + " N" + this.side1[i].getNationNo() + ": ", true);
                int str = this.computeNativeArmyStrength(this.side2[i], null, true);
                // adjust for relations
                int relMod = CombatModifiers.getRelationModifier(this.side2Relations[i][MAX_ALL-1]);
                str = (int)(str * (double)relMod / 100d);
                attackerStr += str;
                
                this.addToPlayerLog("\tRelation Modifier,  " + relMod + ", new Str.: " + str, true);
                
                ArmyElement wmEl = this.side2[i].getWM(); 
                int wm = 0;
                if (wmEl != null)  wm = wmEl.getNumber(); 
                warMachines += wm;
                this.addToPlayerLog("\tWarmachines: " + wm + " (" + warMachines + " total so far)", true);
                totalCon += computNativeArmyConstitution(this.side2[i]);
                losses[i] = this.side2[i].getLosses();
                //if (round == 0) {
                	attackerStr += this.side2[i].getOffensiveAddOns();
                	this.addToPlayerLog("\tOffense Add Ons: " + this.side1[i].getOffensiveAddOns(), true);
                //}
            }
        }
        this.addToPlayerLog("\nTotal Con: " + totalCon, true);
        this.addToPlayerLog("", true);
        
        // compute pop center defense and attack
        int popCenterStr = computePopCenterStrength(pc, warMachines);
        
        pc.setCaptured(popCenterStr <= attackerStr);
        
        this.addToPlayerLog("PC Captured: " + pc.getCapturedStr(), true);
        
        pc.setStrengthOfAttackingArmies(attackerStr);
        this.addToPlayerLog("\nNew Losses: ", true);
        for (int i=0; i<MAX_ARMIES; i++) {
            if (attackerSide == 0) {
                if (this.side1[i] == null) continue;
                this.addToPlayerLog("Army " + this.side1[i].getCommander(), true);
                double l = computeNewLossesFromPopCenter(this.side1[i], pc, this.popCenterRelations[i], totalCon, warMachines, round, popCenterStr);
                this.side1[i].setLosses(Math.min(this.side1[i].getLosses() + l, 100));
            } else {
                if (this.side2[i] == null) continue;
                this.addToPlayerLog("Army " + this.side2[i].getCommander(), true);
                double l = computeNewLossesFromPopCenter(this.side2[i], pc, this.side1Relations[MAX_ALL - 1][i], totalCon, warMachines, round, popCenterStr);
                this.side2[i].setLosses(Math.min(this.side2[i].getLosses() + l, 100));
            }
        }
    }

    public void runArmyBattle() {
        this.rounds = 0;
        boolean finished = false;
        this.log = "";
        addToLog("Side 1:");
        for (int i = 0; i < this.side1.length && this.side1[i] != null; i++){
	     	addToLog(this.side1[i].getCommander());
	    }
        addToLog("Side 2:");
        for (int i = 0; i < this.side2.length && this.side2[i] != null; i++){
        	addToLog(this.side2[i].getCommander());
        }
        addToLog("Side Other:");
        for (int i = 0; i < this.otherSide.length && this.otherSide[i] != null; i++){
        	addToLog(this.otherSide[i].getCommander());
        }
        
        do {
            addToLog("Starting round " + this.rounds);
            this.addToPlayerLog("Starting round " + this.rounds);
            double[] side1Losses = new double[MAX_ARMIES];
            double[] side2Losses = new double[MAX_ARMIES];

            // compute constitution for each side
            int side1Con = 0;
            int side2Con = 0;
            for (int i=0; i<MAX_ARMIES; i++) {
                CombatArmy army = this.side1[i];
                if (army == null) continue;
                if(this.rounds == 0) army.setStrOfAttackingArmy(0);
                int currentConstitution = computNativeArmyConstitution(army);
                int originalConstitution = computNativeArmyConstitution(army, 0d);
                this.addToPlayerLog("Side 1, army " + i + " (" + army.getCommander() + ")");
                this.addToPlayerLog("\tCon: " + currentConstitution + "/" + originalConstitution);
                this.addToPlayerLog("\tLosses: " + army.getLosses());
                addToLog("Side 1, army " + i + " (" + army.getCommander() + ")" + " con: " + currentConstitution + "/" + originalConstitution);
                side1Con += currentConstitution;
                side1Losses[i] = army.getLosses();
            }
            addToLog("Total Side 1 con: " + side1Con);
            this.addToPlayerLog("Total Side 1 con: " + side1Con);
            addToLog("");
            this.addToPlayerLog("");
            for (int i=0; i<MAX_ARMIES; i++) {
                CombatArmy army = this.side2[i];
                if (army == null) continue;
                if(this.rounds == 0) army.setStrOfAttackingArmy(0);
                int constit = computNativeArmyConstitution(army);
                int sconstit = computNativeArmyConstitution(army, 0d);
                addToLog("Side 2, army " + i + " con: " + constit + "/" + sconstit);
                this.addToPlayerLog("Side 2, army " + i + " (" + army.getCommander() + ")");
                this.addToPlayerLog("\tCon: " + constit + "/" + sconstit);
                this.addToPlayerLog("\tLosses: " + army.getLosses());
                side2Con += constit;
                side2Losses[i] = army.getLosses();
            }
            addToLog("Total Side 2 con: " + side2Con);
            
            this.addToPlayerLog("\nTotal Side 2 con: " + side2Con);
            side1Con = Math.max(side1Con, 1);
            side2Con = Math.max(side2Con, 1);
            //if (side1Con == 1 || side2Con == 1) return;
            
            addToLog("");
            this.addToPlayerLog("\n");

            boolean side1Alive = false;
            boolean side2Alive = false;
            // compute losses for each army
            for (int i=0; i<MAX_ARMIES; i++) {
                CombatArmy ca1 = this.side1[i];
                if (ca1 == null) continue;
                
                for (int j=0; j<MAX_ARMIES; j++) {
                    CombatArmy ca2 = this.side2[j];
                    if (ca2 == null) continue;

                    // losses for ca1
                    addToLog("");
                    this.addToPlayerLog("");
                    addToLog("Computing Losses for 2," + j + " attacking 1," + i);
                    this.addToPlayerLog("Computing Losses for side 2, army " + j + " (" + ca2.getCommander() + ") " + " attacking side 1, army " + i+ " (" + ca1.getCommander() + ")");
                    side1Losses[i] += computeNewLosses(this.terrain, this.climate, ca2, ca1, this.side2Relations[j][i], side1Con, this.rounds);
                    if (side1Losses[i] < 99.5) {
                        side1Alive = true;
                    }

                    // losses for ca2
                    addToLog("");
                    this.addToPlayerLog("");
                    addToLog("Computing Losses for 1," + i + " attacking 2," + j);
                    this.addToPlayerLog("Computing Losses for side 1, army " + j + " (" + ca1.getCommander() + ") " + " attacking side 2, army " + i+ " (" + ca2.getCommander() + ")");
                    side2Losses[j] += computeNewLosses(this.terrain, this.climate, ca1, ca2, this.side1Relations[i][j], side2Con, this.rounds);
                    if (side2Losses[j] < 99.5) {
                        side2Alive = true;
                    }
                }
            }
            
            // assign losses to armies
            for (int i=0; i<MAX_ARMIES; i++) {
                CombatArmy ca1 = this.side1[i];
                if (ca1 == null) continue;
                ca1.setLosses(Math.min(side1Losses[i], 100));
                addToLog("Side 1 army " + i + " new con : " + computNativeArmyConstitution(ca1));
                //this.addToPlayerLog("Side 1 army " + i + " new con : " + computNativeArmyConstitution(ca1));
            }
            for (int i=0; i<MAX_ARMIES; i++) {
                CombatArmy ca2 = this.side2[i];
                if (ca2 == null) continue;
                ca2.setLosses(Math.min(side2Losses[i], 100));
                //this.addToPlayerLog("Side 2 army " + i + " new con : " + computNativeArmyConstitution(ca2));
                addToLog("Side 2 army " + i + " new con : " + computNativeArmyConstitution(ca2));
            }        
            
            this.rounds++;
            finished = !(side1Alive && side2Alive) || this.rounds >= this.maxRounds;
            addToLog("");
            addToLog("");
        } while (!finished);
    }
    
    public void runWholeCombat() {
    	runArmyBattle();
    	if (getAttackPopCenter() && getSide2Pc() != null) runPcBattle(0, 1);
    	//System.out.println(this.log);
    }
    
    public int computePopCenterStrength(CombatPopCenter pc) {
        return computePopCenterStrength(pc, 0);
    }
    
    public int computePopCenterStrength(CombatPopCenter pc, int numberOfWarMachines) {
        int popDef = new int[]{0, 200, 500, 1000, 2500, 5000}[pc.getSize().getCode()];
        int fortDef = new int[]{0, 2000, 6000, 10000, 16000, 24000}[pc.getFort().getSize()];
        int wmStr = numberOfWarMachines * 200;

        this.addToPlayerLog("Warmachines strength: " + wmStr, true);
        this.addToPlayerLog("Fortification Defense: " + fortDef, true);
        this.addToPlayerLog("Population Center Defense: " + popDef, true);
        this.addToPlayerLog("PC Loyalty: " + pc.getLoyalty(), true);
        
        fortDef = Math.max(fortDef - wmStr, 0);
        this.addToPlayerLog("Fortification Def. after Warmachines: " + fortDef, true);

        this.addToPlayerLog("PC strength (PCDef + fortDef) * (100 + PCLoyalty)/100 = " + (int)Math.round(((double)popDef + (double)fortDef) * (100d + (double)pc.getLoyalty()) / 100d), true);
        
        return (int)Math.round(((double)popDef + (double)fortDef) * (100d + (double)pc.getLoyalty()) / 100d);
    }
    
    public double computeNewLossesFromPopCenter(CombatArmy army,
            CombatPopCenter pc,
            NationRelationsEnum relations,
            int armySideTotalCon,
            int armyTotalWMs,
            int round,
            int attStr) {
    	
    	this.log = "";
    	
        int relMod = CombatModifiers.getRelationModifier(relations);
        int defCon = computNativeArmyConstitution(army);
        this.addToPlayerLog("\tDefending army con: " + defCon, true);
        int defBonus = 0;
        
        // adjust by relations
        attStr = (int)((double)attStr * (double)relMod / 100d);
        this.addToPlayerLog("\tRelation Modifier, " + relMod + ", strength of PC after: " + attStr, true);
        
        // handle first round
        //if (round == 0) {
            defBonus = army.getDefensiveAddOns();
            this.addToPlayerLog("\tDefense Bonus: " + defBonus, true);
        //}
        
        double lossesFactor = (double)defCon / (double)armySideTotalCon;
        addToLog("Defender loss factor: " + lossesFactor);
        this.addToPlayerLog("\tDefender loss factor: " + lossesFactor, true);
        
        attStr = (int)(attStr * lossesFactor) - defBonus;
        if (attStr < 0) attStr = 0;
        this.addToPlayerLog("\t Final Attackins Strength to army ((attackStrength * lossesFactor) - defBonus): " + attStr, true);
        
        double losses = computeLosses(army, attStr);
        addToLog("New losses: " + losses);
        this.addToPlayerLog("\tNew Losees: " + losses, true);

        return losses;
    }
    
    public double computeNewLosses(HexTerrainEnum terrain1,
                                            ClimateEnum climate1,
                                            CombatArmy att,
                                            CombatArmy def,
                                            NationRelationsEnum relations,
                                            int defenderSideTotalCon,
                                            int round) {
        int relMod = CombatModifiers.getRelationModifier(relations);
        int defCon = computNativeArmyConstitution(def);
        int attStr = this.computeModifiedArmyStrength(att, def);
        addToLog("Relations mod: " + relMod);
        addToLog("Attacker modified str: " + attStr);
        this.addToPlayerLog("\tRelations mod: " + relMod);
        int attBonus = 0;
        int defBonus = 0;
        // adjust by relations
        attStr = (int)((double)attStr * (double)relMod / 100d);
        
        this.addToPlayerLog("\tAttaacker Str. w/Relations bonus: " + attStr);
        
        ArmyElement wmEl = att.getWM(); 
        int wm = 0;
        if (wmEl != null) {
        	wm = wmEl.getNumber();
        	this.addToPlayerLog("\t" + wm + " WM, new strength: " + wm * 50);
        }
        
        attStr += wm * 50;
        
        // handle first round
        //if (round == 0) {
            attBonus = att.getOffensiveAddOns();
            defBonus = def.getDefensiveAddOns();
            attStr += attBonus;
            addToLog("First round - str: " + attBonus + " con: " + defBonus);
            
            this.addToPlayerLog("\tFirst round - Offense bonus to str.: " + attBonus + ", Defense bonus to con.: " + defBonus);
        //}
        double lossesFactor = (double)defCon / (double)defenderSideTotalCon;
        addToLog("Defender loss factor: " + lossesFactor);
        
        attStr = (int)(attStr * lossesFactor) - defBonus;
        if (attStr < 0) attStr = 0;
        double losses = computeLosses(def, attStr);
        if(round == 0) def.addStrToAttackingArmy(attStr);
        addToLog("New losses: " + losses);
        
        return losses;
    }

    
    public ClimateEnum getClimate() {
        return this.climate;
    }

    
    public void setClimate(ClimateEnum climate) {
        this.climate = climate;
    }

    
    public CombatArmy[] getSide1() {
        return this.side1;
    }

    
    public void setSide1(CombatArmy[] side1) {
        this.side1 = side1;
    }

    
    public boolean[][] getSide1Attack() {
        return this.side1Attack;
    }

    
    public void setSide1Attack(boolean[][] side1Attack) {
        this.side1Attack = side1Attack;
    }

    
    public NationRelationsEnum[][] getSide1Relations() {
        return this.side1Relations;
    }

    
    public void setSide1Relations(NationRelationsEnum[][] side1Relations) {
        this.side1Relations = side1Relations;
    }
    
    public NationRelationsEnum[] getPCRelations() {
    	return this.popCenterRelations;
    }

    
    public CombatArmy[] getSide2() {
        return this.side2;
    }

    
    public void setSide2(CombatArmy[] side2) {
        this.side2 = side2;
    }

    
    public boolean[][] getSide2Attack() {
        return this.side2Attack;
    }

    
    public void setSide2Attack(boolean[][] side2Attack) {
        this.side2Attack = side2Attack;
    }

    
    public NationRelationsEnum[][] getSide2Relations() {
        return this.side2Relations;
    }

    
    public void setSide2Relations(NationRelationsEnum[][] side2Relations) {
        this.side2Relations = side2Relations;
    }

    public CombatArmy[] getOtherSide() {
    	return this.otherSide;
    }
    
    public HexTerrainEnum getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(HexTerrainEnum terrain) {
        this.terrain = terrain;
    }

    public boolean addToSide(int side, CombatArmy ca) {
        if (side == 0) {
            for (int i=0; i<this.side1.length; i++) {
                if (this.side1[i] == null) {
                    this.side1[i] = ca;
                    return true;
                }
            }
            return false;
        } else if (side == 1){
            for (int i=0; i<this.side2.length; i++) {
                if (this.side2[i] == null) {
                    this.side2[i] = ca;
                    return true;
                }
            }
            return false;
        } else {
            for (int i=0; i<this.otherSide.length; i++) {
                if (this.otherSide[i] == null) {
                    this.otherSide[i] = ca;
                    return true;
                }
            }
            return false;
        }
    }

    public boolean removeFromSide(int side, CombatArmy ca) {
        boolean found = false;
        if (side == 0) {
            for (int i=0; i<this.side1.length; i++) {
                if (this.side1[i] == ca) {
                    this.side1[i] = null;
                    found = true;
                }
                if (i > 0 && this.side1[i-1] == null && this.side1[i] != null) {
                    this.side1[i-1] = this.side1[i];
                    this.side1[i] = null;
                }
            }
            return found;
        } else if(side == 1){
            for (int i=0; i<this.side2.length; i++) {
                if (this.side2[i] == ca) {
                    this.side2[i] = null;
                    found = true;
                }
                if (i > 0 && this.side2[i-1] == null && this.side2[i] != null) {
                    this.side2[i-1] = this.side2[i];
                    this.side2[i] = null;
                }
            }
            return found;

        } else {
            for (int i=0; i<this.otherSide.length; i++) {
                if (this.otherSide[i] == ca) {
                    this.otherSide[i] = null;
                    found = true;
                }
                if (i > 0 && this.otherSide[i-1] == null && this.otherSide[i] != null) {
                    this.otherSide[i-1] = this.otherSide[i];
                    this.otherSide[i] = null;
                }
            }
            return found;

        }
    }
    
    public NationAllegianceEnum estimateAllegianceForSide(int side) {
        NationAllegianceEnum ret = null;
        Game g = GameHolder.instance().getGame();
        CombatArmy[] cas = (side == 0 ? this.side1 : this.side2);
        for (CombatArmy ca : cas) {
            if (ca == null) continue;
            if (ca.getNationNo() > 0) {
                NationRelations nr = g.getTurn().getNationRelations(ca.getNationNo());
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
        return this.maxRounds;
    }

    
    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    
    public String getDescription() {
        return this.description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    
    public int getHexNo() {
        return this.hexNo;
    }

    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    
    
    public int getArmyIndex(int side, CombatArmy a) {
        CombatArmy[] cas = (side == 0 ? this.side1 : this.side2);
        for (int i=0; i<cas.length; i++) {
            if (cas[i] == a) return i;
        }
        return -1;
    }
    
    @Override
	public int getX() {
    	return getHexNo() / 100;
    }
    
    @Override
	public int getY() {
    	return getHexNo() % 100;
    }

    
    public CombatPopCenter getSide1Pc() {
        return this.side1Pc;
    }

    
    public void setSide1Pc(CombatPopCenter side1Pc) {
        this.side1Pc = side1Pc;
    }

    
    public CombatPopCenter getSide2Pc() {
        return this.side2Pc;
    }

    
    public void setSide2Pc(CombatPopCenter side2Pc) {
        this.side2Pc = side2Pc;
    }

    public void loadTerrainAndClimateFromHex() {
    	HexInfo hi = GameHolder.instance().getGame().getTurn().getHexInfo(getHexNo());
    	if (hi != null) {
    		setClimate(hi.getClimate());
    	}
    	Hex hex = GameHolder.instance().getGame().getMetadata().getHex(getHexNo());
    	if (hex != null) {
    		setTerrain(hex.getTerrain());
    	}
    }
    
    public void setArmiesAndPCFromHex() {
		String strHex = String.valueOf(this.hexNo);
		if (strHex.length() == 3) strHex = "0" + strHex;
		//ArrayList<Army> allarmies = game.getTurn().getContainer(TurnElementsEnum.Army).findAllByProperties(new String[] { "hexNo" }, new Object[] { strHex });
		ArrayList<Army> fparmies = GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Army).findAllByProperties(new String[] { "hexNo", "nationAllegiance" }, new Object[] { strHex, NationAllegianceEnum.FreePeople });
		ArrayList<Army> dsarmies = GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Army).findAllByProperties(new String[] { "hexNo", "nationAllegiance" }, new Object[] { strHex, NationAllegianceEnum.DarkServants });
		ArrayList<Army> ntarmies = GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Army).findAllByProperties(new String[] { "hexNo", "nationAllegiance" }, new Object[] { strHex, NationAllegianceEnum.Neutral });

//		if (ntarmies.size() > 0) {
//			ErrorDialog.showErrorDialog("createCombatForHexCommand.error.NetrualArmiesFound");
//		}

		PopulationCenter pc = (PopulationCenter) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", this.hexNo);
//		if (pc != null && (pc.getNationNo() == 0 || pc.getNation().getAllegiance().equals(NationAllegianceEnum.Neutral))) {
//			ErrorDialog.showErrorDialog("createCombatForHexCommand.error.PopWithUnknownOrNeutralNationFound");
//		}

		if (pc != null) {
			CombatPopCenter cpc = new CombatPopCenter(pc);
			this.setSide2Pc(cpc);
		}
		
		ArrayList<Army> aside1;
		ArrayList<Army> aside2;
		ArrayList<Army> other = ntarmies;

		if(pc != null) {
			if(pc.getNation().getAllegiance().equals(NationAllegianceEnum.FreePeople)) {
				aside2 = fparmies;
				aside1 = dsarmies;
			} else if(pc.getNation().getAllegiance().equals(NationAllegianceEnum.Neutral) && fparmies.size() == 0) {
				aside2 = fparmies;
				aside1 = dsarmies;
			} else {
				aside1 = fparmies;
				aside2 = dsarmies;
			}
		} else {
			aside1 = fparmies;
			aside2 = dsarmies;
		}
		
		for (int i=0; i<MAX_ALL; i++) {
	        this.side1[i] = null;
	        this.side2[i] = null;
	        this.otherSide[i] = null;
		}

		for (int i = 0; i < 3; i++) {
			ArrayList<Army> sideArmies;
			if (i != 2) sideArmies = i == 0 ? aside1 : aside2;
			else sideArmies = other;
			
			for (Army a : sideArmies) {
				CombatArmy ca;
				if (a.computeNumberOfMen() > 0) {
					ca = new CombatArmy(a);
				} else {
					ArmyEstimate ae = (ArmyEstimate) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.ArmyEstimate).findFirstByProperty("commanderName", a.getCommanderName());
					if (ae != null) {
						ca = new CombatArmy(ae);

					} else {
						ca = new CombatArmy(a);

					}
				}
				this.addToSide(i, ca);
				ca.setBestTactic();
				this.fortDefBonus(ca);
			}
		}
    }
    
    public void fortDefBonus(CombatArmy ca) {
    	if(this.getSide2Pc() == null) return;
    	if(this.getSide2Pc().getNationNo() == ca.getNationNo()) {
    		ca.setDefensiveAddOns(ca.getDefensiveAddOns() + this.getSide2Pc().getFort().getSize() * 200);
    	}
    }
    
    public void autoSetRelationsToHated() {
    	
    	for (int i=0; i<MAX_ALL; i++) {
    		for (int j=0; j<MAX_ALL; j++) {
    			this.side1Relations[i][j] = NationRelationsEnum.Hated;
    			this.side2Relations[i][j] = NationRelationsEnum.Hated;
    		}
    		this.popCenterRelations[i] = NationRelationsEnum.Hated;
    	}
    }

    public void autoDetectCombatArmyRelations(int side, int caInd, boolean pC) {
    	NationRelations nR = null;
    	Game g = GameHolder.instance().getGame();
    	if (pC) nR = g.getTurn().getNationRelations(this.getSide2Pc().getNationNo());
    	else if (side == 0) nR = g.getTurn().getNationRelations(this.side1[caInd].getNationNo());
    	else if(side == 1) nR = g.getTurn().getNationRelations(this.side2[caInd].getNationNo());
    	NationAllegianceEnum nA = g.getMetadata().getNationByNum(g.getMetadata().getNationNo()).getAllegiance();
    	
    	if(pC && nR != null) {
	    	for (int i = 0; i < MAX_ARMIES&& this.side1[i] != null; i++) {
	    		if(this.side1[i].getNationNo() > 25) continue;
	    		if(this.getSide2Pc().getNation().getAllegiance() != nA) this.popCenterRelations[i] = NationRelationsEnum.Hated;
	    		else this.popCenterRelations[i] = nR.getRelationsFor(this.side1[i].getNationNo());
	    	}
    	} else if (side == 0 && nR != null) {
	    	for (int i = 0; i < MAX_ARMIES && this.side2[i] != null; i++) {
	    		if(this.side2[i].getNationNo() > 25) continue;
	    		if(this.side1[caInd].getNation().getAllegiance() != nA) this.side1Relations[caInd][i] = NationRelationsEnum.Hated;
	    		else this.side1Relations[caInd][i] = nR.getRelationsFor(this.side2[i].getNationNo());
	    	}
	    	
	    	if (this.side1[caInd].getNation().getAllegiance() != nA) this.side1Relations[caInd][10] = NationRelationsEnum.Hated;
	    	else if (this.side2Pc != null && this.side2Pc.getNationNo() < 26) this.side1Relations[caInd][10] = nR.getRelationsFor(this.side2Pc.getNationNo());
    	} else if (side == 1 && nR != null){
	    	for (int i = 0; i < MAX_ARMIES && this.side1[i] != null; i++) {
	    		if(this.side1[i].getNationNo() > 25) continue;
	    		if(this.side2[caInd].getNation().getAllegiance() != nA) this.side2Relations[caInd][i] = NationRelationsEnum.Hated;
	    		else this.side2Relations[caInd][i] = nR.getRelationsFor(this.side1[i].getNationNo());
	    	}
	    	
	    	if (this.side2[caInd].getNation().getAllegiance() != nA) this.side2Relations[caInd][10] = NationRelationsEnum.Hated;
	    	else if (this.side1Pc != null && this.side1Pc.getNationNo() < 26) this.side2Relations[caInd][10] = nR.getRelationsFor(this.side1Pc.getNationNo());
    	} 
    }
    
    public void autoSetCombatRelations() {
    	//Side1 relations
    	for (int i = 0; i < MAX_ARMIES && this.side1[i] != null; i++) {
    		this.autoDetectCombatArmyRelations(0, i, false);
    	}
    	
    	//Side2 relations
   		for (int i = 0; i < MAX_ARMIES && this.side2[i] != null; i++) {
    		this.autoDetectCombatArmyRelations(1, i, false);
    	}
   		
   		//PopCenter Relations;
   		if(this.getSide2Pc() == null) return;
   		for (int i = 0; i < MAX_ARMIES && this.side1[i] != null; i++) {
   			this.autoDetectCombatArmyRelations(1, i, true);
   		}
    }

	public boolean getAttackPopCenter() {
		return this.attackPopCenter;
	}



	public void setAttackPopCenter(boolean attackPopCenter) {
		this.attackPopCenter = attackPopCenter;
	}
	
	public class combatLogClass implements Serializable{

		private static final long serialVersionUID = -7661130288950998690L;
		ArrayList<String> roundLogs;
		String pcLog;
		public boolean addMode;
		public int round;
		
		public combatLogClass() {
			this.roundLogs = new ArrayList<String>();
			this.pcLog = "";
			this.addMode = false;
			this.round = -1;
		}
		
		public ArrayList<String> getRoundLogs() {
			return this.roundLogs;
		}
		
		public String getPCLogs() {
			return this.pcLog;
		}
		
		public void reset() {
			this.roundLogs.clear();
			this.pcLog = "";
			this.round = -1;
			this.addMode = true;
			this.roundLogs.add("");
		}
		
		public void newRound() {
			if(!this.addMode) return;
			
			this.roundLogs.add("");
			this.round++;
		}
		
		public void appendCurrentLog(String msg) {
			if(this.round == -1) return;
			this.roundLogs.set(this.round, this.roundLogs.get(this.round) + msg + "\n");
		}
		
		public void appendPCLog(String msg) {
			this.pcLog += msg + "\n";
		}
		
		public boolean isPC() {
			if(this.pcLog == "") return false;
			return true;
		}
		
	}
    
}
