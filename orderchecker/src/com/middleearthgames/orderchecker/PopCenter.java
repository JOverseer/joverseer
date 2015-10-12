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
        this.name = null;
        this.nation = -1;
        this.fortification = -1;
        this.size = -1;
        this.dock = -1;
        this.capital = false;
        this.loyalty = -1;
        this.hidden = false;
        this.newpc = false;
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
        return this.capital && nationNumber == this.nation;
    }

    public void mergePopulationCenter(PopCenter pc)
    {
        if(this.nation <= 0 && pc.getNation() > 0)
        {
            this.name = pc.getName();
            this.nation = pc.getNation();
        }
        if(this.fortification <= 0 && pc.getFortification() > 0)
        {
            this.fortification = pc.getFortification();
        }
        if(this.size <= 0 && pc.getSize() > 0)
        {
            this.size = pc.getSize();
        }
        if(this.dock <= 0 && pc.getDock() > 0)
        {
            this.dock = pc.getDock();
        }
        if(!this.capital && pc.getCapital())
        {
            this.capital = true;
        }
        if(this.loyalty <= 0 && pc.getLoyalty() > 0)
        {
            this.loyalty = pc.getLoyalty();
        }
        if(!this.hidden && pc.getHidden())
        {
            this.hidden = true;
        }
    }

    void initStateInformation()
    {
        this.troopsAvailable = getMaximumTroops();
        this.possibleDestruction = 9999;
        this.possibleCapture = 9999;
        this.possibleInfluence = 9999;
        this.enemyArmyPresent = Main.main.getNation().isEnemyArmyPresent(this.nation, this.location);
        this.capturingNation = Main.main.getNation().capturingNation(this, 0);
    }

    int improvementRequirement()
    {
        return improveReq[this.size];
    }

    int threatenRequirement()
    {
        return threatenReq[this.size];
    }

    int getMaximumTroops()
    {
        return this.size * 100;
    }

    int reduceTroopLimit(int amount)
    {
        this.troopsAvailable -= amount;
        return this.troopsAvailable;
    }

    int getFoodProvided()
    {
        return food[this.size];
    }

    void setPossibleDestruction(int value)
    {
        this.possibleDestruction = value;
    }

    void setPossibleCapture(int value)
    {
        this.possibleCapture = value;
    }

    void setPossibleInfluence(int value)
    {
        this.possibleInfluence = value;
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
        this.fortification = fort;
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
        this.newpc = true;
    }

    public int getNation()
    {
        return this.nation;
    }

    public String getName()
    {
        return this.name;
    }

    public int getLocation()
    {
        return this.location;
    }

    int getFortification()
    {
        return this.fortification;
    }

    int getSize()
    {
        return this.size;
    }

    int getDock()
    {
        return this.dock;
    }

    boolean getCapital()
    {
        return this.capital;
    }

    int getLoyalty()
    {
        return this.loyalty;
    }

    boolean getHidden()
    {
        return this.hidden;
    }

    int getPossibleDestruction()
    {
        return this.possibleDestruction;
    }

    int getPossibleCapture()
    {
        return this.possibleCapture;
    }

    int getPossibleInfluence()
    {
        return this.possibleInfluence;
    }

    boolean getEnemyArmyPresent()
    {
        return this.enemyArmyPresent;
    }

    int getCapturingNation()
    {
        return this.capturingNation;
    }

    boolean isPopCenterComplete()
    {
        return this.location != -1 && this.name != null && this.nation != -1 && this.fortification != -1 && this.size != -1 && this.dock != -1 && this.loyalty != -1;
    }

    @Override
	public String toString()
    {
        String locStr = String.valueOf(this.location);
        if(locStr.length() == 3)
        {
            locStr = "0" + locStr;
        }
        if(this.newpc)
        {
            return locStr;
        }
        String desc = this.name + " (" + locStr + ", ";
        if(this.location == Main.main.getNation().getCapital())
        {
            desc = desc + "Capital";
        } else
        {
            desc = desc + sizeName[this.size];
            if(this.fortification != 0)
            {
                desc = desc + "/" + fortName[this.fortification];
            }
            if(this.nation != Main.main.getNation().getNation())
            {
                desc = desc + ", " + Main.main.getNation().getNationName(this.nation);
            }
        }
        desc = desc + ")";
        return desc;
    }

}
