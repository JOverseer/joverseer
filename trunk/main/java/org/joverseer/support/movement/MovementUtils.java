package org.joverseer.support.movement;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;

import java.util.HashMap;
import java.util.LinkedList;

public class MovementUtils {
    static int[] infMovementCost = 	new int[]{3, 3, 5, 6, 5, 12, 4, -1, -1};
    static int[] infRoadMovementCost =	new int[]{2, 2, 3, 3, 3,  6, 2, -1, -1};
    static int[] cavMovementCost =		new int[]{2, 2, 5, 5, 3, 12, 2, -1, -1};
    static int[] cavRoadMovementCost = new int[]{1, 1, 2, 2, 1,  3, 1, -1, -1};

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

    public static int calculateMovementCostForArmy(int startHexNo, String direction, boolean isCavalry, boolean isFed) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        GameMetadata gm = g.getMetadata();
        Hex start = gm.getHex(startHexNo);

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
        } else if (md == MovementDirection.Northwest) {
            side = HexSideEnum.TopLeft;
        } else if (md == MovementDirection.NorthEast) {
            side = HexSideEnum.TopRight;
        } 
        Hex dest = gm.getHex(getHexNoAtDir(startHexNo, md));
        if (dest == null) {
            // out of map
            return -1;
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
        } else if (dir == MovementDirection.Northwest) {
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

    public static HashMap calculateArmyRangeHexes(int startHexNo, boolean isCavalry, boolean isFed) {
        HashMap<Integer, Integer> rangeHexes = new HashMap<Integer, Integer>();
        LinkedList<Integer> hexesToProcess = new LinkedList<Integer>();
        hexesToProcess.add(startHexNo);
        int prevCost = 0;
        while (hexesToProcess.size() > 0) {
            int hexNo = (Integer)hexesToProcess.remove(0);
            for (MovementDirection dir : MovementDirection.values()) {
                int cost = calculateMovementCostForArmy(hexNo, dir.getDir(), isCavalry, isFed);
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
