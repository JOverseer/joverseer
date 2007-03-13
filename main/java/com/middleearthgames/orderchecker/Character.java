// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Character.java

package com.middleearthgames.orderchecker;

import com.middleearthgames.orderchecker.gui.OCTreeNode;
import java.io.PrintStream;
import java.util.*;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

// Referenced classes of package com.middleearthgames.orderchecker:
//            StateList, Order, Army, Main, 
//            Nation, ArtifactList, Artifact

public class Character
    implements Comparable
{

    public static final int HOSTAGE_LOCATION = 0;
    static final int NUM_RANKS = 4;
    static final int COMMAND_RANK = 0;
    static final int AGENT_RANK = 1;
    static final int EMISSARY_RANK = 2;
    static final int MAGE_RANK = 3;
    private static final String rankNames[] = {
        "Command", "Agent", "Emissary", "Mage"
    };
    private String id;
    private String name;
    private int location;
    private int nation;
    private int command;
    private int totalCommand;
    private int agent;
    private int totalAgent;
    private int emissary;
    private int totalEmissary;
    private int mage;
    private int totalMage;
    private int stealth;
    private int totalStealth;
    private int challenge;
    private int health;
    private Vector artifacts;
    private Vector artiInfo;
    private Vector spells;
    private Vector spellInfo;
    private Vector orders;
    StateList army;
    StateList finalLocation;
    StateList companyLeader;
    StateList newArtifacts;

    public Character(String id)
    {
        this.id = null;
        name = null;
        location = -1;
        nation = -1;
        command = -1;
        totalCommand = -1;
        agent = -1;
        totalAgent = -1;
        emissary = -1;
        totalEmissary = -1;
        mage = -1;
        totalMage = -1;
        stealth = -1;
        totalStealth = -1;
        challenge = -1;
        health = -1;
        artifacts = new Vector();
        artiInfo = new Vector();
        spells = new Vector();
        spellInfo = new Vector();
        orders = new Vector();
        army = new StateList();
        finalLocation = new StateList();
        companyLeader = new StateList();
        newArtifacts = new StateList();
        this.id = id;
    }

    static String getRankName(int rank)
    {
        if(rank >= 0 && rank < 4)
        {
            return rankNames[rank];
        } else
        {
            return "";
        }
    }

    int getNaturalRank(int type)
    {
        int rank = -1;
        switch(type)
        {
        case 0: // '\0'
            rank = command;
            break;

        case 1: // '\001'
            rank = agent;
            break;

        case 2: // '\002'
            rank = emissary;
            break;

        case 3: // '\003'
            rank = mage;
            break;

        default:
            throw new RuntimeException("getNaturalRank(): invalid type (" + type + ")");
        }
        return rank;
    }

    void initStateInformation()
    {
        army.clear();
        Army startArmy = Main.main.getNation().findCharacterInArmy(name);
        if(startArmy != null)
        {
            army.putDefaultValue(((Object) (startArmy)));
        }
        finalLocation.clear();
        companyLeader.clear();
        newArtifacts.clear();
        int size = artifacts.size();
        if(size > 0)
        {
            int artiList[] = new int[size];
            for(int i = 0; i < size; i++)
            {
                Integer artifact = (Integer)artifacts.get(i);
                artiList[i] = artifact.intValue();
            }

            Arrays.sort(artiList);
            newArtifacts.putDefaultValue(((Object) (artiList)));
        }
    }

    void printStateInformation(int order)
    {
        if(nation != Main.main.getNation().getNation())
        {
            return;
        }
        String armyDesc = "NO";
        Army army = getArmy(order);
        if(army != null)
        {
            armyDesc = army.getArmyCommander(order).getName();
        }
        String desc = "CHARACTER, " + order + ": " + getName() + ", Location " + getLocation(order) + ", company = " + isCompanyCO(order) + ", army = " + armyDesc;
        System.out.println(desc);
    }

    public String getEndOfTurnInfo()
    {
        Army army = getArmy(9999);
        int location = getLocation(9999);
        if(army == null && getArmy(0) == null && location == getLocation(0))
        {
            return null;
        }
        StringBuffer desc = new StringBuffer();
        desc.append("End of Turn: Location " + Main.main.locationStr(location));
        if(isCompanyCO(9999))
        {
            desc.append(", Company CO");
        }
        if(army != null)
        {
            String armyDesc = army.getArmyDescription(this, 9999);
            desc.append(", " + armyDesc);
        }
        return desc.toString();
    }

    void collectOrders(int phase, Vector orderList)
    {
        if(phase == 1)
        {
            Collections.sort(((java.util.List) (orders)));
        }
        for(int i = 0; i < orders.size(); i++)
        {
            Order charOrder = (Order)orders.get(i);
            boolean bInserted = false;
            for(int j = 0; j < orderList.size() && !bInserted; j++)
            {
                Order order = (Order)orderList.get(j);
                if(charOrder.getOrder() < order.getOrder())
                {
                    orderList.add(j, ((Object) (charOrder)));
                    bInserted = true;
                }
            }

            if(!bInserted)
            {
                orderList.add(((Object) (charOrder)));
            }
        }

    }

    void addTreeNodes(JTree tree, DefaultMutableTreeNode parent)
    {
        OCTreeNode charNode = new OCTreeNode(tree, ((Object) (this)), true);
        parent.add(((javax.swing.tree.MutableTreeNode) (charNode)));
        int size = orders.size();
        for(int i = 0; i < size; i++)
        {
            Order order = (Order)orders.get(i);
            order.addTreeNodes(tree, charNode);
        }

    }

    void getInfoRequests(Vector list)
    {
        int size = orders.size();
        for(int i = 0; i < size; i++)
        {
            Order order = (Order)orders.get(i);
            order.getInfoRequests(list);
        }

    }

    boolean isCommanderInArmy(int order)
    {
        return getTotalCommandRank() != 0 && getArmy(order) != null;
    }

    String isSpellKnown(int spellNum)
    {
        int size = spells.size();
        for(int i = 0; i < size; i++)
        {
            Integer spell = (Integer)spells.get(i);
            if(spell.intValue() == spellNum)
            {
                return (String)spellInfo.get(i);
            }
        }

        return null;
    }

    boolean hasArtifact(int artifactNum, int order)
    {
        int keys[] = newArtifacts.getKeys();
        Object values[] = newArtifacts.getValues();
        if(keys.length == 0)
        {
            return false;
        }
        if(keys.length != values.length)
        {
            throw new RuntimeException("hasArtifact(): keys and values mismatch");
        }
        boolean hasArtifact = false;
        for(int i = 0; i < keys.length; i++)
        {
            if(order <= keys[i] && keys[i] != 0)
            {
                continue;
            }
            int artifactList[] = (int[])(int[])values[i];
            if(Arrays.binarySearch(artifactList, artifactNum) >= 0)
            {
                hasArtifact = true;
            }
            if(Arrays.binarySearch(artifactList, artifactNum * -1) >= 0)
            {
                return false;
            }
        }

        return hasArtifact;
    }

    String getArtifactName(int artifactNum)
    {
        Artifact artifact = Main.main.getArtifactList().findArtifact(artifactNum);
        if(artifact != null)
        {
            return artifact.toString();
        }
        for(int i = 0; i < artifacts.size(); i++)
        {
            Integer artifactVal = (Integer)artifacts.get(i);
            if(artifactVal.intValue() == artifactNum)
            {
                return (String)artiInfo.get(i);
            }
        }

        return null;
    }

    public String getAttributes()
    {
        StringBuffer attributes = new StringBuffer();
        String attribute = checkAttribute("C", command, totalCommand);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attribute = checkAttribute("A", agent, totalAgent);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attribute = checkAttribute("E", emissary, totalEmissary);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attribute = checkAttribute("M", mage, totalMage);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attribute = checkAttribute("St", stealth, totalStealth);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attributes.delete(attributes.length() - 2, attributes.length());
        return attributes.toString();
    }

    private String checkAttribute(String desc, int base, int total)
    {
        if(base == 0)
        {
            return null;
        }
        String attribute = desc + base;
        if(base != total)
        {
            attribute = attribute + "/" + total;
        }
        return attribute;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setLocation(int location)
    {
        this.location = location;
    }

    public void setNation(int nation)
    {
        this.nation = nation;
    }

    public void setCommandRank(int rank)
    {
        command = rank;
    }

    public void setTotalCommandRank(int rank)
    {
        totalCommand = rank;
    }

    public void setAgentRank(int rank)
    {
        agent = rank;
    }

    public void setTotalAgentRank(int rank)
    {
        totalAgent = rank;
    }

    public void setEmissaryRank(int rank)
    {
        emissary = rank;
    }

    public void setTotalEmissaryRank(int rank)
    {
        totalEmissary = rank;
    }

    public void setMageRank(int rank)
    {
        mage = rank;
    }

    public void setTotalMageRank(int rank)
    {
        totalMage = rank;
    }

    public void setStealth(int rank)
    {
        stealth = rank;
    }

    public void setTotalStealth(int rank)
    {
        totalStealth = rank;
    }

    public void setChallenge(int rank)
    {
        challenge = rank;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public void addArtifact(Integer artifact, String info)
    {
        artifacts.add(((Object) (artifact)));
        artiInfo.add(((Object) (info)));
    }

    public void addSpell(Integer spell, String info)
    {
        spells.add(((Object) (spell)));
        spellInfo.add(((Object) (info)));
    }

    public void addOrder(Order order)
    {
        orders.add(((Object) (order)));
    }

    void setArmy(Army army, int order)
    {
        this.army.put(order, ((Object) (army)));
    }

    void setNewLocation(int location, int order)
    {
        finalLocation.put(order, ((Object) (new Integer(location))));
    }

    void setCompanyCO(boolean state, int order)
    {
        companyLeader.put(order, ((Object) (new Boolean(state))));
    }

    void setNewArtifacts(int artifactList[], int order)
    {
        Arrays.sort(artifactList);
        newArtifacts.put(order, ((Object) (artifactList)));
    }

    public String getId()
    {
        return id;
    }

    public int getNation()
    {
        return nation;
    }

    public String getName()
    {
        return name;
    }

    public int getLocation(int order)
    {
        Integer location = (Integer)finalLocation.findValueByOrderNumber(order);
        if(location == null)
        {
            return this.location;
        } else
        {
            return location.intValue();
        }
    }

    boolean getLocationChange()
    {
        return !finalLocation.isEmpty();
    }

    int getNaturalCommandRank()
    {
        return command;
    }

    int getTotalCommandRank()
    {
        return totalCommand;
    }

    int getNaturalAgentRank()
    {
        return agent;
    }

    int getTotalAgentRank()
    {
        return totalAgent;
    }

    int getNaturalEmissaryRank()
    {
        return emissary;
    }

    int getTotalEmissaryRank()
    {
        return totalEmissary;
    }

    int getNaturalMageRank()
    {
        return mage;
    }

    int getTotalMageRank()
    {
        return totalMage;
    }

    int getOrderCount()
    {
        return orders.size();
    }

    int getOrder(int index)
    {
        if(index >= 0 && index < orders.size())
        {
            return ((Integer)orders.get(index)).intValue();
        } else
        {
            return -1;
        }
    }

    int getSpellCount()
    {
        return spells.size();
    }

    int[] getSpells()
    {
        try {
            int size;
            int list[];
            size = getSpellCount();
            list = new int[size];
            for(int i = 0; i < size; i++)
            {
                Integer value = (Integer)spells.get(i);
                list[i] = value.intValue();
            }
    
            return list;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public Army getArmy(int order)
    {
        Army army = (Army)this.army.findValueByOrderNumber(order);
        return army;
    }

    boolean isArmyCO(int order)
    {
        Army army = getArmy(order);
        return army != null && army.getArmyCommander(order) == this;
    }

    boolean isCompanyCO(int order)
    {
        Boolean companyCO = (Boolean)companyLeader.findValueByOrderNumber(order);
        if(companyCO == null)
        {
            return false;
        } else
        {
            return companyCO.booleanValue();
        }
    }

    boolean isCharacterComplete()
    {
        return name != null && location != -1 && command != -1 && totalCommand != -1 && agent != -1 && totalAgent != -1 && emissary != -1 && totalEmissary != -1 && mage != -1 && totalMage != -1 && stealth != -1 && totalStealth != -1 && challenge != -1 && health != -1 && nation != -1 && id != null;
    }

    public int compareTo(Object object)
    {
        Character character = (Character)object;
        return id.compareTo(character.id);
    }

    public String toString()
    {
        return name;
    }

    
    public Vector getOrders() {
        return orders;
    }

}
