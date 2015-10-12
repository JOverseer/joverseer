// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Character.java

package com.middleearthgames.orderchecker;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.middleearthgames.orderchecker.gui.OCTreeNode;

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
        this.name = null;
        this.location = -1;
        this.nation = -1;
        this.command = -1;
        this.totalCommand = -1;
        this.agent = -1;
        this.totalAgent = -1;
        this.emissary = -1;
        this.totalEmissary = -1;
        this.mage = -1;
        this.totalMage = -1;
        this.stealth = -1;
        this.totalStealth = -1;
        this.challenge = -1;
        this.health = -1;
        this.artifacts = new Vector();
        this.artiInfo = new Vector();
        this.spells = new Vector();
        this.spellInfo = new Vector();
        this.orders = new Vector();
        this.army = new StateList();
        this.finalLocation = new StateList();
        this.companyLeader = new StateList();
        this.newArtifacts = new StateList();
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
            rank = this.command;
            break;

        case 1: // '\001'
            rank = this.agent;
            break;

        case 2: // '\002'
            rank = this.emissary;
            break;

        case 3: // '\003'
            rank = this.mage;
            break;

        default:
            throw new RuntimeException("getNaturalRank(): invalid type (" + type + ")");
        }
        return rank;
    }

    void initStateInformation()
    {
        this.army.clear();
        Army startArmy = Main.main.getNation().findCharacterInArmy(this.name);
        if(startArmy != null)
        {
            this.army.putDefaultValue(((Object) (startArmy)));
        }
        this.finalLocation.clear();
        this.companyLeader.clear();
        this.newArtifacts.clear();
        int size = this.artifacts.size();
        if(size > 0)
        {
            int artiList[] = new int[size];
            for(int i = 0; i < size; i++)
            {
                Integer artifact = (Integer)this.artifacts.get(i);
                artiList[i] = artifact.intValue();
            }

            Arrays.sort(artiList);
            this.newArtifacts.putDefaultValue(((Object) (artiList)));
        }
    }

    void printStateInformation(int order)
    {
        if(this.nation != Main.main.getNation().getNation())
        {
            return;
        }
        String armyDesc = "NO";
        Army army1 = getArmy(order);
        if(army1 != null)
        {
            armyDesc = army1.getArmyCommander(order).getName();
        }
        String desc = "CHARACTER, " + order + ": " + getName() + ", Location " + getLocation(order) + ", company = " + isCompanyCO(order) + ", army = " + armyDesc;
        System.out.println(desc);
    }

    public String getEndOfTurnInfo()
    {
        Army army1 = getArmy(9999);
        int location1 = getLocation(9999);
        if(army1 == null && getArmy(0) == null && location1 == getLocation(0))
        {
            return null;
        }
        StringBuffer desc = new StringBuffer();
        desc.append("End of Turn: Location " + Main.main.locationStr(location1));
        if(isCompanyCO(9999))
        {
            desc.append(", Company CO");
        }
        if(army1 != null)
        {
            String armyDesc = army1.getArmyDescription(this, 9999);
            desc.append(", " + armyDesc);
        }
        return desc.toString();
    }

    void collectOrders(int phase, Vector orderList)
    {
        if(phase == 1)
        {
            Collections.sort(((java.util.List) (this.orders)));
        }
        for(int i = 0; i < this.orders.size(); i++)
        {
            Order charOrder = (Order)this.orders.get(i);
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
        int size = this.orders.size();
        for(int i = 0; i < size; i++)
        {
            Order order = (Order)this.orders.get(i);
            order.addTreeNodes(tree, charNode);
        }

    }

    void getInfoRequests(Vector list)
    {
        int size = this.orders.size();
        for(int i = 0; i < size; i++)
        {
            Order order = (Order)this.orders.get(i);
            order.getInfoRequests(list);
        }

    }

    boolean isCommanderInArmy(int order)
    {
        return getTotalCommandRank() != 0 && getArmy(order) != null;
    }

    String isSpellKnown(int spellNum)
    {
        int size = this.spells.size();
        for(int i = 0; i < size; i++)
        {
            Integer spell = (Integer)this.spells.get(i);
            if(spell.intValue() == spellNum)
            {
                return (String)this.spellInfo.get(i);
            }
        }

        return null;
    }

    boolean hasArtifact(int artifactNum, int order)
    {
        int keys[] = this.newArtifacts.getKeys();
        Object values[] = this.newArtifacts.getValues();
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
        for(int i = 0; i < this.artifacts.size(); i++)
        {
            Integer artifactVal = (Integer)this.artifacts.get(i);
            if(artifactVal.intValue() == artifactNum)
            {
                return (String)this.artiInfo.get(i);
            }
        }

        return null;
    }

    public String getAttributes()
    {
        StringBuffer attributes = new StringBuffer();
        String attribute = checkAttribute("C", this.command, this.totalCommand);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attribute = checkAttribute("A", this.agent, this.totalAgent);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attribute = checkAttribute("E", this.emissary, this.totalEmissary);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attribute = checkAttribute("M", this.mage, this.totalMage);
        if(attribute != null)
        {
            attributes.append(attribute + ", ");
        }
        attribute = checkAttribute("St", this.stealth, this.totalStealth);
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
        this.command = rank;
    }

    public void setTotalCommandRank(int rank)
    {
        this.totalCommand = rank;
    }

    public void setAgentRank(int rank)
    {
        this.agent = rank;
    }

    public void setTotalAgentRank(int rank)
    {
        this.totalAgent = rank;
    }

    public void setEmissaryRank(int rank)
    {
        this.emissary = rank;
    }

    public void setTotalEmissaryRank(int rank)
    {
        this.totalEmissary = rank;
    }

    public void setMageRank(int rank)
    {
        this.mage = rank;
    }

    public void setTotalMageRank(int rank)
    {
        this.totalMage = rank;
    }

    public void setStealth(int rank)
    {
        this.stealth = rank;
    }

    public void setTotalStealth(int rank)
    {
        this.totalStealth = rank;
    }

    public void setChallenge(int rank)
    {
        this.challenge = rank;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public void addArtifact(Integer artifact, String info)
    {
        this.artifacts.add(((Object) (artifact)));
        this.artiInfo.add(((Object) (info)));
    }

    public void addSpell(Integer spell, String info)
    {
        this.spells.add(((Object) (spell)));
        this.spellInfo.add(((Object) (info)));
    }

    public void addOrder(Order order)
    {
        this.orders.add(((Object) (order)));
    }

    void setArmy(Army army, int order)
    {
        this.army.put(order, ((Object) (army)));
    }

    void setNewLocation(int location, int order)
    {
        this.finalLocation.put(order, ((Object) (new Integer(location))));
    }

    void setCompanyCO(boolean state, int order)
    {
        this.companyLeader.put(order, ((Object) (new Boolean(state))));
    }

    void setNewArtifacts(int artifactList[], int order)
    {
        Arrays.sort(artifactList);
        this.newArtifacts.put(order, ((Object) (artifactList)));
    }

    public String getId()
    {
        return this.id;
    }

    public int getNation()
    {
        return this.nation;
    }

    public String getName()
    {
        return this.name;
    }

    public int getLocation(int order)
    {
        Integer location1 = (Integer)this.finalLocation.findValueByOrderNumber(order);
        if(location1 == null)
        {
            return this.location;
        } else
        {
            return location1.intValue();
        }
    }

    boolean getLocationChange()
    {
        return !this.finalLocation.isEmpty();
    }

    int getNaturalCommandRank()
    {
        return this.command;
    }

    int getTotalCommandRank()
    {
        return this.totalCommand;
    }

    int getNaturalAgentRank()
    {
        return this.agent;
    }

    int getTotalAgentRank()
    {
        return this.totalAgent;
    }

    int getNaturalEmissaryRank()
    {
        return this.emissary;
    }

    int getTotalEmissaryRank()
    {
        return this.totalEmissary;
    }

    int getNaturalMageRank()
    {
        return this.mage;
    }

    int getTotalMageRank()
    {
        return this.totalMage;
    }

    int getOrderCount()
    {
        return this.orders.size();
    }

    int getOrder(int index)
    {
        if(index >= 0 && index < this.orders.size())
        {
            return ((Integer)this.orders.get(index)).intValue();
        } else
        {
            return -1;
        }
    }

    int getSpellCount()
    {
        return this.spells.size();
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
                Integer value = (Integer)this.spells.get(i);
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
        Army anotherArmy = (Army)this.army.findValueByOrderNumber(order);
        return anotherArmy;
    }

    boolean isArmyCO(int order)
    {
        Army army1 = getArmy(order);
        return army1 != null && army1.getArmyCommander(order) == this;
    }

    boolean isCompanyCO(int order)
    {
        Boolean companyCO = (Boolean)this.companyLeader.findValueByOrderNumber(order);
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
        return this.name != null && this.location != -1 && this.command != -1 && this.totalCommand != -1 && this.agent != -1 && this.totalAgent != -1 && this.emissary != -1 && this.totalEmissary != -1 && this.mage != -1 && this.totalMage != -1 && this.stealth != -1 && this.totalStealth != -1 && this.challenge != -1 && this.health != -1 && this.nation != -1 && this.id != null;
    }

    @Override
	public int compareTo(Object object)
    {
        Character character = (Character)object;
        return this.id.compareTo(character.id);
    }

    @Override
	public String toString()
    {
        return this.name;
    }

    
    public Vector getOrders() {
        return this.orders;
    }

}
