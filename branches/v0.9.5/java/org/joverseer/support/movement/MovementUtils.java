package org.joverseer.support.movement;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.springframework.richclient.application.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MovementUtils {
    static int[] infMovementCost = 	new int[]{3, 3, 5, 6, 5, 12, 4, -1, -1};
    static int[] infRoadMovementCost =	new int[]{2, 2, 3, 3, 3,  6, 2, -1, -1};
    static int[] cavMovementCost =      new int[]{2, 2, 5, 5, 3, 12, 2, -1, -1};
    static int[] cavRoadMovementCost =  new int[]{1, 1, 2, 2, 1,  3, 1, -1, -1};

    static int bridgeFordCost = 1;
    static int riverCost = 1;
    static int majorRiverCost = -1;

    public static int distance(int hexA, int hexB) {
        Integer x1 = hexA / 100;
        Integer y1 = hexA % 100;
        Integer x2 = hexB / 100;
        Integer y2 = hexB % 100;

        int a1 = x1 - (new Double(Math.ceil(y1.doubleValue() / 2))).intValue();
        int a2 = x1 + (new Double(Math.floor(y1.doubleValue() / 2))).intValue();

        int b1 = x2 - (new Double(Math.ceil(y2.doubleValue() / 2))).intValue();
        int b2 = x2 + (new Double(Math.floor(y2.doubleValue() / 2))).intValue();

        int a = a1 - b1;
        int b = a2 - b2;
        int d;
        if (a * b > 0) {
            d = Math.max(Math.abs(a), Math.abs(b));
        } else {
            d = Math.abs(a) + Math.abs(b);
        }

        return d;
    }
    
    public static boolean movementAlongMajorRiver(Hex startHex, MovementDirection md) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        GameMetadata gm = g.getMetadata();
        Hex dest = gm.getHex(getHexNoAtDir(startHex.getHexNo(), md));
        
        ArrayList<HexSideEnum> connectingSides = new ArrayList<HexSideEnum>();
        if (md == MovementDirection.East) {
            connectingSides.add(HexSideEnum.TopLeft);
            connectingSides.add(HexSideEnum.Left);
            connectingSides.add(HexSideEnum.BottomLeft);
        } else if (md == MovementDirection.NorthEast) {
            connectingSides.add(HexSideEnum.Left);
            connectingSides.add(HexSideEnum.BottomLeft);
            connectingSides.add(HexSideEnum.BottomRight);
        } else if (md == MovementDirection.NorthWest) {
            connectingSides.add(HexSideEnum.Right);
            connectingSides.add(HexSideEnum.BottomLeft);
            connectingSides.add(HexSideEnum.BottomRight);
        } else if (md == MovementDirection.West) {
            connectingSides.add(HexSideEnum.Left);
            connectingSides.add(HexSideEnum.TopRight);
            connectingSides.add(HexSideEnum.BottomRight);
        } else if (md == MovementDirection.SouthWest) {
            connectingSides.add(HexSideEnum.Right);
            connectingSides.add(HexSideEnum.TopLeft);
            connectingSides.add(HexSideEnum.TopRight);
        } else if (md == MovementDirection.SouthEast) {
            connectingSides.add(HexSideEnum.Left);
            connectingSides.add(HexSideEnum.TopLeft);
            connectingSides.add(HexSideEnum.TopRight);
        }
        for (HexSideEnum hse : connectingSides) {
            if (dest.getHexSideElements(hse).contains(HexSideElementEnum.MajorRiver)) return true;
        }
        return false;
    }
    
    public static int calculateMovementCostForNavy(int startHexNo, String direction, boolean isFed, int initialHex) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        GameMetadata gm = g.getMetadata();
        Hex start = gm.getHex(startHexNo);
        MovementDirection md = MovementDirection.getDirectionFromString(direction);

        int movementCost = isFed ? 1 : 2;
        
        Hex dest = gm.getHex(getHexNoAtDir(startHexNo, md));
        if (dest == null) {
            // out of map
            return -1;
        } else {
            // out of map
            MapMetadata mm = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
            if (dest.getColumn() < mm.getMinMapColumn()) return -1;
            if (dest.getColumn() > mm.getMaxMapColumn()) return -1;
            if (dest.getRow() < mm.getMinMapRow()) return -1;
            if (dest.getRow() > mm.getMaxMapRow()) return -1;
        }
        
