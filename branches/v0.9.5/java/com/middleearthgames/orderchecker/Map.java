// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Map.java

package com.middleearthgames.orderchecker;

import java.util.Vector;

// Referenced classes of package com.middleearthgames.orderchecker:
//            Hex, Main, Nation, PopCenter

public class Map
{

    static final int REMAINING_MOVEMENT = 999;
    private final int infMove[] = {
        0, 3, 3, 5, 6, 5, 12, 4, -1, -1
    };
    private final int infRoad[] = {
        0, 2, 2, 3, 3, 3, 6, 2, -1, -1
    };
    private final int cavMove[] = {
        0, 2, 2, 5, 5, 3, 12, 2, -1, -1
    };
    private final int cavRoad[] = {
        0, 1, 1, 2, 2, 1, 3, 1, -1, -1
    };
    private Vector hexes;

    public Map()
    {
        hexes = new Vector();
    }

    int calcArmyMovement(int startLoc, int direction, boolean cavalryOnly)
    {
        Hex startHex = findHex(startLoc);
        if(startHex == null)
        {
            return -1;
        }
        int endLoc = startHex.newHexLocation(direction);
        Hex endHex = findHex(endLoc);
        if(endHex == null)
        {
            return -1;
        }
        boolean road = startHex.hasFeature(5, direction);
        int base = cavalryOnly ? cavMove[endHex.getTerrain()] : infMove[endHex.getTerrain()];
        if(road)
        {
            base = cavalryOnly ? cavRoad[endHex.getTerrain()] : infRoad[endHex.getTerrain()];
        }
        if(base == -1)
        {
            return -1;
        }
        if(startHex.hasFeature(3, direction) && !startHex.hasFeature(2, direction))
        {
            return -1;
        }
        if(startHex.getTerrain() == 6 && endHex.getTerrain() == 6 && !road)
        {
            return -1;
        }
        if(startHex.hasFeature(2, direction) || startHex.hasFeature(1, direction))
        {
            base++;
        } else
        if(startHex.hasFeature(4, direction))
        {
            base += 2;
        }
        return base;
    }

    int calcNavyMovement(int startLoc, int direction)
    {
        Hex startHex = findHex(startLoc);
        if(startHex == null)
        {
            return -1;
        }
        int endLoc = startHex.newHexLocation(direction);
        Hex endHex = findHex(endLoc);
        if(endHex == null)
        {
            return -1;
        }
        int terrain = endHex.getTerrain();
        PopCenter pc = Main.main.getNation().findPopulationCenter(endLoc);
        boolean validDockHex = false;
        if(terrain == 2 || pc != null && pc.getDock() != 0)
        {
            validDockHex = true;
        }
        if(terrain != 9 && terrain != 8 && !validDockHex && !endHex.hasFeature(3))
        {
            return -2;
        }
        return !validDockHex || startHex.getTerrain() != 8 || endHex.hasFeature(3) ? 1 : 999;
    }

    public Hex findHex(int location)
    {
        int size = hexes.size();
        for(int i = 0; i < size; i++)
        {
            Hex hex = (Hex)hexes.get(i);
            if(hex.getLocation() == location)
            {
                return hex;
            }
        }

        return null;
    }

    int getAdjacentHex(int location, int direction)
    {
        Hex hex = findHex(location);
        if(hex != null)
        {
            return hex.newHexLocation(direction);
        } else
        {
            return -1;
        }
    }

    boolean adjacentToWater(Hex location)
    {
        if(location.hasFeature(3))
        {
            return true;
        }
        for(int i = 1; i <= 6; i++)
        {
            int adjacentLocation = location.newHexLocation(i);
            Hex adjacentHex = findHex(adjacentLocation);
            if(adjacentHex != null && (adjacentHex.getTerrain() == 9 || adjacentHex.getTerrain() == 8))
            {
                return true;
            }
        }

        return false;
    }

    public void addHex(Hex hex)
    {
        hexes.add(((Object) (hex)));
    }

    public boolean isMapComplete()
    {
        int size = hexes.size();
        for(int i = 0; i < size; i++)
        {
            Hex hex = (Hex)hexes.get(i);
            if(!hex.isHexComplete())
            {
                return false;
            }
        }

        return true;
    }
}
