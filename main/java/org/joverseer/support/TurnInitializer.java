package org.joverseer.support;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.Note;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.springframework.richclient.application.Application;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility class that initializes a new turn. Based on the existence of not of a previous turn
 * the new turn is initialized accordingly (e.g. with pop centers from the previous turn
 * or with pop center metadata)
 * 
 * @author Marios Skounakis
 */
public class TurnInitializer {
    public void initializeTurnWith(Turn newTurn, Turn previousTurn) {
        newTurn.getContainers().put(TurnElementsEnum.PopulationCenter, new Container(new String[]{"hexNo", "nationNo"}));
        Container newPcs = newTurn.getContainer(TurnElementsEnum.PopulationCenter);

        newTurn.getContainers().put(TurnElementsEnum.NationRelation, new Container(new String[]{"nationNo"}));
        Container newRelations = newTurn.getContainer(TurnElementsEnum.NationRelation);

        Container newPlayerInfo = newTurn.getContainer(TurnElementsEnum.PlayerInfo);
        GameMetadata gm = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame().getMetadata();
        
        Container newNotes = newTurn.getContainer(TurnElementsEnum.Notes);

        if (previousTurn != null) {
            // copy pcs
            Container oldPcs = previousTurn.getContainer(TurnElementsEnum.PopulationCenter);
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)oldPcs.items) {
                PopulationCenter newPc = pc.clone();
                newPcs.addItem(newPc);
            }
            // copy relations
            Container oldRelations = previousTurn.getContainer(TurnElementsEnum.NationRelation);
            for (NationRelations nr : (ArrayList<NationRelations>)oldRelations.items) {
                Nation n = gm.getNationByNum(nr.getNationNo());
                NationRelations newNr = nr.clone();
                newNr.setAllegiance(n.getAllegiance());
                newNr.setEliminated(n.getEliminated());
                newRelations.addItem(newNr);
            }
        } else {
            // get pcs from metadata
            Container gmPCs = gm.getPopulationCenters();
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)gmPCs.items) {
                PopulationCenter newPc = pc.clone();
                newPcs.addItem(newPc);
            }

            // init relations from metadata
            for (int i=1; i<26; i++) {
                NationRelations nr = new NationRelations();
                nr.setNationNo(i);
                
                Nation n = gm.getNationByNum(i);
                nr.setAllegiance(n.getAllegiance());
                nr.setEliminated(n.getEliminated());
                if (gm.getGameType() == GameTypeEnum.game1650 || 
                        gm.getGameType() == GameTypeEnum.game2950) {
                    for (int j=1; j<26; j++) {
                        if (i <= 10 && j <= 10) {
                            nr.setRelationsFor(j, NationRelationsEnum.Tolerated);
                        } else if (i > 10 && i < 21 && j > 10 && j < 21) {
                            nr.setRelationsFor(j, NationRelationsEnum.Tolerated);
                        } else if (i > 20 || j > 20) {
                            nr.setRelationsFor(j, NationRelationsEnum.Neutral);
                        } else {
                            nr.setRelationsFor(j, NationRelationsEnum.Disliked);
                        }
                    }
                } else if (gm.getGameType() == GameTypeEnum.gameBOFA) {
                    for (int j=1; j<26; j++) {
                        if (i == 10 || i == 11) {
                            if (j == 10 || j == 11) {
                                nr.setRelationsFor(j, NationRelationsEnum.Friendly);
                            } else if (j >= 12 && j <= 14) {
                                nr.setRelationsFor(j, NationRelationsEnum.Hated);
                            } else {
                                nr.setRelationsFor(j, NationRelationsEnum.Neutral);
                            }
                        } else if (i >= 12 && i <= 14) {
                            if (j == 10 || j == 11) {
                                nr.setRelationsFor(j, NationRelationsEnum.Hated);
                            } else if (j >= 12 && j <= 14) {
                                nr.setRelationsFor(j, NationRelationsEnum.Friendly);
                            } else {
                                nr.setRelationsFor(j, NationRelationsEnum.Neutral);
                            }
                        } else {
                            nr.setRelationsFor(j, NationRelationsEnum.Neutral);
                        }
                    }
                } else if (gm.getGameType() == GameTypeEnum.gameFA) {
                	// init relations according to allegiance
                    for (int j=1; j<26; j++) {
                    	Nation n1 = gm.getNationByNum(j);
                    	if (n.getAllegiance() == NationAllegianceEnum.Neutral ||
                    			n1.getAllegiance() == NationAllegianceEnum.Neutral) {
                    		nr.setRelationsFor(j, NationRelationsEnum.Neutral);
                    	} else if (n.getAllegiance() == n1.getAllegiance()) {
                    		nr.setRelationsFor(j, NationRelationsEnum.Tolerated);
                    	} else {
                    		nr.setRelationsFor(j, NationRelationsEnum.Disliked);
                    	}
                    }
                }
                newRelations.addItem(nr);
            }
        }
        newTurn.getContainers().put(TurnElementsEnum.Character, new Container(new String[]{"id", "name", "hexNo", "nationNo"}));
        
        Container armies = new Container(new String[]{"hexNo", "nationNo", "commanderName"});
        
        if (previousTurn == null) {
        	// get armies from metadata
            Container gmArmies = gm.getArmies();
            for (Army a : (ArrayList<Army>)gmArmies.items) {
                Army newArmy = a.clone();
                armies.addItem(newArmy);
            }

        }
        
        newTurn.getContainers().put(TurnElementsEnum.Army, armies);
        newTurn.getContainers().put(TurnElementsEnum.NationEconomy, new Container(new String[]{"nationNo"}));
        newTurn.getContainers().put(TurnElementsEnum.Artifact, new Container(new String[]{"number", "hexNo"}));
        Container hexInfo = new Container(new String[]{"hexNo"});
        for (Hex h : (Collection <Hex>)gm.getHexes()) {
            HexInfo hi = new HexInfo();
            hi.setVisible(false);
            hi.setX(h.getColumn());
            hi.setY(h.getRow());
            hexInfo.addItem(hi);
        }
        newTurn.getContainers().put(TurnElementsEnum.HexInfo, hexInfo);
        newTurn.getContainers().put(TurnElementsEnum.NationMessage, new Container(new String[]{"hexNo"}));
        newTurn.getContainers().put(TurnElementsEnum.Company, new Container(new String[]{"hexNo", "commander"}));
        newTurn.getContainers().put(TurnElementsEnum.Combat, new Container(new String[]{"hexNo"}));
        newTurn.getContainers().put(TurnElementsEnum.ArmyEstimate, new Container(new String[]{"hexNo"}));
    }
}
