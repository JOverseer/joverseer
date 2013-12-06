// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Order.java

package com.middleearthgames.orderchecker;

import com.middleearthgames.orderchecker.gui.OCTreeNode;
import java.util.Vector;
import javax.swing.JTree;

// Referenced classes of package com.middleearthgames.orderchecker:
//            Ruleset, Main, Nation, Character, 
//            Map, Hex

public class Order
    implements Comparable
{

    public static final int NO_ORDER = 9999;
    public static final int START_TURN = 0;
    public static final String infoOrder = "[G] ";
    public static final String warnOrder = "[Y] ";
    public static final String errorOrder = "[R] ";
    public static final String helpOrder = "[H] ";
    private final int orderNumber;
    private Ruleset rules;
    private final Vector parameters = new Vector();
    private final Character parent;
    private boolean done;
    private Vector infoResults;
    private Vector warnResults;
    private Vector errorResults;
    private Vector helpResults;

    public Order(Character parent, int number)
    {
        this.infoResults = new Vector();
        this.warnResults = new Vector();
        this.errorResults = new Vector();
        this.helpResults = new Vector();
        this.parent = parent;
        this.orderNumber = number;
    }

    void printStateInformation()
    {
        this.rules.printStateInformation();
    }

    String implementPhase(int phase)
    {
        if(this.orderNumber == 9999)
        {
            if(phase == 1)
            {
                this.errorResults.add("Missing order.");
            }
            this.done = true;
            return null;
        }
        if(phase == 1)
        {
            this.rules = Main.main.getRuleSet().getRulesForOrder(this.orderNumber, false);
        }
        if(this.rules.size() == 0)
        {
            if(phase == 2)
            {
                this.warnResults.add("No rules exist for this order.");
            }
            this.done = true;
            return null;
        }
        if(phase == 1)
        {
            this.infoResults.clear();
            this.warnResults.clear();
            this.errorResults.clear();
            this.helpResults.clear();
            this.done = false;
        }
        String result = this.rules.processRules(this, phase);
        if(result == null && this.rules.getDone())
        {
            this.done = true;
        }
        return result;
    }

    boolean getDone()
    {
        return this.done;
    }

    boolean getStateDone(int state)
    {
        if(this.rules != null)
        {
            return this.rules.getStateDone(state);
        } else
        {
            return true;
        }
    }

    Ruleset getRules()
    {
        return this.rules;
    }

    void addWarning(String msg)
    {
        this.warnResults.add(((Object) (msg)));
    }

    void addError(String msg)
    {
        this.errorResults.add(((Object) (msg)));
    }

    void addInfo(String msg)
    {
        this.infoResults.add(((Object) (msg)));
    }

    void addHelp(String msg)
    {
        this.helpResults.add(((Object) (msg)));
    }

    public void addTreeNodes(JTree tree, OCTreeNode parent1)
    {
        OCTreeNode orderNode = new OCTreeNode(tree, ((Object) (this)), true);
        parent1.add(((javax.swing.tree.MutableTreeNode) (orderNode)));
        int errors = this.errorResults.size();
        for(int i = 0; i < errors; i++)
        {
            String resultNode = "[R] " + (String)this.errorResults.get(i);
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }

        int warnings = this.warnResults.size();
        for(int i = 0; i < warnings; i++)
        {
            String resultNode = "[Y] " + (String)this.warnResults.get(i);
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }

        int messages = this.infoResults.size();
        for(int i = 0; i < messages; i++)
        {
            String resultNode = "[G] " + (String)this.infoResults.get(i);
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }

        int helpmsgs = this.helpResults.size();
        for(int i = 0; i < helpmsgs; i++)
        {
            String resultNode = "[H] " + (String)this.helpResults.get(i);
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }

        if(errors + warnings + messages + helpmsgs == 0)
        {
            String resultNode = "[G] ";
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }
    }

    public String getParameterList()
    {
        String paramList = "";
        int size = this.parameters.size();
        for(int i = 0; i < size; i++)
        {
            String param = (String)this.parameters.get(i);
            paramList = paramList + param + " ";
        }

        return paramList;
    }

    public void addParameter(String param)
    {
        this.parameters.add(((Object) (param)));
    }

    int getNumberOfParameters()
    {
        return this.parameters.size();
    }

    public int getOrder()
    {
        return this.orderNumber;
    }

    Character getParent()
    {
        return this.parent;
    }

    void getInfoRequests(Vector list)
    {
        if(this.rules != null)
        {
            this.rules.getInfoRequests(list);
        }
    }

    @Override
	public int compareTo(Object object)
    {
        Order order = (Order)object;
        if(this.orderNumber < order.orderNumber)
        {
            return -1;
        }
        return this.orderNumber != order.orderNumber ? 1 : 0;
    }

    @Override
	public String toString()
    {
        return String.valueOf(this.orderNumber);
    }

    String getParameterString(int paramNumber)
    {
        int index = paramNumber - 1;
        if(index >= 0 && index < this.parameters.size())
        {
            return (String)this.parameters.get(index);
        } else
        {
            return null;
        }
    }

    int getParameterNumber(int paramNumber)
    {
        try {
            String strValue;
            strValue = getParameterString(paramNumber);
            if(strValue == null)
            {
                return -1;
            }
            return Integer.parseInt(strValue);
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }

    Character extractCharacter(int param, boolean reportError)
    {
        Character character = null;
        if(param == 0)
        {
            character = this.parent;
        } else
        {
            String id = getParameterString(param);
            if(id != null)
            {
                character = Main.main.getNation().findCharacterById(id);
                if(character == null)
                {
                    character = Main.main.getNation().findCharacterIdInArmy(id);
                }
            }
            if(character == null && reportError)
            {
                if(id != null)
                {
                    this.warnResults.add(((Object) ("No information could be obtained for " + id + ".")));
                } else
                {
                    this.errorResults.add("No character was specified.");
                }
            }
        }
        return character;
    }

    int extractLocation(int param, boolean reportError)
    {
        int location = this.parent.getLocation(this.orderNumber);
        if(param > 0)
        {
            location = getParameterNumber(param);
        }
        if(location == -1)
        {
            if(reportError)
            {
                this.errorResults.add("Could not determine the location.");
            }
        } else
        if(location < 101 || location > 4439)
        {
            if(reportError)
            {
                this.errorResults.add(((Object) ("Invalid location of " + location + ".")));
            }
            location = -1;
        }
        return location;
    }

    boolean onLand(int param)
    {
        int location = extractLocation(param, false);
        if(location >= 0)
        {
            Hex hex = Main.main.getMap().findHex(location);
            if(hex != null)
            {
                int terrain = hex.getTerrain();
                if(terrain == 9 || terrain == 8)
                {
                    return false;
                }
            }
        }
        return true;
    }

    
    public Vector getErrorResults() {
        return this.errorResults;
    }

    
    public Vector getHelpResults() {
        return this.helpResults;
    }

    
    public Vector getInfoResults() {
        return this.infoResults;
    }

    
    public Vector getWarnResults() {
        return this.warnResults;
    }
    
    
}