//        if (start.getTerrain() == HexTerrainEnum.shore && initialHex != startHexNo) {
//        	return -1;
//        }
        
        // check start hex terrain
        if (start.getTerrain() != HexTerrainEnum.sea && start.getTerrain() != HexTerrainEnum.ocean && dest.getTerrain() != HexTerrainEnum.sea && dest.getTerrain() != HexTerrainEnum.ocean) {
            // check connecting river
            if (movementAlongMajorRiver(start, md)) return movementCost;
            return -1;
        }
        
        if (startHexNo != initialHex && start.getTerrain() == HexTerrainEnum.shore && dest.getTerrain() != HexTerrainEnum.shore) {
            return -1;
        }
        
        
        if (md == MovementDirection.Home) {
            // for home, return immediatelly
            return 1;
        }
        
        
        if (dest.getTerrain() != HexTerrainEnum.shore && dest.getTerrain() != HexTerrainEnum.sea && dest.getTerrain() != HexTerrainEnum.ocean) {
            // see if there is a pop center there
            PopulationCenter pc = (PopulationCenter)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", dest.getHexNo());
            if (pc == null) {
                return -1;
            }
            else {
                return movementCost;
            }
        } else {
            return movementCost;
        }
    }

    public static int calculateMovementCostForArmy(int startHexNo, String direction, boolean isCavalry, boolean isFed, boolean ignoreEnemyPops, NationAllegianceEnum allegiance, int initialHex) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        GameMetadata gm = g.getMetadata();
        Hex start = gm.getHex(startHexNo);

        if (!ignoreEnemyPops && startHexNo != initialHex) {
            PopulationCenter pc = (PopulationCenter)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", startHexNo);
            if (pc != null && pc.getFortification() != FortificationSizeEnum.none) {
                if (pc.getNationNo() == 0) {
                    return -1;
                }
                NationRelations nr = (NationRelations)g.getTurn().getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", pc.getNationNo());
                if (nr.getAllegiance() != allegiance) return -1;
            }
        }
        
        
        MovementDirection md = MovementDirection.getDirectionFromString(direction);

        if (md == MovementDirection.Home) {
            // for home, return immediatelly
            return 1;
        }

        // decide if road exists
        HexSideEnum side = null;
        if (md == MovementDirection.East) {
            side = HexSideEnum.Right;
        } else if (md == MovementDirection.SouthEast) {
            side = HexSideEnum.BottomRight;
        } else if (md == MovementDirection.SouthWest) {
            side = HexSideEnum.BottomLeft;
        } else if (md == MovementDirection.West) {
            side = HexSideEnum.Left;
        } else if (md == MovementDirection.NorthWest) {
            side = HexSideEnum.TopLeft;
        } else if (md == MovementDirection.NorthEast) {
            side = HexSideEnum.TopRight;
        } 
        Hex dest = gm.getHex(getHexNoAtDir(startHexNo, md));
        if (dest == null) {
            // out of map
            return -1;
        } else {
            // out of map
            MapMetadata mm = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
            if (dest.getColumn() < mm.getMinMapColumn()) return -1;
            if (dest.getColumn() > mm.getMaxMapColumn()) return -1;
            if (dest.getRow() < mm.getMinMapRow()) return -1;
            if (dest.getRow() > mm.getMaxMapRow()) return -1;
        }
        
        boolean roadExists = false;
        boolean bridgeOrFord = false;
        boolean minorRiver = false;
        boolean majorRiver = false;
        if (side != null) {
            roadExists = start.getHexSideElements(side).contains(HexSideElementEnum.Road);
            bridgeOrFord = start.getHexSideElements(side).contains(HexSideElementEnum.Bridge) ||
                    start.getHexSideElements(side).contains(HexSideElementEnum.Ford);
            minorRiver = start.getHexSideElements(side).contains(HexSideElementEnum.MinorRiver);
            majorRiver = start.getHexSideElements(side).contains(HexSideElementEnum.MajorRiver);
        }
        
        // check if both start and dest are mountains and there is no road
        if (start.getTerrain() == HexTerrainEnum.mountains && dest.getTerrain() == HexTerrainEnum.mountains && !roadExists) {
            return -1;
        }
        
        if (majorRiver && !bridgeOrFord) {
        	return -1;
        }
            
        // find appropriate cost matrix
        int[] movementCosts;
        if (!isCavalry) {
            if (roadExists) {
                movementCosts = infRoadMovementCost;
            } else {
                movementCosts = infMovementCost;
            }
        } else {
            if (roadExists) {
                movementCosts = cavRoadMovementCost;
            } else {
                movementCosts = cavMovementCost;
            }
        }
        int cost = movementCosts[dest.getTerrain().getTerrain() - 1];
        if (!isFed) {
            cost = cost + (new Double(Math.ceil(new Double(cost) / 3))).intValue();
        }
        if (majorRiver) {
            cost += 1;
        }
        if (minorRiver && !bridgeOrFord) {
            cost += 2;
        }
        if (bridgeOrFord) {
            cost += 1;
        }
        return cost;
    }

    public static int getHexNoAtDir(int startHexNo, MovementDirection dir) {
        int destHexNo = startHexNo;
        if (dir == MovementDirection.East) {
            destHexNo += 100;
        } else if (dir == MovementDirection.SouthEast) {
            destHexNo += 101;
            if ((startHexNo % 100) % 2 == 1 ) {
                destHexNo -= 100;
            }
        } else if (dir == MovementDirection.SouthWest) {
            destHexNo += 1;
            if ((startHexNo % 100) % 2 == 1 ) {
                destHexNo -= 100;
            }
        } else if (dir == MovementDirection.West) {
            destHexNo -= 100;
        } else if (dir == MovementDirection.NorthWest) {
            destHexNo -= 1;
            if ((startHexNo % 100) % 2 == 1 ) {
                destHexNo -= 100;
            }
        } else if (dir == MovementDirection.NorthEast) {
            destHexNo += 99;
            if ((startHexNo % 100) % 2 == 1 ) {
                destHexNo -= 100;
            }
        } else if (dir == MovementDirection.Home) {
            destHexNo = startHexNo;
        }
        return destHexNo;
    }

    public static HashMap calculateArmyRangeHexes(int startHexNo, boolean isCavalry, boolean isFed, boolean ignoreEnemyPops, NationAllegianceEnum allegiance) {
        HashMap<Integer, Integer> rangeHexes = new HashMap<Integer, Integer>();
        LinkedList<Integer> hexesToProcess = new LinkedList<Integer>();
        hexesToProcess.add(startHexNo);
        int prevCost = 0;
        while (hexesToProcess.size() > 0) {
            int hexNo = (Integer)hexesToProcess.remove(0);
            for (MovementDirection dir : MovementDirection.values()) {
                int cost = calculateMovementCostForArmy(hexNo, dir.getDir(), isCavalry, isFed, ignoreEnemyPops, allegiance, startHexNo);
                if (cost < 0) continue;
                if (hexNo == startHexNo) {
                    prevCost = 0;
                } else {
                    prevCost = (Integer)rangeHexes.get(hexNo);
                }
                int totalCost = cost + prevCost;
                if (totalCost > 0 && totalCost < 15) {
                    int destHexNo = getHexNoAtDir(hexNo, dir);
                    if (destHexNo == startHexNo) continue;
                    int currentCost = 100;
                    if (rangeHexes.containsKey(destHexNo)) {
                        currentCost = (Integer)rangeHexes.get(destHexNo);
                        if (currentCost < totalCost) {
                            continue;
                        }
                        rangeHexes.remove(destHexNo);
                    }
                    rangeHexes.put(destHexNo, totalCost);
                    if (!hexesToProcess.contains(destHexNo)) {
                        hexesToProcess.add(destHexNo);
                    }
                }
            }
        }
        return rangeHexes;
    }
}
