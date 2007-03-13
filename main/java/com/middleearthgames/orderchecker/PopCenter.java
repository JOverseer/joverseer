// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   PopCenter.java

package com.middleearthgames.orderchecker;


// Referenced classes of package com.middleearthgames.orderchecker:
//            Main, Nation

public class PopCenter
{

    private static int improveReq[] = {
        -1, 70, 90, 110, 130, -1
    };
    private static int threatenReq[] = {
        -1, 0, 200, 1000, 2500, 5000
    };
    private static int food[] = {
        -1, 0, 200, 1000, 2500, 5000
    };
    static final int SIZE_NONE = 0;
    static final int SIZE_CAMP = 1;
    static final int SIZE_VILLAGE = 2;
    static final int SIZE_TOWN = 3;
    static final int SIZE_MAJOR_TOWN = 4;
    static final int SIZE_CITY = 5;
    private static final String sizeName[] = {
        "", "Camp", "Village", "Town", "Major Town", "City"
    };
    static final int FORT_NONE = 0;
    static final int FORT_TOWER = 1;
    static final int FORT_FORT = 2;
    static final int FORT_CASTLE = 3;
    static final int FORT_KEEP = 4;
    static final int FORT_CITADEL = 5;
    private static final String fortName[] = {
        "", "Tower", "Fort", "Castle", "Keep", "Citadel"
    };
    static final int DOCK_NONE = 0;
    static final int DOCK_HARBOR = 1;
    static final int DOCK_PORT = 2;
    private int location;
    private String name;
    private int nation;
    private int fortification;
    private int size;
    private int dock;
    private boolean capital;
    private int loyalty;
    private boolean hidden;
    private boolean newpc;
    private int troopsAvailable;
    private int possibleDestruction;
    private int possibleCapture;
    private int possibleInfluence;
    private boolean enemyArmyPresent;
    private int capturingNation;

    public PopCenter(int location)
    {
        this.location = -1;
        name = null;
        nation = -1;
        fortification = -1;
        size = -1;
        dock = -1;
        capital = false;
        loyalty = -1;
        hidden = false;
        newpc = false;
        this.location = location;
    }

    static String getSizeName(int sizeValue)
    {
        if(sizeValue >= 0 && sizeValue <= 5)
        {
            return sizeName[sizeValue];
        } else
        {
            return "";
        }
    }

    boolean isNationsCapital(int nationNumber)
    {
        return capital && nationNumber == nation;
    }

    public void mergePopulationCenter(PopCenter pc)
    {
        if(nation <= 0 && pc.getNation() > 0)
        {
            name = pc.getName();
            nation = pc.getNation();
        }
        if(fortification <= 0 && pc.getFortification() > 0)
        {
            fortification = pc.getFortification();
        }
        if(size <= 0 && pc.getSize() > 0)
        {
            size = pc.getSize();
        }
        if(dock <= 0 && pc.getDock() > 0)
        {
            dock = pc.getDock();
        }
        if(!capital && pc.getCapital())
        {
            capital = true;
        }
        if(loyalty <= 0 && pc.getLoyalty() > 0)
        {
            loyalty = pc.getLoyalty();
        }
        if(!hidden && pc.getHidden())
        {
            hidden = true;
        }
    }

    void initStateInformation()
    {
        troopsAvailable = getMaximumTroops();
        possibleDestruction = 9999;
        possibleCapture = 9999;
        possibleInfluence = 9999;
        enemyArmyPresent = Main.main.getNation().isEnemyArmyPresent(nation, location);
        capturingNation = Main.main.getNation().capturingNation(this, 0);
    }

    int improvementRequirement()
    {
        return improveReq[size];
    }

    int threatenRequirement()
    {
        return threatenReq[size];
    }

    int getMaximumTroops()
    {
        return size * 100;
    }

    int reduceTroopLimit(int amount)
    {
        troopsAvailable -= amount;
        return troopsAvailable;
    }

    int getFoodProvided()
    {
        return food[size];
    }

    void setPossibleDestruction(int value)
    {
        possibleDestruction = value;
    }

    void setPossibleCapture(int value)
    {
        possibleCapture = value;
    }

    void setPossibleInfluence(int value)
    {
        possibleInfluence = value;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setNation(int nation)
    {
        this.nation = nation;
    }

    public void setFortification(int fort)
    {
        fortification = fort;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public void setDock(int dock)
    {
        this.dock = dock;
    }

    public void setCapital(int capital)
    {
        if(capital == 0)
        {
            this.capital = false;
        } else
        {
            this.capital = true;
        }
    }

    public void setLoyalty(int loyalty)
    {
        this.loyalty = loyalty;
    }

    public void setHidden(int hidden)
    {
        if(hidden == 0)
        {
            this.hidden = false;
        } else
        {
            this.hidden = true;
        }
    }

    public void setNewPC()
    {
        newpc = true;
    }

    public int getNation()
    {
        return nation;
    }

    public String getName()
    {
        return name;
    }

    public int getLocation()
    {
        return location;
    }

    int getFortification()
    {
        return fortification;
    }

    int getSize()
    {
        return size;
    }

    int getDock()
    {
        return dock;
    }

    boolean getCapital()
    {
        return capital;
    }

    int getLoyalty()
    {
        return loyalty;
    }

    boolean getHidden()
    {
        return hidden;
    }

    int getPossibleDestruction()
    {
        return possibleDestruction;
    }

    int getPossibleCapture()
    {
        return possibleCapture;
    }

    int getPossibleInfluence()
    {
        return possibleInfluence;
    }

    boolean getEnemyArmyPresent()
    {
        return enemyArmyPresent;
    }

    int getCapturingNation()
    {
        return capturingNation;
    }

    boolean isPopCenterComplete()
    {
        return location != -1 && name != null && nation != -1 && fortification != -1 && size != -1 && dock != -1 && loyalty != -1;
    }

    public String toString()
    {
        String locStr = String.valueOf(location);
        if(locStr.length() == 3)
        {
            locStr = "0" + locStr;
        }
        if(newpc)
        {
            return locStr;
        }
        String desc = name + " (" + locStr + ", ";
        if(location == Main.main.getNation().getCapital())
        {
            desc = desc + "Capital";
        } else
        {
            desc = desc + sizeName[size];
            if(fortification != 0)
            {
                desc = desc + "/" + fortName[fortification];
            }
            if(nation != Main.main.getNation().getNation())
            {
                desc = desc + ", " + Main.main.getNation().getNationName(nation);
            }
        }
        desc = desc + ")";
        return desc;
    }

}
