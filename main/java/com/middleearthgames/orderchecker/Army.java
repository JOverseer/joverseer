// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Army.java

package com.middleearthgames.orderchecker;

import java.io.PrintStream;
import java.util.Vector;

// Referenced classes of package com.middleearthgames.orderchecker:
//            StateList, Character, Main, Nation

public class Army
{

    static final int HEAVY_CAVALRY = 0;
    static final int LIGHT_CAVALRY = 1;
    static final int HEAVY_INFANTRY = 2;
    static final int LIGHT_INFANTRY = 3;
    static final int ARCHERS = 4;
    static final int MEN_AT_ARMS = 5;
    private static final String troopTypes[] = {
        "HC", "LC", "HI", "LI", "AR", "MA"
    };
    private static final String troopNames[] = {
        "Heavy Cavalry", "Light Cavalry", "Heavy Infantry", "Light Infantry", "Archers", "Men-at-Arms"
    };
    private int location;
    private int nation;
    private int troops;
    private String commander;
    private String extraInfo;
    private boolean navy;
    private String charsWith;
    private StateList troopContent;
    private StateList armyCommander;
    private int foodRequired;
    private boolean hasEnoughFood;

    public Army(int location)
    {
        this.location = -1;
        nation = -1;
        troops = -1;
        commander = null;
        extraInfo = null;
        navy = false;
        charsWith = null;
        troopContent = new StateList();
        armyCommander = new StateList();
        foodRequired = 0;
        hasEnoughFood = false;
        this.location = location;
    }

    static String getTroopName(int type)
    {
        if(type >= 0 && type < 6)
        {
            return troopNames[type];
        } else
        {
            return "";
        }
    }

    static int findTroopType(String name)
    {
        for(int i = 0; i < 6 && name != null; i++)
        {
            if(troopTypes[i].equalsIgnoreCase(name))
            {
                return i;
            }
        }

        throw new RuntimeException("findTroopType(): troop (" + name + ") not found");
    }

    void initStateInformation()
    {
        troopContent.clear();
        troopContent.putDefaultValue(((Object) (getTroopContent())));
        Character character = Main.main.getNation().findCharacterByFullName(commander);
        armyCommander.putDefaultValue(((Object) (character)));
        foodRequired = 0;
        hasEnoughFood = false;
    }

    void printStateInformation(int order)
    {
        if(nation == Main.main.getNation().getNation() || Nation.isEnemy(nation, Main.main.getNation().getNation()))
        {
            String desc = "ARMY, " + order + ": " + getArmyDescription(((Character) (null)), order);
            desc = desc + ", food = " + foodRequired + ", has enough = " + hasEnoughFood;
            System.out.println(desc);
        }
    }

    void setNewLocation(int location, int order)
    {
        Vector characters = Main.main.getNation().findCharactersByArmy(this, order);
        for(int i = 0; i < characters.size(); i++)
        {
            Character character = (Character)characters.get(i);
            character.setNewLocation(location, order);
        }

    }

    void disbandArmy(Army army, int order, boolean includeCO)
    {
        Vector characters = Main.main.getNation().findCharactersByArmy(this, order);
        for(int i = 0; i < characters.size(); i++)
        {
            Character character = (Character)characters.get(i);
            if(character == getArmyCommander(order) && includeCO || character != getArmyCommander(order))
            {
                character.setArmy(army, order);
            }
        }

        emptyArmy(order);
        setArmyCommander(((Character) (null)), order);
    }

    private void emptyArmy(int order)
    {
        int amounts[] = getTroopContent(order);
        for(int i = 0; i < 6; i++)
        {
            amounts[i] *= -1;
        }

        setTroopContent(amounts, order);
    }

    public String getArmyDescription(Character character, int order)
    {
        StringBuffer armyDesc = new StringBuffer();
        Character armyCommander = getArmyCommander(order);
        if(armyCommander == null)
        {
            armyDesc.append("Army Disbanded with ");
        } else
        if(armyCommander == character)
        {
            armyDesc.append("Commands Army with ");
        } else
        {
            armyDesc.append("In " + armyCommander.getName() + "'s Army with ");
        }
        Vector characters = Main.main.getNation().findCharactersByArmy(this, order);
        for(int i = 0; i < characters.size(); i++)
        {
            Character armyChar = (Character)characters.get(i);
            if(character != armyChar && armyChar != armyCommander)
            {
                armyDesc.append(armyChar.getName() + ", ");
            }
        }

        boolean troopPresent = false;
        int amounts[] = getTroopContent(order);
        for(int i = 0; i < 6; i++)
        {
            if(amounts[i] > 0)
            {
                armyDesc.append(amounts[i] + " " + troopTypes[i] + ", ");
                troopPresent = true;
            }
        }

        if(troopPresent)
        {
            armyDesc.delete(armyDesc.length() - 2, armyDesc.length());
        }
        return armyDesc.toString();
    }

    boolean isCharacterInArmy(String name)
    {
        if(charsWith == null)
        {
            return false;
        } else
        {
            int index = charsWith.indexOf(name);
            return index != -1;
        }
    }

