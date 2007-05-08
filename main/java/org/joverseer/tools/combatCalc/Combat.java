package org.joverseer.tools.combatCalc;

import java.util.ArrayList;
import java.util.HashMap;

import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.support.info.InfoUtils;

import sun.security.action.GetLongAction;

public class Combat {
    static int maxArmies = 10;
    static int maxAll = 11;
    
    HexTerrainEnum terrain;
    ClimateEnum climate;

    CombatArmy[] side1 = new CombatArmy[maxArmies];
    CombatArmy[] side2 = new CombatArmy[maxArmies];
    
    NationRelationsEnum[][] side1Relations = new NationRelationsEnum[maxArmies][maxAll];
    NationRelationsEnum[][] side2Relations = new NationRelationsEnum[maxArmies][maxAll];
     
    boolean[][] side1Attack = new boolean[maxArmies][maxAll];
    boolean[][] side2Attack = new boolean[maxArmies][maxAll];
    
    int rounds = 0;

    public Combat() {
        for (int i=0; i<maxArmies; i++) {
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
    
    public static int computeNativeArmyStrength(CombatArmy ca, HexTerrainEnum terrain, ClimateEnum climate) {
        int strength = 0;
        for (ArmyElement ae : ca.getElements()) {
            Integer s = InfoUtils.getTroopStrength(ae.getArmyElementType(), "Attack");
            if (s == null)
                continue;
            int tacticMod = InfoUtils.getTroopTacticModifier(ae.getArmyElementType(), ca.getTactic());
            int terrainMod = InfoUtils.getTroopTerrainModifier(ae.getArmyElementType(), terrain);
            double mod = (double) (ae.getTraining() + ae.getWeapons() + tacticMod + terrainMod) / 400d;
            strength += s * ae.getNumber() * mod;
        }
        //System.out.println("Str before mods: " + strength);
        strength = strength
                * (ca.getCommandRank() + ca.getMorale() + CombatModifiers.getModifierFor(
                        ca.getNationNo(), terrain, climate) * 2) / 400;
        strength = (int)(strength * (double)(100 - ca.getLosses()) / 100d);
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
        int s = computeNativeArmyStrength(att, terrain, climate);
        int tacticVsTacticMod = InfoUtils.getTacticVsTacticModifier(att.getTactic(), def.getTactic());
        s = (int) (s * (double) tacticVsTacticMod / 100d);
        return s;
    }

    public static double computeLosses(CombatArmy ca, int enemyStrength) {
        int constit = computNativeArmyConstitution(ca, 0d);
        return Math.min((double)enemyStrength * 100 / (double)Math.max(constit,1), 100);
    }

    public void runBattle() {
        rounds = 0;
        boolean finished = false;
        do {
            System.out.println("Starting round " + rounds);
            double[] side1Losses = new double[maxArmies];
            double[] side2Losses = new double[maxArmies];

            // compute constitution for each side
            int side1Con = 0;
            int side2Con = 0;
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca = side1[i];
                if (ca == null) continue;
                side1Con += computNativeArmyConstitution(ca);
            }
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca = side2[i];
                if (ca == null) continue;
                side2Con += computNativeArmyConstitution(ca);
            }
            side1Con = Math.max(side1Con, 1);
            side2Con = Math.max(side2Con, 1);
            if (side1Con == 1 || side2Con == 1) return;
            System.out.println("Total side1 con: " + side1Con);
            System.out.println("Total side2 con: " + side2Con);
            
            boolean side1Alive = false;
            boolean side2Alive = false;
            // compute losses for each army
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca1 = side1[i];
                if (ca1 == null) continue;
                
                for (int j=0; j<maxArmies; j++) {
                    CombatArmy ca2 = side2[i];
                    if (ca2 == null) continue;
                    
                    // losses for ca1
                    side1Losses[i] = computeNewLosses(terrain, climate, ca2, ca1, side2Relations[j][i], side1Con, rounds);
                    if (side1Losses[i] < 99.5) {
                        side1Alive = true;
                    }
                    
                    // losses for ca2
                    side2Losses[j] = computeNewLosses(terrain, climate, ca1, ca2, side1Relations[i][j], side2Con, rounds);
                    if (side2Losses[j] < 99.5) {
                        side2Alive = true;
                    }
                }
            }
            
            // assign losses to armies
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca1 = side1[i];
                if (ca1 == null) continue;
                ca1.setLosses(side1Losses[i]);
                System.out.println("Side1 army " + i + " con : " + computNativeArmyConstitution(ca1));
            }
            for (int i=0; i<maxArmies; i++) {
                CombatArmy ca2 = side2[i];
                if (ca2 == null) continue;
                ca2.setLosses(side2Losses[i]);
                System.out.println("Side2 army " + i + " con : " + computNativeArmyConstitution(ca2));
            }        
            
            
            finished = !(side1Alive && side2Alive) || rounds > 20;
            rounds++;
        } while (!finished);
    }
    
    public static double computeNewLosses(HexTerrainEnum terrain,
                                            ClimateEnum climate,
                                            CombatArmy att,
                                            CombatArmy def,
                                            NationRelationsEnum relations,
                                            int defenderSideTotalCon,
                                            int round) {
        double losses1 = def.getLosses();
        int relMod = CombatModifiers.getRelationModifier(relations);
        int defCon = computNativeArmyConstitution(def);
        int attStr = computeModifiedArmyStrength(terrain, climate, att, def);
        int attBonus = 0;
        int defBonus = 0;
        if (round == 0) {
            attBonus = att.getOffensiveAddOns();
            defBonus = def.getDefensiveAddOns();
            attStr += attBonus - defBonus;
        }
        double l = computeLosses(def, attStr *
                    relMod / 100 * 
                    defCon / defenderSideTotalCon);
        return Math.min(losses1 + l, 100);
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
    
    
}
