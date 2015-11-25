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
        this.terrain = -1;
        this.directions = new Vector();
        this.features = new Vector();
        this.location = location;
    }

    static int convertDirection(String direction)
    {
        if(direction.equalsIgnoreCase("ne"))
        {
            return DIRECTION_NE;//1
        }
        if(direction.equalsIgnoreCase("e"))
        {
            return DIRECTION_E;
        }
        if(direction.equalsIgnoreCase("se"))
        {
            return DIRECTION_SE;
        }
        if(direction.equalsIgnoreCase("sw"))
        {
            return DIRECTION_SW;
        }
        if(direction.equalsIgnoreCase("w"))
        {
            return DIRECTION_W;
        }
        if(direction.equalsIgnoreCase("nw"))
        {
            return DIRECTION_NW;
        }
        return !direction.equalsIgnoreCase("H") ? -1 : DIRECTION_HOLD;
    }

    static String featureDescription(int feature)
    {
        if(feature < 1 || feature >= MAX_FEATURES)
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
        int size = this.features.size();
        for(int i = 0; i < size; i++)
        {
            Integer value = (Integer)this.features.get(i);
            if(value.intValue() != feature)
            {
                continue;
            }
            Integer dirValue = (Integer)this.directions.get(i);
            if(dirValue.intValue() == direction)
            {
                return true;
            }
        }

        return false;
    }

    boolean hasFeature(int feature)
    {
        int size = this.features.size();
        for(int i = 0; i < size; i++)
        {
            Integer value = (Integer)this.features.get(i);
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
        case DIRECTION_NE: // '\001'
            if(y % 2 == 0)
            {
                x++;
            }
            y--;
            break;

        case DIRECTION_E: // '\002'
            x++;
            break;

        case DIRECTION_SE: // '\003'
            if(y % 2 == 0)
            {
                x++;
            }
            y++;
            break;

        case DIRECTION_SW: // '\004'
            if(y % 2 == 1)
            {
                x--;
            }
            y++;
            break;

        case DIRECTION_W: // '\005'
            x--;
            break;

        case DIRECTION_NW: // '\006'
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
            int location1 = x * 100 + y;
            return location1;
        }
    }

    public void setTerrain(int terrain)
    {
        this.terrain = terrain;
    }

    public void addDirection(Integer direction)
    {
        this.directions.add(((Object) (direction)));
    }

    public void addFeature(Integer feature)
    {
        this.features.add(((Object) (feature)));
    }

    int getLocation()
    {
        return this.location;
    }

    int getTerrain()
    {
        return this.terrain;
    }

    boolean isHexComplete()
    {
        return this.location != -1 && this.terrain != -1 && this.directions.size() == this.features.size();
    }

}