    Character getCharacterIdInArmy(String id)
    {
        if(commander == null)
        {
            return null;
        }
        String charName = "";
        if(commander.length() >= 5)
        {
            charName = commander.substring(0, 5);
        }
        if(!charName.equalsIgnoreCase(id))
        {
            if(charsWith == null)
            {
                return null;
            }
            String charList[] = charsWith.split(" - ");
            for(int i = 0; i < charList.length && !charName.equalsIgnoreCase(id); i++)
            {
                if(charList[i].length() >= 5)
                {
                    charName = charList[i].substring(0, 5);
                }
            }

        }
        if(charName.equalsIgnoreCase(id))
        {
            Character newChar = new Character(id);
            newChar.setName(id);
            newChar.setNation(nation);
            newChar.setNewLocation(location, 0);
            newChar.setArmy(this, 0);
            return newChar;
        } else
        {
            return null;
        }
    }

    boolean isAllCavalry(int order)
    {
        int amounts[] = getTroopContent(order);
        for(int i = 0; i < 6; i++)
        {
            if(i != 0 && i != 1 && amounts[i] > 0)
            {
                return false;
            }
        }

        return true;
    }

    private int[] getTroopContent()
    {
        int amounts[] = new int[6];
        for(int type = 0; type < 6; type++)
        {
            try
            {
                int index = extraInfo.indexOf(troopTypes[type]);
                if(index != -1)
                {
                    int begin = extraInfo.lastIndexOf(' ', index);
                    String amountString = extraInfo.substring(begin + 1, index);
                    amounts[type] = Integer.parseInt(amountString);
                }
            }
            catch(Exception ex) { }
        }

        return amounts;
    }

    int[] getTroopContent(int order)
    {
        int amounts[] = new int[6];
        int keys[] = troopContent.getKeys();
        Object values[] = troopContent.getValues();
        if(keys.length == 0)
        {
            throw new RuntimeException("getTroopContent(): no troops found");
        }
        if(keys.length != values.length)
        {
            throw new RuntimeException("getTroopContent(): keys and values mismatch");
        }
        for(int i = 0; i < keys.length; i++)
        {
            if(order <= keys[i] && keys[i] != 0)
            {
                continue;
            }
            int troops[] = (int[])(int[])values[i];
            for(int j = 0; j < 6; j++)
            {
                amounts[j] += troops[j];
            }

        }

        return amounts;
    }

    int getFoodRequirement()
    {
        int amounts[] = getTroopContent(0);
        if(amounts == null)
        {
            throw new RuntimeException("getFoodRequirement:  no troops");
        }
        int totalFood = 0;
        for(int i = 0; i < 6; i++)
        {
            int troops = amounts[i];
            totalFood += troops;
            if(i == 0 || i == 1)
            {
                totalFood += troops;
            }
        }

        return totalFood;
    }

    public void setNation(int nation)
    {
        this.nation = nation;
    }

    public void setTroopAmount(int troops)
    {
        this.troops = troops;
    }

    public void setCommander(String commander)
    {
        this.commander = commander;
    }

    public void setExtraInfo(String info)
    {
        extraInfo = info;
    }

    public void setNavy(int navy)
    {
        if(navy == 0)
        {
            this.navy = false;
        } else
        {
            this.navy = true;
        }
    }

    public void setCharactersWith(String name)
    {
        charsWith = name;
    }

    void setTroopContent(int amounts[], int order)
    {
        int change[] = new int[6];
        for(int i = 0; i < 6; i++)
        {
            change[i] = amounts[i];
        }

        troopContent.put(order, ((Object) (change)));
    }

    void setArmyCommander(Character character, int order)
    {
        armyCommander.put(order, ((Object) (character)));
    }

    void setFoodRequired(int amount)
    {
        foodRequired = amount;
    }

    void setHasEnoughFood(boolean value)
    {
        hasEnoughFood = value;
    }

    void setNewArmy(Character commander, int amounts[], int order)
    {
        int troops[] = new int[6];
        setTroopContent(troops, 0);
        setArmyCommander(((Character) (null)), 0);
        setArmyCommander(commander, order);
        commander.setArmy(this, order);
        setNation(commander.getNation());
        setTroopContent(amounts, order);
    }

    public int getNation()
    {
        return nation;
    }

    int getLocation()
    {
        return location;
    }

    public String getCommander()
    {
        return commander;
    }

    int getTroops()
    {
        return troops;
    }

    int getTotalTroops(int order)
    {
        int amounts[] = getTroopContent(order);
        if(amounts == null)
        {
            throw new RuntimeException("getTotalTroops(): no troops found");
        }
        int total = 0;
        for(int i = 0; i < 6; i++)
        {
            total += amounts[i];
        }

        return total;
    }

    boolean isNavy()
    {
        return navy;
    }

    Character getArmyCommander(int order)
    {
        Character commander = (Character)armyCommander.findValueByOrderNumber(order);
        return commander;
    }

    int getFoodRequired()
    {
        return foodRequired;
    }

    boolean getHasEnoughFood()
    {
        return hasEnoughFood;
    }

    boolean IsArmyComplete()
    {
        return location != -1 && nation != -1 && troops != -1 && commander != null;
    }

}
