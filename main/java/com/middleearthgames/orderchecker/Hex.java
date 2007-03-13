// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Hex.java

package com.middleearthgames.orderchecker;

import java.util.Vector;

public class Hex
{

    static final int MIN_LOCATION = 101;
    static final int MAX_LOCATION = 4439;
    static final int TERRAIN_PLAINS = 1;
    static final int TERRAIN_SHORE = 2;
    static final int TERRAIN_FOREST = 3;
    static final int TERRAIN_SWAMP = 4;
    static final int TERRAIN_HILLS = 5;
    static final int TERRAIN_MOUNTAIN = 6;
    static final int TERRAIN_DESERT = 7;
    static final int TERRAIN_SHALLOW_WATER = 8;
    static final int TERRAIN_DEEP_WATER = 9;
    private static final int MAX_FEATURES = 6;
    static final int FEATURE_FORD = 1;
    static final int FEATURE_BRIDGE = 2;
    static final int FEATURE_DEEP_RIVER = 3;
    static final int FEATURE_SHALLOW_RIVER = 4;
    static final int FEATURE_ROAD = 5;
    static final String featureDesc[] = {
        "Ford", "Bridge", "Deep River", "Shallow River", "Road"
    };
    static final int DIRECTION_NONE = 0;
    private static final int DIRECTION_NE = 1;
    private static final int DIRECTION_E = 2;
    private static final int DIRECTION_SE = 3;
    private static final int DIRECTION_SW = 4;
    private static final int DIRECTION_W = 5;
    private static final int DIRECTION_NW = 6;
    private static final int DIRECTION_HOLD = 7;
    private int location;
    private int terrain;
    private Vector directions;
    private Vector features;

    public Hex(int location)
    {
        this.location = -1;
        terrain = -1;
        directions = new Vector();
        features = new Vector();
        this.location = location;
    }

    static int convertDirection(String direction)
    {
        if(direction.equalsIgnoreCase("ne"))
        {
            return 1;
        }
        if(direction.equalsIgnoreCase("e"))
        {
            return 2;
        }
        if(direction.equalsIgnoreCase("se"))
        {
            return 3;
        }
        if(direction.equalsIgnoreCase("sw"))
        {
            return 4;
        }
        if(direction.equalsIgnoreCase("w"))
        {
            return 5;
        }
        if(direction.equalsIgnoreCase("nw"))
        {
            return 6;
        }
        return !direction.equalsIgnoreCase("H") ? -1 : 7;
    }

    static String featureDescription(int feature)
    {
        if(feature < 1 || feature >= 6)
        {
            return null;
        } else
        {
            return featureDesc[feature - 1];
        }
    }

    static int calcHexDistance(int p1, int p2)
    {
        int x1 = p1 / 100;
        int y1 = p1 - x1 * 100;
        int x2 = p2 / 100;
        int y2 = p2 - x2 * 100;
        int x1h = x1 - (int)Math.ceil((double)y1 / 2D);
        int y1h = x1 + (int)Math.floor((double)y1 / 2D);
        int x2h = x2 - (int)Math.ceil((double)y2 / 2D);
        int y2h = x2 + (int)Math.floor((double)y2 / 2D);
        int xd = x2h - x1h;
        int yd = y2h - y1h;
        return (Math.abs(xd) + Math.abs(yd) + Math.abs(xd - yd)) / 2;
    }

    boolean hasFeature(int feature, int direction)
    {
        int size = features.size();
        for(int i = 0; i < size; i++)
        {
            Integer value = (Integer)features.get(i);
            if(value.intValue() != feature)
            {
                continue;
            }
            Integer dirValue = (Integer)directions.get(i);
            if(dirValue.intValue() == direction)
            {
                return true;
            }
        }

        return false;
    }

    boolean hasFeature(int feature)
    {
        int size = features.size();
        for(int i = 0; i < size; i++)
        {
            Integer value = (Integer)features.get(i);
            if(value.intValue() == feature)
            {
                return true;
            }
        }

        return false;
    }

    int newHexLocation(int direction)
    {
        int x = this.location / 100;
        int y = this.location - x * 100;
        switch(direction)
        {
        case 1: // '\001'
            if(y % 2 == 0)
            {
                x++;
            }
            y--;
            break;

        case 2: // '\002'
            x++;
            break;

        case 3: // '\003'
            if(y % 2 == 0)
            {
                x++;
            }
            y++;
            break;

        case 4: // '\004'
            if(y % 2 == 1)
            {
                x--;
            }
            y++;
            break;

        case 5: // '\005'
            x--;
            break;

        case 6: // '\006'
            if(y % 2 == 1)
            {
                x--;
            }
            y--;
            break;

        default:
            return -1;
        }
        if(x < 1 || x > 44 || y < 1 || y > 39)
        {
            return -1;
        } else
        {
            int location = x * 100 + y;
            return location;
        }
    }

    public void setTerrain(int terrain)
    {
        this.terrain = terrain;
    }

    public void addDirection(Integer direction)
    {
        directions.add(((Object) (direction)));
    }

    public void addFeature(Integer feature)
    {
        features.add(((Object) (feature)));
    }

    int getLocation()
    {
        return location;
    }

    int getTerrain()
    {
        return terrain;
    }

    boolean isHexComplete()
    {
        return location != -1 && terrain != -1 && directions.size() == features.size();
    }

}
